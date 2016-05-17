import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

import org.apache.commons.net.ftp.FTPFile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



public class Session {
	private String ftpUserLogin;
	private String ftpUserPassword;
	private LocalDateTime dateTimeCreated;
	private List<FTPFile> ftpFileList = new ArrayList<>();
	private List<FTPFile> ftpTxtFileList = new ArrayList<>();
	private static List<Session> sessionList = new ArrayList<>();
	
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
	
	public List<FTPFile> getFTPTxtFiles() {
		return ftpTxtFileList;
	}
	
	public static List<Session> getSessionList() {
		return sessionList;
	}
	
	public void setFTPFiles(List<FTPFile> fileList) {
		for (int i=0; i<fileList.size(); i++) {
			FTPFile file = fileList.get(i); 
			if (file.isFile()) {
				String[] parts = file.getName().split("[.]");
				if (parts[parts.length-1].equals("zip")) {
					this.ftpFileList.add(file);
				} else {
					this.ftpTxtFileList.add(file);
				}
			} else {
				this.ftpFileList.add(file);   //populate list with directories
			}
		}
	}
	
	public static Session findSessionByUserLogin(String ftpUserLogin) {
		for (int i=0; i<sessionList.size(); i++) {
			if (sessionList.get(i).getFTPUserLogin().equals(ftpUserLogin)) {
				return sessionList.get(i);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<String> getFilesJSON() {
		JSONArray arrayJASON = new JSONArray();
		for (int i=0; i<ftpFileList.size(); i++) {
			JSONObject objectJSON = new JSONObject();
			objectJSON.put("text", ftpFileList.get(i).getName());
			objectJSON.put("icon", "glyphicon glyphicon-folder-close");
			objectJSON.put("selectedIcon", "glyphicon glyphicon-folder-open");
			arrayJASON.add(objectJSON);
		}
		return arrayJASON;
	}
}
