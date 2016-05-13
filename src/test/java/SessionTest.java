import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;


public class SessionTest {
	
	@Test
	public void session_createdCorrectly() {
		String ftpUserLogin = "lps_chespa";
		String ftpUserPasswrd = "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F";
		Session session = new Session(ftpUserLogin, ftpUserPasswrd);
		assertTrue(Session.getSessionList().size() > 0);
	}
	
	@Test
	public void session_findUserByLogin() {
		String ftpUserLogin = "lps_chespa";
		String ftpUserPasswrd = "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F";
		new Session(ftpUserLogin, ftpUserPasswrd);
		assertTrue(Session.findSessionByUserLogin(ftpUserLogin) != null);
	}
	
	@Test 
	public void sesion_zipListCorrectlyPopulated() {
		HashMap<String, String> userCredential = new HashMap<>();
		userCredential.put("login", "lps_chespa");
		userCredential.put("password", "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F");
		List<FTPFile> fileList = FTPReader.loadFiles(userCredential);
		String ftpUserLogin = "lps_chespa";
		String ftpUserPasswrd = "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F";
		Session session = new Session(ftpUserLogin, ftpUserPasswrd);
		session.setFTPFiles(fileList);
		assertTrue(session.getFTPFiles().size() > 0);
		assertTrue(session.getFTPTxtFiles().size() > 0);
	}

}

