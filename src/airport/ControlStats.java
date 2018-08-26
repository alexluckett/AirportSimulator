package airport;

import java.text.DecimalFormat;

/**
 * Stores statistics for the simulation. Stores waiting time, number of departures,
 * number of arrivals, crashes, p value and queue type.
 * 
 * @author Alex Luckett
 * @version 20/04/2014
 */
public class ControlStats {
	private int totalWaitingTime; // stored as number of steps
	private int totalLandings; // when a plane SUCCESSFULLY lands, increment
	private int totalDeparted; // number of aircraft that take off
	private int totalCrashes; // every time a plane crashes (lack of fuel), increment
	private final Double pValue; // the value of P used to create the statistics
	private final String queueType; // queue type used to create the statistics
	
	public ControlStats (double pValue, String queueType) {
		this.totalWaitingTime = 0;
		this.totalDeparted = 0;
		this.totalCrashes = 0;
		this.totalLandings = 0;
		
		this.pValue = pValue;
		this.queueType = queueType;
	}
	
	public int getTotalWaitingTime() {
		return totalWaitingTime;
	}

	public int getTotalLandings() {
		return totalLandings;
	}

	public int getTotalDeparted() {
		return totalDeparted;
	}

	public int getTotalCrashes() {
		return totalCrashes;
	}
	
	public void addWaitingTime(int timeToAdd) {
		totalWaitingTime += timeToAdd;
	}
	
	public void addCrash() {
		totalCrashes++;
	}
	
	public void addLanding() {
		totalLandings++;
	}
	
	public void addDeparture() {
		totalDeparted++;
	}
	
	public int getCrashes() {
		return totalCrashes;
	}
	
	/**
	 * Gets the value of P (commercial aircraft probability)
	 * as a String, formatted to 4 decimal places.
	 * @return P as a String (4 decimal places)
	 */
	public String getPasString() {
		DecimalFormat format = new DecimalFormat("#.####");
		return format.format(pValue);
	}

	public String getQueueType() {
		return queueType;
	}
	
	/**
	 * Takes in a number of ticks, converts to minutes and appends "mins to end"
	 * @param tickNumber
	 * @return String representation of ticks in mins
	 */
	private String convertTicksToMins(double tickNumber) {
		return (tickNumber/2) + " mins"; // to convert ticks to mins, times minutes value by 2
	}
	
	public String toString() {
		return 	"Commercial probability: " + getPasString() + "\n" +
				"Queue type: " + queueType + "\n" + "\n" +
				"Total Waiting Time: " + convertTicksToMins(totalWaitingTime) + "\n" +
				"Total Landings: " + totalLandings + "\n" +
				"Total Departures: " + totalDeparted + "\n" +
				"Total Crashes: " + totalCrashes + "\n \n" +
				"Average waiting time: " + convertTicksToMins((totalWaitingTime/(totalLandings + totalDeparted))) + "\n" + // calculates the waiting time in minutes, converts to minutes
				"==========" + "\n";
	}
	
	/**
	 * Prints out the stats in CSV format. Useful for adding into spreadsheets!
	 * @return String representing the stats in CSV format
	 */
	public String toStringCSV() {
		return getPasString() + "," +
				convertTicksToMins(totalWaitingTime) + "," +
				totalLandings + "," +
				totalDeparted + "," +
				totalCrashes + "," +
				convertTicksToMins((totalWaitingTime/(totalLandings + totalDeparted)));
	}
}
