import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;

public class FTPReaderTest {
	@Test
	public void ftp_getFilesListFromServer() {
		HashMap<String, String> userCredential = new HashMap<>();
		userCredential.put("login", "lps_nort");
		userCredential.put("password", "*9AB6B66ED84DF00A6A5FBBC30CFBCA963BF05517");
		assertTrue(FTPReader.loadFiles(userCredential).size() > 0);
		assertTrue(FTPReader.loadFiles(userCredential).size() > 0);
	}
	
	@Test
	public void ftp_getZipFileNotNull() {
		HashMap<String, String> userCredential = new HashMap<>();
//		userCredential.put("login", "lps_cach");
//		userCredential.put("password", "*104225F624BE7AFB3D736E46DC84ED3528739B71");
		userCredential.put("login", "lps_nort");
		userCredential.put("password", "*9AB6B66ED84DF00A6A5FBBC30CFBCA963BF05517");
		FTPFile ftpFile = FTPReader.loadFiles(userCredential).get(1);
		assertTrue(FTPReader.getZipFile(userCredential, ftpFile) != null);
	}
}
