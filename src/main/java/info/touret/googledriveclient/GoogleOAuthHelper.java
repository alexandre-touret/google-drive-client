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
import java.util.Arrays;

/**
 * Created by touret-a on 20/05/2015.
 */
public class GoogleOAuthHelper {
    private static String CLIENT_ID = "972023561074-0iabmkv7dk0hign9tqc5o3tss2uqr3rh.apps.googleusercontent.com";
    private static String CLIENT_SECRET = "GznNd84qlr6lOfxNT7ewBfkv";
    private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";


    public static GoogleCredential getGoogleCredential(HttpTransport httpTransport, JsonFactory jsonFactory) throws IOException {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();

        String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
        System.out.println("Please open the following URL in your browser then type the authorization code:");
        System.out.println("  " + url);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();
        //String code ="4/spRdhR_rVLtZ_5wlJIGJuEwtIDM3OCQaCYKtZJZgFVE.8kT8mwW9qw0coiIBeO6P2m9-0oHhmgI";
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();


        GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
        String accessToken =credential.getAccessToken();
        System.out.println("Access Token : "+accessToken);
        return credential;
    }
}
