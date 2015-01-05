package utils;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Debug output.
 * 
 * @author Alex Luckett
 * @version 16/04/2014
 */
public class DebugTest {
	private String fileLocation;

	@Before
	public void setUp() throws Exception {
		utils.Debug.setEnabled(true);
		
		fileLocation = "C:\\Users\\Alex\\Desktop\\printfiletest\\newfile.txt";
		utils.Debug.saveToFile(fileLocation);
	}
	
	/**
	 * Ensures that file exists after creation
	 */
	@Test
	public void printConsoleFile() {		
		File existingFile = new File(fileLocation);
		assertTrue(existingFile.exists());
	}

}
