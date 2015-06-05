package info.touret.googledriveclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by touret-a on 03/06/2015.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GoogleDriveClient.class})
public class GoogleDriveClientTest {

    private GoogleDriveClient googleDriveClient;

    @Test
    public void testSynchronize_OK() throws Exception {
    }
}
