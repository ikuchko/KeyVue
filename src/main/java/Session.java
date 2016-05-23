import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
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
		String query = String.format("SELECT * FROM users WHERE userid LIKE '%s' AND keyvue_passwd LIKE '%s'", user, passwrd);
		this.ftpUserPassword = DB.requestData(query, DB.DB_FTPUSER, "passwd");
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
			objectJSON.put("type", "dir");
//			objectJSON.put("selectedIcon", "glyphicon glyphicon-folder-open");
//			JSONObject stateJSON = new JSONObject();
//			stateJSON.put("expanded", "false");
//			objectJSON.put("state", stateJSON);
			if (isFolderExtracted(ftpFileList.get(i))) {
				objectJSON.put("nodes", getFolderContentJSON(ftpFileList.get(i).getName()));
			}
			arrayJASON.add(objectJSON);
			
		}
		return arrayJASON;
	}
	
	private Boolean isFolderExtracted(FTPFile ftpFile) {
		File file = new File(ZipArchive.DESTINATION + ftpUserLogin + "/" + ftpFile.getName());
		return file.exists();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getFolderContentJSON(String folderName) {
		JSONArray arrayJSON = new JSONArray();
		List<String> txtFiles = ZipArchive.getFiles("txt", folderName, this);
		List<List<String>> tiffFiles = ZipArchive.assambleTiffFiles(ZipArchive.getFiles("tif", folderName, this));
		for (int i=0; i<txtFiles.size(); i++) {
			JSONObject objectJSON = new JSONObject();
			objectJSON.put("text", txtFiles.get(i));
			objectJSON.put("icon", "glyphicon glyphicon-download");
			objectJSON.put("type", "txt");
			arrayJSON.add(objectJSON);
		}
		for (int i=0; i<tiffFiles.size(); i++) {
			JSONObject objectJSON = new JSONObject();
			objectJSON.put("text", FilenameUtils.getBaseName(tiffFiles.get(i).get(0)));
			objectJSON.put("icon", "glyphicon glyphicon-picture");
			objectJSON.put("type", "tif");
			JSONArray filesJSONArray = new JSONArray();
			for (int index=0; index<tiffFiles.get(i).size(); index++) {
				JSONObject imageJSON = new JSONObject();
				imageJSON.put("imageName", tiffFiles.get(i).get(index));
				filesJSONArray.add(imageJSON);
			}
			objectJSON.put("files", filesJSONArray);
			arrayJSON.add(objectJSON);
		}
		return arrayJSON;
	}

	public FTPFile getFTPFileByName(String fileName) {
		for (int i=0; i<ftpFileList.size(); i++) {
			if (ftpFileList.get(i).getName().equals(fileName)) {
				return ftpFileList.get(i);
			}
		}
		return null;
	}
}
