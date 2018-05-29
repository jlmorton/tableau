package net.jlmorton.tableau;

import com.tableausoftware.TableauException;
import com.tableausoftware.common.Type;
import com.tableausoftware.extract.Extract;
import com.tableausoftware.extract.Row;
import com.tableausoftware.extract.Table;
import com.tableausoftware.extract.TableDefinition;

public class TdeExtractAdapter implements ExtractAdapter {
    private final Properties properties;

    private TableDefinition tableDefinition;
    private Extract extract;
    private Table table;

    @SuppressWarnings("WeakerAccess")
    public TdeExtractAdapter(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void openExtract() throws TableauException {
        Extract extract = new Extract(getProperties().getExtractFilePath());
        setExtract(extract);

        if (extract.hasTable(getTableName())) {
            setTable(extract.openTable(getTableName()));
        } else {
            TableDefinition tableDefinition = createTableDefinitionFromSchema(getSchema());
            setTable(extract.addTable(getTableName(), tableDefinition));
            setTableDefinition(tableDefinition);
        }
    }

    @Override
    public void closeExtract() {
        getExtract().close();
        setExtract(null);
        setTable(null);
        setTableDefinition(null);
    }

    public TableDefinition getTableDefinition() {
        if (this.tableDefinition == null) {
            throw new IllegalStateException("Table has not been opened");
        }

        return this.tableDefinition;
    }

    @Override
    public void insertRow(Row row) throws TableauException {
        // Table is not thread-safe.  Though we can do the work of parsing a row in parallel,
        // inserting into the table must be synchronized
        synchronized (getTable()) {
            getTable().insert(row);
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
            throw new RuntimeException("Could not add column to table definition", e);
        }
    }

    private Schema getSchema() {
        return getProperties().getSchema();
    }

    private Properties getProperties() {
        return this.properties;
    }

    public void setExtract(Extract extract) {
        this.extract = extract;
    }

    private Table getTable() {
        return this.table;
    }

    private void setTable(Table table) {
        this.table = table;
    }

    private void setTableDefinition(TableDefinition tableDefinition) {
        this.tableDefinition = tableDefinition;
    }

    Extract getExtract() {
        return this.extract;
    }
}
