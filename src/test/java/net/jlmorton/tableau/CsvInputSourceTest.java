package net.jlmorton.tableau;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class CsvInputSourceTest {
    @Test
    public void testParseCsvFile() {
        File sampleCsvFile = new File(getClass().getResource("sample-extract.csv").getFile());
        CsvInputSource csvInputSource = new CsvInputSource(sampleCsvFile);

        assertEquals("100", csvInputSource.getNextRow().get(1));
        assertTrue(csvInputSource.hasNext());
        assertTrue(csvInputSource.hasNext());
        assertTrue(csvInputSource.hasNext());
        assertTrue(csvInputSource.hasNext());
        assertEquals("200", csvInputSource.getNextRow().get(1));
    }
}