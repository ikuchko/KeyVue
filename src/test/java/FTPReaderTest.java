import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;

public class FTPReaderTest {
	@Test
	public void ftp_getFilesListFromServer() {
		Session session = new Session("lps_dane", "password");
		assertTrue(FTPReader.loadFiles(session).size() > 0);
		assertTrue(FTPReader.loadFiles(session).size() > 0);
	}
	
	@Test
	public void ftp_getZipFileNotNull() {
		Session session = new Session("lps_dane", "password");
		FTPFile ftpFile = FTPReader.loadFiles(session).get(1);
		assertTrue(FTPReader.getZipFile(session, ftpFile) != null);
	}
}
