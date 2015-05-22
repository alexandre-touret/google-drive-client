package info.touret.googledriveclient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by touret-a on 22/05/2015.
 */
public class LocalFileHelper {

    public List<File> listRecentFilesFromAFolder(Path path, Instant instant) {
        List<File> files = new ArrayList<>();
        try {
            Files.list(path)
                    .filter((p) -> Instant.ofEpochMilli(p.toFile().lastModified()).isAfter(instant))
                    .forEach((p) -> files.add(p.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }



}
