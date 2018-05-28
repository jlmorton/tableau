package net.jlmorton.tableau;

import com.tableausoftware.TableauException;
import com.tableausoftware.server.ServerAPI;
import com.tableausoftware.server.ServerConnection;

class Publisher {
    static void publish(Properties properties) throws TableauException {
        ServerAPI.initialize();
        ServerConnection serverConnection = new ServerConnection();

        String tableauServerUrl = properties.getTableauServerUrl();
        String tableauServerUsername = properties.getTableauServerUsername();
        String tableauServerPassword = properties.getTableauServerPassword();
        String tableauSiteName = properties.getTableauSiteName();

        String extractPath = properties.getExtractFilePath();
        String projectName = properties.getTableauProjectName();
        String datasourceName = properties.getTableauDatasourceName();

        serverConnection.connect(tableauServerUrl, tableauServerUsername, tableauServerPassword, tableauSiteName);
        serverConnection.publishExtract(extractPath, projectName, datasourceName, true);
    }
}
