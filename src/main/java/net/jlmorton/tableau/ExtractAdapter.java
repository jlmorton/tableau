package net.jlmorton.tableau;

import com.tableausoftware.TableauException;
import com.tableausoftware.extract.Row;
import com.tableausoftware.extract.TableDefinition;

public interface ExtractAdapter {
    void openExtract() throws TableauException;

    void closeExtract();

    void insertRow(Row row) throws TableauException;

    TableDefinition getTableDefinition();

    default String getTableName() {
        return "Extract";
    }
}
