import static org.junit.Assert.*;

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
			zipFile = new ZipFile(FTPReader.DESTINATION_DIRECTORY + "lps_nort/NHA-20160425.zip");
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Session session = new Session("lps_chespa", "empty");
		ZipArchive zip = new ZipArchive(zipFile, session);
		assertTrue(zip.extractFiles());
	}

	@Test
	public void zip_populatedFileListsSuccessfully() {
//		ZipFile zipFile = null;
//		try {
//			zipFile = new ZipFile(FTPReader.DESTINATION_DIRECTORY + "lps_nort/NHA-20160425.zip");
//		} catch (ZipException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Session session = new Session("lps_chespa", "empty");
//		ZipArchive zip = new ZipArchive(zipFile, session);
//		zip.readExtractedFiles();
//		assertTrue(zip.getTxtFiles().size() > 0);
//		assertTrue(zip.getTiffFiles().size() > 0);
//		assertTrue(zip.getTiffFiles().get(0).size() > 0);
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
		Session session = new Session("lps_chespa", "empty");
		assertTrue(ZipArchive.getTXTFiles("NHA-20160425.zip", session).size() > 0);
	}
	
//	@Test
//	public void zip_returnsListOfTIFFilesIfDirectoryHasBeenExtracted() {
//		Session session = new Session("lps_chespa", "empty");
//		assertTrue(ZipArchive.getTIFFFiles("NHA-20160425.zip", session).size() > 0);
//	}
}
