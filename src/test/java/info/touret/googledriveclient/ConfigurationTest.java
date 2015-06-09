package info.touret.googledriveclient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static info.touret.googledriveclient.Configuration.ACCESS_TOKEN;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by touret-a on 09/06/2015.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.Builder.class, Files.class})
public class ConfigurationTest {
    Configuration configuration;
    private Properties properties;
    private Path mockConfFile;
    private Path mockDPath;


    @Before
    public void setUp() throws Exception {
        mockDPath = mock(Path.class);
        properties = mock(Properties.class);


        mockConfFile = mock(Path.class);
        doReturn(new String("mock")).when(mockDPath).toString();
        //    when(Paths.get(mockDPath.toString(), Configuration.GDRIVE_CONF)).thenReturn(mockConfFile);
        mockStatic(Files.class);
        when(Files.createFile(mockConfFile)).thenReturn(mockConfFile);
        final FileWriter fileWriter = mock(FileWriter.class);
        final FileReader fileReader = mock(FileReader.class);

        whenNew(FileWriter.class).withAnyArguments().thenReturn(fileWriter);
        whenNew(FileReader.class).withAnyArguments().thenReturn(fileReader);
        //doThrow(new FileNotFoundException("File Doesnt exist")).when(properties).store(fileWriter, null);

        whenNew(Properties.class).withNoArguments().thenReturn(properties);
        doNothing().when(properties).store(fileWriter, null);
    }

    @Test
    public void testGetValue_OK() throws Exception {
        when(properties.getProperty(ACCESS_TOKEN)).thenReturn("token");
        configuration = new Configuration.Builder().withDirectory(mockDPath).build();
        assertNotNull(configuration.getValue(ACCESS_TOKEN).get());
    }

    @Test
    public void testGetValue_No_Parameter_Found() throws Exception {
        configuration = new Configuration.Builder().withDirectory(mockDPath).build();
        assertFalse(configuration.getValue(ACCESS_TOKEN).isPresent());
    }

    @Test
    public void testGetValue_Load_File_With_Token() throws Exception {
        when(Files.exists(Matchers.any())).thenReturn(true);
        when(properties.getProperty(ACCESS_TOKEN)).thenReturn("token");
        configuration = new Configuration.Builder().withDirectory(mockDPath).build();
        assertTrue(configuration.getValue(ACCESS_TOKEN).isPresent());
    }

    @Test
    public void testGetValue_Load_File_Without_Token() throws Exception {
        when(Files.exists(Matchers.any())).thenReturn(true);
        configuration = new Configuration.Builder().withDirectory(mockDPath).build();
        assertFalse(configuration.getValue(ACCESS_TOKEN).isPresent());
    }

    @Test
    public void testPutValue_OK() throws Exception {
        configuration = new Configuration.Builder().withDirectory(mockDPath).build();
        final String mock = "mock";
        configuration.putValue(ACCESS_TOKEN, mock);
        when(properties.getProperty(ACCESS_TOKEN)).thenReturn(mock);
        assertEquals(mock, configuration.getValue(ACCESS_TOKEN).get());
    }

    @Test
    public void testStore_OK() throws Exception {
        configuration = new Configuration.Builder().withDirectory(mockDPath).build();
        configuration.store();
    }
}
