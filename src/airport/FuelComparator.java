package airport;

import java.util.Comparator;

import aircraft.Aircraft;

/**
 * Compares the amount of fuel within an aircraft. Aircraft with the least 
 * amount of fuel stay at the top.
 * 
 * @author Alex Luckett
 * @version 18/04/2014
 */
public class FuelComparator implements Comparator<Aircraft> {

	@Override
	public int compare(Aircraft aircraft1, Aircraft aircraft2) {
		int fuel1 = aircraft1.getTimeLeftToFly();
		int fuel2 = aircraft2.getTimeLeftToFly();
		
		if(fuel1 < fuel2) {
			return -1;
		} else if (fuel1 > fuel2) {
			return 1;
		} else {
			return 0;
		}
	}
}
