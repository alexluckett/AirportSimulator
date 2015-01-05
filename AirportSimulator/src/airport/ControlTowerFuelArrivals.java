package airport;

import java.util.Comparator;

import aircraft.Aircraft;

/**
 * Sets up a simulation using fuel priority logic.
 * 
 * @author Alex Luckett
 * @version 29/04/2014
 */
public class ControlTowerFuelArrivals extends ControlTower {

	public ControlTowerFuelArrivals(double pValue) {
		super(pValue);
	}

	@Override
	protected void runwayLogic(Aircraft currentArrival, Aircraft currentDeparture) {
		int arrivalWait = 0;
		int arrivalFuelLeft = Integer.MAX_VALUE; // set as maximum so departure can take off, unless set below 

		if(currentArrival != null) {
			arrivalWait   = currentArrival.getWaitingTime();
			arrivalFuelLeft = currentArrival.getTimeLeftToFly();
		}

		if(currentDeparture != null) {
			int departureWait = currentDeparture.getWaitingTime();

			// if current departure has been waiting for longer than current arrival
			//  then compare if aircraft can depart without causing next one to crash. if so, depart.
			if(departureWait > arrivalWait && currentDeparture.getTimeToTakeoff() < arrivalFuelLeft) {
				runway = departures.poll(); // pull most urgent aircraft from departures queue
				runwayBusyTime = runway.getTimeToTakeoff(); // make the runway unusable for landing time of current aircraft
				stats.addDeparture(); // increment statistics for number of departures
				utils.Debug.println("DEPARTURE: " + runway.toString(), true);
				return; // aircraft has taken off, therefore done for this tick. exit out of method.
			}
		}

		fifoLogic(currentArrival, currentDeparture); // if above logic will cause plane to crash, revert back to fifo logic
	}

	@Override
	public Comparator<Aircraft> getArrivalsComparator() {
		return new FuelComparator(); // orders queue by fuel remaining (smallest first)
	}

	@Override
	public String getSimulationType() {
		return "Fuel priority";
	}

}
