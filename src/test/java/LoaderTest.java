import static org.junit.Assert.*;
import org.junit.Test;

public class LoaderTest {
	
	@Test
	public void loader_getsListOfLocalFiles() {
		String destination = FTPReader.DESTINATION_ZIP + "lps_dane";
		assertTrue(Loader.getLocalFiles(destination).size() > 0);
	}
	
	@Test
	public void loader_removeZipFilesAndFolders() {
		assertTrue(Loader.updateFiles());
	}
}
