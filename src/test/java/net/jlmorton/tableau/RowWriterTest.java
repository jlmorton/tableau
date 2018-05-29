package net.jlmorton.tableau;

import com.tableausoftware.common.Type;
import com.tableausoftware.extract.Row;
import com.tableausoftware.extract.TableDefinition;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class RowWriterTest {
    @Test
    public void testCreateRow() throws Exception {
        TableDefinition tableDefinition = new TableDefinition();
        tableDefinition.addColumn("foo_char", Type.CHAR_STRING);
        tableDefinition.addColumn("foo_date", Type.DATE);
        tableDefinition.addColumn("foo_int", Type.INTEGER);

        String[] textRow = new String[]{"bar", "2017-05-01", "100"};
        Row row = RowWriter.parseAndCreateRow(textRow, tableDefinition);

        assertNotNull(row);
    }
}