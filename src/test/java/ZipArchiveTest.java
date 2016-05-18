import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;
import org.openqa.selenium.io.Zip;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;


public class ZipArchiveTest {

	@Test
	public void zip_extracteFilesReturnTrue() {
		ZipFile zipFile = null;
		try {
			String fileName = new File(FTPReader.DESTINATION_DIRECTORY + "lps_chespa").listFiles()[0].getName();
			zipFile = new ZipFile(FTPReader.DESTINATION_DIRECTORY + "lps_chespa/" + fileName);
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Session session = new Session("lps_chespa", "empty");
		assertTrue(ZipArchive.extractFiles(zipFile, session));
	}
	
	@Test
	public void zip_assembleTIFFFilesByNameCorrectly() {
		List<String> tiffList = new ArrayList<>();
		tiffList.add("2016009713.001");
		tiffList.add("2016009713.002");
		tiffList.add("2016009713.003");
		tiffList.add("2016009714.001");
		tiffList.add("2016009714.002");
		List<List<String>> a = ZipArchive.assambleTiffFiles(tiffList);
		assertEquals(3, ZipArchive.assambleTiffFiles(tiffList).get(0).size());
		assertEquals(2, ZipArchive.assambleTiffFiles(tiffList).get(1).size());
	}
	
	@Test
	public void zip_returnsListOfTXTFilesIfDirectoryHasBeenExtracted() {
		Session session = new Session("lps_chespa", "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F");
		String fileName = new File(FTPReader.DESTINATION_DIRECTORY + "lps_chespa").listFiles()[0].getName();
		assertTrue(ZipArchive.getFiles("txt", fileName, session).size() > 0);
	}
	
	@Test
	public void zip_returnsListOfDisassembledTIFFiles() {
		Session session = new Session("lps_chespa", "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F");
		String fileName = new File(FTPReader.DESTINATION_DIRECTORY + "lps_chespa").listFiles()[0].getName();
		assertTrue(ZipArchive.getFiles("tif", fileName, session).size() > 0);
	}
	
	@Test
	public void zip_returnsListOfAssembledTIFFiles() {
		Session session = new Session("lps_chespa", "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F");
		String fileName = new File(FTPReader.DESTINATION_DIRECTORY + "lps_chespa").listFiles()[0].getName();
		assertTrue(ZipArchive.assambleTiffFiles(ZipArchive.getFiles("tif", fileName, session)).get(0).size() > 0);
	}
	
	@Test
	public void zip_AssembledTIFFilesHasBeenProcessedAllFiles() {
		String fileName = new File(FTPReader.DESTINATION_DIRECTORY + "lps_chespa").listFiles()[0].getName();
		Session session = new Session("lps_chespa", "*3D7137A3EE89D2F819F1987C766F6D4471C52F0F");
		session.setFTPFiles(FTPReader.loadFiles(session));
		List<List<String>> assembledList = ZipArchive.assambleTiffFiles(ZipArchive.getFiles("tif", fileName, session));
		int amount = 0;
		for (int i=0; i<assembledList.size(); i++) {
			for (int index=0; index<assembledList.get(i).size(); index++) {
				amount++;
			}
		}
		assertEquals(amount, ZipArchive.getFiles("tif", fileName, session).size());
	}
}
