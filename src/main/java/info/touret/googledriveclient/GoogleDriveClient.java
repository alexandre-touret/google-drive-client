package info.touret.googledriveclient;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by touret-a on 22/05/2015.
 */
public class GoogleDriveClient {
    private final static Logger LOGGER = Logger.getLogger(GoogleDriveClient.class.getName());

    public void synchronize(Drive drive, Path folder) {
        GoogleDriveHelper googleDriveHelper = new GoogleDriveHelper();
        LocalFileHelper localFileHelper = new LocalFileHelper();

        try {
            List<File> files = googleDriveHelper.listRealFilesOfAFolder(drive, "root");
            for (File file : files) {
                LOGGER.fine("Checking " + file.getTitle());
                Path fileToCheck = Paths.get(folder.toString(), file.getTitle().concat(".").concat(file.getFileExtension()));
                if (isNewOrMoreRecentInGoogleDrive(file, fileToCheck.toFile())) {
                    googleDriveHelper.downloadFile(drive, file, folder);
                    LOGGER.fine("Fichier téléchargé : " + fileToCheck.toString());
                }
            }

        } catch (IOException e) {

            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private Boolean isNewOrMoreRecentInGoogleDrive(File gdriveFile, java.io.File localFile) {
        return !localFile.exists() || localFile.lastModified() < gdriveFile.getModifiedDate().getValue();
    }
}
