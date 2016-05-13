import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;

public class FTPReaderTest {
	@Test
	public void ftp_getFilesListFromServer() {
		HashMap<String, String> userCredential = new HashMap<>();
		userCredential.put("login", "lps_chespa");
		userCredential.put("password", "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F");
		assertTrue(FTPReader.loadFiles(userCredential).size() > 0);
		assertTrue(FTPReader.loadFiles(userCredential).size() > 0);
	}
	
	@Test
	public void ftp_getZipFileNotNull() {
		HashMap<String, String> userCredential = new HashMap<>();
//		userCredential.put("login", "lps_cach");
//		userCredential.put("password", "*104225F624BE7AFB3D736E46DC84ED3528739B71");
		userCredential.put("login", "lps_chespa");
		userCredential.put("password", "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F");
		FTPFile ftpFile = FTPReader.loadFiles(userCredential).get(1);
		assertTrue(FTPReader.getZipFile(userCredential, ftpFile) != null);
	}
}
