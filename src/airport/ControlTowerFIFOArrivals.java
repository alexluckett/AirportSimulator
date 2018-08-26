package airport;

import java.util.Comparator;

import aircraft.Aircraft;

/**
 * Sets up a default simulation using first in, first out logic.
 * 
 * As this is the default behaviour for a ControlTower, there isn't much here.
 * 
 * @author Alex Luckett
 * @version 29/04/2014
 */
public class ControlTowerFIFOArrivals extends ControlTower {

	/**
	 * Constructs a new FIFO control tower. Default behaviour of superclass, so not much to do here.
	 * @param pValue
	 * @throws Exception
	 */
	public ControlTowerFIFOArrivals(double pValue) {
		super(pValue);
	}

	@Override
	protected void runwayLogic(Aircraft currentArrival,	Aircraft currentDeparture) {
		fifoLogic(currentArrival, currentDeparture); // no special code to run here, so just revert back to standard fifo logic
	}

	@Override
	public Comparator<Aircraft> getArrivalsComparator() {
		return new WaitingTimeComparator(); // orders queue by waiting time (smallest first), effectively FIFO
	}

	@Override
	public String getSimulationType() {
		return "Waiting time (FIFO)";
	}

}
