package net.jlmorton.tableau;

import java.io.File;
import java.io.IOException;
import com.tableausoftware.extract.Extract;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MultiThreadedExtractWriterTest {
    @Test
    public void testCreateExtract() throws Exception {
        String schemaFile = getClass().getResource("sample-schema.json").getFile();
        Schema schema = Schema.fromJson(schemaFile);

        Properties properties = getProperties(schema);
        RowInputSource rowInputSource = new CsvInputSource(getSampleCsvFile());
        TdeExtractAdapter extractAdapter = new TdeExtractAdapter(properties);

        ExtractWriter extractWriter = new MultiThreadedExtractWriter(properties, rowInputSource, extractAdapter);
        extractWriter.writeExtract();

        Extract extract = extractAdapter.getExtract();

        assertNotNull(extract);
        assertTrue(extract.hasTable(extractAdapter.getTableName()));

        extractWriter.closeExtract();
    }

    private String getTemporaryFilePath() {
        try {
            File tempFile = File.createTempFile(RandomStringUtils.randomAlphanumeric(8), ".tde");
            //noinspection ResultOfMethodCallIgnored
            tempFile.delete();

            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Could not create temporary file", e);
        }
    }

    private File getSampleCsvFile() {
        return new File(this.getClass().getResource("sample-extract.csv").getFile());
    }

    private Properties getProperties(Schema schema) {
        return new Properties() {
            @Override
            public Schema getSchema() {
                return schema;
            }

            @Override
            public File getCsvFile() {
                return getSampleCsvFile();
            }

            @Override
            public String getExtractFilePath() {
                return getTemporaryFilePath();
            }

            @Override
            public String getTableauSiteName() {
                return null;
            }

            @Override
            public String getTableauProjectName() {
                return null;
            }

            @Override
            public String getTableauDatasourceName() {
                return null;
            }

            @Override
            public String getTableauServerUrl() {
                return null;
            }

            @Override
            public String getTableauServerUsername() {
                return null;
            }

            @Override
            public String getTableauServerPassword() {
                return null;
            }

            @Override
            public int getNumberOfThreads() {
                return 1;
            }

            @Override
            public boolean isPublish() {
                return false;
            }

            @Override
            public boolean isExtract() {
                return false;
            }

        };
    }
}