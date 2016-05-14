import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import net.lingala.zip4j.core.ZipFile;

public class ZipArchive {
	private static String DESTINATION_DIRECTORY;
	private ZipFile zipFile;
	private Session session;
	private List<List<String>> tiffFiles = new ArrayList<List<String>>();
	private List<String> txtFiles = new ArrayList<>();
	
	public ZipArchive(ZipFile zipFile, Session session) {
		this.zipFile = zipFile;
		this.DESTINATION_DIRECTORY = "src/main/resources/public/temp/" + session.getFTPUserLogin() + "/" + zipFile.getFile().getName();
	}
	
	public Boolean extractFiles () {
		File tempFolder = new File(DESTINATION_DIRECTORY);
		tempFolder.mkdirs();
		try {
	         zipFile.extractAll(DESTINATION_DIRECTORY);
	    } catch (ZipException e) {
	        e.printStackTrace();
	        return false;
	    }
		return true;
	}
	
	public List<String> getTxtFiles() {
		return txtFiles;
	}
	
	public List<List<String>> getTiffFiles() {
		return tiffFiles;
	}
	
	private List<String> getFiles(String type) {
		List<String> files = new ArrayList<>();
		File folder = new File(DESTINATION_DIRECTORY);
		File[] listOfFiles = folder.listFiles();
		for (int i=0; i<listOfFiles.length-1; i++) {
			if (listOfFiles[i].isFile()){
				if (FilenameUtils.getExtension(listOfFiles[i].getPath()).equals(type) && type.equals("txt")) {
					files.add(listOfFiles[i].getName());
				} else if (!FilenameUtils.getExtension(listOfFiles[i].getPath()).equals("txt")){
					files.add(listOfFiles[i].getName());
				}
			}
		}
		return files;
	}
	
	public void readExtractedFiles() {
		this.txtFiles = getFiles("txt");
		this.tiffFiles = assambleTiffFiles(getFiles("tif"));
	}

	private List<List<String>> assambleTiffFiles(List<String> files) {
		List<List<String>> resultList = new ArrayList<List<String>>();
		String fileName = "";
		List<String> tiffFile = new ArrayList<>();
		for (int i=0; i<files.size(); i++) {
			if (FilenameUtils.getBaseName(files.get(i)).equals(fileName)) {
				tiffFile.add(files.get(i));
			} else {
				if (tiffFile.size() > 0) {
					resultList.add(tiffFile);
					tiffFile.clear();					
				}
				tiffFile.add(files.get(i));
				fileName = FilenameUtils.getBaseName(files.get(i));
			}
		}
		return resultList;
	}
}

