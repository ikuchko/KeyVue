import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTPReader {
	//private static List<FTPFile> ftpFiles;
	public static final String DESTINATION_ZIP = "build/resources/main/temp/FTPInput/";

	public static List<FTPFile> loadFiles(String login, String password) {
		FTPClient ftp = new FTPClient();
		List<FTPFile> fileList = null;
		try {
			if (!connectToFTPServer(ftp, login, password)) {
				return null;
			}

			// Get data from the server
			fileList = Arrays.asList(ftp.listFiles("/"));
			for (Iterator<FTPFile> iter = fileList.iterator(); iter.hasNext(); ) {
				FTPFile ftpFile = iter.next();
				System.out.println(ftpFile.getName());
				String[] parts = ftpFile.getName().split("[.]");
				if (!parts[parts.length-1].equals("zip")) {
					fileList.remove(ftpFile);
					System.out.println(ftpFile.getName() + " removed");
//					iter.remove();
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					// do nothing
				}
			}
		}
		return fileList;
	}

	public static List<FTPFile> loadFiles (Session session) {
		return loadFiles(session.getFTPUserLogin(), session.getFTPUserPassword());
	}

	public static ZipFile getZipFile(String login, String password, String fileName) {
		FTPClient ftpClient = new FTPClient();
		ZipFile zipFile = null;
		String destination = DESTINATION_ZIP + login + "/";

		File file = new File(destination + fileName);
		if (file.exists() && !file.isDirectory() && file.length() > 100) {
			try {
				zipFile = new ZipFile(destination + fileName);
			} catch (ZipException ze) {
				ze.printStackTrace();
			}
		} else {
			try {
				Files.deleteIfExists(file.toPath());
				File tempFolder = new File(destination);
				tempFolder.mkdirs();

				FileOutputStream outStream = new FileOutputStream(new File(destination + fileName));
				
				if (!connectToFTPServer(ftpClient, login, password)) {
					outStream.close();
					return null;
				}
				
//				ftpClient.setFileTransferMode(FTPClient.BLOCK_TRANSFER_MODE);
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

				ftpClient.retrieveFile(fileName, outStream);
				outStream.close();
				ftpClient.logout();
				ftpClient.disconnect();
				
				File newFile = new File(destination + fileName);
				if (newFile.exists() && !newFile.isDirectory() && newFile.length() > 10) {
					zipFile = new ZipFile(destination + fileName);
				} else{
					Files.delete(newFile.toPath());
				}
							
				
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (ZipException ze) {
				ze.printStackTrace();
			} finally {
				if (ftpClient.isConnected()) {
					try {
						ftpClient.disconnect();
					} catch (IOException ioe) {
						// do nothing
					}
				}
			}
		}
		return zipFile;
	}
	
	public static ZipFile getZipFile(Session session, String fileName) {
		return getZipFile(session.getFTPUserLogin(), session.getFTPUserPassword(), fileName);
	}
	
	public static String readProperty(String propertyName) {
		Properties properties = new Properties();
		String result ="";
		try {
			properties.load(new FileInputStream("./resources/mySQL.properties"));
			result = properties.getProperty(propertyName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static Boolean connectToFTPServer(FTPClient ftpClient, String login, String password) {
		int reply;
		try {
			ftpClient.connect(readProperty("FTP_SERVER_ADDRESS"));
			reply = ftpClient.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				System.out.println("Could not login to server");
				return false;
			}

			// Login to the server
			ftpClient.enterLocalPassiveMode();
			if (!ftpClient.login(login, password)){
				System.out.println("Could not login to server with login: " + login + "and password: " + password);
				return false;
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
