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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
    private String first;
    private Properties properties;
    private Path mockConfFile;

    @Before
    public void setUp() throws Exception {
        mockDir = mock(Path.class);
        doReturn("path").when(mockDir).toString();
        httpTransport = mock(HttpTransport.class);
        jsonFactory = mock(JsonFactory.class);
        mockStatic(Files.class, Paths.class);

        googleOAuthHelper = new GoogleOAuthHelper(mockDir);

        first = mockDir.toString();

        properties = mock(Properties.class);
        whenNew(Properties.class).withNoArguments().thenReturn(properties);
        when(properties.getProperty(GoogleOAuthHelper.ACCESS_TOKEN)).thenReturn("token");
        mockConfFile = mock(Path.class);
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
        whenNew(FileReader.class).withAnyArguments().thenReturn(mock(FileReader.class));
        doNothing().when(properties).load(Matchers.any(InputStream.class));
        when(Paths.get(first, GoogleOAuthHelper.GDRIVE_CONF)).thenReturn(mockConfFile);
//        when(mockConfFile.toFile()).thenReturn(mockFile);
        final Optional<String> accessToken = googleOAuthHelper.getAccessToken();
        assertNotNull(accessToken);
        assertTrue(accessToken.isPresent());
        assertEquals("token", accessToken.get());
    }


    @Test
    public void testGetAccessToken_File_not_exists() throws Exception {
        File mockFile = mock(File.class);

        when(mockConfFile.toFile()).thenReturn(mockFile);

        whenNew(FileReader.class).withArguments(mockFile).thenThrow(new FileNotFoundException("File Doesnt exist"));
        doNothing().when(properties).load(Matchers.any(InputStream.class));
        when(Paths.get(first, GoogleOAuthHelper.GDRIVE_CONF)).thenReturn(mockConfFile);

        try {
            final Optional<String> accessToken = googleOAuthHelper.getAccessToken();
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void teststoreCredentialInConfigFile_OK() throws Exception {
        // mock initialization
        when(Paths.get(first, GoogleOAuthHelper.GDRIVE_CONF)).thenReturn(mockConfFile);
        when(Files.createFile(mockConfFile)).thenReturn(null);
        final FileWriter fileWriter = mock(FileWriter.class);
        whenNew(FileWriter.class).withAnyArguments().thenReturn(fileWriter);
        doNothing().when(properties).store(fileWriter, null);
        // running test
        googleOAuthHelper.storeCredentialInConfigFile("token");
    }

    @Test
    public void teststoreCredentialInConfigFile_KO() throws Exception {
        // mock initialization
        when(Paths.get(first, GoogleOAuthHelper.GDRIVE_CONF)).thenReturn(mockConfFile);
        when(Files.createFile(mockConfFile)).thenThrow(new FileNotFoundException(""));
        final FileWriter fileWriter = mock(FileWriter.class);
        whenNew(FileWriter.class).withAnyArguments().thenReturn(fileWriter);
        doThrow(new FileNotFoundException("File Doesnt exist")).when(properties).store(fileWriter, null);
        doNothing().when(properties).store(fileWriter, null);
        // running test
        try {
            googleOAuthHelper.storeCredentialInConfigFile("token");
            fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
