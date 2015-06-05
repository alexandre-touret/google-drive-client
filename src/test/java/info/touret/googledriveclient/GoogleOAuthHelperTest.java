package info.touret.googledriveclient;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by touret-a on 03/06/2015.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GoogleOAuthHelper.class})
public class GoogleOAuthHelperTest {

    private GoogleOAuthHelper googleOAuthHelper;

    @Mock
    private Path mockDir;

    private HttpTransport httpTransport;
    private JsonFactory jsonFactory;

    @Mock
    private Properties mockSecrets;

    @Before
    public void setUp() throws Exception {
        mockDir = mock(Path.class);
        doReturn("path").when(mockDir).toString();
        httpTransport = mock(HttpTransport.class);
        jsonFactory = mock(JsonFactory.class);
        mockStatic(Files.class, Paths.class);

        googleOAuthHelper = new GoogleOAuthHelper(mockDir);
    }


    public void testGetGoogleCredential_OK() throws Exception {
        final BufferedReader mock = mock(BufferedReader.class);
        when(mock.readLine()).thenReturn("MOCK");
        mockStatic(GoogleAuthorizationCodeFlow.Builder.class);
        GoogleAuthorizationCodeFlow.Builder googleAuthorizationCodeFlowBuilder = mock(GoogleAuthorizationCodeFlow.Builder.class);
        whenNew(GoogleAuthorizationCodeFlow.Builder.class).withAnyArguments().thenReturn(googleAuthorizationCodeFlowBuilder);
        whenNew(BufferedReader.class).withAnyArguments().thenReturn(mock);
        GoogleCredential googleCredential = googleOAuthHelper.getGoogleCredential(httpTransport, jsonFactory);
        assertNotNull(googleCredential);
    }

    @Test
    public void testGetAccessToken_OK() throws Exception {
        Path mockConfFile = mock(Path.class);
        //File mockFile = mock(File.class);
        final String first = mockDir.toString();
        final Properties properties = mock(Properties.class);
        when(properties.getProperty(GoogleOAuthHelper.ACCESS_TOKEN)).thenReturn("token");
        whenNew(Properties.class).withNoArguments().thenReturn(properties);
        whenNew(FileReader.class).withAnyArguments().thenReturn(mock(FileReader.class));
        doNothing().when(properties).load(Matchers.any(InputStream.class));
        when(Paths.get(first, GoogleOAuthHelper.GDRIVE_CONF)).thenReturn(mockConfFile);
//        when(mockConfFile.toFile()).thenReturn(mockFile);
        final Optional<String> accessToken = googleOAuthHelper.getAccessToken();
        assertNotNull(accessToken);
        assertTrue(accessToken.isPresent());
        assertEquals("token", accessToken.get());
    }

}
