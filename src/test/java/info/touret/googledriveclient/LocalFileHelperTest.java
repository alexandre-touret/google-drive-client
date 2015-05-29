package info.touret.googledriveclient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by touret-a on 22/05/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalFileHelperTest {

    private LocalFileHelper localFileHelper;
    @Mock
    private Path mockRootPath;

    @Mock
    private FileSystem mockFileSystem;

    @Before
    public void setUp() throws Exception {
        when(mockRootPath.getFileSystem()).thenReturn(mockFileSystem);
        localFileHelper = new LocalFileHelper();
    }

    @Test
    public void testlistRecenFilesFromAFolder_Empty() throws Exception {
        List<File> fileList=localFileHelper.listRecentFilesFromAFolder(mockRootPath, Instant.now());
        assertTrue(fileList.isEmpty());
    }
}
