package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;


/**
 * 
 * @author Jason Harrison
 * Provides a universal output stream which adds logging options and allows output to be consumed by event listeners
 */
public class Debug
{
	public interface DataOutListener
	{
		public void dataOut(String data);
		public void SuperDataOut(String data);
	}
	
	/**
	 * list of event listeners
	 */
	static ArrayList<DataOutListener> listeners = new ArrayList<DataOutListener>();
	
	/**
	 * 
	 * @param dataOutListener instance of event listener to add
	 */
    public static void addDataOutListener(DataOutListener dataOutListener) {listeners.add(dataOutListener);}

	/**
	 * enables/disables print and println methods 
	 */
    private static boolean enabled = false;
    
    /**
     * Class which called the debug method
     */
	private static String callingClass = "";
	
	/**
	 * tracks how many sub functions have been called to track how far up the call stack to trace to get the calling object.
	 */
	private static int moveUpStack = 0;
	
	/**
	 * file output stream
	 */
	private static PrintStream printStreamFile = null;

	public Debug() {}

	/**
	 * 
	 * @return enabled 
	 */
	public static boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * 
	 * @param enabled
	 */
	public static void setEnabled(boolean enabled)
	{
		Debug.enabled = enabled;
	}

	/**
	 * print string without any time or calling class info
	 * @param str to print
	 */
	public static void print(String str)
	{
		moveUpStack++;
		print(str, false);
	}

	/**
	 * Print string and carriage return with time stamp and calling class
	 * @param str
	 */
	public static void println(String str)
	{
		moveUpStack++;
		println(str, true);
	}

	/**
	 * Print string without timestamp and with option to include calling class
	 * @param str
	 * @param prependCallingClass
	 */
	public static void print(String str, boolean prependCallingClass)
	{
		moveUpStack++;
		print(str,prependCallingClass, false);
	}

	/**
	 * Prints string and carriage return with timestamp and with option to include calling class
	 * @param str
	 * @param prependCallingClass
	 */
	public static void println(String str, boolean prependCallingClass)
	{
		moveUpStack++;
		println(str, prependCallingClass, true);
	}

	/**
	 * Prints string with option to include timestamp and calling class
	 * @param str
	 * @param prependCallingClass
	 * @param prependTimestamp
	 */
	public static void print(String str, boolean prependCallingClass, boolean prependTimestamp)
	{
		if (enabled)
		{
			moveUpStack++;
			if (prependCallingClass){str = prependCallingClass(str);}
			if (prependTimestamp){str = prependTimeStamp(str);}
			System.out.print(str);
			sendToDataOutListeners(str);

			if(printStreamFile != null) {
				printStreamFile.print(str);
			}
		}
		moveUpStack=0;
	}

	/**
	 * Prints string and carriage return with option to include timestamp and calling class
	 * @param str
	 * @param prependCallingClass
	 * @param prependTimestamp
	 */
	public static void println(String str, boolean prependCallingClass, boolean prependTimestamp)
	{
		if (enabled)
		{
			moveUpStack++;
			if (prependCallingClass){str = prependCallingClass(str);}
			if (prependTimestamp){str = prependTimeStamp(str);}
			System.out.println(str);
			sendToDataOutListeners(str+"\n");
			
			if(printStreamFile != null) {
				printStreamFile.println(str);
			}
		}
		moveUpStack=0;
	}
	
	/**
	 * Always prints string and carriage return, even when debug output is disabled
	 * @param str
	 */
	public static void superPrintln(String str) {
			moveUpStack++;
			System.out.println(str);
			sendToSuperDataOutListeners(str+"\n");
			
			if(printStreamFile != null) {
				printStreamFile.println(str);
			}
		moveUpStack=0;
	}

	/**
	 * Always prints string, even when debug output is disabled
	 * @param str
	 */
	public static void superPrint(String str) {
		moveUpStack++;
		System.out.print(str);
		sendToSuperDataOutListeners(str);
		
		if(printStreamFile != null) {
			printStreamFile.print(str);
		}
		moveUpStack=0;
	}
	
	/**
	 * Send data to print listeners
	 * @param data
	 */
    public static void sendToDataOutListeners(String data)
    {
        for (DataOutListener dataOutListener : listeners)
        	dataOutListener.dataOut(data);
    }
    
    /**
     * Send data to superprint listeners
     * @param data
     */
    public static void sendToSuperDataOutListeners(String data)
    {
        for (DataOutListener dataOutListener : listeners)
        	dataOutListener.SuperDataOut(data);
    }

    /**
     * Write debug information to a file. Must be set BEFORE debug commands
     * ran, so it creates a new print stream for the debug to channel through.
     * 
     * @param fileLocation The text file to create.
     * @throws FileNotFoundException If file is not inside a valid location
     * 			(e.g. the containing folder does not exist).
     */
	public static void saveToFile(String fileLocation) {
		File newFile = new File(fileLocation);		
		try {
			OutputStream outputStream = new FileOutputStream(newFile);
			printStreamFile = new PrintStream(outputStream);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Could not save to file. Do you have permission?");
			e.printStackTrace();
		}
	}
	
	/**
	 * Disables saving to a file	
	 */
	public static void resetSaveToFile()
	{
		if(printStreamFile != null)
		{
			printStreamFile.flush();
			printStreamFile.close();
			printStreamFile = null;
		}

	}

	/**
	 * Add time stamp to debug message
	 * @param str The String with the original message
	 * @return Message The original message with time stamp appended
	 */
	private static String prependTimeStamp(String str)
	{
		return "" + System.currentTimeMillis() + "ms: " + str;
	}

	/**
	 * Add calling class to original message
	 * @param str The original message
	 * @return Message with calling class appended
	 */
	private static String prependCallingClass(String str)
	{
		// getStackTrace()[1] = the method which called this method
		callingClass = new Exception().getStackTrace()[1 + moveUpStack].getClassName();
		return callingClass + ": " + str;
	}


}
