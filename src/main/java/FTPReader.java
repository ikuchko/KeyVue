import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.Arrays;
import java.util.HashMap;
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
	public static final String DESTINATION_DIRECTORY = "build/resources/main/temp/FTPInput/";
	private static FTPClient ftp = new FTPClient();
	private static String server = "";

	public static List<FTPFile> loadFiles (Session session, String path) {
		Properties properties = new Properties();
		List<FTPFile> fileList = null;
		int reply;
		try {
			properties.load(new FileInputStream("./resources/mySQL.properties"));
			server = properties.getProperty("FTP_SERVER_ADDRESS");
			ftp.connect(server);
			System.out.println(ftp.getReplyString());
			reply = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return null;
			}

			// Login to the server
			ftp.enterLocalPassiveMode();
			if (!ftp.login(session.getFTPUserLogin(), session.getFTPUserPassword())){
				System.out.println("Could not login to server.");
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
					System.out.println("dissconected");
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
		Properties properties = new Properties();
		FTPClient ftpClient = new FTPClient();
		ZipFile zipFile = null;
		String destination = DESTINATION_DIRECTORY + session.getFTPUserLogin() + "/";
		int reply;

		File file = new File(destination + ftpFile.getName());
		System.out.println("File length is: " + file.length());
		if (file.exists() && !file.isDirectory() && file.length() > 100) {
			try {
				zipFile = new ZipFile(destination + ftpFile.getName());
			} catch (ZipException ze) {
				// TODO Auto-generated catch block
				ze.printStackTrace();
				System.out.println("Error while reading zipFile in getZipFile methode");
			}
		} else {
			try {
				Files.deleteIfExists(file.toPath());
				File tempFolder = new File(destination);
				tempFolder.mkdirs();

				FileOutputStream outStream = new FileOutputStream(new File(destination + ftpFile.getName()));
				
				properties.load(new FileInputStream("./resources/mySQL.properties"));
				server = properties.getProperty("FTP_SERVER_ADDRESS");
				ftpClient.connect(server);
				System.out.println(ftpClient.getReplyString());
				reply = ftpClient.getReplyCode();
				if(!FTPReply.isPositiveCompletion(reply)) {
					outStream.close();
					ftpClient.disconnect();
					return null;
				}

				// Login to the server
				ftpClient.enterLocalPassiveMode();
				if (!ftpClient.login(session.getFTPUserLogin(), session.getFTPUserPassword())){
					System.out.println("Could not login to server.");
					outStream.close();
					return null;
				}

				ftpClient.login(session.getFTPUserLogin(), session.getFTPUserPassword());
//				ftpClient.setFileTransferMode(FTPClient.BLOCK_TRANSFER_MODE);
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

				ftpClient.retrieveFile(ftpFile.getName(), outStream);
				outStream.close();
				ftpClient.logout();
				ftpClient.disconnect();
				
				File newFile = new File(destination + ftpFile.getName());
				System.out.println("File length is: " + newFile.length());
				if (newFile.exists() && !newFile.isDirectory() && newFile.length() > 100) {
					zipFile = new ZipFile(destination + ftpFile.getName());
				} else{
					Files.delete(newFile.toPath());
					System.out.println("ZipFile was corrupted and I delete it");
				}
							
				
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (ZipException ze) {
				ze.printStackTrace();
			} finally {
				if (ftpClient.isConnected()) {
					try {
						ftpClient.disconnect();
						System.out.println("dissconected");
					} catch (IOException ioe) {
						// do nothing
					}
				}
			}
		}
		return zipFile;
	}
}
