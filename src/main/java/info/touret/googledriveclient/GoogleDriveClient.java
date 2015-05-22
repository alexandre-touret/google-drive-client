package info.touret.googledriveclient;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by touret-a on 22/05/2015.
 */
public class GoogleDriveClient {
    public void synchronize(Drive drive,Path folder){
        GoogleDriveHelper googleDriveHelper = new GoogleDriveHelper();
        LocalFileHelper localFileHelper = new LocalFileHelper();

        try {
            List<File> files = googleDriveHelper.listRealFilesOfAFolder(drive, "root");
            for(File file : files){

                System.out.println("Checking "+file.getTitle());
                Path fileToCheck = Paths.get(folder.toString(),file.getTitle().concat(".").concat(file.getFileExtension()));
                if( isNewOrMoreRecentInGoogleDrive(file,fileToCheck.toFile())){
                    googleDriveHelper.downloadFile(drive,file,folder);
                    System.out.println("Fichier téléchargé : "+fileToCheck.toString());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean isNewOrMoreRecentInGoogleDrive(File gdriveFile, java.io.File localFile) {
        return !localFile.exists() || localFile.lastModified()<gdriveFile.getModifiedDate().getValue();
    }
}
