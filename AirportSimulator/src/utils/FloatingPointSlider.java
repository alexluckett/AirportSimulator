package utils;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.*;
import javax.swing.event.*;

/**
 * Floating Point slider inspired by LaballedSlider by Ian T. Nabney
 * Allows a floating point (double) value to be associated with the slider control
 *
 * @author Jason Harrison
 * @version v1.002 - 01/05/2014
 */
public class FloatingPointSlider extends JSlider {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1416097992661425166L;
	
	/**
	 * The floating point value of the slider
	 */
	private double dValue;
	
	/**
	 * the scale.  derived from decimalPlaces
	 */
	private int scaler;
	
	/**
	 * Creates a new FloatingPointSlider
	 * 
	 * @param value Current position of slider
	 * @param minValue lowest point on slider scale
	 * @param maxValue highest point on slider scale
	 * @param decimalPlaces number of decimal places to work to. / resolution in 1*10^decimalPlaces  
	 */
	public FloatingPointSlider(double value, double minValue, double maxValue, int decimalPlaces)
	{
		//FloatingPointSlider thisSlider = this;
		scaler = (int)Math.pow(10, decimalPlaces);
		dValue = value;
		if (value < minValue || value > maxValue)	{throw new IllegalArgumentException("Value out of range");}
		setDoubleBuffered(true);
		setMinimum((int)(minValue*scaler));
		setMaximum((int)(maxValue*scaler));
		int sval = (int)(dValue*scaler);
		this.setValue(sval);
		setMajorTickSpacing((int)((maxValue*scaler-minValue*scaler)/10)); //10 tick marks
		setPaintTicks(true);
		
		/**
		 * Action to increase or decrease with the mouse wheel 
		 */
		addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent mw) 
			{
				int val = getValue()-mw.getWheelRotation();
				if(val<getMinimum()){val = getMinimum();} 
				setValue(val); //this will call the changerListener
			}
		});
		
		/**
		 * actions when the slider changes
		 */
		addChangeListener(new ChangeListener() 
		{
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				dValue = (double)getValue()/scaler;
				firePropertyChange("dValue", 0, dValue);  //attaching to the propertyChangeListener rather than the changeListener guarantees dValue is set correctly
			}
		});
		
	}
	
	/**
	 * Gets the scaled slider value
	 *
	 * @return the scaled floating point number
	 */
	public double getValue_FP()
	{
		return dValue;
	}

	/**
	 * Sets the scaled slider value
	 *
	 * @param FloatingPontValue the scaled floating point number to set
	 */
	public void setValue_FP(double FloatingPontValue)
	{
		dValue = FloatingPontValue;
		setValue((int)(dValue*scaler));
	}

	// *********************************************************************************************************
	// methods from super
	@Override
	public void setDoubleBuffered(boolean aFlag) {
		super.setDoubleBuffered(aFlag);
	}

	@Override
	public void setMajorTickSpacing(int n) {
		super.setMajorTickSpacing(n);
	}

	@Override
	public void setPaintTicks(boolean b) {
		super.setPaintTicks(b);
	}

	@Override
	public int getMaximum() {
		return super.getMaximum();
	}

	@Override
	public int getMinimum() {
		return super.getMinimum();
	}

	@Override
	public int getValue() {
		return super.getValue();
	}

	@Override
	public void setMaximum(int maximum) {
		super.setMaximum(maximum);
	}

	@Override
	public void setMinimum(int minimum) {
		super.setMinimum(minimum);
	}

	@Override
	public void setValue(int n) {
		super.setValue(n);
	}
	
	

}