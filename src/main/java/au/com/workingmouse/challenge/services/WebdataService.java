package au.com.workingmouse.challenge.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WebdataService{
	
    /**
	 *takes in a ckan webaddress and retries all available web data by checking the number
     * of total available records and pulling 100 (ckan default) at a time.
     * Returns raw data pulled from the webaddress
     */
	
	public static List<String> retrieveWebdata(String addr, int requestedRecords) throws NumberFormatException, IOException {
		System.out.println("Getting web data....");
		
		int limit = 0;
		
		List<String> webData = new ArrayList<>();
		String params = "&records_format=csv&sort=RECORD";

		boolean gotData = false;
		
		if (!(requestedRecords == 0)){
			limit = requestedRecords;
		}
		
		while(!(gotData)) {

	        URL url = new URL(addr+params+"&limit="+String.valueOf(limit));
	        
	        URLConnection con = url.openConnection();
	        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
	
	        String webLine = null;
	        
	        while ((webLine = br.readLine()) != null) {
	        	if (limit == 0) {
		        	String[] temp = webLine.split("\\s+");
		        	limit = Integer.parseInt(temp[temp.length-1].replaceAll("[\\D]", ""));
	        	}
	        	else {
	        	webData.add(webLine);
	        	gotData = true;
	        	}
	        }
		}
		return webData;
	}
	
	
    /**
     * Takes in raw webdata retirved from a ckan dataset and processes it.
     * Returns data formatted in a way that would be returned from reading
     * a .csv file
     */
	
	
	public static List<String> processWebData (List<String> webLines){
		String[] lineParts;
		List<String> formattedLines = new ArrayList<>();
		
		formattedLines.add("IGNORE");//first line is ignored when using VelocityAndDirectionSeriver.parseLines
		
		for (String webLine : webLines) {
			//split the lines up
    		String[] lines = webLine.split(Pattern.quote("\\n"));
    		
    		int count = 0;
    		for(String line: lines) {
    			if(!(count==lines.length-1)) {
        			if(count++== 0) {
        				//first line contains header information that isn't seperared from the first true line of
        				//data when splitting by (\\n), and requires further split
        				lineParts = line.split(Pattern.quote(": "));
        				lineParts = lineParts[lineParts.length-1].split(",");
        			}
        			else {
        				//split the real data lines up into each data piece
        				lineParts = line.split(",");
        			}		        			
        			String formattedLine = "";
        			int count2 = 0;
    				for (String linePart: lineParts) {
    					if (!(count2++== 0)) { // first data piece is an index(?) and is not needed
    						if(count2==2) {
    							//remove the 'T' that exists in the timestamps
    							linePart = linePart.replaceAll("T", " ");	
    						}
    						formattedLine += linePart; //join it back together piece by piece
    						
    						if (!(count2==lineParts.length)) {//don't add comma to last data piece
    							formattedLine += ",";
    						}      				       						
    					}
    				}
    			formattedLines.add(formattedLine);
    			}
    		}
		}
	return formattedLines;
	}	
	
	
	
}
