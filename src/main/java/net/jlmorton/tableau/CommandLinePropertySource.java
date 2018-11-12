package net.jlmorton.tableau;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

class CommandLinePropertySource {
    private static final Logger LOGGER = LogManager.getLogger(CommandLinePropertySource.class);

    private final String[] args;

    CommandLinePropertySource(String... args) {
        this.args = args;
    }

    Properties getProperties() {
        CommandLine commandLine = parseCommandLineOptions();

        return new Properties() {
            @Override
            public Schema getSchema() {
                String schemaPath = commandLine.getOptionValue("schema");
                return createSchemaFromJson(schemaPath);
            }

            @Override
            public File getCsvFile() {
                String csvFilePath = commandLine.getOptionValue("file");

                if (StringUtils.isBlank(csvFilePath)) {
                    return null;
                }

                return new File(csvFilePath);
            }

            @Override
            public String getExtractFilePath() {
                return commandLine.getOptionValue("extract");
            }

            @Override
            public String getTableauSiteName() {
                return commandLine.getOptionValue("site");
            }

            @Override
            public String getTableauProjectName() {
                return commandLine.getOptionValue("project");
            }

            @Override
            public String getTableauDatasourceName() {
                return commandLine.getOptionValue("datasource");
            }

            @Override
            public String getTableauServerUrl() {
                return commandLine.getOptionValue("url");
            }

            @Override
            public String getTableauServerUsername() {
                return commandLine.getOptionValue("username");
            }

            @Override
            public String getTableauServerPassword() {
                return commandLine.getOptionValue("password");
            }

            @Override
            public int getNumberOfThreads() {
                if (commandLine.hasOption("threads")) {
                    return Integer.valueOf(commandLine.getOptionValue("threads"));
                }

                return 1;
            }

            @Override
            public boolean isPublish() {
                return commandLine.hasOption("publish");
            }

            @Override
            public boolean isExtract() {
                return commandLine.hasOption("extract") && commandLine.hasOption("file");
            }

        };
    }

    private Schema createSchemaFromJson(String schemaPath) {
        try {
            return Schema.fromJson(schemaPath);
        } catch (IOException e) {
            LOGGER.error("Error creating schema with path {}", schemaPath, e);
            throw new RuntimeException(e);
        }
    }

    private CommandLine parseCommandLineOptions() {
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine commandLine = parser.parse(getOptions(), args);
            if (commandLine.hasOption("help")) {
                printHelp();
            }

            return commandLine;
        } catch (ParseException e) {
            LOGGER.error("Could not parse command line options", e);
            throw new RuntimeException("Could not parse command line options", e);
        }
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("s", "schema", true, "Schema file for extract");
        options.addOption("f", "file", true, "CSV file to import");
        options.addOption("t", "threads", true, "Number of threads (default: 1)");
        options.addOption("p", "publish", false, "Publish an extract to Tableau (requires --extract, --site, --project, --datasource, --username --password, and --url,");
        options.addOption("s", "site", true, "Tableau site name to publish");
        options.addOption("c", "project", true, "Project name to publish to");
        options.addOption("e", "extract", true, "Filename of extract");
        options.addOption("d", "datasource", true, "Name of datasource to publish");
        options.addOption("u", "url", true, "Tableau Server URL for publishing");
        options.addOption("n", "username", true, "Tableau Server username for publishing");
        options.addOption("x", "password", true, "Tableau Server password for publishing");
        options.addOption("h", "help", false, "");

        return options;
    }

    static void printHelp() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("java -jar tableau.jar", getOptions());
        System.exit(1);
    }
}
