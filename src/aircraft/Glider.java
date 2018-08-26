package aircraft;

/**
 * @author Alex Luckett & Shahbaz Hussain
 * @version 01/04/2014
 */
public class Glider extends Aircraft {
	private static final double spawnProbability = 0.002;
	
	public Glider(){
		super(6, 8); // takeoff time 6, landing time 8 in ticks
	}
	
	/**
	 * Returns the spawnProbability out of 1 that an Aircraft will spawn.
	 * @return spawnProbability
	 */
	public static double getSpawnProbability() {
		return spawnProbability;
	}
	
	/**
	 * Returns the time the aircraft can fly for. Always returns the max int value, regardless
	 * of if the fuel value has changed because it can never run out.
	 */
	@Override
	public int getTimeLeftToFly() {
		return Integer.MAX_VALUE; // can never run out of fuel, so always return max amount (2147483647)
	}
	
	/**
	 * Returns a string with the amount of fuel (timeLeftToFly) and the waitingTime.
	 */
	@Override
	public String toString() {
		return "Glider.     Fuel: " + getTimeLeftToFly() + ", Waiting time: " + getWaitingTime();
	}
}