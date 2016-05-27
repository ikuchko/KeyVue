import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
			List<File> localZips = getLocalFiles(zipDestination);
			List<File> localFolders = getLocalFiles(folderDestination);
			List<File> allLocals = new ArrayList<File>(localZips);
			allLocals.addAll(localFolders);
			Integer removedFiles = removeLocalFiles(allLocals, ftpFiles);
			Integer loadedFiles = loadFTPFiles(localZips, ftpFiles, login, password);
			if ((removedFiles < 0) || (loadedFiles < 0) ) {
				result = false;
			};
			System.out.println("--- " + login + " (removed: " + removedFiles + ", loaded: " + loadedFiles + ")");
		}
		return result;
	}

	private static Integer loadFTPFiles(List<File> localZips, List<FTPFile> ftpFiles, String login, String password) {
		Integer count = 0;
		for (int ftpI=0; ftpI<ftpFiles.size(); ftpI++) {
			Boolean match = false;
			for (int localI=0; localI<localZips.size(); localI++) {
				if (localZips.get(localI).getName().equals(ftpFiles.get(ftpI).getName())) {
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

	private static Integer removeLocalFiles(List<File> allLocals, List<FTPFile> ftpFiles) {
		int count = 0;
		for (int localI=0; localI<allLocals.size(); localI++) {
			Boolean match = false;
			for (int ftpI=0; ftpI<ftpFiles.size(); ftpI++) {
				if (allLocals.get(localI).getName().equals(ftpFiles.get(ftpI).getName())) {
					match = true;
				}
			}
			if (!match) {
				try {
					if (allLocals.get(localI).isDirectory()) {
						FileUtils.deleteDirectory(allLocals.get(localI));
					} else {
						Files.delete(allLocals.get(localI).toPath());
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

	public static List<File> getLocalFiles(String destination) {
		File folder = new File(destination);
		return new LinkedList<File>(Arrays.asList(folder.listFiles()));
	}

	@Override
	public void run() {
		//Infinite loop with sleep method
		Boolean alwaysAlive = true;
		Calendar cal;
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		try {
			while (alwaysAlive) {
				cal = Calendar.getInstance();
				System.out.println("Started synchronization process at: " + sdf.format(cal.getTime()));
				if (updateFiles()) {
					cal = Calendar.getInstance();
			        System.out.println("Ended synchronization process at: " + sdf.format(cal.getTime()) );
				};
				Thread.sleep(1000 * 60 * 60);
			}
		} catch (InterruptedException e) {
			System.out.println("Something interupt loader thread.");
		}
		
	}
}
