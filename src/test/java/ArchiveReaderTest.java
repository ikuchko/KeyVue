import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;


public class ArchiveReaderTest {
	
	@Test
	public void ftp_getFilesListFromServer() {
		HashMap<String, String> userCredential = new HashMap<>();
		userCredential.put("login", "lps_nort");
		userCredential.put("password", "*9AB6B66ED84DF00A6A5FBBC30CFBCA963BF05517");
		assertTrue(ArchiveReader.loadFiles(userCredential, "/").size() > 0);
	}
	
	@Test
	public void ftp_getZipFileNotNull() {
		HashMap<String, String> userCredential = new HashMap<>();
		userCredential.put("login", "lps_nort");
		userCredential.put("password", "*9AB6B66ED84DF00A6A5FBBC30CFBCA963BF05517");
		FTPFile ftpFile = ArchiveReader.loadFiles(userCredential, "/").get(2);
		assertTrue(ArchiveReader.getZipFile(userCredential, ftpFile) != null);
	}
}