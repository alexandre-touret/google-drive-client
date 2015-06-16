package info.touret.googledriveclient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

/**
 * Helper class for local files
 * Created by touret-a on 22/05/2015.
 */
public class LocalFileHelper {
    private final static Logger LOGGER = Logger.getLogger(LocalFileHelper.class.getName());

    /**
     * Lists recent files of the corresponding folder
     *
     * @param path    the folder to check
     * @param instant the time to check
     * @return Corresponding files
     */
    public List<File> listRecentFilesFromAFolder(Path path, Instant instant) {
        List<File> files = new ArrayList<>();
        try {
            Files.list(path)
                    .filter((p) -> Instant.ofEpochMilli(p.toFile().lastModified()).isAfter(instant))
                    .forEach((p) -> files.add(p.toFile()));
        } catch (IOException e) {
            LOGGER.log(SEVERE, e.getMessage(), e);
            throw new GoogleDriveException(e);
        }
        return files;
    }

    /**
     * Returns and creates if needed the corresponding folder
     *
     * @param rootFolder
     * @param newFolder
     * @return The Path of the folder
     */
    public Path createOrGetFolder(Path rootFolder, String newFolder) {
        Path folder = null;
        try {
            folder = Paths.get(rootFolder.toString(), newFolder);
            if (!Files.exists(folder)) {
                Files.createDirectory(folder);
            }
        } catch (IOException e) {
            LOGGER.log(SEVERE, e.getMessage(), e);
            throw new GoogleDriveException(e);
        }
        return folder;
    }

}
