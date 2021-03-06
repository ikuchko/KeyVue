import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPFile;

import net.lingala.zip4j.core.ZipFile;

public class ZipArchive {
	public static final String DESTINATION = "build/resources/main/public/temp/";

	public static Boolean extractFiles (ZipFile zipFile, Session session) {
		String destination = DESTINATION + session.getFTPUserLogin() + "/" + zipFile.getFile().getName();
		File tempFolder = new File(destination);
		tempFolder.mkdirs();
		try {
	         zipFile.extractAll(destination);
	    } catch (ZipException e) {
	        e.printStackTrace();
	        System.out.println("Error while extracting zipFile in extractFiles methode");
	        return false;
	    }
		return true;
	}

	public static List<String> getFiles(String type, String searchFolder, Session session) {
		List<String> files = new ArrayList<>();
		File folder = new File(DESTINATION + session.getFTPUserLogin() + "/" + searchFolder);
		if ((!folder.exists()) || folder.listFiles().length < 1) {
			extractFiles(FTPReader.getZipFile(session, searchFolder), session);
		}
		File[] listOfFiles = folder.listFiles();
		for (int i=0; i<listOfFiles.length-1; i++) {
			if (listOfFiles[i].isFile()){
				if (FilenameUtils.getExtension(listOfFiles[i].getPath()).equals(type) && type.equals("txt")) {
					files.add(listOfFiles[i].getName());
				} 
				
				if (!(FilenameUtils.getExtension(listOfFiles[i].getPath()).equals("txt")) && !(type.equals("txt"))) {
					files.add(listOfFiles[i].getName());
				}
			}
		}
		return files;
	}

	public static List<List<String>> assambleTiffFiles(List<String> files) {
		List<List<String>> resultList = new ArrayList<List<String>>();
		String fileName = "";
		List<String> tiffFile = new ArrayList<>();
		for (int i=0; i<files.size(); i++) {
			if (FilenameUtils.getBaseName(files.get(i)).equals(fileName)) {
				tiffFile.add(files.get(i));
			} else {
				if (tiffFile.size() > 0) {
					resultList.add(new ArrayList<>(tiffFile));
					tiffFile.clear();
				}
				tiffFile.add(files.get(i));
				fileName = FilenameUtils.getBaseName(files.get(i));
			}
			if (i == files.size()-1) {
				resultList.add(new ArrayList<>(tiffFile));
			}
		}
		return resultList;
	}
}
