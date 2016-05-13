import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;


public class ArchiveReaderTest {
	
	@Test
	public void zip_extracteFilesReturnTrue() {
		Session session = new Session("lps_nort", "empty");
		assertTrue(ArchiveReader.extractFiles("NHA-20160425.zip", session));
	}
}