package utils;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import aircraft.Aircraft;
import aircraft.CommercialAircraft;
import aircraft.Glider;
import aircraft.LightAircraft;

/**
 * Tests to ensure WaitingList can successfully add and remove items
 * 
 * @author Alex Luckett
 * @version 10/04/2014
 */
public class WaitingListTest {
	
	/**
	 * Adds 4 test aircraft to a waiting list
	 * @param waitingAircraft
	 */
	private void addTestAircraft(WaitingList<Aircraft> waitingAircraft) {
		waitingAircraft.add(new CommercialAircraft());
		waitingAircraft.add(new LightAircraft(false));
		waitingAircraft.add(new LightAircraft(true));
		waitingAircraft.add(new CommercialAircraft());
	}
	
	/**
	 * Ensure seed is set
	 */
	@Before
	public void setUp() {
		utils.RandomGenerator.setSeed(42); // set seed for fuel generator
	}

	/**
	 * Ensure that all 4 items have been added into WaitingList
	 */
	@Test
	public void testAddObject() {
		WaitingList<Aircraft> waitingAircraft = new WaitingList<Aircraft>(27);
		addTestAircraft(waitingAircraft);
		
		assertTrue(waitingAircraft.size() == 4);
	}
	
	/**
	 * Run simulation for 50 ticks. Ensure that after those ticks, it returns the aircraft
	 * Also ensure that the aircraft are removed from original collection
	 */
	@Test
	public void testReturnOnFinish() {
		WaitingList<Aircraft> waitingAircraft = new WaitingList<Aircraft>(50);
		addTestAircraft(waitingAircraft);
		
		for(int i = 0; i < 30; i++) {
			waitingAircraft.oneTick();
		}
		
		waitingAircraft.add(new Glider());
		
		for(int i = 0; i < 20; i++) {
			waitingAircraft.oneTick();
		}
		
		ArrayList<Aircraft> pullFinished = waitingAircraft.poll(); // pull out all objects that have finished
		ArrayList<Aircraft> peekWaiting = waitingAircraft.peekWaiting(); // peek at all those remaining
		
		assertTrue(peekWaiting.size() == 1); // should only be 1 object that hasn't finished, as it was added only 20 ticks ago
		assertTrue(peekWaiting.get(0) instanceof Glider); // make sure that it only the Glider that was added in
		
		assertTrue(pullFinished.size() == 4); // make sure that all aircraft that have been waiting 50 ticks are returned
	}

}
