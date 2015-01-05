package utils;

import java.util.Random;

/**
 * A random number generator, using a consistent approach throughout. Intended to be used
 * by multiple classes, each would therefore be using a set seed.
 * 
 * @author Jason Harrison
 * @version v1.01 01/05/2014
 */
public class RandomGenerator
{
	/**
	 * Random number generator
	 */
	private static Random random;
	
	/**
	 * seed to use for number generation
	 */
	private static long seed;
	
	/**
	 * indicates whether the seed has already been set or not
	 */
	private static boolean isSeedSet = false;
	
	public RandomGenerator(){}
	
	/**
	 * indicates whether the seed has already been set or not
	 * @return isSeedSet
	 */
	public static boolean isSeedSet() 
	{
		return isSeedSet;
	}

	/**
	 * Gets the current seed
	 * @return seed
	 */
	public static long getSeed()
	{
		return seed;
	}

	/**
	 * Sets the seed to a specific value
	 * @param seed
	 */
	public static void setSeed(long seed)
	{
		RandomGenerator.seed = seed;
		random = new Random(seed);
		isSeedSet = true;
	}

	/**
	 * Sets the seed to a pseudo-random value.
	 */
	public static void setSeedToRandom()
	{
		if(isSeedSet)
		{//this avoids duplicate seeds when called more than once a millisecond
			RandomGenerator.seed = getRandomLong() ^ System.currentTimeMillis();
		}
		else
		{
			RandomGenerator.seed = System.currentTimeMillis();
		}
		random = new Random(seed);
		isSeedSet = true;
	}

	/**
	 * Gets a random number of type double
	 * @return double A random double
	 */
	public static double getRandomDouble()
	{
		try 
		{
			return random.nextDouble(); 
		}
		catch (NullPointerException e)
		{
			throw new SeedNotSetException();
		}
	}

	/**
	 * Gets a random int from 0 to range inclusive
	 * @param range
	 * @return int A random int
	 */
	public static int getRandomInt(int range)
	{
		//in nextInt(VALUE) the VALUE is exclusive but we want inclusive values so range+1
		range++;
		try 
		{
			return random.nextInt(range); 
		}
		catch (NullPointerException e)
		{
			throw new SeedNotSetException();
		}
	}

	/**
	 * Gets a random number of type int
	 * @return int A random int
	 */
	public static int getRandomInt()
	{
		try 
		{
			return random.nextInt(); 
		}
		catch (NullPointerException e)
		{
			throw new SeedNotSetException();
		}
	}

	/**
	 * Gets a random number of type long
	 * @return long A random long
	 */
	public static long getRandomLong()
	{
		try 
		{
			return random.nextLong(); 
		}
		catch (NullPointerException e)
		{
			throw new SeedNotSetException();
		}
	}

	/**
	 * Gets a random boolean value
	 * @return boolean A random boolean
	 */
	public static boolean getRandomBoolean()
	{
		try 
		{
			return random.nextBoolean();
		}
		catch (NullPointerException e)
		{
			throw new SeedNotSetException();
		}
	}
	
	/**
	 * Gets a random number of type int from min to max inclusive
	 * @param min The smallest number that can be generated
	 * @param max The largest number that can be generated
	 * @return int An integer between the specified min and max
	 */
	public static int getRandomIntRange(int min, int max)
	{
		//in nextInt(VALUE) the VALUE is exclusive but we want inclusive values so max+1
		max++;
		
		try
		{
			return random.nextInt(max-min) + min;
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalArgumentException("Are you entering a range of zero?");
		}
		catch (NullPointerException e)
		{
			throw new SeedNotSetException();
		}
	}
	
	/**
	 * Exception raised if a random number has been requested but seed is not set
	 * @author Jason Harrison
	 *
	 */
	public static class SeedNotSetException extends RuntimeException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 164019137386846899L;

		public SeedNotSetException()
	    {
	        super("RandomGenerator.setSeed() or RandomGenerator.setSeedToRandom() should be called before requesting a random number");
	    }
	}


}
