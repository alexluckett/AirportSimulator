package aircraft;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the aircraft hierarchy
 * 
 * @author Shahbaz Hussain
 * @version 04/05/2014
 */
public class AircraftTest {
	CommercialAircraft com1; 
	Glider g1;
	LightAircraft light1;
	LightAircraft light2;
	
	public AircraftTest() { 
		utils.RandomGenerator.setSeed(42);
		com1 = new CommercialAircraft(); // engined aircraft
		g1 = new Glider();
		light1 = new LightAircraft(false); // engined aircraft
		light2 = new LightAircraft(true); //engined aircraft with a glider 
	}
	
	/**
	 * Before anything is created, we need to set the seed (as the planes use it for their fuel generation).
	 */
	@Before
	public void setup(){
		utils.RandomGenerator.setSeed(42);
	}
	
	/**
	 * Tests each aircraft is created with the correct amount of fuel
	 */
	@Test
	public void testCreationFuel() {
		CommercialAircraft comAir = new CommercialAircraft();
		LightAircraft lightAir = new LightAircraft(false);
		
		assertTrue(40 < comAir.getTimeLeftToFly() && comAir.getTimeLeftToFly() < 80); // commercial aircraft should have a fuel value between 40-80
		assertTrue(20 < lightAir.getTimeLeftToFly() && lightAir.getTimeLeftToFly() < 40); // light aircraft should have a fuel value between 20-40
	}
	
	/**
	 * Tests that when a glider is attached to a light aircraft, the takeoff time is that of the glider (since they're longer)
	 */
	@Test
	public void testGliderAttach() {
		LightAircraft lightAir = new LightAircraft(true);
		Glider gliderTest = new Glider();
		
		// ensure that when glider is attached, lightaircraft uses those times for takeoff	
		assertTrue(lightAir.hasGlider());
		assertTrue(lightAir.getTimeToTakeoff() == gliderTest.getTimeToTakeoff());
		
		// remove the glider
		lightAir.removeGlider();
		
		// lightaircraft should now being using its own times, not that of the attached glider
		assertFalse(lightAir.hasGlider());
		assertTrue(lightAir.getTimeToTakeoff() != gliderTest.getTimeToTakeoff());
	}
	
	/**
	 * Tests whether the user could set the time left to fly
	 */
	@Test
	public void testTimeLeftToFly() {
		com1.setTimeLeftToFly(10);
		assertEquals(com1.getTimeLeftToFly(), 10);
		
		g1.setTimeLeftToFly(20);
		assertEquals(g1.getTimeLeftToFly(), 2147483647); 
		
		light1.setTimeLeftToFly(30);
		assertEquals(light1.getTimeLeftToFly(), 30);
	}

	/**
	 * Tests the constructor of all planes
	 */
	@Test
	public void testConstructor() { 
		assertEquals(com1.timeToTakeoff, 4);
		assertEquals(com1.timeToLand, 6); 
		
		assertEquals(light1.timeToLand, 6);
		assertEquals(light1.timeToTakeoff, 4);
		
		assertEquals(g1.timeToLand, 8);
		assertEquals(g1.timeToTakeoff, 6); 

	}
	
	/**
	 * Tests some of the specific things assigned to the LightAircraft
	 */
	@Test
	public void testLightAircraft() {
		assertEquals(light1.hasGlider(), false); 
		assertEquals(light2.hasGlider(), true);
		
		assertEquals(light1.getTimeToTakeoff(), 4);
		assertEquals(light2.getTimeToTakeoff(), 6); 
	}
	
	/**
	 * Tests the waitingTime methods in all the plane classes
	 */
	@Test
	public void testWaitingTime(){
		assertEquals(com1.getWaitingTime(), 0);
		com1.incrementWaitingTime();
		assertEquals(com1.getWaitingTime(), 1);
		
		assertEquals(light1.getWaitingTime(), 0);
		light1.incrementWaitingTime();
		assertEquals(light1.getWaitingTime(), 1);
		
		assertEquals(g1.getWaitingTime(), 0);
		g1.incrementWaitingTime();
		assertEquals(g1.getWaitingTime(), 1);
	}
	
	/**
	 * Tests the fuel method in all the plane classes
	 */
	@Test
	public void testFuel() {
		com1.setTimeLeftToFly(10);
		assertEquals(com1.getTimeLeftToFly(), 10);
		com1.decrementFuel();
		assertEquals(com1.getTimeLeftToFly(), 9);
		
		light1.setTimeLeftToFly(20);
		assertEquals(light1.getTimeLeftToFly(), 20);
		light1.decrementFuel();
		assertEquals(light1.getTimeLeftToFly(), 19);
		
		g1.setTimeLeftToFly(1000);
		assertEquals(g1.getTimeLeftToFly(), 2147483647); // always should return the Max Value
		g1.decrementFuel();
		assertEquals(g1.getTimeLeftToFly(), 2147483647); // always should return the Max Value
	}
	
}