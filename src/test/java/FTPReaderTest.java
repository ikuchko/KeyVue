import static org.junit.Assert.*;
import org.junit.Test;

public class FTPReaderTest {
	
	@Test 
	public void ftp_readPropertyFromPropertyFile() {
		assertEquals(FTPReader.readProperty("FTP_SERVER_ADDRESS"), "ftpdas2.com");
	}
	
	@Test
	public void ftp_getFilesListFromServer() {
		Session session = new Session("lps_dane", "password");
		assertTrue(FTPReader.loadFiles(session).size() > 0);
	}
	
	@Test
	public void ftp_getZipFileNotNull() {
		Session session = new Session("lps_dane", "password");
		String ftpFile = FTPReader.loadFiles(session).get(1).getName();
		assertTrue(FTPReader.getZipFile(session, ftpFile) != null);
	}
}
