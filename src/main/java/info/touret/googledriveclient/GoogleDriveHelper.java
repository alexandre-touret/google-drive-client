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

/**
 * Created by touret-a on 22/05/2015.
 */
public class GoogleDriveHelper {
    public List<File> listFolders(Drive drive, String root) throws IOException {
        List<File> files = new ArrayList<>();
        ChildList childList = drive.children().list(root).setQ("mimeType = 'application/vnd.google-apps.folder' ").execute();

        for (ChildReference children : childList.getItems()) {
            final File file = drive.files().get(children.getId()).execute();
            files.add(file);
            System.out.println("Folder trouvé : " + file.getTitle());
        }
        return files;
    }

    public List<File> listFilesOfAFolder(Drive drive, String root) throws IOException {
        List<File> files = new ArrayList<>();
        ChildList childList = drive.children().list(root).execute();
        for (ChildReference children : childList.getItems()) {

            final File file = drive.files().get(children.getId()).execute();
            if (!file.getMimeType().equals("application/vnd.google-apps.folder")) {
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
            e.printStackTrace();
        }
        try (FileOutputStream outputStream = new FileOutputStream(newFile.toFile())) {
            drive.files().get(gdriveFile.getId()).executeMediaAndDownloadTo(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Drive buildDrive(HttpTransport httpTransport, JsonFactory jsonFactory, GoogleCredential credential) {
        return new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("googledriveclient").build();
    }



}
