import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

import org.apache.commons.net.ftp.FTPFile;

public class Session {
	private String ftpUserLogin;
	private String ftpUserPassword;
	private LocalDateTime dateTimeCreated;
	private List<FTPFile> ftpFileList = new ArrayList<>();
	private static ArrayList<Session> sessionList = new ArrayList<>();
	
	public Session (String user, String passwrd) {
		this.ftpUserLogin = user;
		this.ftpUserPassword = passwrd;
		this.dateTimeCreated = LocalDateTime.now();
		sessionList.add(this);
	}
	
	public String getFTPUserLogin() {
		return ftpUserLogin;
	}
	
	public String getFTPUserPassword() {
		return ftpUserPassword;
	}
	
	public List<FTPFile> getFTPFiles() {
		return ftpFileList;
	}
	
	public static ArrayList<Session> getSessionList() {
		return sessionList;
	}
	
	public void setZipFiles(List<FTPFile> fileList) {
		this.ftpFileList = fileList;
	}
	
	public static Session findSessionByUserLogin(String ftpUserLogin) {
		for (int i=0; i<sessionList.size(); i++) {
			if (sessionList.get(i).getFTPUserLogin().equals(ftpUserLogin)) {
				return sessionList.get(i);
			}
		}
		return null;
	}
}
