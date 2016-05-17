import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTPReader {
	//private static List<FTPFile> ftpFiles;
	public static final String DESTINATION_DIRECTORY = "src/main/resources/temp/FTPInput/";
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
		if (file.exists() && !file.isDirectory() && file.length() > 0) {
			try {
				zipFile = new ZipFile(destination + ftpFile.getName());
			} catch (ZipException ze) {
				// TODO Auto-generated catch block
				ze.printStackTrace();
			}
		} else {
			try {
				File tempFolder = new File(destination);
				tempFolder.mkdirs();

				FileOutputStream outStream = new FileOutputStream(new File(destination + ftpFile.getName()));
				ftpClient.connect(server);

				ftpClient.login(session.getFTPUserLogin(), session.getFTPUserPassword());
				ftpClient.setFileTransferMode(FTPClient.BLOCK_TRANSFER_MODE);
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

				ftpClient.retrieveFile(ftpFile.getName(), outStream);
				outStream.close();
				ftpClient.logout();
				ftpClient.disconnect();

				zipFile = new ZipFile(destination + ftpFile.getName());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (ZipException ze) {
				ze.printStackTrace();
			}
		}
		return zipFile;
	}
}
