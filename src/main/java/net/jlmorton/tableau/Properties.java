package net.jlmorton.tableau;

import java.io.File;

public interface Properties {
    Schema getSchema();

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
}
