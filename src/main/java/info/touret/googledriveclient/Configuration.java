package info.touret.googledriveclient;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by touret-a on 08/06/2015.
 */
public class Configuration {
    public static final String GDRIVE_CONF = ".gdrive.conf";
    public static final String TIMESTAMP = "timestamp";
    public static final String ACCESS_TOKEN = "access_token";
    private final static Logger LOGGER = Logger.getLogger(Configuration.class.getName());
    private Path directory;
    private Properties properties;


    private Configuration(Path directory, Properties properties) {
        this.directory = directory;
        this.properties = properties;
    }


    public void putValue(String key, String value) {
        properties.setProperty(key, value);
    }

    public Optional<String> getValue(String key) {
        return Optional.ofNullable(properties.getProperty(key));
    }

    public void store() {
        try {
            this.properties.store(new FileWriter(Paths.get(directory.toString(), Configuration.GDRIVE_CONF).toFile()), null);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to store the config file ", e);
            throw new GoogleDriveException(e);
        }
    }

    public static class Builder {
        private Path directory;

        public Builder withDirectory(Path _directory) {
            directory = _directory;
            return this;
        }

        public Configuration build() {
            return new Configuration(directory, retreiveConfiguration());
        }

        private Properties retreiveConfiguration() {
            Path confFile = Paths.get(directory.toString(), Configuration.GDRIVE_CONF);
            Properties properties = new Properties();
            try {
                if (Files.exists(confFile)) {
                    LOGGER.fine("Loading file [" + confFile.toFile() + "]");
                    properties.load(new FileReader(confFile.toFile()));
                } else {
                    Files.createFile(confFile);
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Unable to remove the config file ", e);
                throw new GoogleDriveException(e);
            }
            return properties;
        }
    }

}
