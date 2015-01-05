package utils;

import java.awt.FlowLayout;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utils.NumberTextBox.DataValidatedListener;
import utils.NumberTextBox.NumberType;

@SuppressWarnings("serial")
public class SuperSlider extends JComponent  {

	private DataValidatedListener TB_DVL;
	private PropertyChangeListener S_PCL;
	private ChangeListener S_CL;
	
	public NumberTextBox TB; //this is a form control - direct access is allowed
	public JSlider S; //this is a form control - direct access is allowed
	private NumberFormat numberFormat;
	private int precision = 3; 

	public SuperSlider(double defaultValue, double minValue, double maxValue, int decimalPlaces, int textBoxColumns)
	{ // constructor for double type
		precision = decimalPlaces;

		
		TB = new NumberTextBox("" + defaultValue, minValue, true , maxValue, true);
		S = new FloatingPointSlider(defaultValue,minValue,maxValue,precision);

		TB.setColumns(textBoxColumns);
		
		S_PCL = new PropertyChangeListener() //for FloatingPointSlider
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt) 
			{
				TB.removeDataValidatedListener(TB_DVL);//stop infinite recursion from occurring 
				TB.setText(numberFormat.format(evt.getNewValue()));
				TB.addDataValidatedListener(TB_DVL);
			}
		};
		
		S.addPropertyChangeListener("dValue", S_PCL);
		
		
		commonSetup();
		
		TB.setText("" + defaultValue);  // set value and force validation after setup
		
	}

	public SuperSlider(int defaultValue, int minValue, int maxValue, int textBoxColumns)
	{ // constructor integer type
		
		precision = 0;
		
		TB = new NumberTextBox("" + defaultValue, minValue, maxValue);
		S = new JSlider(minValue,maxValue,defaultValue);
		
		TB.setColumns(textBoxColumns);
		
		S_CL = new ChangeListener() //for standard JSlider
		{
			@Override
			public void stateChanged(ChangeEvent e) {
				TB.removeDataValidatedListener(TB_DVL);//stop infinite recursion from occurring 
				TB.setText("" + S.getValue());
				TB.addDataValidatedListener(TB_DVL);
			}
		};
		
		S.addChangeListener(S_CL);
		
		S.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent mw) {
				int val = S.getValue()-mw.getWheelRotation();
				if(val<S.getMinimum()){val = S.getMinimum();} 
				S.setValue(val); //this will call the changerListener
			}
		});
		
		S.setMajorTickSpacing((int)((maxValue-minValue)/10)); //10 tick marks
		S.setPaintTicks(true);
		
		commonSetup();
		
		TB.setText("" + defaultValue);  // set value and force validation after setup
		
	}
	

	private void commonSetup()
	{
		numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(precision);
		numberFormat.setMinimumFractionDigits(precision);
		numberFormat.setGroupingUsed(false);
		
		this.add(TB);
		this.add(S);
		this.setDoubleBuffered(true);
		this.setLayout(new FlowLayout());
		
		TB_DVL = new DataValidatedListener()
		{
			@Override
			public void dataValidated()
			{
				if (TB.getNumberType() == NumberType.DOUBLE)
				{
					S.removePropertyChangeListener("dValue", S_PCL); //stop infinite recursion from occurring
					((FloatingPointSlider)S).setValue_FP(Double.parseDouble(TB.getText()));
					S.addPropertyChangeListener("dValue", S_PCL);
				}
				else
				{
					S.removeChangeListener(S_CL); //stop infinite recursion from occurring
					S.setValue(Integer.parseInt(TB.getText()));
					S.addChangeListener(S_CL);
				}
			}
		};
		
		TB.addDataValidatedListener(TB_DVL);
	}

	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		TB.setEnabled(enabled);
		S.setEnabled(enabled);
	}
	

	
}
