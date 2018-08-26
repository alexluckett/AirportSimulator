package airport;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

import org.junit.Before;
import org.junit.Test;

import aircraft.*;

/**
 * Test for the ControlTower and subclasses.
 * 
 * @author Alex Luckett
 */
public class ControlTowerTest {
	private int stepsToRun = 2880; // 24 hours
	private double pValue = 0.007; // decent value to test with. shouldn't matter what value though.

	@Before
	public void setSeed() {
		utils.RandomGenerator.setSeed(42);
		assertEquals(utils.RandomGenerator.getSeed(), 42);
	}

	/**
	 * Helper method to generate planes. Will run for specified number of 'stepsToRun'
	 * @param controltower The control tower to generate the planes for
	 */
	private void generatePlanes(ControlTower controltower) {
		utils.Debug.setEnabled(false);
		for (int i = 0; i < stepsToRun; i++) {
			controltower.generateArrivalsOneTick();
			controltower.generateDeparturesOneTick();
		}
	}

	/**
	 * Ensures that the correct amount of planes have been generated. 
	 */
	@Test
	public void testGeneratePlanes() {
		utils.Debug.setEnabled(false);
		ControlTower testTower = new ControlTowerFIFOArrivals(0.007); // generation of aircraft is same no matter what queue

		generatePlanes(testTower);

		PriorityQueue<Aircraft> testArrivals = testTower.getArrivals();
		PriorityQueue<Aircraft> testDepartures = testTower.getDepartures();

		double aircraftNumber = (stepsToRun * Glider.getSpawnProbability()) + 
				(stepsToRun * LightAircraft.getSpawnProbability()) +
				(stepsToRun * CommercialAircraft.getSpawnProbability());
		
		int minAircraft = (int) aircraftNumber - ((int) aircraftNumber / 4);
		// division by 4 is just a bit of leeway to take into account random nature of aircraft generation
		int maxAircraft = (int) aircraftNumber + ((int) aircraftNumber / 4);
		// division by 4 is just a bit of leeway to take into account random nature of aircraft generation

		/*
		 * Ensure amount of aircraft are between the minimum and maximum for both queues
		 */
		assertTrue(minAircraft < testArrivals.size() && testArrivals.size() < maxAircraft);
		assertTrue(minAircraft < testDepartures.size() && testDepartures.size() < maxAircraft);
	}

	/**
	 * Tests to ensure that when PriorityQueue is used, that the
	 * aircraft are ordered by amount of fuel left. Lowest fuel first.
	 * @throws Exception 
	 */
	@Test
	public void testArrivalOrderFuel() {
		utils.Debug.setEnabled(false);
		ControlTower testTower = new ControlTowerFuelArrivals(pValue);
		PriorityQueue<Aircraft> testArrivals = testTower.getArrivals();

		generatePlanes(testTower);

		int lastFuel = 0;
		while(testArrivals.peek() != null) {
			Aircraft currentAircraft = testArrivals.poll();
			/*
			 * As queues are sorted by the amount of fuel, ensure that every entity has more fuel than the last
			 */
			assertTrue(currentAircraft.getTimeLeftToFly() >= lastFuel);
			lastFuel = currentAircraft.getTimeLeftToFly();
		}
	}

	/**
	 * Tests to ensure that when FIFO (waiting time) queue is used, that the
	 * aircraft are ordered by amount of waiting time. Longest wait first.
	 * @throws Exception 
	 */
	@Test
	public void testArrivalOrderFIFO() {
		utils.Debug.setEnabled(false);
		ControlTower testTower = new ControlTowerFIFOArrivals(pValue);
		PriorityQueue<Aircraft> testArrivals = testTower.getArrivals();

		generatePlanes(testTower);

		int lastWait = 0;

		while(testArrivals.peek() != null) {
			Aircraft currentAircraft = testArrivals.poll();			
			/*
			 * As queues are sorted by the waiting time,ensure that every entity has a smaller waiting time than the last.
			 */
			assertTrue(currentAircraft.getWaitingTime() >= lastWait);
			lastWait = currentAircraft.getWaitingTime();
		}
	}

	/**
	 * Ensure that the waiting time for each aircraft in the arrivals and
	 * departures queue increments over time.
	 * @throws Exception 
	 */
	@Test
	public void testIncreaseWaitingTime() {
		utils.Debug.setEnabled(false);
		ControlTower testTower = new ControlTowerFIFOArrivals(pValue);
		generatePlanes(testTower);

		for(int i = 0; i < stepsToRun; i++) {
			testTower.increaseWaitingTime();
		}

		Collection<Aircraft> testArrivals = testTower.getArrivals();
		Collection<Aircraft> testDepartures = testTower.getDepartures();

		for(Aircraft aircraft : testArrivals) {
			assertTrue(aircraft.getWaitingTime() == stepsToRun);
		}

		for(Aircraft aircraft : testDepartures) { 
			assertTrue(aircraft.getWaitingTime() == stepsToRun);
		}
	}

	@Test
	public void testDecreaseFuel() {
		utils.Debug.setEnabled(false);
		ControlTower testTower = new ControlTowerFIFOArrivals(pValue);
		generatePlanes(testTower);

		Collection<Aircraft> testArrivals = testTower.getArrivals();

		// record fuel before running simulation
		ArrayList<Integer> beforeFuel = new ArrayList<Integer>();

		for(Aircraft aircraft : testArrivals) {
			if(!(aircraft instanceof Glider)) { // glider fuel never decreases, as max value is always returned. therefore do not include in tests.
				beforeFuel.add(aircraft.getTimeLeftToFly());
			}
		}

		// decrease fuel for 24 hours
		for(int i = 0; i < stepsToRun; i++) {
			testTower.decreaseFuel();
		}

		// record fuel after running simulation		
		ArrayList<Integer> afterFuel = new ArrayList<Integer>();

		for(Aircraft aircraft : testArrivals) {
			if(!(aircraft instanceof Glider)) {
				afterFuel.add(aircraft.getTimeLeftToFly()); // glider fuel never decreases, as max value is always returned. therefore do not include in tests.
			}
		}

		// compare before and after fuel
		assertEquals(beforeFuel.size(), afterFuel.size());

		for(int i = 0; i < beforeFuel.size(); i++) {
			int currentBeforeFuel = beforeFuel.get(i);
			int currentAfterFuel = afterFuel.get(i);

			assertEquals(currentAfterFuel, currentBeforeFuel - stepsToRun); // fuel should be reduced by the amount of ticks the simulation has ran
		}
	}

	@Test
	public void testRepairYard() {
		utils.Debug.setEnabled(false);
		ControlTower testTower = new ControlTowerFIFOArrivals(pValue);
		generatePlanes(testTower);

		Object[] beforeAircraft = testTower.getDepartures().toArray(); // store before departures

		// run repair yard for specified time
		for(int i = 0; i < stepsToRun; i++) {
			testTower.repairYardOneTick();
		}
		
		/*
		 * All aircraft that have been repaired should now be back in the departures queue,
		 * with the exception of those that have not been repaired yet (peekWaiting)
		 */
		assertTrue(beforeAircraft.length == (testTower.getDepartures().size() + testTower.peekRepairYardWaiting().size()));
	}

}
