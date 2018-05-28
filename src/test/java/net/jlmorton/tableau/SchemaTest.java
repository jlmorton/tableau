package net.jlmorton.tableau;

import java.util.Iterator;
import com.tableausoftware.TableauException;
import com.tableausoftware.common.Type;
import com.tableausoftware.extract.Extract;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SchemaTest {
    @Before
    public void setUp() {
        try {
            new Extract("foo");
        } catch (TableauException e) {
            // To initialize Tableau
        }
    }

    @Test
    public void testColumnOrdering() {
        Schema schema = Schema.newInstance();
        schema.addColumn("foo", Type.CHAR_STRING)
            .addColumn("bar", Type.INTEGER)
            .addColumn("date", Type.DATE);

        Iterator<String> keySetIterator = schema.getSchema().keySet().iterator();
        assertEquals("foo", keySetIterator.next());
        assertEquals("bar", keySetIterator.next());
        assertEquals("date", keySetIterator.next());
    }

    @Test
    public void testReadFromJson() throws Exception {
        String fileName = getClass().getResource("sample-schema.json").getFile();
        Schema schema = Schema.fromJson(fileName);

        assertEquals(schema.getSchema().get("foo"), Type.CHAR_STRING);
        assertEquals(schema.getSchema().get("bar"), Type.INTEGER);
        assertEquals(schema.getSchema().get("test"), Type.DATE);
        assertEquals(schema.getSchema().get("test_time"), Type.DATETIME);
    }
}