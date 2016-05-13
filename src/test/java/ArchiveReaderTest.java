import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;


public class ArchiveReaderTest {
	
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
		assertTrue(ArchiveReader.extractFiles(zipFile, session));
	}
}