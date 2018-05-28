package net.jlmorton.tableau;

import java.io.File;
import java.io.IOException;
import com.tableausoftware.extract.Extract;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExtractWriterImplTest {
    @Test
    public void testCreateExtract() throws Exception {
        String sampleSchemaFileName = getClass().getResource("sample-schema.json").getFile();
        Schema sampleSchema = Schema.fromJson(sampleSchemaFileName);

        RowInputSource rowInputSource = new CsvInputSource(getSampleCsvFile());
        ExtractWriter extractWriter = new ExtractWriterImpl(sampleSchema, rowInputSource, getProperties());
        Extract extract = extractWriter.createExtract();
        assertNotNull(extract);
        assertTrue(extract.hasTable("Extract"));
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

    private Properties getProperties() {
        return new Properties() {
            @Override
            public String getSchemaPath() {
                return getClass().getResource("sample-schema.json").getFile();
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

            @Override
            public boolean isAppend() {
                return false;
            }
        };
    }
}