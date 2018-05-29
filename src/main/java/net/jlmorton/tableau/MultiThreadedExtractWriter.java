package net.jlmorton.tableau;

import com.tableausoftware.TableauException;
import com.tableausoftware.extract.Row;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

public class MultiThreadedExtractWriter implements ExtractWriter {
    private static final Logger LOGGER = LogManager.getLogger(MultiThreadedExtractWriter.class);
    private static final Logger REJECTS = LogManager.getLogger("Rejects");

    private final RowInputSource rowInputSource;
    private final Properties properties;
    private final ExtractAdapter extractAdapter;

    MultiThreadedExtractWriter(Properties properties, RowInputSource rowInputSource, ExtractAdapter extractAdapter) {
        this.rowInputSource = rowInputSource;
        this.properties = properties;
        this.extractAdapter = extractAdapter;
    }

    @Override
    public void writeExtract() {
        try {
            final ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();
            getExtractAdapter().openExtract();

            LOGGER.info("Parsing Rows");
            while (getRowInputSource().hasNext()) {
                int currentRowNumber = getRowInputSource().getCurrentRowNumber();
                String[] textRow = getRowInputSource().getNextRow();

                logProgress(currentRowNumber);
                threadPoolExecutor.submit(() -> parseAndInsertRow(textRow, currentRowNumber));
            }

            threadPoolExecutor.shutdown();
            threadPoolExecutor.awaitTermination(8, TimeUnit.HOURS);

            LOGGER.info("Completed writing {} rows to {} extract", getRowInputSource().getCurrentRowNumber(), getSchema().getName());
        } catch (TableauException | InterruptedException e) {
            LOGGER.error("Error while writing extract", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeExtract() {
        getExtractAdapter().closeExtract();
    }

    private void parseAndInsertRow(String[] textRow, int rowIndex) {
        try {
            final Row row = RowWriter.parseAndCreateRow(textRow, getExtractAdapter().getTableDefinition());
            getExtractAdapter().insertRow(row);
            row.close();
        } catch (Exception e) {
            REJECTS.info("Row {} {}", rowIndex, e.getMessage());
            LOGGER.error("Could not parse row {}", rowIndex, e);
        }
    }

    private void logProgress(int currentRowParsed) {
        if (currentRowParsed > 0 && (currentRowParsed % 10000) == 0) {
            LOGGER.info("Inserted {} rows to {}", currentRowParsed, getSchema().getName());
        }
    }

    private ExtractAdapter getExtractAdapter() {
        return this.extractAdapter;
    }

    private Properties getProperties() {
        return this.properties;
    }

    private RowInputSource getRowInputSource() {
        return rowInputSource;
    }

    private int getNumberOfThreads() {
        return getProperties().getNumberOfThreads();
    }

    private Schema getSchema() {
        return getProperties().getSchema();
    }

    private ThreadPoolExecutor getThreadPoolExecutor() {
        return new ThreadPoolExecutor(getNumberOfThreads(), getNumberOfThreads(), 0L, TimeUnit.MILLISECONDS, getBlockingQueue(), getRejectedExecutionHandler());
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