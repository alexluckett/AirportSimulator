package airport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import utils.WaitingList;
import aircraft.*;

/**
 * Sets up a simulation for a given value of P (commercial aircraft probability of arrival).
 * Default behavior is a fifo system for the arrivals queue. Subclasses need to be made to implement
 * runway logic for different queue types.
 * 
 * In order to progress the simulation, oneTick() method must be called per tick.
 * 
 * @author Alex Luckett
 * @version 29/04/2014
 */
public abstract class ControlTower {
	protected PriorityQueue<Aircraft> arrivals;
	protected PriorityQueue<Aircraft> departures; // will use FIFO comparator, for consistency
	private WaitingList<Aircraft> repairYard;

	protected Aircraft runway;
	protected int runwayBusyTime;

	protected ControlStats stats;

	/**
	 * Constructs a new Control Tower. Default logic is FIFO. 
	 * 
	 * @param pValue Value of p (commercial aircraft probability)
	 */
	public ControlTower(double pValue) {
		arrivals = new PriorityQueue<Aircraft>(1, getArrivalsComparator()); // order arrivals by waiting time (effectively FIFO)
		departures = new PriorityQueue<Aircraft>(1, new WaitingTimeComparator()); // order departures by waiting time (effectively FIFO)
		repairYard = new WaitingList<Aircraft>(120); // store aircraft for 120 ticks (1 hour)
		
		runway = null;
		runwayBusyTime = 0;
		
		stats = new ControlStats(pValue, getSimulationType());
		utils.Debug.println(getSimulationType() + " queue enabled");
		
		CommercialAircraft.setProbability(pValue);
	}
	
	/**
	 * Used to set the order the arrivals queue
	 * @return Comparator<Aircraft> to order the arrivals queue
	 */
	public abstract Comparator<Aircraft> getArrivalsComparator();

	/**
	 * Runs one tick of each simulation component.
	 */
	public void oneTick() {
		checkCrash();

		arrivalsOneTick();
		departuresOneTick();
		repairYardOneTick();
		runwayOneTick();
	}

	/**
	 * Increase the waiting time of all aircraft if inside the arrivals
	 * and departure queues. Does not need to take into account runway/repair yard,
	 * as they are not waiting to use runway.
	 * <b>Only really should be used for testing - not efficient to have multiple loops for different actions</b>
	 */
	public void increaseWaitingTime() {
		for(Aircraft aircraft : arrivals) {
			aircraft.incrementWaitingTime();
		}

		for(Aircraft aircraft : departures) {
			aircraft.incrementWaitingTime();
		}
	}

	/**
	 * Decrease the fuel of aircraft. Only for the arrivals queue, as they're
	 * the only ones using up fuel (when flying).
	 * 
	 * <b>Only really should be used for testing - not efficient to have multiple loops for different actions.</b>
	 */
	public void decreaseFuel() {
		for(Aircraft aircraft : arrivals) {
			aircraft.decrementFuel();
		}
	}

	/* ##########################################
	   # RUNWAY HELPER METHODS              #
	   ########################################## */

	/**
	 * Runs the runway each tick. Will choose which aircraft can arrive and depart,
	 * depending on which control type is being used for the arrivals queue.
	 */
	public void runwayOneTick() {
		Aircraft currentArrival = arrivals.peek();
		Aircraft currentDeparture = departures.peek();
		runwayBusyTime--;

		// check if runway empty and not waiting, then collect stats
		if(runwayBusyTime <= 0 && runway != null) {
			stats.addWaitingTime(runway.getWaitingTime()); // if runway holds a plane, add it to the waiting time
		}

		if(runwayBusyTime <= 0) { // if the runway is no longer busy with an aircraft taking off
			runwayLogic(currentArrival, currentDeparture);
		} else {
			utils.Debug.println("Runway busy for " + runwayBusyTime, true);
		}
	}
	
	/**
	 * Runway logic to be used BEFORE the standard first in, first out logic.
	 * 
	 * @param currentArrival Current aircraft waiting to arrive
	 * @param currentDeparture Current aircraft waiting to depart
	 */
	protected abstract void runwayLogic(Aircraft currentArrival, Aircraft currentDeparture);

	/**
	 * Standard, fifo logic for a runway. Operates on a first in, first out basis.
	 *  
	 * @param currentArrival Current aircraft waiting to arrive
	 * @param currentDeparture Current aircraft waiting to depart
	 */
	protected void fifoLogic(Aircraft currentArrival, Aircraft currentDeparture) {
		if(currentArrival != null) {
			runway = arrivals.poll(); // pull first in arrivals queue out
			runwayBusyTime = runway.getTimeToLand(); // make runway busy for the current aircraft's landing time
			stats.addLanding(); // increment  number of arrivals statistic
			utils.Debug.println("ARRIVAL: " + runway.toString(), true); 
		} else if (currentDeparture != null) {
			runway = departures.poll(); // pull first in departures queue out
			runwayBusyTime = runway.getTimeToTakeoff(); // make runway busy for the current aircraft's takeoff time
			stats.addDeparture(); // increment number of departures statistic
			utils.Debug.println("DEPARTURE: " + runway.toString(), true);
			
			/*
			 * If the current aircraft departing from the runway has a glider, ensure that the light aircraft lands again
			 */
			if(runway instanceof LightAircraft) {
				if(((LightAircraft)runway).hasGlider()) {
					arrivals.add(runway);
				}
			}
			
		} else {
			runway = null; // nothing left in any queue. discard aircraft on runway when time runs out.
		}
	}

	public Aircraft getRunwayAircraft() {
		return runway;
	}


	
	/* ##########################################
	   # DEPARTURES HELPER METHODS              #
	   ########################################## */

	public void departuresOneTick() {
		generateDeparturesOneTick();

		for(Aircraft aircraft : departures) {
			aircraft.incrementWaitingTime();
		}
	}

	/**
	 * Generates the departures for one tick. Checks probability of each aircraft spawning,
	 * spawns them if the probability is hit. 
	 */
	public void generateDeparturesOneTick() {
		double spawnProb = utils.RandomGenerator.getRandomDouble();

		double gliderProb     = Glider.getSpawnProbability();
		double lightProb      = gliderProb + LightAircraft.getSpawnProbability();
		double commercialProb = lightProb + CommercialAircraft.getSpawnProbability();

		if(spawnProb <= gliderProb) {
			departures.add(new LightAircraft(true)); // glider needs light aircraft to take off. spawn light aircraft with glider attached.
			utils.Debug.println("New LightAircraft (with Glider) in departures", true);
		} else if (spawnProb <= lightProb) {
			departures.add(new LightAircraft(false)); // light aircraft with no glider
			utils.Debug.println("New LightAircraft in departures", true);
		} else if (spawnProb <= commercialProb) {
			departures.add(new CommercialAircraft());
			utils.Debug.println("New CommercialAircraft in departures", true);
		}
	}

	/**
	 * Returns the departures queue
	 * 
	 * @return LinkedList<Aircraft> The departures queue 
	 */
	public PriorityQueue<Aircraft> getDepartures() {
		return departures;
	}



	/* ##########################################
	   # ARRIVALS HELPER METHODS                #
	   ########################################## */

	public void arrivalsOneTick() {
		generateArrivalsOneTick();

		for(Aircraft aircraft : arrivals) {
			aircraft.incrementWaitingTime();
			aircraft.decrementFuel(); // only need to decrease fuel whilst aircraft in use (in the air)
		}
	}

	/**
	 * Generates the arrivals for one tick. Checks probability of each aircraft spawning,
	 * spawns them if the probability is hit. 
	 */
	public void generateArrivalsOneTick() {
		double spawnProb = utils.RandomGenerator.getRandomDouble();

		double gliderProb     = Glider.getSpawnProbability();
		double lightProb      = gliderProb + LightAircraft.getSpawnProbability();
		double commercialProb = lightProb + CommercialAircraft.getSpawnProbability();

		if(spawnProb <= gliderProb) {
			arrivals.add(new Glider()); // gliders can arrive if in the air, so create it
			utils.Debug.println("New Glider in arrivals", true);
		} else if (spawnProb <= lightProb) {
			arrivals.add(new LightAircraft(false)); // light aircraft with no glider
			utils.Debug.println("New LightAircraft in arrivals", true);
		} else if (spawnProb <= commercialProb) {
			arrivals.add(new CommercialAircraft());
			utils.Debug.println("New CommercialAircraft in arrivals", true);
		}
	}

	/**
	 * Returns the arrivals queue
	 * 
	 * @return PriorityQueue<Aircraft> The arrivals queue 
	 */
	public PriorityQueue<Aircraft> getArrivals() {
		return arrivals;
	}

	/**
	 * Iterates through the arrivals queue. Checks if a crash has happened,
	 * records it in ControlStats. 
	 * Logic done here using iterator, as can't loop through queue above
	 * and remove at same time -> ConcurrentModificationException
	 */
	private void checkCrash() {
		Iterator<Aircraft> iter = arrivals.iterator();
		while (iter.hasNext()) {
			Aircraft currentAircraft = iter.next();
			if(currentAircraft.getTimeLeftToFly() <= 0) {
				utils.Debug.println("****************************************************************************************");
				utils.Debug.println("* CRASH HAS OCCURED: " + currentAircraft.toString());
				utils.Debug.println("****************************************************************************************");
				stats.addCrash();			
				iter.remove();
			}
		}
	}


	/* ##########################################
	   # REPAIR YARD HELPER METHODS             #
	   ########################################## */

	/**
	 * Runs the repair yard. Calls code to check if breakdown occurs (checkBreakdown())
	 * then runs a tick for it (increasing the waiting time). If aircraft have been waiting
	 * for the specified wait time, then add them back into departures.
	 */
	public void repairYardOneTick() {
		checkBreakdown();
		repairYard.oneTick();

		// if the simulation finishes, the aircraft still inside the repair yard won't be accounted for
		// this is normal behaviour, as the coursework spec doesn't specify otherwise
		if(repairYard.peek().size() != 0) {
			ArrayList<Aircraft> repairedAircraft = repairYard.poll(); // DON'T FORGET THIS DELETES THE STUFF FROM REPAIR YARD!!! can't be accessed later
			for(Aircraft currentAircraft : repairedAircraft) {
				currentAircraft.resetWaitingTime(); // because broke down, don't want to inflate end statistics with anomalous results
				departures.add(currentAircraft); // add the fixed aircraft to the back of the departures queue
				utils.Debug.println("Aircraft repaired, added to back of departures", true);
			}
		}
	}

	/**
	 * Iterates through the departures queue. If generated probability is
	 * less than or equal to probability of breakdown then add into the repair yard
	 */
	private void checkBreakdown() {
		Iterator<Aircraft> iter = departures.iterator();
		while (iter.hasNext()) {
			double rand = utils.RandomGenerator.getRandomDouble();

			Aircraft test = iter.next();
			if(rand <= Aircraft.getBreakdownProbability()) {
				repairYard.add(test);
				iter.remove();
			}
		}
	}

	/**
	 * Peaks at the finished waiting aircraft within the repair yard. This does
	 * not remove any aircraft from that collection.
	 * 
	 * @return ArrayList<Aircraft> containing the current aircraft finished from repair yard
	 */
	public ArrayList<Aircraft> peekRepairYardFinished() {
		return repairYard.peek();
	}

	/**
	 * Peeks at the current aircraft waiting within the repair yard, that not yet finished waiting
	 * Does NOT remove them from repair yard.
	 * 
	 * @return ArrayList<Aircraft> containing the current aircraft within the repair yard
	 */
	public ArrayList<Aircraft> peekRepairYardWaiting() {
		return repairYard.peekWaiting();
	}

	/* ###########################################
	   # STATISTICS HELPER METHODS               #
	   ########################################### */

	/**
	 * Returns the waiting time of the aircraft on the runway
	 * Useful when making the end statistics.
	 * 
	 * @return waitingTime The waiting time of the aircraft on the runway
	 */
	public int getWaitingTime() {
		return runway.getWaitingTime();
	}

	public ControlStats getStats() {
		return stats;
	}
	
	/**
	 * Returns a String containing the type of simulation currently running.
	 * EG: If currently using FIFO arrivals queue, return "FIFO"
	 * 
	 * @return String representing the simulation type (e.g. Waiting Time (FIFO)
	 */
	public abstract String getSimulationType();
	
}