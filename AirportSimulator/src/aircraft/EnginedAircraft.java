package aircraft;

/**
 * @author Alex Luckett & Shahbaz Hussain
 * @version 01/04/2014
 */
public abstract class EnginedAircraft extends Aircraft {
	
	public EnginedAircraft(int timeToTakeoff, int timeToLand){
		  super(timeToTakeoff, timeToLand);
	}
	
	/**
	 * Sets the amount of fuel between the minimum and maximum
	 * @param minFuel - minimum amount of fuel
	 * @param maxFuel - maximum amount of fuel
	 */
	protected void setRandomFuel(int minFuel, int maxFuel) {
		timeLeftToFly = utils.RandomGenerator.getRandomIntRange(minFuel, maxFuel);
	}
	
	/**
	 * Returns the amount of fuel (timeLeftToFly).
	 * @return timeLeftToFly
	 */
	@Override
	public int getTimeLeftToFly() {
		return timeLeftToFly;
	}
}