package au.com.workingmouse.challenge.services;

import au.com.workingmouse.challenge.models.VelocityAndDirectionData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class VelocityAndDirectionService {

    public static VelocityAndDirectionData parseLine(String line) {
        // NOTE: This CSV parsing is not fully inclusive
        String[] parts = line.split(",");

        VelocityAndDirectionData velocityAndDirectionData = new VelocityAndDirectionData();

        String timestamp = parts[0].replaceAll("^\"|\"$", "");
        
        velocityAndDirectionData.setTimestamp(Timestamp.valueOf(timestamp));
        velocityAndDirectionData.setRecord(Integer.parseInt(parts[1]));
        velocityAndDirectionData.setDcsModel(Integer.parseInt(parts[2]));
        velocityAndDirectionData.setDcsSerial(Integer.parseInt(parts[3]));
        velocityAndDirectionData.setDcsAbsspdAvg(Double.parseDouble(parts[4]));
        velocityAndDirectionData.setDcsDirectionAvg(Double.parseDouble(parts[5]));
        velocityAndDirectionData.setDcsNorthCurAvg(Double.parseDouble(parts[6]));
        velocityAndDirectionData.setDcsEastCurAvg(Double.parseDouble(parts[7]));
        velocityAndDirectionData.setDcsHeadingAvg(Double.parseDouble(parts[8]));
        velocityAndDirectionData.setDcsTiltXAvg(Double.parseDouble(parts[9]));
        velocityAndDirectionData.setDcsTiltYAvg(Double.parseDouble(parts[10]));
        velocityAndDirectionData.setDcsSpStdAvg(Double.parseDouble(parts[11]));
        velocityAndDirectionData.setDcsSigStrengthAvg(Double.parseDouble(parts[12]));
        velocityAndDirectionData.setDcsPingCntAvg(Double.parseDouble(parts[13]));
        velocityAndDirectionData.setDcsAbsTiltAvg(Double.parseDouble(parts[14]));
        velocityAndDirectionData.setDscMaxTiltAvg(Double.parseDouble(parts[15]));
        velocityAndDirectionData.setDcsStdTiltAvg(Double.parseDouble(parts[16]));

        return velocityAndDirectionData;
    }

    /**
     * Takes the given ordered list of VelocityAndDirectionData properties and initialises a new
     * VelocityAndDirectionData object.
     * @param line - row to parse
     * @return VelocityAndDirectionData
     */
    public static VelocityAndDirectionData parseLine(List<String> line) {

        if (line.size() != 17) {
            throw new IllegalArgumentException("VelocityAndDirectionData Objects require 17 input arguments.");
        }

        return new VelocityAndDirectionData(
                Timestamp.valueOf(line.get(0)),
                Integer.parseInt(line.get(1)),
                Integer.parseInt(line.get(2)),
                Integer.parseInt(line.get(3)),
                Double.parseDouble(line.get(4)),
                Double.parseDouble(line.get(5)),
                Double.parseDouble(line.get(6)),
                Double.parseDouble(line.get(7)),
                Double.parseDouble(line.get(8)),
                Double.parseDouble(line.get(9)),
                Double.parseDouble(line.get(10)),
                Double.parseDouble(line.get(11)),
                Double.parseDouble(line.get(12)),
                Double.parseDouble(line.get(13)),
                Double.parseDouble(line.get(14)),
                Double.parseDouble(line.get(15)),
                Double.parseDouble(line.get(16))
        );
    }

    public static List<VelocityAndDirectionData> parseLines(List<String> lines) {
        var parsedLines = new ArrayList<VelocityAndDirectionData>();

        int count = 0;
        for (String line : lines) {
            if (count++ == 0) {
                // Skip header
                continue;
            }
            int i = 0;
            parsedLines.add(VelocityAndDirectionService.parseLine(line));
        }

        return parsedLines;
    }
    /**
     * Takes in the full velocityAndDirectionDataset and calculates the sum for each column
     * (excluding timestamps)
     * Returns a velocityAndDirectionData object containing the sums.
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * 
     */
    
    public static VelocityAndDirectionData sumVelocityAndDirectionData(List<VelocityAndDirectionData> velocityAndDirectionDataset) throws IllegalArgumentException, IllegalAccessException{

    	VelocityAndDirectionData velocityAndDirectionTotals = new VelocityAndDirectionData();    	

    	for(VelocityAndDirectionData velocityAndDirectionData: velocityAndDirectionDataset) {
	    	Field[] fields = velocityAndDirectionData.getClass().getDeclaredFields();

			 for ( Field field : fields  ) {
				 
				 if (!Timestamp.class.isAssignableFrom(field.getType())) { //ignore timestamps
					 field.setAccessible(true);
					 
					 if (Integer.class.isAssignableFrom(field.getType())) {
						 if (field.get(velocityAndDirectionTotals) == null) {
							 field.set(velocityAndDirectionTotals,0); // initalise with an integer
						 }
						 field.set(velocityAndDirectionTotals, (Integer) field.get(velocityAndDirectionData) + (Integer) field.get(velocityAndDirectionTotals)) ;
					 }
					 if (Double.class.isAssignableFrom(field.getType())) {
			
						 if (field.get(velocityAndDirectionTotals) == null) {
							 field.set(velocityAndDirectionTotals,0.0); // initalise with a double
			    		}
						field.set(velocityAndDirectionTotals, (Double) field.get(velocityAndDirectionData) + (Double) field.get(velocityAndDirectionTotals));
					 }
				 }
			 }	  
    	}
    	return velocityAndDirectionTotals;
    }
    
    /**
     * Takes in the sum of the velocityAndDirectionDataset and calculates the average for each column
     * (excluding timestamps)
     * Returns a velocityAndDirectionData object containing the averages.
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * 
     */	
 
    public static VelocityAndDirectionData averageSummedVelocityAndDirectionData(VelocityAndDirectionData velocityAndDirectionTotals, int datasetSize) throws IllegalArgumentException, IllegalAccessException{
    	
    	VelocityAndDirectionData velocityAndDirectionAverages = new VelocityAndDirectionData();  

    	Field[] fields = velocityAndDirectionTotals.getClass().getDeclaredFields();
		 for ( Field field : fields  ) {
			 
			 if (!Timestamp.class.isAssignableFrom(field.getType())) {
				 field.setAccessible(true);
				 //initalise with an integer and set correct field by dividing sum by dataset size
				 if (Integer.class.isAssignableFrom(field.getType())) {
					 field.set(velocityAndDirectionAverages,0);
					 field.set(velocityAndDirectionAverages, (Integer) field.get(velocityAndDirectionTotals) / datasetSize );
				 }//initalise with a double and set correct field by dividing sum by dataset size
				 if (Double.class.isAssignableFrom(field.getType())) {
					 field.set(velocityAndDirectionAverages,0.0);
					 field.set(velocityAndDirectionAverages, (Double) field.get(velocityAndDirectionTotals) / datasetSize );
				 } 
			  }	 
		  }
		return velocityAndDirectionAverages;	
    }
    
    
    
    
    public static VelocityAndDirectionData calculateAverages (List<VelocityAndDirectionData> velocityAndDirectionDataset) throws IllegalArgumentException, IllegalAccessException{
    	int datasetSize = velocityAndDirectionDataset.size();
    	VelocityAndDirectionData velocityAndDirectionTotals = sumVelocityAndDirectionData(velocityAndDirectionDataset);
    	VelocityAndDirectionData velocityAndDirectionAverages = averageSummedVelocityAndDirectionData(velocityAndDirectionTotals, datasetSize);
    	return velocityAndDirectionAverages;
    	
    }


    public static String summarise(List<VelocityAndDirectionData> velocityAndDirectionDataset) throws IllegalArgumentException, IllegalAccessException{
        Integer totalLines = velocityAndDirectionDataset.size();
        VelocityAndDirectionData velocityAndDirectionAverages = velocityAndDirectionDataset.get(velocityAndDirectionDataset.size()-1);
        var summaryBuilder = new StringBuilder() ;

        
        String averages = "";
        //Read averages from fields and add them to the string.
    	Field[] fields = velocityAndDirectionAverages.getClass().getDeclaredFields();
			for ( Field field : fields  ) {
				 if (!Timestamp.class.isAssignableFrom(field.getType())) {
					 field.setAccessible(true);
					 averages += "<br/>"+field.getName() + ": " + field.get(velocityAndDirectionAverages).toString();
				 }
			}

        summaryBuilder.append("<head></head>")
                .append("<body>")
                .append("<h2>Summary</h2>")
                .append("<br />")
                .append("<strong>Total Lines:</strong>" + totalLines.toString())
                .append("<p>")  // Add p element to HTML for displaying averages
        		.append(averages)
        		.append("</p>")
                .append("</body>");

        return summaryBuilder.toString();
    }
}
