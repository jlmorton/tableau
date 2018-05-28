package net.jlmorton.tableau;

import java.io.File;

public interface Properties {
    String getSchemaPath();

    File getCsvFile();

    String getExtractFilePath();

    String getTableauSiteName();

    String getTableauProjectName();

    String getTableauDatasourceName();

    String getTableauServerUrl();

    String getTableauServerUsername();

    String getTableauServerPassword();

    int getNumberOfThreads();

    boolean isPublish();

    boolean isExtract();

    boolean isAppend();
}
