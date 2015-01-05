package airport;

import java.util.Comparator;

import aircraft.Aircraft;
import aircraft.LightAircraft;
/**
 * Compares the time an aircraft has been waiting. Aircraft with longer
 * waiting times come out on top.
 * 
 * @author Alex Luckett
 * @version 18/04/2014
 */
public class WaitingTimeComparator implements Comparator<Aircraft> {

	@Override
	public int compare(Aircraft aircraft1, Aircraft aircraft2) {
		int wait1 = aircraft1.getWaitingTime();
		int wait2 = aircraft2.getWaitingTime();

		if(aircraft1 instanceof LightAircraft) {
			if( ((LightAircraft)aircraft1).hasGlider() ) {
				((LightAircraft)aircraft1).removeGlider(); // won't make a difference to the simulation, but cleaner
				return 1; // need lightAircraft that had Gliders to land ASAP, so push to start of queue
			}
		}

		if(wait1 < wait2) {
			return -1;
		} else if (wait1 > wait2) {
			return 1;
		} else {
			return 0;
		}
	}
}
