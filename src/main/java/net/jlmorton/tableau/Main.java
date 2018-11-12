package net.jlmorton.tableau;

import com.tableausoftware.TableauException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String... args) throws Exception {
        CommandLinePropertySource commandLinePropertySource = new CommandLinePropertySource(args);
        Properties properties = commandLinePropertySource.getProperties();

        if (properties.isPublish()) {
            publish(properties);
        }

        if (properties.isExtract()) {
            createExtract(properties);
        }

        if (!properties.isExtract() & !properties.isPublish()) {
            CommandLinePropertySource.printHelp();
        }
    }

    private static void publish(Properties properties) throws TableauException {
        validatePropertiesForPublishing(properties);

        LOGGER.info("Publishing Extract {}", properties.getExtractFilePath());
        LOGGER.info("Tableau Server: {}", properties.getTableauServerUrl());
        LOGGER.info("Tableau Username: {}", properties.getTableauServerUsername());
        LOGGER.info("Tableau Password: XXXXXXXX");
        LOGGER.info("Tableau Site Name: {}", properties.getTableauSiteName());
        LOGGER.info("Tableau Project Name: {}", properties.getTableauProjectName());
        LOGGER.info("Tableau Datasource Name: {}", properties.getTableauDatasourceName());

        Publisher publisher = new TableauSdkPublisher(properties);
        publisher.publish();
    }

    private static void createExtract(Properties properties) throws TableauException {
        validatePropertiesForExtract(properties);

        LOGGER.info("Creating Extract {}", properties.getSchema().getName());
        LOGGER.info("CSV File Path: {}", properties.getCsvFile());
        LOGGER.info("Extract Path: {}", properties.getExtractFilePath());
        LOGGER.info("Number of Threads: {}", properties.getNumberOfThreads());

        RowInputSource rowInputSource = new CsvInputSource(properties.getCsvFile());
        ExtractAdapter extractAdapter = new TdeExtractAdapter(properties);

        ExtractWriter extractWriter = new MultiThreadedExtractWriter(properties, rowInputSource, extractAdapter);
        extractWriter.writeExtract();
        extractWriter.closeExtract();
    }

    private static void validatePropertiesForPublishing(Properties properties) {
        boolean hasServerUrl = StringUtils.isNotBlank(properties.getTableauServerUrl());
        boolean hasUsername = StringUtils.isNotBlank(properties.getTableauServerUsername());
        boolean hasPassword = StringUtils.isNotBlank(properties.getTableauServerPassword());
        boolean hasSiteName = StringUtils.isNotBlank(properties.getTableauSiteName());
        boolean hasProjectName = StringUtils.isNotBlank(properties.getTableauProjectName());
        boolean hasDatasourceName = StringUtils.isNotBlank(properties.getTableauDatasourceName());
        boolean hasExtractPath = !Objects.isNull(properties.getExtractFilePath());

        if (!(hasServerUrl && hasUsername && hasPassword && hasSiteName && hasProjectName && hasExtractPath && hasDatasourceName)) {
            LOGGER.error("Must provide Tableau Server URL, username, password, site name, project name, datasource name, and extract file path when publishing");
            CommandLinePropertySource.printHelp();
        }

        if (!new File(properties.getExtractFilePath()).exists()) {
            LOGGER.error("Extract path {} does not exist or is not readable", properties.getExtractFilePath());
            CommandLinePropertySource.printHelp();
        }
    }

    private static void validatePropertiesForExtract(Properties properties) {
        boolean hasExtractPath = !Objects.isNull(properties.getExtractFilePath());
        boolean hasCsvFilePath = !Objects.isNull(properties.getCsvFile());
        boolean hasSchemaPath = !Objects.isNull(properties.getSchema());

        if (!(hasExtractPath && hasCsvFilePath && hasSchemaPath)) {
            LOGGER.error("Must provide extract path, CSV file path, and Schema path when creating an extract");
            CommandLinePropertySource.printHelp();
        }

        if (!properties.getCsvFile().exists()) {
            LOGGER.error("CSV file {} does not exist or is not readable", properties.getCsvFile().getAbsolutePath());
            CommandLinePropertySource.printHelp();
        }
    }
}
