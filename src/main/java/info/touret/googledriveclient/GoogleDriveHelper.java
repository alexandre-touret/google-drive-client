package info.touret.googledriveclient;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by touret-a on 22/05/2015.
 */
public class GoogleDriveHelper {
    public static final String APPLICATION_VND_GOOGLE_APPS_FOLDER = "application/vnd.google-apps.folder";
    public static final String APPLICATION_VND_GOOGLE_APPS = "application/vnd.google-apps";
    private final static Logger LOGGER = Logger.getLogger(GoogleDriveHelper.class.getName());

    /**
     * Liste les repertoires
     *
     * @param drive
     * @param root
     * @return
     * @throws IOException
     */
    public List<File> listFolders(Drive drive, String root, Configuration configuration) throws IOException {
        List<File> files = new ArrayList<>();
        ChildList childList = drive.children().list(root).setQ("mimeType = 'application/vnd.google-apps.folder' ").execute();

        for (ChildReference children : childList.getItems()) {
            final File file = drive.files().get(children.getId()).execute();
            if (!configuration.isLastSyncMoreRecentThan(file.getModifiedDate().getValue())) {
                files.add(file);
                LOGGER.fine("Folder found : " + file.getTitle());
            }
        }
        return files;
    }

    /**
     * Liste les fichiers qui ne sont pas des documents google. Attention un filtre est réalisé par rapport a la derniere date de consultation
     *
     * @param drive
     * @param root
     * @return La liste des documents
     * @throws IOException
     */
    public List<File> listRealFilesOfAFolder(Drive drive, String root, Configuration configuration) throws IOException {
        List<File> files = new ArrayList<>();
        ChildList childList = drive.children().list(root).execute();
        for (ChildReference children : childList.getItems()) {
            final File file = drive.files().get(children.getId()).execute();
            if (!file.getMimeType().startsWith(APPLICATION_VND_GOOGLE_APPS)
                    && !configuration.isLastSyncMoreRecentThan(file.getModifiedDate().getValue())) {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * Telecharge un fichier donne
     *
     * @param drive
     * @param gdriveFile
     * @param folder
     */
    public void downloadFile(Drive drive, File gdriveFile, Path folder) {
        Path newFile = Paths.get(folder.toString(), gdriveFile.getTitle());
        try (FileOutputStream outputStream = new FileOutputStream(newFile.toFile())) {
            drive.files().get(gdriveFile.getId()).executeMediaAndDownloadTo(outputStream);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GoogleDriveException(e);
        }
    }

    /**
     * @param httpTransport
     * @param jsonFactory
     * @param credential
     * @return
     */
    public Drive buildDrive(HttpTransport httpTransport, JsonFactory jsonFactory, GoogleCredential credential) {
        return new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("googledriveclient").build();
    }


    /**
     * @param drive
     * @param fileToCheck
     * @param gdriveFolder
     */
    public void uploadFile(Drive drive, java.io.File fileToCheck, String gdriveFolder) {
        File file = new File();
        file.setTitle(fileToCheck.getName());
        file.setDescription(fileToCheck.getName());
        file.setMimeType(URLConnection.guessContentTypeFromName(fileToCheck.getAbsolutePath()));
        file.setParents(Arrays.asList(new ParentReference().setId(gdriveFolder)));
        try {
            drive.files().insert(file).execute();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GoogleDriveException(e);
        }
    }

}
