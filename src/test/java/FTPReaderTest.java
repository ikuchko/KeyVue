import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;

public class FTPReaderTest {
	@Test
	public void ftp_getFilesListFromServer() {
		Session session = new Session("lps_chespa", "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F");
		assertTrue(FTPReader.loadFiles(session).size() > 0);
		assertTrue(FTPReader.loadFiles(session).size() > 0);
	}
	
	@Test
	public void ftp_getZipFileNotNull() {
		Session session = new Session("lps_chespa", "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F");
		FTPFile ftpFile = FTPReader.loadFiles(session).get(1);
		assertTrue(FTPReader.getZipFile(session, ftpFile) != null);
	}
}
