package info.touret.googledriveclient;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client google drive
 * Created by touret-a on 22/05/2015.
 */
public class GoogleDriveClient {
    public static final String ROOT_FOLDER = "root";
    private final static Logger LOGGER = Logger.getLogger(GoogleDriveClient.class.getName());

    /**
     * Synchronise un repertoire de maniere recursive
     *
     * @param drive
     * @param folder
     */
    public void synchronize(Drive drive, Path folder) {
        synchronizeGoogleDriveFolder(drive, folder, ROOT_FOLDER, new GoogleDriveHelper(), new LocalFileHelper());
    }

    /**
     * Indique si un fichier et plus recent dans google drive
     *
     * @param gdriveFile
     * @param localFile
     * @return
     */
    private Boolean isNewOrMoreRecentInGoogleDrive(File gdriveFile, java.io.File localFile) {
        return !localFile.exists() || localFile.lastModified() < gdriveFile.getModifiedDate().getValue();
    }


    /**
     * @param drive
     * @param localFolder
     * @param gdriveFolder
     * @param googleDriveHelper
     * @param localFileHelper
     */
    private void synchronizeGoogleDriveFolder(Drive drive, Path localFolder, String gdriveFolder, GoogleDriveHelper googleDriveHelper, LocalFileHelper localFileHelper) {

        try {
            /* On gere les fichiers contenus dans le repertoire */
            List<File> files = googleDriveHelper.listRealFilesOfAFolder(drive, gdriveFolder);
            for (File file : files) {
                LOGGER.fine("Checking [" + file.getTitle() + "] ...");
                Path fileToCheck = Paths.get(localFolder.toString(), file.getTitle().concat(".").concat(file.getFileExtension()));
                if (isNewOrMoreRecentInGoogleDrive(file, fileToCheck.toFile())) {
                    googleDriveHelper.downloadFile(drive, file, localFolder);
                    LOGGER.fine("Downloaded file : [" + fileToCheck.toString() + " ]");
                }
            }
        } catch (InvalidPathException e1) {
            LOGGER.log(Level.WARNING, e1.getMessage());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GoogleDriveException(e);
        }


        try {
          /* On gere les repertoires */
            List<File> folders = googleDriveHelper.listFolders(drive, gdriveFolder);
            for (File currentFolder : folders) {
                LOGGER.info(currentFolder.getId());
                Path newFolder = localFileHelper.createOrGetFolder(localFolder, currentFolder.getTitle());
                synchronizeGoogleDriveFolder(drive, newFolder, currentFolder.getId(), googleDriveHelper, localFileHelper);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GoogleDriveException(e);
        }
    }

}
