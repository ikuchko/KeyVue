import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTPReader {
	//private static List<FTPFile> ftpFiles;
	public static final String DESTINATION_DIRECTORY = "build/resources/main/temp/FTPInput/";

	public static List<FTPFile> loadFiles (Session session, String path) {
		FTPClient ftp = new FTPClient();
		List<FTPFile> fileList = null;
		try {
			if (!connectToFTPServer(ftp, session)) {
				return null;
			}

			// Get data from the server
			fileList = Arrays.asList(ftp.listFiles(path));

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
		return loadFiles(session, "/");
	}

	public static ZipFile getZipFile(Session session, FTPFile ftpFile) {
		FTPClient ftpClient = new FTPClient();
		ZipFile zipFile = null;
		String destination = DESTINATION_DIRECTORY + session.getFTPUserLogin() + "/";

		File file = new File(destination + ftpFile.getName());
		if (file.exists() && !file.isDirectory() && file.length() > 100) {
			try {
				zipFile = new ZipFile(destination + ftpFile.getName());
			} catch (ZipException ze) {
				// TODO Auto-generated catch block
				ze.printStackTrace();
			}
		} else {
			try {
				Files.deleteIfExists(file.toPath());
				File tempFolder = new File(destination);
				tempFolder.mkdirs();

				FileOutputStream outStream = new FileOutputStream(new File(destination + ftpFile.getName()));
				
				if (!connectToFTPServer(ftpClient, session)) {
					outStream.close();
					return null;
				}
				
//				ftpClient.setFileTransferMode(FTPClient.BLOCK_TRANSFER_MODE);
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

				ftpClient.retrieveFile(ftpFile.getName(), outStream);
				outStream.close();
				ftpClient.logout();
				ftpClient.disconnect();
				
				File newFile = new File(destination + ftpFile.getName());
				if (newFile.exists() && !newFile.isDirectory() && newFile.length() > 10) {
					zipFile = new ZipFile(destination + ftpFile.getName());
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
	
	public static String readProperty(String propertyName) {
		Properties properties = new Properties();
		String result ="";
		try {
			properties.load(new FileInputStream("./resources/mySQL.properties"));
			result = properties.getProperty(propertyName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	private static Boolean connectToFTPServer(FTPClient ftpClient, Session session) {
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
			if (!ftpClient.login(session.getFTPUserLogin(), session.getFTPUserPassword())){
				System.out.println("Could not login to server with login: " + session.getFTPUserLogin());
				return false;
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
