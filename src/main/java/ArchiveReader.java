import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.zip.*;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class ArchiveReader {
	//private static List<FTPFile> ftpFiles;
	private static FTPClient ftp = new FTPClient();
	private static String server = "";
	
	public static List<FTPFile> loadFiles (HashMap<String, String> credential, String path) {
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
			if (!ftp.login(credential.get("login"), credential.get("password"))){
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
				} catch (IOException ioe) {
					// do nothing
				}
			}
		}
		return fileList;
	}
	
	public static List<FTPFile> loadFiles (HashMap<String, String> credential) {
		return loadFiles(credential, "/");
	}
	
	public static ZipFile getZipFile(HashMap<String, String> credential, FTPFile ftpFile) {
		FTPClient ftpClient = new FTPClient();
		ZipFile zipFile = null;
		File file = new File("temp/" + ftpFile.getName());
		if (file.exists() && !file.isDirectory() && file.length() > 0) {
			try {
				zipFile = new ZipFile("temp/" + ftpFile.getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				File tempFolder = new File("temp/");
				tempFolder.mkdirs();
				
				FileOutputStream outStream = new FileOutputStream(new File("temp/" + ftpFile.getName()));
				ftpClient.connect(server);
				
				ftpClient.login(credential.get("login"), credential.get("password"));
				ftpClient.setFileTransferMode(FTPClient.BLOCK_TRANSFER_MODE);
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				
				ftpClient.retrieveFile(ftpFile.getName(), outStream);
				outStream.close();
				ftpClient.logout();
				ftpClient.disconnect();
				
				zipFile = new ZipFile("temp/" + ftpFile.getName());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return zipFile;
	}
	
	
}

