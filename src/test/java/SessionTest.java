import static org.junit.Assert.*;

import java.io.File;
import org.junit.Test;


public class SessionTest {
	
	@Test
	public void session_createdCorrectly() {
		String ftpUserLogin = "lps_dane";
		String ftpUserPasswrd = "password";
		new Session(ftpUserLogin, ftpUserPasswrd);
		assertTrue(Session.getSessionList().size() > 0);
	}
	
	@Test
	public void session_findUserByLogin() {
		String ftpUserLogin = "lps_dane";
		String ftpUserPasswrd = "password";
		new Session(ftpUserLogin, ftpUserPasswrd);
		assertTrue(Session.findSessionByUserLogin(ftpUserLogin) != null);
	}
	
	@Test 
	public void sesion_zipListCorrectlyPopulated() {
		Session session = new Session("lps_dane", "password");
//		List<FTPFile> fileList = FTPReader.loadFiles(session);
		assertTrue(session.readLocalFiles().size() > 0);
//		assertTrue(session.getFTPTxtFiles().size() > 0);
	}
	
	@Test 
	public void sesion_getFileByName() {
		Session session = new Session("lps_dane", "password");
		File file = session.readLocalFiles().get(2);
		assertEquals(file, session.getFileByName(file.getName()));
	}

}

