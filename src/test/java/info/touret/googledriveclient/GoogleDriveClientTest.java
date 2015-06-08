package info.touret.googledriveclient;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by touret-a on 03/06/2015.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GoogleDriveClient.class, ChildList.class, ChildReference.class, File.class})
public class GoogleDriveClientTest {

    private GoogleDriveClient googleDriveClient;
    private Drive drive;
    private Path dir;
    private GoogleDriveHelper googleDriveHelper;


    @Before
    public void setUp() throws Exception {
        drive = mock(Drive.class);
        dir = mock(Path.class);
        Drive.Children children = mock(Drive.Children.class);
        final Drive.Files mock1 = mock(Drive.Files.class);
        when(drive.files()).thenReturn(mock1);
        Drive.Files.Get get = mock(Drive.Files.Get.class);
        when(mock1.get(anyString())).thenReturn(get);
        final File file = mock(File.class);
        when(get.execute()).thenReturn(file);
        when(file.getMimeType()).thenReturn("application/pdf");
        ChildList list = mock(ChildList.class);
        ChildReference childReference1 = mock(ChildReference.class);
        ChildReference childReference2 = mock(ChildReference.class);

        List<ChildReference> childReferences = new ArrayList<>();
        childReferences.add(childReference1);
        childReferences.add(childReference2);
        googleDriveClient = new GoogleDriveClient();
        when(drive.children()).thenReturn(children);
        final Drive.Children.List mock = mock(Drive.Children.List.class);
        when(children.list(anyString())).thenReturn(mock);
        final Drive.Children.List childList = children.list(anyString());
        when(childList.execute()).thenReturn(list);
        when(list.getItems()).thenReturn(childReferences);
        mockStatic(Paths.class);
        final Path path = mock(Path.class);
        when(path.toFile()).thenReturn(mock(java.io.File.class));
        when(Paths.get(anyString(), anyString())).thenReturn(path);
        googleDriveHelper = mock(GoogleDriveHelper.class);
        whenNew(GoogleDriveHelper.class).withNoArguments().thenReturn(googleDriveHelper);
        doNothing().when(googleDriveHelper).downloadFile(drive, file, path);


    }

    @Test
    public void testSynchronize_OK() throws Exception {
        googleDriveClient.synchronize(drive, dir);
    }

    @Test
    public void testSynchronize_Incorrect_Google_Drive_Client() throws Exception {
        when(googleDriveHelper.listFolders(drive, dir.toString())).thenThrow(new IOException());
        googleDriveClient.synchronize(drive, dir);
    }


}
