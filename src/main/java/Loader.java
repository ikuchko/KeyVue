import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.net.ftp.FTPFile;

public class Loader implements Runnable {
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
			Integer removedFiles = removeLocalFiles(allLocals, ftpFiles);
			Integer loadedFiles = loadFTPFiles(localZips, ftpFiles, login, password);
			if ((removedFiles < 0) || (loadedFiles < 0) ) {
				result = false;
			};
			System.out.println("--- " + login + " (removed: " + removedFiles + ", loaded: " + loadedFiles + ")");
		}
		return result;
	}

	private static Integer loadFTPFiles(File[] localZips, List<FTPFile> ftpFiles, String login, String password) {
		Integer count = 0;
		for (int ftpI=0; ftpI<ftpFiles.size(); ftpI++) {
			Boolean match = false;
			for (int localI=0; localI<localZips.length; localI++) {
				if (localZips[localI].getName().equals(ftpFiles.get(ftpI).getName())) {
					match = true;
				}
			}
			if (!match) {
				FTPReader.getZipFile(login, password, ftpFiles.get(ftpI).getName());
				count++;
			}
		}
		return count;
	}

	private static Integer removeLocalFiles(File[] allLocals, List<FTPFile> ftpFiles) {
		int count = 0;
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
					count++;
				} catch (IOException e) {
					e.printStackTrace();
//					return -1;
				}
			}
		}
		return count;
	}

	private static File[] getLocalFiles(String destination) {
		File folder = new File(destination);
		return folder.listFiles();
	}

	@Override
	public void run() {
		//Infinite loop with sleep method
		Boolean alwaysAlive = true;
		try {
			while (alwaysAlive) {
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
				System.out.println("Started synchronization process at: " + sdf.format(cal.getTime()));
				if (updateFiles()) {
			        System.out.println("Ended synchronization process at: " + sdf.format(cal.getTime()) );
				};
				Thread.sleep(1000 * 60 * 60);
			}
		} catch (InterruptedException e) {
			System.out.println("Something interupt loader thread.");
		}
		
	}
}
