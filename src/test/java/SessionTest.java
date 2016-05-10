import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;


public class SessionTest {
	
	@Test
	public void session_createdCorrectly() {
		String ftpUserLogin = "lps_nort";
		String ftpUserPasswrd = "*9AB6B66ED84DF00A6A5FBBC30CFBCA963BF05517";
		Session session = new Session(ftpUserLogin, ftpUserPasswrd);
		assertTrue(Session.getSessionList().size() > 0);
	}
	
	@Test
	public void session_findUserByLogin() {
		String ftpUserLogin = "lps_nort";
		String ftpUserPasswrd = "*9AB6B66ED84DF00A6A5FBBC30CFBCA963BF05517";
		new Session(ftpUserLogin, ftpUserPasswrd);
		assertTrue(Session.findSessionByUserLogin(ftpUserLogin) != null);
	}
	
	@Test 
	public void sesion_zipListCorrectlyPopulated() {
		HashMap<String, String> userCredential = new HashMap<>();
		userCredential.put("login", "lps_nort");
		userCredential.put("password", "*9AB6B66ED84DF00A6A5FBBC30CFBCA963BF05517");
		List<FTPFile> fileList = ArchiveReader.loadFiles(userCredential);
		String ftpUserLogin = "lps_nort";
		String ftpUserPasswrd = "*9AB6B66ED84DF00A6A5FBBC30CFBCA963BF05517";
		Session session = new Session(ftpUserLogin, ftpUserPasswrd);
		session.setZipFiles(fileList);
		assertTrue(session.getFTPFiles().size() > 0);
	}

}

