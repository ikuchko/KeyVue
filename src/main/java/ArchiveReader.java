import net.lingala.zip4j.exception.ZipException;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;

public class ArchiveReader {
	private static final String SOURCE_DIRECTORY = "src/main/resources/temp/FTPInput/";
	private static final String DESTINATION_DIRECTORY = "src/main/resources/public/temp/";
	
	public static Boolean extractFiles (ZipFile zipFile, Session session) {
		String destination = DESTINATION_DIRECTORY + session.getFTPUserLogin() + "/" + zipFile.getFile().getName();
		File tempFolder = new File(destination);
		tempFolder.mkdirs();
		try {
	         zipFile.extractAll(destination);
	    } catch (ZipException e) {
	        e.printStackTrace();
	        return false;
	    }
		return true;
	}
}

