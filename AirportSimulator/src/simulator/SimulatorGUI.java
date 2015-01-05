package simulator;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import airport.ControlStats;
import simulator.Simulator.SimulatorListener;
import utils.Debug;
import utils.Debug.DataOutListener;
import utils.IO;
import utils.NumberTextBox;
import utils.SuperSlider;

/**
 * GUI to set up the simulation
 * 
 * @author Jason Harrison & Shahbaz Hussain 
 * @version 01/05/2014
 */
public class SimulatorGUI 
{
	/**
	 * Title of the application
	 */
	private String appTitle = "Airport Capacity Simulator";
	
	/**
	 * spacing on elements which need spacing 
	 */
	private final int standardBorderSpacing = 4;
	
	/**
	 * states whether the form is dirty / user has changed any inputs
	 */
	private boolean dirty = false;

	/**
	 * Tracks the mode the form is currently in
	 */
	private FormState formState = FormState.CONFIG;
	
	/**
	 * Stores the time the simulation was started so a total execution time can be worked out.
	 */
	private long startExecTime = 0;

	/**
	 * used to track/show the app is still executing
	 */
	private long lastTime = 0;
	
	/**
	 * keeps track of the alive marker in the progress bar
	 */
	private int aliveMarkerTracker = 0;
	
	/**
	 * 
	 */
	SwingWorker<Void, Void> simThread = null;
	
	// **************************************************************************************************************
	// *** Create Controls ***
	private final JFrame mainFrame = new JFrame(appTitle);
	private final JPanel leftPanel = new JPanel();
	private final JPanel rightPanel = new JPanel();
	
	private final JLabel title1 = new JLabel("Team 15");
	private final JLabel title2 = new JLabel(appTitle);
	private final JButton goBtn = new JButton("Run");
	private final JButton helpBtn = new JButton("Help"); 
	private final JComboBox<LevelComboItemType> levelCombobox ;
	
	private final JCheckBox pAutoCk = new JCheckBox("Simulate for all values of P");
	private final JLabel pValueLbl = new JLabel("P:");
	private final SuperSlider pSlider = new SuperSlider(Simulator.minP,Simulator.minP,Simulator.maxP,4,7);
	private final JCheckBox seedCk = new JCheckBox("Automatically generate a random seed");
	private final JLabel seedValueLbl = new JLabel("Seed:");
	private final NumberTextBox seedTxt = new NumberTextBox(""+utils.RandomGenerator.getSeed(), Long.MIN_VALUE, Long.MAX_VALUE);
	private final JLabel timeValueLbl = new JLabel("Ticks:");
	private final SuperSlider timeSlider = new SuperSlider(Simulator.ticksInADay, 1, Simulator.ticksInAWeek*4, 5);
	private final JLabel timeValueMinLbl = new JLabel("Minutes");
	private final JCheckBox fileCk = new JCheckBox("Write output to file");
	private final JCheckBox debugCk = new JCheckBox("Verbose File Output");
	private final JTextField filePathTxt = new JTextField("C:\\");
	private final JButton fileButton = new JButton("...");
	private final JFileChooser fileChooser = new JFileChooser();
	private final NumberTextBox numberOfRuns = new NumberTextBox("10", 1, 750);
	private final JLabel numberOfRunsLbl = new JLabel("times.");
	private final JTextArea textSimulationSummary = new JTextArea();
	private final JScrollPane summaryScroller = new JScrollPane(textSimulationSummary, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private final JProgressBar SimulationProgressPrBar = new JProgressBar(0, 100);


	
	/**
	 * Creates the user interface
	 */
	public SimulatorGUI()
	{
		// **************************************************************************************************************
		// *** Set to OS Specific look ***
		try {	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	} 
		catch (Exception e1) {	e1.printStackTrace();	} // we don't care if this fails 

		// **************************************************************************************************************
		// *** Create Panels ***

		JPanel topleftPanel = new JPanel();
		JPanel OuterPPanel = new JPanel();
		JPanel fileOptionPanel = new JPanel();
		JPanel pPanel = new JPanel();
		JPanel p1Panel = new JPanel();
		JPanel OuterSeedPanel = new JPanel();
		JPanel OuterFilePanel = new JPanel();
		JPanel seedPanel = new JPanel();
		JPanel timePanel = new JPanel();
		JPanel titlePanel = new JPanel();
		JPanel simOptionsPanel0 = new JPanel();
		JPanel LevelPanel = new JPanel();
		JPanel simOptionsPanel1 = new JPanel();
		JPanel simOptionsPanel2 = new JPanel();
		JPanel simOptionsPanel3 = new JPanel();
		JPanel outputOptionsPanel = new JPanel();
		JPanel textOutputBox = new JPanel();
		JPanel outerActionPanel = new JPanel();
		JPanel InnerLeftActionPanel = new JPanel(); 
		JPanel InnerRightActionPanel = new JPanel(); 
		
		
		// **************************************************************************************************************
		// *** Panel layout and size setting***
		((JPanel)mainFrame.getContentPane()).setBorder(new EmptyBorder(standardBorderSpacing, standardBorderSpacing, 0, standardBorderSpacing));
		mainFrame.setLayout(new BorderLayout());
		leftPanel.setLayout(new BorderLayout());
		topleftPanel.setLayout(new BorderLayout());
		OuterPPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		OuterPPanel.setLayout(new BorderLayout());
		fileOptionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		pPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		p1Panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		OuterSeedPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		OuterSeedPanel.setLayout(new BorderLayout());
		OuterFilePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		OuterFilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		seedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		timePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		timePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		rightPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Output", TitledBorder.LEFT, TitledBorder.TOP));
		rightPanel.setLayout(new BorderLayout());
		titlePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS) );
		titlePanel.setBackground(Color.orange);
		simOptionsPanel0.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Simulation Options", TitledBorder.LEFT, TitledBorder.TOP));
		simOptionsPanel0.setLayout(new BorderLayout());
		LevelPanel.setBorder(new EmptyBorder(standardBorderSpacing, standardBorderSpacing, standardBorderSpacing, 100));
		LevelPanel.setLayout(new BorderLayout());
		simOptionsPanel1.setLayout(new BorderLayout());
		simOptionsPanel2.setLayout(new BorderLayout());
		simOptionsPanel3.setLayout(new BorderLayout());
		outputOptionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Output Options", TitledBorder.LEFT, TitledBorder.TOP));
		outputOptionsPanel.setLayout(new BorderLayout());
		textOutputBox.setLayout(new BorderLayout());	
		outerActionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		outerActionPanel.setLayout(new BorderLayout());
		InnerLeftActionPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); 
		InnerRightActionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		
		// **************************************************************************************************************
		// *** Nest Panels ***
		mainFrame.getContentPane().add(titlePanel, BorderLayout.NORTH);
		mainFrame.getContentPane().add(outerActionPanel, BorderLayout.SOUTH);
		mainFrame.getContentPane().add(leftPanel, BorderLayout.CENTER);

		leftPanel.add(topleftPanel, BorderLayout.NORTH);
		topleftPanel.add(simOptionsPanel0, BorderLayout.CENTER);
		topleftPanel.add(outputOptionsPanel, BorderLayout.SOUTH);
		rightPanel.add(textOutputBox, BorderLayout.CENTER);
		simOptionsPanel0.add(simOptionsPanel1, BorderLayout.NORTH);
		simOptionsPanel0.add(simOptionsPanel2, BorderLayout.CENTER);
		simOptionsPanel0.add(simOptionsPanel3, BorderLayout.SOUTH);
		simOptionsPanel1.add(LevelPanel, BorderLayout.NORTH);
		simOptionsPanel1.add(OuterPPanel, BorderLayout.CENTER);
		OuterPPanel.add(p1Panel, BorderLayout.NORTH);
		OuterPPanel.add(pPanel, BorderLayout.SOUTH);
		simOptionsPanel2.add(OuterSeedPanel, BorderLayout.NORTH);
		simOptionsPanel2.add(timePanel, BorderLayout.SOUTH);
		OuterSeedPanel.add(seedPanel, BorderLayout.SOUTH);
		outputOptionsPanel.add(fileOptionPanel, BorderLayout.NORTH);
		outputOptionsPanel.add(OuterFilePanel, BorderLayout.SOUTH);
		outerActionPanel.add(InnerLeftActionPanel, BorderLayout.WEST); 
		outerActionPanel.add(InnerRightActionPanel, BorderLayout.EAST);

		// **************************************************************************************************************
		// *** Components created as global fields near start of file ***
		// **************************************************************************************************************
		
		// **************************************************************************************************************
		// *** adjust component settings
		title1.setFont(new Font("Arial", Font.BOLD, 20));
		title2.setFont(new Font("Arial", Font.BOLD, 20));
		pAutoCk.setSelected(true);
		seedCk.setSelected(true);
		seedTxt.setColumns(18);
		filePathTxt.setColumns(35);
		numberOfRuns.setColumns(7);
		textSimulationSummary.setEditable(false); // don't want user to change this field
		textSimulationSummary.setBackground(null); // remove ugly white background
		textSimulationSummary.setText("Please change the input variables on the left, then press the 'Run' button to start.");
		textSimulationSummary.setLineWrap(true);
		
		summaryScroller.setBorder(null);
		SimulationProgressPrBar.setStringPainted(true);

		// *** Combobox setup ***
		LevelComboItemType Priority = new LevelComboItemType("Level 1 (Priority)", QueueTypeEnum.PRIORITY);
		LevelComboItemType FIFO = new LevelComboItemType("Level 2 (FIFO)", QueueTypeEnum.FIFO);
		LevelComboItemType[] QueueCBList = { Priority, FIFO };
		levelCombobox = new JComboBox<SimulatorGUI.LevelComboItemType>(QueueCBList);
		
		// *** tool tips		
		String timeTTString = "<HTML>Set the amount of time to simulate in ticks<br><br>From 1 to " + Simulator.ticksInAWeek*4 + "</HTML>";
		timeValueLbl.setToolTipText(timeTTString);
		timeSlider.setToolTipText(timeTTString);
		timeSlider.S.setToolTipText(timeTTString);
		timeSlider.TB.setToolTipText(timeTTString);
		timeValueMinLbl.setToolTipText(timeTTString);
		
		String pTTString = "<HTML>Set the possibility of a commercial jet being generated for take off or landing<br><br>From 0 to " + Simulator.maxP + "</HTML>";
		pValueLbl.setToolTipText(pTTString);
		pSlider.setToolTipText(pTTString);
		pSlider.S.setToolTipText(pTTString);
		pSlider.TB.setToolTipText(pTTString);
		
		numberOfRuns.setToolTipText("<HTML>Number of times to run the simulation.<br><br>An average maximum P value will be generated at the end of the simulation<br><br>From 1 to 750</HTML>");
		levelCombobox.setToolTipText("<HTML>Decides which settings to use for the simulation</HTML>");
		filePathTxt.setToolTipText("<HTML>Enter the location to save an output file</HTML>");
		goBtn.setToolTipText("<HTML>Run the simulation</HTML>");
		helpBtn.setToolTipText("<HTML>Click for help</HTML>");
		
		String seedTTString = "<HTML>The random seed to set when running the simulation<br><br>The results of the simulation will always remain the same for any given value of seed</HTML>";
		seedValueLbl.setToolTipText(seedTTString);
		seedTxt.setToolTipText(seedTTString);
		
		timeSlider.S.setMajorTickSpacing(	(int)Simulator.ticksInADay		); //tick mark per day
		timeValueMinLbl.setText(autoRangeTime(Integer.parseInt(timeSlider.TB.getText())));
		
		filePathTxt.setText(fileChooser.getCurrentDirectory().getPath()+"\\AirportSimulatorOutput.txt");
		fileChooser.setSelectedFile(new File("AirportSimulatorOutput.txt"));

		
		// *** these controls are disabled at form show
		filePathTxt.setEnabled(false);
		fileButton.setEnabled(false);
		debugCk.setEnabled(false);
		pSlider.setEnabled(false);
		seedTxt.setEnabled(false);
		

		// **************************************************************************************************************
		// *** Add Components to form ***
		titlePanel.add(title1);
		titlePanel.add(title2);
		LevelPanel.add(levelCombobox, BorderLayout.NORTH);
		p1Panel.add(pAutoCk);
		p1Panel.add(numberOfRuns);
		p1Panel.add(numberOfRunsLbl);
		pPanel.add(pValueLbl);
		pPanel.add(pSlider);
		OuterSeedPanel.add(seedCk, BorderLayout.NORTH);
		seedPanel.add(seedValueLbl);
		seedPanel.add(seedTxt);
		timePanel.add(timeValueLbl);
		timePanel.add(timeSlider);
		timePanel.add(timeValueMinLbl);
		InnerRightActionPanel.add(SimulationProgressPrBar);
		InnerRightActionPanel.add(goBtn);
		InnerLeftActionPanel.add(helpBtn); 
		fileOptionPanel.add(fileCk);
		fileOptionPanel.add(debugCk);
		OuterFilePanel.add(filePathTxt);
		OuterFilePanel.add(fileButton);
		textOutputBox.add(summaryScroller, BorderLayout.CENTER);
		
		// **************************************************************************************************************
		// *** Attach Listeners ***
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter(){	public void windowClosing(WindowEvent e) {exitApp();}	});

		// *** add components to the form dirty listener ***
		addToDirtyListener(levelCombobox);
		addToDirtyListener(pAutoCk);
		addToDirtyListener(seedCk);
		addToDirtyListener(fileCk);
		addToDirtyListener(debugCk);
		addToDirtyListener(filePathTxt);
		addToDirtyListener(pSlider.TB);
		addToDirtyListener(seedTxt);
		addToDirtyListener(timeSlider.TB);
		addToDirtyListener(numberOfRuns);
		addToDirtyListener(goBtn);
		addToDirtyListener(helpBtn);
		
		// *** Form actions ***
		
		
		
		debugCk.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent debug) {
				if(debugCk.isSelected())
				{
					utils.Debug.setEnabled(true);
				}
				else
				{
					utils.Debug.setEnabled(false);
				}
				
			}
		});
		
		goBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent btn)
			{	
				switch (formState) {
				case RUNNING:
					simThread.cancel(true);
					while(!simThread.isDone());
					changeFormState(FormState.SHOWRESULTS);
					break;
				case CONFIG:
					changeFormState(FormState.RUNNING);
					if(!ConfigureAndStartSimulation())
					{//if there was a problem starting the simulator
						changeFormState(FormState.CONFIG);
					}
					break;
				case SHOWRESULTS:
					changeFormState(FormState.CONFIG);
					break;
				}
			}
		});
		
	 helpBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed (ActionEvent e)
			{
				try {
					getHelpText();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			}});
		
		
		fileButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent btn) {
				if(fileChooser.showDialog(mainFrame,"Select")== JFileChooser.APPROVE_OPTION)
				{
					filePathTxt.setText(fileChooser.getSelectedFile().getPath());
				}
				
			}
		});
		
		
		
		fileCk.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent item) {
				if(fileCk.isSelected())
				{
					filePathTxt.setEnabled(true);					
					fileButton.setEnabled(true);
					debugCk.setEnabled(true);
				}
				else
				{
					filePathTxt.setEnabled(false);
					fileButton.setEnabled(false);
					debugCk.setEnabled(false);
				}
				
			}
		});
		
		pAutoCk.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent item) {
				if(pAutoCk.isSelected() == true)
				{
					pSlider.setEnabled(false);
					numberOfRuns.setEnabled(true);
					//seedCk.setEnabled(false);
					//seedCk.setSelected(true);
				}
				else
				{
					pSlider.setEnabled(true);
					numberOfRuns.setEnabled(false);
					//seedCk.setEnabled(true);
				}
				
			}
		});
		
		seedCk.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent item) {
				if(seedCk.isSelected() == true)
				{
					seedTxt.setEnabled(false);
				}
				else
				{
					seedTxt.setEnabled(true);
				}
				
			}
		});
		
		timeSlider.TB.addDataValidatedListener(new utils.NumberTextBox.DataValidatedListener() {
			
			@Override
			public void dataValidated() {
				timeValueMinLbl.setText(autoRangeTime(Integer.parseInt(timeSlider.TB.getText())));
			}
		});
		
		// **************************************************************************************************************
		// *** Update textArea ***		
		utils.Debug.addDataOutListener(new DataOutListener() {
			
			@Override
			public void dataOut(String data)
			{
				//for debugging.  don't want to throw this data at the user
				//textSimulationSummary.append(data);
			}
			
			@Override
			public void SuperDataOut(String data)
			{
				if(textSimulationSummary.getText().length()>250000)
				{
					//after x long then delete y of data off the start to keep the sim running fast.
					textSimulationSummary.replaceRange("...Data truncated...", 0, 50000);
				}
				textSimulationSummary.append(data);
				textSimulationSummary.setCaretPosition(textSimulationSummary.getDocument().getLength());
			}
		});
		
		// **************************************************************************************************************
		// *** Pack and Show ***
		mainFrame.pack();
		mainFrame.setSize(450, mainFrame.getHeight());
		mainFrame.setMinimumSize(mainFrame.getSize());
		SwingUtilities.updateComponentTreeUI(mainFrame);
		mainFrame.setVisible(true);
	}
	// **************************************************************************************************************
	// **************************************************************************************************************
	// **************************************************************************************************************
	
	
	
	
	// **************************************************************************************************************
	// *** Display Time Helper Function ***
	/**
	 * Takes the number of ticks to then return the corresponding amount of time in a relevant unit.
	 * @param ticks
	 * @return String
	 */
	private String autoRangeTime(int ticks)
	{
		NumberFormat numberFormat;
		numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(4);
		numberFormat.setMinimumFractionDigits(0);
		numberFormat.setGroupingUsed(false);
		
		double minutes = (double)ticks / (Simulator.ticksInAnHour / 60);
		if(minutes>48*60)//days
		{
			return "" + numberFormat.format(minutes/(60*24)) + " Days";
		}
		if(minutes>2*60)//Hours
		{
			return "" + numberFormat.format(minutes/(60)) + " Hours";
		}

		return "" + numberFormat.format(minutes/(1)) + " Minutes";
	}
	
	/**
	 * Changes the state of GUI and records the current state
	 * @param formState
	 */
	private void changeFormState(FormState formState)
	{
		switch (formState) 
		{
		case RUNNING:
			goBtn.setText("Cancel");
			mainFrame.getContentPane().remove(rightPanel);
			mainFrame.getContentPane().remove(leftPanel);
			mainFrame.getContentPane().add(rightPanel, BorderLayout.CENTER);
			textSimulationSummary.setText("");
			SwingUtilities.updateComponentTreeUI(mainFrame);
			this.formState = FormState.RUNNING;
			break;

		case CONFIG:
			goBtn.setText("Run");
			mainFrame.getContentPane().remove(leftPanel);
			mainFrame.getContentPane().remove(rightPanel);
			mainFrame.getContentPane().add(leftPanel, BorderLayout.CENTER);
			SimulationProgressPrBar.setValue(0);
			SimulationProgressPrBar.setString(null);
			SwingUtilities.updateComponentTreeUI(mainFrame);
			this.formState = FormState.CONFIG;
			break;
			
		case SHOWRESULTS:
			goBtn.setText("Back");
			mainFrame.getContentPane().remove(rightPanel);
			mainFrame.getContentPane().remove(leftPanel);
			mainFrame.getContentPane().add(rightPanel, BorderLayout.CENTER);
			this.formState = FormState.SHOWRESULTS;
			break;
		}
		
	
		
	}

	
	// **************************************************************************************************************
	// *** Application exit code ***
	
	/**
	 * Exit dialog.  appears if user attempts to close the application and the form is dirty.
	 * If the user selects "Yes" to quit then the method exits the application upon dialog close.
	 * 
	 */
	private void exitApp() 
	{
		// Display confirmation dialog before exiting application
		if(dirty)
		{ // if the form has changed...
			int response = JOptionPane.showConfirmDialog(mainFrame, 
					"Are you sure you want to quit?",
					appTitle,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.YES_OPTION)
			{
				if(formState == FormState.RUNNING)
				{
					simThread.cancel(true);
					while(!simThread.isDone());
				}
				System.exit(0);
			}
			else
			{
				// Don't quit
			}
		}
		else
		{ // form data hasn't changed.  quit without prompting
			System.exit(0);
		}
	}
	
	// **************************************************************************************************************
	// *** Application help code ***
	
	
	private void getHelpText() throws IOException {
		
		JOptionPane.showMessageDialog(mainFrame, IO.ReadStreamToString(
				Simulator.class.getResourceAsStream(Simulator.resourceLocation + "GUIHelpText.txt")));
		
		
		//return response; 
		
	}
	
	// **************************************************************************************************************
	// *** Form Dirty code***
	
	/**
	 * Marks the form as dirty which signifies that user data will be lost if the application is closed.
	 */
	public void markDirty()
	{
		dirty = true;
		mainFrame.setTitle(appTitle + "*");
	}

	/**
	 * Marks the form as clean which signifies that no data will be lost 
	 */
	public void unmarkDirty()
	{
		dirty = false;
		mainFrame.setTitle(appTitle);
	}

	/**
	 * Adds a component to the list of components which are monitored to decide if the form is dirty or not.
	 * @param comp The component to be added to the dirty listener.
	 * Valid components are JTextField, JCheckBox, JComboBox and JButton
	 */
	private void addToDirtyListener(Component comp)
	{
		if(comp instanceof JTextField)
		{
			((JTextField)comp).getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					markDirty();
				}
				public void removeUpdate(DocumentEvent e) {
					markDirty();
				}
				public void insertUpdate(DocumentEvent e) {
					markDirty();
				}
			});
		}
		
		if(comp instanceof JCheckBox)
		{
			((JCheckBox)comp).addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					markDirty();
				}
			});
		}
		
		if(comp instanceof JComboBox<?>)
		{
			((JComboBox<?>)comp).addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent cbe) {
					markDirty();
					
				}
			});
		}
		if(comp instanceof JButton)
		{
			((JButton)comp).addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent btn) {
					markDirty();
				}
			});
		}
	}
	
	// **************************************************************************************************************
	// *** Run Simulation Code ***
	
	/**
	 * Code executes when the user clicks on the Run button
	 * @return if the simulation was successfully started
	 */
	private boolean ConfigureAndStartSimulation()
	{
		// Check the file location is ok
		int fileStatus;
		if(fileCk.isSelected())
		{
			fileStatus = fileLocationStatus();
			switch (fileStatus) 
			{
			case JOptionPane.YES_OPTION:
				Debug.saveToFile(filePathTxt.getText());
				//all is good
				break;
			case JOptionPane.NO_OPTION:
				fileDoesNotExistWarning(); //that's not even a valid file name!
				return false;

			case JOptionPane.CANCEL_OPTION:
				// user decided not to overwrite their file after all.
				return false;

			default:
				return false;
			}	
			
		}
		if(pAutoCk.isSelected())
		{ // SIMULATE MANY
			//check all pertinent fields are valid
			if(numberOfRuns.isNumberGood() && timeSlider.TB.isNumberGood() && (seedCk.isSelected() || seedTxt.isNumberGood()))
			{ //good inputs
				if(!seedCk.isSelected()){utils.RandomGenerator.setSeed(Long.parseLong(seedTxt.getText()));}


				final class SimThread extends SwingWorker<Void, Void>
				{
					@Override
					public Void doInBackground() {
						int iNumberOfRuns = Integer.parseInt(numberOfRuns.getText());
						Simulator sim = new Simulator();
						sim.setNumTicks(Integer.parseInt(timeSlider.TB.getText()));
						sim.setQueueType(((LevelComboItemType)levelCombobox.getSelectedItem()).getQueueType());
						sim.addSimulatorListener(simListener);
						sim.setTicksToComplete((int)((Simulator.maxP-Simulator.minP)/Simulator.pInterval)*sim.getNumTicks()*iNumberOfRuns);
						//Debug.saveToFile(fileLocation);
						startExecTime = System.currentTimeMillis();
						
						sim.simulateAuto(iNumberOfRuns); //AUTO SIMULATE!
						
						return null;
					}

					@Override
					protected void done()
					{
						changeFormState(FormState.SHOWRESULTS);
						SimulationProgressPrBar.setValue(100);
						Debug.superPrintln("Total execution time: "+ (double)((System.currentTimeMillis() - startExecTime))/1000+"s");
						SimulationProgressPrBar.setValue(100);
						SimulationProgressPrBar.setString(null);

						// close the file.  doesn't matter if we never opened it in the first place
						Debug.resetSaveToFile();

					}
				}

				simThread = new SimThread();
				simThread.execute();

			}
			else
			{ //bad inputs
				BadInputDialog();
				return false;
			}
		}
		else
		{//SIMULATE ONCE
			if(pSlider.TB.isNumberGood() && timeSlider.TB.isNumberGood() && (seedCk.isSelected() || seedTxt.isNumberGood()))
			{
				if(!seedCk.isSelected()){utils.RandomGenerator.setSeed(Long.parseLong(seedTxt.getText()));}


				final class SimThread extends SwingWorker<Void, Void>
				{
					@Override
					public Void doInBackground()
					{
						Simulator sim = new Simulator();
						sim.setNumTicks(Integer.parseInt(timeSlider.TB.getText()));
						sim.setProbability(Double.parseDouble(pSlider.TB.getText()));
						sim.setQueueType(((LevelComboItemType)levelCombobox.getSelectedItem()).getQueueType());
						sim.addSimulatorListener(simListener);	
						sim.setTicksToComplete(sim.getNumTicks());
						startExecTime = System.currentTimeMillis();
						
						sim.simulate(); //SIMULATE!
						
						return null;
					}

					@Override
					protected void done()
					{
						changeFormState(FormState.SHOWRESULTS);
						SimulationProgressPrBar.setValue(100);
						SimulationProgressPrBar.setString(null);
						Debug.superPrintln("Total execution time: "+ (double)((System.currentTimeMillis() - startExecTime))/1000+"s");

						// close the file.  doesn't matter if we never opened it in the first place
						Debug.resetSaveToFile();
					}
				}

				simThread = new SimThread();
				simThread.execute();
			}
		}

		return true;
	}

	private SimulatorListener simListener = new SimulatorListener()
	{
		@Override
		public void progressTracker(double percent)
		{
			int per = (int)percent;
			SimulationProgressPrBar.setValue(per);
		}
		
		@Override
		public boolean afterTick()
		{
			if(lastTime+500 < System.currentTimeMillis())//if it's been more than half a second
			{
				lastTime = System.currentTimeMillis();
				String aliveMarker = "";
				switch (aliveMarkerTracker)
				{
				case 0:
					aliveMarker = "|";
					aliveMarkerTracker=1;	
					break;
				case 1:
					aliveMarker = "/";
					aliveMarkerTracker=2;	
					break;
				case 2:
					aliveMarker = "-";
					aliveMarkerTracker=3;	
					break;
				case 3:
					aliveMarker = "\\";
					aliveMarkerTracker=0;	
					break;
				}
				if(simThread.isCancelled())
				{
					return true; // CANCEL!
				}
				
				SimulationProgressPrBar.setString(aliveMarker + " " + SimulationProgressPrBar.getValue() + "%");
				
			}
			return false; //no cancel request
		}
		
		@Override
		public void afterSimulate(ControlStats cs)
		{
			
		}
	};


	/**
	 * Tells the user that the file path entered is not valid
	 */
	private void fileDoesNotExistWarning()
	{
		JOptionPane.showConfirmDialog(mainFrame, 
				"You know, you can't just make this stuff up! \nPlease pick an existing folder and valid file name.",
				appTitle,
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE);	
	}
	
	/**
	 * Shown if any number input fields are invalid
	 */
	private void BadInputDialog()
	{
	JOptionPane.showConfirmDialog(mainFrame, 
			"You've somehow entered some invalid data! Try again.",
			appTitle,
			JOptionPane.DEFAULT_OPTION,
			JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Asks the user if they want to overwrite the file
	 * @return True if the file is allowed to be overwritten
	 */
	private boolean confirmFileOverwrite()
	{
		if(JOptionPane.showConfirmDialog(mainFrame, 
			"Wo there, do you really want to overwrite that there file?",
			appTitle,
			JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * checks that the folder exists and if the file already exists it asks the user if they can overwrite it.
	 * @return status of if file location is valid and the user want's to overwrite if applicable
	 * JOptionPane.YES_OPTION if all is good.
	 * JOptionPane.CANCEL_OPTION if user wants to cancel the operation.
	 * JOptionPane.NO_OPTION if file is invalid.
	 */
	private int fileLocationStatus()
	{
		File file = new File(filePathTxt.getText());
		if(file.exists() && !file.isDirectory())
		{
			if(!confirmFileOverwrite())
			{
				//the user does not want to overwrite the file
				return JOptionPane.CANCEL_OPTION;
			}
			else
			{
				//all ok here.  let's overwrite the file
			}
		}
		if(!file.isDirectory() && file.getParentFile().isDirectory())
		{
			//this is probably a valid file.  only way to be sure now it to try and make it.
			return JOptionPane.YES_OPTION;
		}
		
		// This is either invalid or it's a folder path
		return JOptionPane.NO_OPTION;
	}
	
	/**
	 * @author Jason Harrison
	 * Item container for the combobox
	 */
	private class LevelComboItemType
	{
		public String name;
		public QueueTypeEnum queueType;
		
		public QueueTypeEnum getQueueType() {
			return queueType;
		}

		public LevelComboItemType(String name, QueueTypeEnum queueType)
		{
			this.name = name;
			this.queueType = queueType;
		}
		
		public String toString()
		{
			return name;
		}
	}
	
	public enum FormState
	{
		CONFIG,
		RUNNING,
		SHOWRESULTS
	}
}

