package utils;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;



/**
 * Provides a validated textbox for the input of doubles and longs.  Invalid character input is not allowed.
 * Invalid number formats, ranges and invalid characters from paste operations are highlighted. 
 * 
 * @author Jason Harrison
 * @version v1.00 01/05/2014
 */
public class NumberTextBox extends JTextField 
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7200697353116686662L;

	//**************************************************************************************************
	//**** Custom Event Code
	
	/**
	 * Interface definition for data validated listener
	 * @author Jason Harrison
	 *
	 */
	public interface DataValidatedListener {public void dataValidated();}

	/**
	 * List of listeners
	 */
    ArrayList<DataValidatedListener> listeners = new ArrayList<DataValidatedListener>();

    /**
     * Add listener to the dataValidedted event 
     */
    public void addDataValidatedListener(DataValidatedListener dataValidatedListener) {listeners.add(dataValidatedListener);}
    
    /**
     * Remove listener from the dataValidedted event 
     */
    public void removeDataValidatedListener(DataValidatedListener dataValidatedListener) {listeners.remove(dataValidatedListener);}
	
    
	//**************************************************************************************************
	//**** Fields
    
	/**
	 * Type of number, numberType.DOUBLE or numberType.LONG 
	 */
	private NumberType numType = NumberType.DOUBLE;
	
	/**
	 * Minimum value for floating point compare
	 */
	private double minValueDouble = Double.MIN_VALUE;

	/**
	 *  Maximum value for floating point compare
	 */
	private double maxValueDouble = Double.MAX_VALUE;
	
	/**
	 * Minimum value for integer compare
	 */
	private long minValueLong = Long.MIN_VALUE;
	
	/**
	 *  Maximum value for integer compare
	 */
	private long maxValueLong = Long.MAX_VALUE;

	/**
	 *  Decides whether the text to be evaluated is inclusive or exclusive of {@link #minValueDouble} 
	 */
	private boolean isMinValueDoubleInclusive = true;
	
	/**
	 *  Decides whether the text to be evaluated is inclusive or exclusive of {@link #maxValueDouble} 
	 */
	private boolean isMaxValueDoubleInclusive = true;
	
	/**
	 * decide whether the {@link #minValueDouble} is evaluated
	 */
	private boolean isMinValueValidated = false;
	
	/**
	 * decide whether the {@link #maxValueDouble} is evaluated
	 */
	private boolean isMaxValueValidated = false;
	
	/**
	 *  The colour of the text field when the value validates
	 */
	private Color goodColour = new Color(0xFFFFFF);
	
	/**
	 *  The colour of the text field when the value does not pass validation
	 */
	private Color badColour = new Color(0xFFAAAA);

	/**
	 * Shows whether the text evaluates to a valid number 
	 */
	private boolean isNumberGood = false;
	
	//getters and setters
	
	/**
	 * Gets the minimum value supported by the NumberTextBox
	 * @return minValueLong
	 */
	public long getMinValueLong() {
		return minValueLong;
	}

	/**
	 * Sets the minimum value
	 * @param minValueLong
	 */
	public void setMinValueLong(long minValueLong) {
		this.minValueLong = minValueLong;
	}

	/**
	 * Returns the maximum value as a long
	 * @return Long Max value as a long
	 */
	public long getMaxValueLong() {
		return maxValueLong;
	}

	/**
	 * 
	 * @param maxValueLong
	 */
	public void setMaxValueLong(long maxValueLong) {
		this.maxValueLong = maxValueLong;
	}
	
	/**
	 * @return numType
	 */
	public NumberType getNumberType() {return numType;}
	
	/**
	 * @param numberType
	 */
	public void setNumberType(NumberType numberType) {this.numType = numberType;}

	/**
	 * @return minValueDouble
	 * @see #minValueDouble minValueDouble
	 * @see #isMinValueDoubleInclusive isMinValueDoubleInclusive
	 * @see #isMinValueValidated isMinValueValidated
	 */
	public double getMinValueDouble() {return minValueDouble;}
	
	/**
	 * @param minValue
	 * @see #minValueDouble minValueDouble
	 * @see #isMinValueDoubleInclusive isMinValueDoubleInclusive
	 * @see #isMinValueValidated isMinValueValidated
	 */
	public void setMinValueDouble(double minValue) {this.minValueDouble = minValue;}
	
	/**
	 * @return maxValueDouble
	 * @see #maxValueDouble maxValueDouble
	 * @see #isMaxValueDoubleInclusive isMaxValueDoubleInclusive
	 * @see #isMaxValueValidated isMaxValueValidated
	 */
	public double getMaxValueDouble() {return maxValueDouble;}
	
	/**
	 * @param maxValue
	 * @see #maxValueDouble maxValueDouble
	 * @see #isMaxValueDoubleInclusive isMaxValueDoubleInclusive
	 * @see #isMaxValueValidated isMaxValueValidated
	 */
	public void setMaxValueDouble(double maxValue) {this.maxValueDouble = maxValue;}
	
	/**
	 * @return isMinValueDoubleInclusive
	 */
	public boolean isMinValueDoubleInclusive() {return isMinValueDoubleInclusive;}
	
	/**
	 * @param isMinValueDoubleInclusive
	 */
	public void setMinValueDoubleInclusive(boolean isMinValueDoubleInclusive) {this.isMinValueDoubleInclusive = isMinValueDoubleInclusive;}
	
	/**
	 * @return isMaxValueDoubleInclusive 
	 */
	public boolean isMaxValueDoubleInclusive() {return isMaxValueDoubleInclusive;}
	
	/**
	 * @param isMaxValueDoubleInclusive
	 */
	public void setMaxValueDoubleInclusive(boolean isMaxValueDoubleInclusive) {this.isMaxValueDoubleInclusive = isMaxValueDoubleInclusive;}
	
	/**
	 * @return isMinValueValidated
	 */
	public boolean isMinValueValidated() {return isMinValueValidated;}
	
	/**
	 * @param isMinValueValidated
	 */
	public void setMinValueValidated(boolean isMinValueValidated) {this.isMinValueValidated = isMinValueValidated;}
	
	/**
	 * @return isMaxValueValidated
	 */
	public boolean isMaxValueValidated() {return isMaxValueValidated;}
	
	/**
	 * @param isMaxValueValidated
	 */
	public void setMaxValueValidated(boolean isMaxValueValidated) {this.isMaxValueValidated = isMaxValueValidated;}
	
	/**
	 * @return goodColour
	 */
	public Color getGoodColour() {return goodColour;}
	
	/**
	 * @param goodColour
	 */
	public void setGoodColour(Color goodColour) {this.goodColour = goodColour;}
	
	/**

	 * @return badColour
	 */
	public Color getBadColour() {return badColour;}

	/**
	 * @param badColour
	 */
	public void setBadColour(Color badColour) {this.badColour = badColour;}
	
	/**

	 * @return isNumberGood
	 */
	public boolean isNumberGood() {return isNumberGood;}

	
	/**
	 * Creates a NumberTextBox
	 * 
	 * @param text Contents of text box
	 * @param minValue is the lowest value
	 * @param isMinInclusive specifies whether the allowable minimum value should be inclusive or exclusive of the minValue
	 * @param maxValue is the highest value
	 * @param isMaxInclusive specifies whether the allowable maximum value should be inclusive or exclusive of the maxValue
	 */
	public NumberTextBox(String text, double minValue, boolean isMinInclusive, double maxValue, boolean isMaxInclusive)
	{ //floating Point
		this();
		super.setText(text);

		this.numType = NumberType.DOUBLE;
		this.minValueDouble = minValue;
		this.isMaxValueDoubleInclusive = isMinInclusive;
		this.maxValueDouble = maxValue;
		this.isMaxValueDoubleInclusive = isMaxInclusive;
		this.isMinValueValidated = true;
		this.isMaxValueValidated = true;
		
	}
	/**
	 * Creates a NumberTextBox
	 * 
	 * @param text Contents of text box
	 * @param minValue is the lowest value
	 * @param maxValue is the highest value
	 */
	public NumberTextBox(String text, long minValue, long maxValue)
	{ //integer
		this();
		
		//for integers we always want the check values to be inclusive
		this.isMinValueDoubleInclusive = true;
		this.isMaxValueDoubleInclusive = true;
		
		super.setText(text);
		
		this.numType = NumberType.LONG;
		this.minValueLong = minValue;
		this.maxValueLong = maxValue;
		this.isMinValueValidated = true;
		this.isMaxValueValidated = true;
		
	}
	
	/**
	 * Creates a NumberTextBox
	 */
	public NumberTextBox()
	{
		super();
		
		super.addKeyListener
		(
				new KeyAdapter() 
				{
					public void keyTyped(KeyEvent e) 	
					{
						char key = e.getKeyChar();
						if(!(Character.isDigit(key) || key == KeyEvent.VK_MINUS || (numType == NumberType.DOUBLE && key == KeyEvent.VK_PERIOD)))  // navigation keys are not caught here so we need not worry about them
						{
							e.consume(); //eat that event up so it doesn't get passed to the textbox
						}
					}
				}
		);	
		
		// when anything changes, verify the number is good
		super.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) 
			{
				verifyNumber();
			}
			public void removeUpdate(DocumentEvent e) {
				verifyNumber();
			}
			public void insertUpdate(DocumentEvent e)
			{
				verifyNumber();
			}
		});
	}

	/**
	 * Checks if the text in the text box evaluates to a valid double or long
	 */
	private boolean verifyNumber()
	{

		try 
		{
			if (numType == NumberType.DOUBLE)
			{
				double valueDouble;
				valueDouble = Double.parseDouble(super.getText());
				if(isMinValueValidated){ verifyMin(valueDouble);}
				if(isMaxValueValidated){ verifyMax(valueDouble);}
			}
			else
			{
				long valueLong;
				String s = super.getText();
				valueLong = Long.parseLong(s);
				if(isMinValueValidated){ verifyMin(valueLong);}
				if(isMaxValueValidated){ verifyMax(valueLong);}
			}
			
			setGoodNumber();
		} 
		catch (NumberFormatException | NumberOutOfRangeException e)
		{
			setBadNumber();
		}
		return false;
	}
	
	/**
	 * Consumed by verifyNumber.
	 * 
	 * @param valueDouble the value to be evaluated
	 * @throws NumberOutOfRangeException if the number is smaller than the minValueDouble.
	 */
	private void verifyMin(double valueDouble)
	{
		if(isMinValueDoubleInclusive)
		{
			if(valueDouble >= minValueDouble)
			{
				//good
			}
			else
			{
				//bad
				throw new NumberOutOfRangeException();
			}
		}
		else
		{
			if(valueDouble > minValueDouble)
			{
				//good
			}
			else
			{
				//bad
				throw new NumberOutOfRangeException();
			}
		}
	}

	/**
	 * Consumed by verifyNumber.
	 * 
	 * @param valueDouble the value to be evaluated
	 * @throws NumberOutOfRangeException if the number is larger than the maxValueDouble.
	 */
	private void verifyMax(double valueDouble)
	{
		if(isMaxValueDoubleInclusive)
		{
			if(valueDouble <= maxValueDouble)
			{
				//good
			}
			else
			{
				//bad
				throw new NumberOutOfRangeException();
			}
		}
		else
		{
			if(valueDouble < maxValueDouble)
			{
				//good
			}
			else
			{
				//bad
				throw new NumberOutOfRangeException();
			}
		}
	}
	
	/**
	 * Consumed by verifyNumber.
	 * 
	 * @param valueLong the value to be evaluated
	 * @throws NumberOutOfRangeException if the number is smaller than the minValueLong.
	 */
	private void verifyMin(long valueLong)
	{
			if(valueLong >= minValueLong)
			{
				//good
			}
			else
			{
				//bad
				throw new NumberOutOfRangeException();
			}
	}
	
	/**
	 * Consumed by verifyNumber.
	 * 
	 * @param valueLong the value to be evaluated
	 * @throws NumberOutOfRangeException if the number is larger than the maxValueLong.
	 */
	private void verifyMax(long valueLong)
	{
		if(valueLong <= maxValueLong)
		{
			//good
		}
		else
		{
			//bad
			throw new NumberOutOfRangeException();
		}			
	}
	
	

	/**
	 * Consumed by verifyNumber().  actions to be performed when the text box validates as good
	 */	
	private void setGoodNumber()
	{
		this.isNumberGood = true;
		super.setBackground(goodColour);
		notifyAllDataValidatedListeners();
	}

	/**
	 * Consumed by verifyNumber().  actions to be performed when the text box fails validation
	 */	
	private void setBadNumber()
	{
		this.isNumberGood = false;
		//Toolkit.getDefaultToolkit().beep();
		super.setBackground(badColour);

	}
	
	
	//**************************************************************************************************
	//**** numberType enum

	/**
	 * Enumerator to describe whether the NumberTextBox accepts doubles or longs
	 */
	public enum NumberType
	{
		DOUBLE,
		LONG
	}

	//**************************************************************************************************
	//**** Custom Exceptions

	public class NumberOutOfRangeException extends RuntimeException 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5863574610375023823L;

		public NumberOutOfRangeException() 
		{
			super("The number is outside the bounds set by minValue or maxValue");
		}

	}

	//**************************************************************************************************
	//**** Custom Event Code
    
	/**
	 * Calls all listeners of dataValidated Event
	 */	
    public void notifyAllDataValidatedListeners()
    {
        for (DataValidatedListener dataValidatedListener : listeners)
        {
        	dataValidatedListener.dataValidated();
        }
    }
}
