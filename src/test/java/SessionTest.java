import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;


public class SessionTest {
	
	@Test
	public void session_createdCorrectly() {
		String ftpUserLogin = "lps_dane";
		String ftpUserPasswrd = "password";
		Session session = new Session(ftpUserLogin, ftpUserPasswrd);
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
		List<FTPFile> fileList = FTPReader.loadFiles(session);
		session.setFTPFiles(fileList);
		assertTrue(session.getFTPFiles().size() > 0);
		assertTrue(session.getFTPTxtFiles().size() > 0);
	}
	
	@Test 
	public void sesion_getFTPFileByName() {
		Session session = new Session("lps_dane", "password");
		List<FTPFile> fileList = FTPReader.loadFiles(session);
		session.setFTPFiles(fileList);
		FTPFile ftpFile = session.getFTPFiles().get(2);
		assertEquals(ftpFile, session.getFTPFileByName(ftpFile.getName()));
	}

}

