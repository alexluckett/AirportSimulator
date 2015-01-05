package aircraft;

/**
 * @author Alex Luckett & Shahbaz Hussain
 * @version 01/04/2014
 */
public class LightAircraft extends EnginedAircraft {
	private static final double spawnProbability = 0.005;
	private Glider attachedGlider; // if towing a glider
	
	public LightAircraft(boolean hasGlider){
		super(4, 6);
		setRandomFuel(20, 40); // sets fuel corresponding 10-20 mins (in ticks)
		
		if(hasGlider) {
			attachedGlider = new Glider();
		}
	}
	
	/**
	 * Returns the spawnProbability out of 1 that an Aircraft will spawn.
	 * @return spawnProbability
	 */
	public static double getSpawnProbability() {
		return spawnProbability;
	}
	
	/**
	 * Returns the time to take off. If a glider is attached, use the takeoff time for that.
	 */
	@Override
	public int getTimeToTakeoff() {
		if(attachedGlider == null) {
			return timeToTakeoff;
		} else {
			return attachedGlider.getTimeToTakeoff();
		}
	}
	
	/**
	 * If the LightAircraft does not have an attachedGlider then it returns a string with amount of fuel (timeLeftToFly) and the waitingTime. 
	 * Otherwise the LightAircraft has an attachedGlider, so it returns a string with amount of fuel (timeLeftToFly) and the waitingTime with attachedGlider. 
	 */
	@Override
	public String toString() {
		if(attachedGlider == null) {
			return "Light.      Fuel: " + getTimeLeftToFly() + ", Waiting time: " + getWaitingTime();
		} else {
			return "Light.      Fuel: " + getTimeLeftToFly() + ", Waiting time: " + getWaitingTime() + ", Attached: " + attachedGlider.toString();
		}
	}
	
	/**
	 * Returns true if the glider is attached. Otherwise, it returns false. 
	 * @return boolean(True or False).
	 */
	public boolean hasGlider() {
		return (attachedGlider != null); // return true if glider is attached, false if not
	}
	
	/**
	 * Removes the attachedGlider from the LightAircraft. 
	 */
	public void removeGlider() {
		attachedGlider = null;
	}
}