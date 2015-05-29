package info.touret.googledriveclient;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by touret-a on 22/05/2015.
 */
public class GoogleDriveHelper {


    private final static Logger LOGGER = Logger.getLogger(GoogleDriveHelper.class.getName());
    public static final String APPLICATION_VND_GOOGLE_APPS_FOLDER = "application/vnd.google-apps.folder";
    public static final String APPLICATION_VND_GOOGLE_APPS = "application/vnd.google-apps";

    public List<File> listFolders(Drive drive, String root) throws IOException {
        List<File> files = new ArrayList<>();
        ChildList childList = drive.children().list(root).setQ("mimeType = 'application/vnd.google-apps.folder' ").execute();

        for (ChildReference children : childList.getItems()) {
            final File file = drive.files().get(children.getId()).execute();
            files.add(file);
            LOGGER.fine("Folder founded : " + file.getTitle());
        }
        return files;
    }

    public List<File> listRealFilesOfAFolder(Drive drive, String root) throws IOException {
        List<File> files = new ArrayList<>();
        ChildList childList = drive.children().list(root).execute();
        for (ChildReference children : childList.getItems()) {
            final File file = drive.files().get(children.getId()).execute();
            if (!file.getMimeType().startsWith(APPLICATION_VND_GOOGLE_APPS)) {
                files.add(file);
            }
        }
        return files;
    }

    public void downloadFile(Drive drive, File gdriveFile, Path folder) {

        Path newFile = Paths.get(folder.toString(), gdriveFile.getTitle().concat(".").concat(gdriveFile.getFileExtension()));
        try {
            newFile = Files.createFile(newFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GoogleDriveException(e);
        }
        try (FileOutputStream outputStream = new FileOutputStream(newFile.toFile())) {
            drive.files().get(gdriveFile.getId()).executeMediaAndDownloadTo(outputStream);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GoogleDriveException(e);
        }
    }

    public Drive buildDrive(HttpTransport httpTransport, JsonFactory jsonFactory, GoogleCredential credential) {
        return new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("googledriveclient").build();
    }
}
