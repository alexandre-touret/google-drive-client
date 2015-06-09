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
        Configuration configuration = new Configuration.Builder().withDirectory(folder).build();
        synchronizeGoogleDriveFolder(drive, folder, ROOT_FOLDER, new GoogleDriveHelper(), new LocalFileHelper(), configuration);
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

    private Boolean isNewLocally(java.io.File localFile, Configuration configuration) {
        Boolean isNewOrMoreRecent = false;
        if (configuration.getValue(Configuration.TIMESTAMP).isPresent()) {
            isNewOrMoreRecent = localFile.lastModified() > Long.parseLong(configuration.getValue(Configuration.TIMESTAMP).get());
        }
        return isNewOrMoreRecent;

    }


    /**
     * @param drive
     * @param localFolder
     * @param gdriveFolder
     * @param googleDriveHelper
     * @param localFileHelper
     */
    private void synchronizeGoogleDriveFolder(Drive drive, Path localFolder, String gdriveFolder, GoogleDriveHelper googleDriveHelper, LocalFileHelper localFileHelper, Configuration configuration) {
        try {
        /* On gere les fichiers contenus dans le repertoire */
            List<File> files = googleDriveHelper.listRealFilesOfAFolder(drive, gdriveFolder);
            for (File file : files) {
                LOGGER.fine("Checking [" + file.getTitle() + "] in [" + localFolder.toString() + "] with extension [" + file.getFileExtension() + "]");
                Path fileToCheck = Paths.get(localFolder.toString(), file.getTitle());
                if (isNewOrMoreRecentInGoogleDrive(file, fileToCheck.toFile())) {
                    googleDriveHelper.downloadFile(drive, file, localFolder);
                    LOGGER.fine("Downloaded file : [" + fileToCheck.toString() + " ]");
                } else {
                    // Suppression fichiers locaux


                    // nouveau fichier local
                    if (isNewLocally(fileToCheck.toFile(), configuration)) {
                        googleDriveHelper.uploadFile(drive, fileToCheck.toFile(), gdriveFolder);
                    }
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
                synchronizeGoogleDriveFolder(drive, newFolder, currentFolder.getId(), googleDriveHelper, localFileHelper, configuration);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GoogleDriveException(e);
        }
    }

}
