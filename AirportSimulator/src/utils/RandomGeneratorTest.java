package utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * To validate setting seeds and correct ranging on getRandomIntRange()
 * @author Jason Harrison
 *
 */
public class RandomGeneratorTest {

	/**
	 * Shows that sequences are repeated based on a fixed seed value
	 */
	@Test
	public void setSeedTest()
	{
		int list1[] = new int[100];
		// any long value is good for this test.
		long seed = -123;
		
		RandomGenerator.setSeed(seed);
		for (int i = 0; i < list1.length; i++)
		{
			list1[i] = RandomGenerator.getRandomInt();
		}

		RandomGenerator.setSeed(seed);
		for (int i = 0; i < list1.length; i++)
		{
			assertEquals(list1[i],RandomGenerator.getRandomInt());
		}

		
	}
	/**
	 * Demonstrates that the seed and in turn the numbers generated are in fact random
	 * this test might fail even when all is well but the chance of that happening is roughly:
	 * (ABS(Long.MIN_VALUE) + Long.MAX_VALUE)/1000 = 18.4x10^15
	 * 
	 * 18.4x10^15 : 1
	 */
	@Test
	public void setSeedToRandomTest()
	{
		
		for (int i = 0; i < 1000; i++)
		{
			RandomGenerator.setSeedToRandom();
			long seedA = RandomGenerator.getSeed();
			long valueA = RandomGenerator.getRandomLong();

			RandomGenerator.setSeedToRandom();
			long seedB = RandomGenerator.getSeed();
			long valueB = RandomGenerator.getRandomLong();
			assertNotEquals(seedA, seedB);
			assertNotEquals(valueA, valueB);
		}
	}
	
	/**
	 * Shows that the getRandomIntRange() method min value is inclusive
	 */
	@Test
	public void getRandomIntRange_MinIsInclusiveTest()
	{
		RandomGenerator.setSeedToRandom();
		int min = 1;
		int max = 100;

		int i=-1;
		while(i != min)
		{
			System.out.println(""+ i + " != " + min);
			i = RandomGenerator.getRandomIntRange(min, max);
		}
		System.out.println("Yay "+ i + " == " + min);
		assertTrue(true);	
	}
	
	/**
	 * Shows that the getRandomIntRange() method max value is inclusive
	 */
	@Test
	public void getRandomIntRange_MaxIsInclusiveTest()
	{
		RandomGenerator.setSeedToRandom();
		int min = 1;
		int max = 100;
		
		int i=-1;
		while(i != max)
		{
			System.out.println(""+ i + " != " + max);
			i = RandomGenerator.getRandomIntRange(min, max);
		}
		System.out.println("Yay "+ i + " == " + max);
		assertTrue(true);
	}
	
	/**
	 * Shows that the getRandomIntRange() method does not generate numbers out of range
	 * we expect this test to fail once every 100,000 times 
	 */
	@Test
	public void getRandomIntRange_OverRangeTest()
	{
		RandomGenerator.setSeedToRandom();
		int min = 1;
		int max = 10;
		
		int i = -1;
		for (int n = 0; n < 1000000; n++)
		{
			i = RandomGenerator.getRandomIntRange(min, max);
			if(i>max || i<min)
			{
				assertTrue(false);
			}
			System.out.println("" + i + " is in range of " + min + " - "+ max);
		}
		assertTrue(true);
	}
}
