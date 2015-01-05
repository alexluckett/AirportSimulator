package aircraft;

/** 
 * @author Alex Luckett & Shahbaz Hussain
 * @version 01/04/2014
 */
public class CommercialAircraft extends EnginedAircraft {
	private static double spawnProbability = 0; // since this can change, final not used here
	
	public CommercialAircraft(){
		super(4, 6); // time to take off, time to land
		setRandomFuel(40, 80); // sets fuel corresponding 20-40 mins (in ticks)
	}
	
	/**
	 * set the spawnProbability out of 1 that an Aircraft will spawn.
	 * @param p Amount of probability that the plane can spawn for
	 */
	public static void setProbability(double p) {
		spawnProbability = p;
	}
	
	/**
	 * Returns the spawnProbability out of 1 that an Aircraft will spawn.
	 * @return spawnProbability
	 */
	public static double getSpawnProbability() {
		return spawnProbability;
	}
	
	/**
	 * Returns a string with amount of fuel (timeLeftToFly). and the waitingTime.
	 */
	@Override
	public String toString() {
		return "Commercial. Fuel: " + getTimeLeftToFly() + ", Waiting time: " + getWaitingTime();
	}
}
