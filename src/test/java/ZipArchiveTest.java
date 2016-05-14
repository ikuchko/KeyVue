import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;

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
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(FTPReader.DESTINATION_DIRECTORY + "lps_nort/NHA-20160425.zip");
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Session session = new Session("lps_chespa", "empty");
		ZipArchive zip = new ZipArchive(zipFile, session);
		zip.readExtractedFiles();
		assertTrue(zip.getTxtFiles().size() > 0);
		assertTrue(zip.getTiffFiles().size() > 0);
		assertTrue(zip.getTiffFiles().get(0).size() > 0);
	}
}