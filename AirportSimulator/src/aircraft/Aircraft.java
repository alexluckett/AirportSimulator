package aircraft;

/**
 * @author Alex Luckett & Shahbaz Hussain
 * @version 01/04/2014
 */
public abstract class Aircraft {
	protected static final double spawnProbability = 0; // probability of being spawned
	protected static final double breakdownProbability = 0.0001; // probability of breakdown for all aircraft. protected visibility allows for specific aircraft to have their own values. 
	protected final int timeToTakeoff; // length of time to take off
	protected final int timeToLand; // length of time to land
	protected int timeLeftToFly; // time until aircraft runs out of fuel
	protected int waitingTime; // waiting time (time it has been waiting to use runway)

	public Aircraft(int timeToTakeoff, int timeToLand) {
		waitingTime = 0;
		timeLeftToFly = 0;
		this.timeToTakeoff = timeToTakeoff;
		this.timeToLand = timeToLand;
	}
	
	/**
	 * Returns the amount of time an aircraft has been waiting
	 * @return waitingTime
	 */
	public int getWaitingTime(){
		return waitingTime; 
	}
	
	/**
	 * Increase the waiting time of an aircraft by 1
	 */
	public void incrementWaitingTime() {
		waitingTime++;
	}
	
	/**
	 * Reset the aircraft's waiting time back to 0
	 */
	public void resetWaitingTime() {
		waitingTime = 0;
	}
	
	/**
	 * Returns the time an aircraft takes to land (in ticks)
	 * @return timeToLand
	 */
	public int getTimeToLand() {
		return timeToLand;
	}
	
	/**
	 * Returns the time an aircraft takes to take off (in ticks)
	 * @return timeToTakeoff
	 */
	public int getTimeToTakeoff() {
		return timeToTakeoff;
	}
	
	/**
	 * Returns the probability out of 1 that an Aircraft will spawn.
	 * @return spawnProbability
	 */
	public static double getSpawnProbability() {
		return spawnProbability;
	}
	
	/**
	 * Returns the probability out of 1 that an Aircraft will break down
	 * @return breakdownProbability
	 */
	public static double getBreakdownProbability() {
		return breakdownProbability;
	}
	
	/**
	 * Returns the amount of fuel (aka time to fly).
	 * Some aircraft may have indefinite fuel, so make abstract.
	 * 
	 * @return timeLeftToFly
	 */
	public abstract int getTimeLeftToFly();
	
	/**
	 * Set amount of fuel
	 * @param fuel Amount of fuel to assign the aircraft
	 */
	protected void setTimeLeftToFly(int fuel) {
		timeLeftToFly = fuel;
	}
	
	/**
	 * Decrease fuel by 1 tick
	 */
	public void decrementFuel() {
		timeLeftToFly--;
	}
	
	/**
	 * Print out the aircraft's properties
	 * Each aircraft string may have different properties in it, so make abstract
	 * 
	 * @return String representing object
	 */
	public abstract String toString();

}