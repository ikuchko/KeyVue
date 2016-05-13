import net.lingala.zip4j.exception.ZipException;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;

public class ArchiveReader {
	private static final String SOURCE_DIRECTORY = "src/main/resources/temp/FTPInput/";
	private static final String DESTINATION_DIRECTORY = "src/main/resources/public/temp/";
	
	public static Boolean extractFiles (String sourceFile, Session session) {
		String userLogin = session.getFTPUserLogin();
		String source = SOURCE_DIRECTORY + userLogin + "/" + sourceFile;
		String destination = DESTINATION_DIRECTORY + session.getFTPUserLogin() + "/" + sourceFile;
		File tempFolder = new File(destination);
		tempFolder.mkdirs();
		try {
	         ZipFile zipFile = new ZipFile(source);
	         zipFile.extractAll(destination);
	    } catch (ZipException e) {
	        e.printStackTrace();
	        return false;
	    }
		return true;
	}
}

