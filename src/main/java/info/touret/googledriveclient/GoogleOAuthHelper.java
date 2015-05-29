package info.touret.googledriveclient;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.DriveScopes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by touret-a on 20/05/2015.
 */
public class GoogleOAuthHelper {
    public static final String GDRIVE_CONF = ".gdrive.conf";
    public static final String ACCESS_TOKEN = "access_token";
    public enum SECRETS {
        CLIENT_ID, CLIENT_SECRET, REDIRECT_URL;
    }


    private Properties secrets;


    private Properties getSecrets() {
        if (secrets == null) {
            secrets= new Properties();
            try {
                secrets.load(this.getClass().getResourceAsStream("/secrets.properties"));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE,e.getMessage(),e);
                throw new GoogleDriveException(e);
            }
        }
        return secrets;
    }

    private void setSecrets(Properties secrets) {
        this.secrets = secrets;
    }

    private final static Logger LOGGER = Logger.getLogger(GoogleOAuthHelper.class.getName());


    public GoogleOAuthHelper(Path directory) {
        this.directory = directory;
    }

    private Path directory;

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        this.directory = directory;
    }


    private void createConfFile() {
        try {
            Path confFile = Paths.get(getDirectory().toString(), GDRIVE_CONF);
            Files.deleteIfExists(confFile);
            Files.createFile(confFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to remove the config file ", e);
            throw new GoogleDriveException(e);
        }
    }

    public void storeCredentialInConfigFile( String accessToken) {
        try {
            createConfFile();
            Path confFile = Paths.get(getDirectory().toString(), GDRIVE_CONF);
            Properties properties = new Properties();
            properties.put(ACCESS_TOKEN, accessToken);
            properties.store(new FileWriter(confFile.toFile()), null);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to store credentials in config file ", e);
            throw new GoogleDriveException(e);
        }
    }


    public Optional<String> getAccessToken()  {
        try {
            Properties properties = new Properties();
            Path confFile = Paths.get(getDirectory().toString(), GDRIVE_CONF);
            properties.load(new FileReader(confFile.toFile()));
            return Optional.ofNullable(properties.getProperty(ACCESS_TOKEN));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to load credentials from config file ", e);
            throw new GoogleDriveException(e);
        }
    }


    public GoogleCredential getGoogleCredential(HttpTransport httpTransport, JsonFactory jsonFactory) throws IOException {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, getSecrets().getProperty(SECRETS.CLIENT_ID.name()), getSecrets().getProperty(SECRETS.CLIENT_SECRET.name()), Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();
        final String redirectUri = getSecrets().getProperty(SECRETS.REDIRECT_URL.name());
        String url = flow.newAuthorizationUrl().setRedirectUri(redirectUri).build();
        System.out.println("Please open the following URL in your browser then type the authorization code:");
        System.out.println("  " + url);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
        GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
        String accessToken = credential.getAccessToken();
        LOGGER.fine("Access Token : " + accessToken);
        return credential;
    }
}
