package info.touret.googledriveclient;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.DriveScopes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static info.touret.googledriveclient.Configuration.ACCESS_TOKEN;

/**
 * Created by touret-a on 20/05/2015.
 */
public class GoogleOAuthHelper {

    private final static Logger LOGGER = Logger.getLogger(GoogleOAuthHelper.class.getName());
    private Properties secrets;
    private Path directory;
    private Configuration configuration;

    public GoogleOAuthHelper(Path directory) {
        this.directory = directory;
        configuration = new Configuration.Builder().withDirectory(directory).build();
    }

    private Properties getSecrets() {
        if (secrets == null) {
            secrets = new Properties();
            try {
                secrets.load(this.getClass().getResourceAsStream("/secrets.properties"));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new GoogleDriveException(e);
            }
        }
        return secrets;
    }

    private void setSecrets(Properties secrets) {
        this.secrets = secrets;
    }

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        this.directory = directory;
    }


    public void storeCredentialInConfigFile(String accessToken) {
        configuration.putValue(ACCESS_TOKEN, accessToken);
        configuration.store();
    }

    public GoogleCredential getGoogleCredential(HttpTransport httpTransport, JsonFactory jsonFactory) throws IOException {
        final GoogleAuthorizationCodeFlow.Builder builder = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, getSecrets().getProperty(SECRETS.CLIENT_ID.name()),
                getSecrets().getProperty(SECRETS.CLIENT_SECRET.name()),
                Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto");
        GoogleAuthorizationCodeFlow flow = builder.build();
        final String redirectUri = getSecrets().getProperty(SECRETS.REDIRECT_URL.name());
        String url = flow.newAuthorizationUrl().setRedirectUri(redirectUri).build();
        System.out.println("Please open the following URL in your browser then type the authorization code:");
        System.out.println("  " + url);
        System.out.println("\n\n");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
        GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
        String accessToken = credential.getAccessToken();
        LOGGER.fine("Access Token : " + accessToken);
        return credential;
    }

    public Optional<String> getAccessToken() {
        return configuration.getValue(ACCESS_TOKEN);
    }


    public enum SECRETS {
        CLIENT_ID, CLIENT_SECRET, REDIRECT_URL;
    }
}
