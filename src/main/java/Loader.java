import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.net.ftp.FTPFile;

public class Loader {
	private static final String QUERY = "SELECT users.userid, users.passwd " +
			"FROM keyvue_users " +
			"JOIN users ON keyvue_users.users_id = users.id " + 
			"JOIN customers ON keyvue_users.customer_id = customers.customer_id " +
			"JOIN counties ON keyvue_users.county_id = counties.county_id " +
			"JOIN states ON counties.state_id = states.state_id ";
	
	public static Boolean updateFiles() {
		List<HashMap<String, String>> dbData = DB.requestData(QUERY, DB.DB_FTPUSER);
		Boolean result = true;
		for (int i=0; i<dbData.size(); i++) {
			String login = dbData.get(i).get("userid");
			String password = dbData.get(i).get("passwd");
			String zipDestination = FTPReader.DESTINATION_ZIP + login;
			String folderDestination = ZipArchive.DESTINATION + login;
			List<FTPFile> ftpFiles = FTPReader.loadFiles(login, password);
			File[] localZips = getLocalFiles(zipDestination);
			File[] localFolders = getLocalFiles(folderDestination);
			File[] allLocals = (File[]) ArrayUtils.addAll(localFolders, localZips);
			if (!(removeLocalFiles(allLocals, ftpFiles)) || !(loadFTPFiles(localZips, ftpFiles, login, password)) ) {
				result = false;
			};
		}
		return result;
	}

	private static boolean loadFTPFiles(File[] localZips, List<FTPFile> ftpFiles, String login, String password) {
		for (int ftpI=0; ftpI<ftpFiles.size(); ftpI++) {
			Boolean match = false;
			for (int localI=0; localI<localZips.length; localI++) {
				if (localZips[localI].getName().equals(ftpFiles.get(ftpI).getName())) {
					match = true;
				}
			}
			if (!match) {
				FTPReader.getZipFile(login, password, ftpFiles.get(ftpI).getName());
			}
		}
		return true;
	}

	private static Boolean removeLocalFiles(File[] allLocals, List<FTPFile> ftpFiles) {
		for (int localI=0; localI<allLocals.length; localI++) {
			Boolean match = false;
			for (int ftpI=0; ftpI<ftpFiles.size(); ftpI++) {
				if (allLocals[localI].getName().equals(ftpFiles.get(ftpI).getName())) {
					match = true;
				}
			}
			if (!match) {
				try {
					if (allLocals[localI].isDirectory()) {
						FileUtils.deleteDirectory(allLocals[localI]);
					} else {
						Files.delete(allLocals[localI].toPath());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	private static File[] getLocalFiles(String destination) {
		File folder = new File(destination);
		return folder.listFiles();
	}
}
