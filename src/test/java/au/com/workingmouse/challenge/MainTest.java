package au.com.workingmouse.challenge;

import au.com.workingmouse.challenge.config.Configuration;
import au.com.workingmouse.challenge.models.VelocityAndDirectionData;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class MainTest extends Main {

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
		File outputFile = new File(OUTPUT_FILENAME);
		if (outputFile.exists()) {
			outputFile.delete();
		}
	}

	@Test
	void main() {
	}

	@Test
	void writeFileSingleLine() throws IOException {
		final String basicText = "Yolo";
		Main.writeFile(basicText);

		String actualOutput = readFile();
		assertEquals(basicText, actualOutput);
	}

	@Test
	void writeFileMultilineLine() throws IOException {
		final String basicText = "Yolo\n"
				+ "But on two lines";
		Main.writeFile(basicText);

		String actualOutput = readFile();
		assertEquals(basicText, actualOutput);
	}
	
	@Test
	void retrieveAndParseWebDataTest() throws IOException {
		Configuration.load();
		File filename = Configuration.getImportFile();
		var addr = Configuration.getWebAddress();
		List<VelocityAndDirectionData> csvFileOutput = Main.loadAndParseFile(filename);
		List<VelocityAndDirectionData> webDataOutput =  Main.retrieveAndParseWebData(addr,csvFileOutput.size());		
		
		assertEquals(webDataOutput, csvFileOutput);
	}
	

	@Test
	void loadAndParseFileTest() {
		// TODO: Complete coverage
	}

	private String readFile() throws IOException {
		return new Scanner(new File(OUTPUT_FILENAME)).useDelimiter("\\A").next();
	}
}