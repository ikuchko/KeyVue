import static org.junit.Assert.*;
import org.junit.Test;

public class LoaderTest {
	
	@Test
	public void loader_removeZipFilesAndFolders() {
		assertTrue(Loader.updateFiles());
	}
}
