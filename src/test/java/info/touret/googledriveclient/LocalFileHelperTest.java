package info.touret.googledriveclient;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by touret-a on 22/05/2015.
 */
public class LocalFileHelperTest {

    private LocalFileHelper localFileHelper;
    private Path rootPath;

    @Before
    public void setUp() throws Exception {
        rootPath = Paths.get("d:/Tmp");
        localFileHelper = new LocalFileHelper();
    }

    @Test
    public void testlistRecenFilesFromAFolder_Empty() throws Exception {
        List<File> fileList=localFileHelper.listRecentFilesFromAFolder(rootPath, Instant.now());
        assertTrue(fileList.isEmpty());
                
    }
}
