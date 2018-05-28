package net.jlmorton.tableau;

import com.tableausoftware.TableauException;
import com.tableausoftware.common.Type;
import com.tableausoftware.extract.Extract;
import com.tableausoftware.extract.Row;
import com.tableausoftware.extract.Table;
import com.tableausoftware.extract.TableDefinition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.*;

public class ExtractWriterImpl implements ExtractWriter {
    private static final Logger LOGGER = LogManager.getLogger(ExtractWriterImpl.class);
    private static final Logger REJECTS = LogManager.getLogger("Rejects");
    private static final String EXTRACT_NAME = "Extract";

    private final Schema schema;
    private final String outputFileName;
    private final int numThreads;
    private final RowInputSource rowInputSource;

    ExtractWriterImpl(Schema schema, RowInputSource rowInputSource, Properties properties) {
        this.schema = schema;
        this.outputFileName = properties.getExtractFilePath();
        this.rowInputSource = rowInputSource;
        this.numThreads = properties.getNumberOfThreads();
    }

    public Extract createExtract() {
        try {
            return createOrAppendToExtract();
        } catch (Exception e) {
            throw new RuntimeException("Could not create extract", e);
        }
    }

    private Extract createOrAppendToExtract() throws TableauException, InterruptedException {
        final Extract extract = new Extract(outputFileName);
        final TableDefinition tableDefinition = createTableDefinitionFromSchema(schema);

        Table table = createOrOpenTable(extract, tableDefinition);

        final ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();

        LOGGER.info("Parsing Rows");

        while (rowInputSource.hasNext()) {
            List<String> row = rowInputSource.getNextRow();
            logProgress(rowInputSource.getCurrentRowNumber());
            threadPoolExecutor.submit(() -> insertRow(tableDefinition, table, row, rowInputSource.getCurrentRowNumber()));
        }

        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(4, TimeUnit.HOURS);

        LOGGER.info("Completed writing {} rows to {} extract", rowInputSource.getCurrentRowNumber(), schema.getName());

        return extract;
    }

    private Table createOrOpenTable(Extract extract, TableDefinition tableDefinition) throws TableauException {
        if (extract.hasTable(EXTRACT_NAME)) {
            return extract.openTable(EXTRACT_NAME);
        } else {
            return extract.addTable(EXTRACT_NAME, tableDefinition);
        }
    }

    private void insertRow(TableDefinition tableDefinition, Table table, List<String> parsedRow, int rowIndex) {
        try {
            final Row row = RowWriter.createRow(parsedRow, tableDefinition);
            insertToTable(table, row);
            row.close();

        } catch (Exception e) {
            REJECTS.info("Row {} {}", rowIndex, e.getMessage());
            LOGGER.error("Could not parse row {}", rowIndex, e);
        }
    }

    private void logProgress(int currentRowParsed) {
        if ((currentRowParsed % 10000) == 0) {
            LOGGER.info("Inserted {} rows to {}", currentRowParsed, schema.getName());
        }
    }

    private void insertToTable(Table table, Row row) throws TableauException {
        // Table is not thread-safe.  Though we can do the work of parsing a row in parallel,
        // inserting into the table must be synchronized
        synchronized (schema) {
            table.insert(row);
        }
    }

    private TableDefinition createTableDefinitionFromSchema(Schema schema) throws TableauException {
        TableDefinition tableDefinition = new TableDefinition();
        schema.getSchema().forEach((name, type) -> safeAddColumn(tableDefinition, name, type));

        return tableDefinition;
    }

    private void safeAddColumn(TableDefinition tableDefinition, String name, Type type) {
        try {
            tableDefinition.addColumn(name, type);
        } catch (TableauException e) {
            LOGGER.error("Error while adding column to table definition: {}", name, e);
        }
    }

    private ThreadPoolExecutor getThreadPoolExecutor() {
        return new ThreadPoolExecutor(numThreads, numThreads, 0L, TimeUnit.MILLISECONDS, getBlockingQueue(), getRejectedExecutionHandler());
    }

    private <E> BlockingQueue<E> getBlockingQueue() {
        return new ArrayBlockingQueue<>(1000);
    }

    private RejectedExecutionHandler getRejectedExecutionHandler() {
        return (runnable, executor) -> {
            try {
                executor.getQueue().put(runnable);
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
        };
    }
}