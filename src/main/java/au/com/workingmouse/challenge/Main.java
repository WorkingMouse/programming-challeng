package au.com.workingmouse.challenge;

import au.com.workingmouse.challenge.config.Configuration;
import au.com.workingmouse.challenge.models.VelocityAndDirectionData;
import au.com.workingmouse.challenge.services.FileService;
import au.com.workingmouse.challenge.services.VelocityAndDirectionService;
import au.com.workingmouse.challenge.services.WebdataService;

import org.apache.log4j.Logger;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class);
	protected static final String OUTPUT_FILENAME = "index.html";

	public static void main(String[] args) throws IOException {
		

		try {
			Configuration.load();

			var addr = Configuration.getWebAddress();

			var lines = retrieveAndParseWebData(addr, 0);
			
			var averages = VelocityAndDirectionService.calculateAverages(lines);
			
			lines.add(averages);
			
			String html = VelocityAndDirectionService.summarise(lines);
			
			writeFile(html);


		} catch (Exception e) {
			LOGGER.error("Failed to run application", e);
		}
	}
	

	protected static List<VelocityAndDirectionData> retrieveAndParseWebData(String addr, int limit) throws IOException, NumberFormatException, InterruptedException {
		List<String> rawLines = WebdataService.retrieveWebdata(addr, limit);
		List<String> lines = WebdataService.processWebData(rawLines);
		
		List<VelocityAndDirectionData> parsedLines = VelocityAndDirectionService.parseLines(lines);

		return parsedLines;
	}
	
	protected static List<VelocityAndDirectionData> loadAndParseFile(File filename) throws IOException {
		FileService fileService = new FileService();
		List<String> lines = fileService.readLines(filename);

		List<VelocityAndDirectionData> parsedLines = VelocityAndDirectionService.parseLines(lines);

		return parsedLines;
	}


	protected static void writeFile(String html) throws IOException {
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUTPUT_FILENAME))) {
			bufferedWriter.write(html);
		}
	}
}
