package simulator;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import aircraft.Glider;
import aircraft.LightAircraft;
import airport.ControlStats;
import airport.ControlTower;
import airport.ControlTowerFIFOArrivals;
import airport.ControlTowerFuelArrivals;
import utils.IO;

/**
 * Responsible for setting up a simulation using given input parameters (P value, queue type, etc).
 * 
 * Once input values are taken in, either by the CLI or GUI, the Simulator sets up a control tower with the
 * appropriate type and probability of Commercial Aircraft spawning. It then runs the ControlTower for a
 * specified number of ticks.
 * 
 *  1 tick = 30 seconds of simulation
 *
 *	@author Alex Luckett & Jason Harrison
 *	@version 07/05/2014
 */
public class Simulator 
{
	public static final int ticksInAnHour = 120;
	public static final int ticksInADay = ticksInAnHour * 24;
	public static final int ticksInAWeek = ticksInADay * 7;
	public static final int ticksInAYear = ticksInADay * 365;
	
	public static final String resourceLocation = "../simulator/resources/";

	public static final double minP = 0;
	public static final double maxP = 1 - (Glider.getSpawnProbability() + LightAircraft.getSpawnProbability());
	private double probability = 0.007; // good default value

	private boolean enableGUI = true; //default is to show GUI. true = enabled, false = disabled

	private QueueTypeEnum queueType = QueueTypeEnum.FIFO;
	private int numberOfTicks = 2880;
	
	private long tickCount = 0;
	private long ticksToComplete = 0;
	
	public static final double pInterval = 0.001;

	public void setTicksToComplete(long ticksToComplete) {
		this.ticksToComplete = ticksToComplete;
	}

	public Simulator() { }
	
	/**
	 * Main method for the program. Takes in a number of input parameters and sets up a simulation using
	 * them, or it opens up the GUI if nothing is specified.
	 * @param args
	 */
	public static void main(String[] args)
	{		
		boolean auto = false;
		int timesToRun = 0;

		Simulator s = new Simulator();

		try 
		{

			for (int i = 0; i < args.length; i++)
			{
				s.setGuiStatus(false); // if this loop runs, there must be some args. disable GUI.

				switch(args[i].toUpperCase())
				{
				case "-P":
					s.setProbability(Double.parseDouble(args[i+1]));
					i++; //skip to arg after next
					break;

				case "-T":
					s.setNumTicks(Integer.parseInt(args[i+1]));
					i++; //skip to arg after next
					break;

				case "-S":
					utils.RandomGenerator.setSeed(Long.parseLong(args[i+1]));
					//utils.RandomGenerator.setSeedSet(true);
					i++; //skip to arg after next
					break;

				case "-D":
					utils.Debug.setEnabled(true);
					utils.Debug.println("Debug Mode On.");
					break;

				case "-PRIORITY":
					s.setQueueType(QueueTypeEnum.PRIORITY);
					break;

				case "-FIFO":
					s.setQueueType(QueueTypeEnum.FIFO);
					break;

				case "-AUTO":
					auto = true;
					timesToRun = Integer.parseInt(args[i+1]);
					i++; //skip to arg after next
					break;

				case "-?":

					try 
					{
						System.out.println(IO.ReadStreamToString(
								Simulator.class.getResourceAsStream(resourceLocation + "CommandLineHelpText.txt")
								));
					}
					catch (IOException e) 
					{
						//this should only ever be seen if there was a build error or the jar file is corrupt
						e.printStackTrace();
						System.out.println("Error showing help!!");
					}
					// help was requested.  all other switches should be ignored and now we should exit.
					System.exit(0);
					break;


				}
			}


		} catch (Exception e)
		{
			System.out.print("Error passing arguments");
			return; // args are faulty, exit out
		}

		if(!utils.RandomGenerator.isSeedSet())
		{
			utils.RandomGenerator.setSeed(System.currentTimeMillis());

			//if the seed is not set by this point then we could expect exceptions to be raised
			//later on when the first random number is requested 
		}

		if (s.getGuiStatus()) {
			new SimulatorGUI();
		} else {
			// start simulation if in command line mode
			if(!auto) {
				s.simulate();
			} else {
				s.simulateAuto(timesToRun);
			}
		}

	}

	public void setGuiStatus(boolean enableGUI) {
		this.enableGUI = enableGUI;
	}

	public boolean getGuiStatus() {
		return enableGUI;
	}

	/**
	 * Set the queueing system to use for simulation
	 * @param queueType
	 */
	public void setQueueType(QueueTypeEnum queueType) {
		this.queueType = queueType;
	}

	public QueueTypeEnum getQueueType() {
		return queueType;
	}

	/**
	 * Set number of ticks to run the simulation for
	 * @param numberOfTicks
	 */
	public void setNumTicks(int numberOfTicks) {
		this.numberOfTicks = numberOfTicks;
	}

	public int getNumTicks() {
		return numberOfTicks;
	}

	/**
	 * Set probability of commercial aircraft spawning
	 * @param probability
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}

	public double getProbability() {
		return numberOfTicks;
	}

	// ************************************************************************************************************
	//SIMULATE
	/**
	 * Runs the simulation for the specified number of steps. Uses a control tower with the
	 * specified probability and queue type.
	 */
	public int simulate() 
	{
		ControlTower controlTower;

		if(getQueueType() == QueueTypeEnum.PRIORITY) {
			controlTower = new ControlTowerFuelArrivals(probability);
		} else {
			controlTower = new ControlTowerFIFOArrivals(probability);
		}


		for(int i = 0; i < numberOfTicks; i++) 
		{
			controlTower.oneTick();
			tickCount++;
			double progper = 100.0/ticksToComplete*tickCount ;
			raiseProgressTrackerEvent(progper);

			if(raiseAfterTickEvent())
			{
				//CANCEL SIMULATION REQUESTED!!!
				utils.Debug.superPrintln("Simulation has been cancelled!!");
				return -1;
			}
		}
		
		raiseAfterSimulateEvent(controlTower.getStats()); 

		
		utils.Debug.superPrintln(controlTower.getStats().toString()); // once finished, print out statistics

		int crashes = controlTower.getStats().getCrashes();
		return crashes; // return number of crashes
	}

	
	
	// ************************************************************************************************************
	//SIMULATE AUTO
	public void simulateAuto(int timesToRun) {
		ArrayList<Double> goodP_allSeed = new ArrayList<Double>(); // list of all the highest P values from each simulation with a different seed
		//double pInterval = 0.001;

				double pLoopSize = ((maxP-minP)/pInterval);

		for (int i = 0; i < timesToRun; i++) {
			//the following line is redundant except to technically meet the spec of the assignment
			utils.RandomGenerator.setSeed(utils.RandomGenerator.getRandomLong());
			double goodPmax_oneSeed = 0; // stores all good P values for one seed
			
			/* 
			 * Run the simulation between minP and maxP, with the interval specified above (pInterval)
			 */
			for(double probability = minP; probability <= maxP; probability += pInterval) {
				this.probability = probability;

				/*
				 * Run the simulation for a given value of P. If that simulation has 0 crashes, add it to the good values list.
				 * Else terminate and don't add to average. 
				 */
				DecimalFormat df = new DecimalFormat("#.###");
				String pThreeSF = df.format(probability);
				int simResult = simulate(); 
				
				if(simResult == 0) { // all is well, no crashes
					if (probability > goodPmax_oneSeed) { goodPmax_oneSeed = probability; } // gets the highest result from all good simulations

					utils.Debug.superPrintln("P" + pThreeSF + " had 0 crashes. Adding to average.");
					utils.Debug.superPrintln("");
					utils.Debug.superPrintln("==========");
				} 
				
				if(simResult > 0) { // oops, we had an aircraft crash
					utils.Debug.superPrintln("P" + pThreeSF + " onwards cause crashes. Discarding.");
					utils.Debug.superPrintln("");
					utils.Debug.superPrintln("==========");
					//terminate loop as we just got a crash this should remove about 90% of the execution time
					// if starting to get crashes, then our good P value is >= the current value! No airport should have crashes.
					
					//re-adjust for progress bar
					tickCount += (pLoopSize - (probability*pInterval))*numberOfTicks;
					break;
				}
				
				if(simResult < 0){ // REQUEST TO CANCEL SIMULATION
					utils.Debug.superPrintln("Simulation has been cancelled!!");
					return;
				}
				
				setProbability(minP); // reset probability back to 0

			}

			goodP_allSeed.add(goodPmax_oneSeed); // add the highest value of P from one seed to total P values list
		}


		/*
		 * Add up all highest P values from each seed to form a total (used for average)
		 */
		double totalPgood = 0;
		for(Double currentP : goodP_allSeed) {
			totalPgood += currentP;
		}

		DecimalFormat format = new DecimalFormat("#.####");
		double finalAvgP = totalPgood/goodP_allSeed.size(); // divide total highest P values by the number to get average
		String finalAvgP_rounded = format.format(finalAvgP); // format the average to 4 decimal places
		
		/*
		 * All work done now, simply print out. Text interface will cause to print out to console, or GUI will handle it
		 */
		
		utils.Debug.superPrintln("___________________________________________");
		utils.Debug.superPrintln("AUTOMATIC SIMULATION FINISHED" + "\n");
		
		if(queueType == QueueTypeEnum.PRIORITY) {
			utils.Debug.superPrintln("Arrivals type: Fuel priority");
		} else if (queueType == QueueTypeEnum.FIFO) {
			utils.Debug.superPrintln("Arrivals type: Waiting time (FIFO)");
		}
			
		utils.Debug.superPrintln("Good P value (0 crashes): " + finalAvgP_rounded + "\n"); // average of all highest good P values is our final average for the user
		utils.Debug.superPrintln("Result averaged over " + goodP_allSeed.size() + " seeds.");
	}

	// ************************************************************************************************************
	// ************************************************************************************************************
	// EVENT CODE 

	/**
	 * 
	 * @author Jason Harrison
	 * Listener interface to allow other parts of the system to tie into the core simulation events
	 */
	public interface SimulatorListener
	{ 
		public boolean afterTick();
		public void afterSimulate(ControlStats cs);
		public void progressTracker(double percent);
	}

	ArrayList<SimulatorListener> listeners = new ArrayList<SimulatorListener>(); 
	public void addSimulatorListener(SimulatorListener SimulatorListener){	listeners.add(SimulatorListener);	}

	public boolean raiseAfterTickEvent()
	{ 
		for (SimulatorListener SimulatorListener : listeners)
		{
			if(SimulatorListener.afterTick()){return true;}
		}
		return false;
	}

	public void raiseAfterSimulateEvent(ControlStats cs)
	{ 
		for (SimulatorListener SimulatorListener : listeners)
		{
			SimulatorListener.afterSimulate(cs);
		}
		
	}
	public void raiseProgressTrackerEvent(double percent)
	{ 
		for (SimulatorListener SimulatorListener : listeners)
		{
			SimulatorListener.progressTracker(percent);
		}
	}

}