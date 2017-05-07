package GUI;

import java.util.NoSuchElementException;
import java.util.TimerTask;

import javax.swing.JTextArea;
import javax.swing.Timer;

import ChronoTimer.*;
import Exceptions.InvalidTimeException;

/*
 * Ryan Thorne
 * Matt Damon's ChronoTrigger
 * ver 0.1
 */

public class Handler {

	JTextArea displayArea, printArea;
	ChronoTrigger main;

	GUIState inputState;
	String curNum;
	boolean raceState;
	boolean printerPower;
	UIPrint currentScreen;
	Timer selfUpdater;
	Timer postToServerTimer;
	int hour = 0, min = 0;
	boolean extraInput = false;
	
	final int TIMERDELAY = 100;
	protected int STARTDELAY = 1000;
	final int SERVERDELAY = 10000;
	
	boolean SERVERENABLED;

	public enum GUIState {
		off, // synonymous with the cts.off state
		start, // extra, unused right now? for when we are powering up the
				// machine, similar to the off state.
		wait, // default state, either display race panel if race started, or
				// nothing? if no race
		fcn, // in the function menus
		num, // choosing a number for entry
		event, // choosing an event type
		print,	//selecting a race to print
		export,	//selecting a race to export
		timeh,	//selecting time in hours
		timem,	//minutes
		times	//seconds
		
	}

	public Handler(JTextArea d, JTextArea p) {
		displayArea = d;
		printArea = p;
		printerPower = false;
		main = new ChronoTrigger();
		main.setPrinter(new Printer(new PrinterStream(printArea)));
		//need to set the default printer as well here
		SERVERENABLED = false;
		inputState = GUIState.off;
		curNum = "";
		raceState = false;
		selfUpdater = new Timer(TIMERDELAY, new Listener(this, "UPDATE"));
		selfUpdater.start();
		postToServerTimer = new Timer(SERVERDELAY, new Listener(this, "SERVER"));
		postToServerTimer.start();
		currentScreen = new Card();
	}

	protected boolean issue(String command) 
	{
		if(command != "UPDATE") System.out.println(command);
		// switch each command to determine course of action
		try 
		{
			switch (command) 
			{
			case "SERVER":
				if(raceState && SERVERENABLED)
					main.exportToServer(ChronoTime.now());
			break;
			case "UPDATE":
				if(raceState && inputState == GUIState.wait)
					currentScreen = main.getCard(ChronoTime.now());

				currentScreen.writeTo();
				break;
			/**
			 * if a run has been created allows the user to navigate the current
			 * runs list of participants if function has been pressed and allows
			 * the user to move up choosing a function
			 */
			case "UP":
				up();
				break;
			case "DOWN":
				down();
				break;
			/**
			 * exit out of a menu, 'go back'
			 */
			case "LEFT":
				left();
				break;
			/**
			 * equivalent to an enter? only on function
			 */
			case "RIGHT":
			case "POUND":
				right();
				// enter key
				break;
			/**
			 * has no function yet
			 */
			case "SWAP":
				swap();
				break;
			/**
			 * starts up list, if list is already started, will cancel it?
			 */
			case "FUNCTION":
				function();
				break;
			case "POWER":
				power();
				break;
			case "PRINTER POWER":
				printerPower();
				break;
			case "STAR":
				star();
				break;
			default:
				String commandArray[] = command.split(" ");
				if(commandArray.length < 2 || commandArray.length > 3)
					throw new NoSuchElementException("could not parse command");
				else
					switch(commandArray[0])
					{
					case "TRIGGER":
						main.triggerSensor(ChronoTime.now(), Integer.parseInt(commandArray[1]));
						break;
					case "TOGGLE":
						main.toggle(ChronoTime.now(), Integer.parseInt(commandArray[1]));
						break;
					case "PLUG":
						main.connectSensor(ChronoTime.now(), Integer.parseInt(commandArray[1]), commandArray[2]);
						break;
					case "NUM":
						num(commandArray[1]);
						break;
					}
			}

			displayArea.setText(currentScreen.getText());
			displayArea.append("\n");
			if(extraInput) displayArea.append("\n>");
			if(hour != 0) displayArea.append("" + hour + ": ");
			if(min != 0) displayArea.append("" + min + ": ");
			displayArea.append(curNum);
			if(!printerPower)
				printArea.setText("");
			
			return true;
		} catch (InvalidTimeException e) 
		{
			System.out.println("time exception");
		return false;
		}
	}
	
	
	private void num(String inputNumber) {
		switch (inputState) 
		{
		case export:
		case print:
		case num:
			if (curNum.length() < 4)
				curNum += inputNumber;
			break;
		case timeh:
		case timem:
		case times:
			if (curNum.length() < 2)
				curNum += inputNumber;
			break;
		default:
			// do nothing
		}
		if(curNum == "0")
			curNum = "";

	}

	private void swap() {
		
	}

	/**
	 * this family of functions act as dissociations between the handler and the actual method called.
	 * this is so I have less 'SuperFunctions' and more maneagable pieces.
	 * @throws InvalidTimeException 
	 */
	
	private void power() throws InvalidTimeException
	{
		switch (inputState) 
		{
		case off:
			main.powerOn(ChronoTime.now());
			currentScreen = new SplashScreen();
			inputState = GUIState.start;
			
			new java.util.Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					currentScreen = new Card();
					inputState = GUIState.wait;
				}
			}, STARTDELAY);
			
			break;
		default:
			main.powerOff(ChronoTime.now());
			inputState = GUIState.off;
			curNum = "";
			raceState = false;
			hour = 0;
			min = 0;
			currentScreen = new Card();
			printerPower = false;
			printArea.setText("");
			extraInput = false;
		}
	}
	
	private void printerPower()
	{
		switch (inputState) 
		{
		case off:
			break;
		default:
			printerPower = !printerPower;	
			if(printerPower)
				printArea.setText("printer On\n");
			else
				printArea.setText("");
		}
	}
	private void function() throws InvalidTimeException
	{
		switch (inputState) 
		{
		case wait:
			// start function protocol
			currentScreen = new Menu(raceState);
			inputState = GUIState.fcn;
			break;
		case num:
		case fcn: 
		case event:
		case print:
		case export:
		case timeh:
		case timem:
		case times:
			// stop function protocol
			extraInput = false;
			curNum = "";
			hour = min = 0;
			retWait();
			break;
		default:

		}
	}
	
	private void up()
	{
		switch (inputState) 
		{
		case fcn:
		case event:
		case wait:
			// should allow user to navigate the scroll view for this
			// run
			currentScreen.up();
			break;
		default:

		}
	}
	private void down()
	{
		switch (inputState) 
		{
		case fcn:
		case event:
		case wait:
			currentScreen.down();
			break;
		default:

		}
	}
	
	private void left() throws InvalidTimeException
	{
		switch (inputState) 
		{
		case fcn:
			// maneuver back in menu
			retWait();
			break;
		case event:
			currentScreen = new Menu(raceState);
		case num:
		case print:
		case export:
		case timeh:
			curNum = "";
			extraInput = false;
			inputState = GUIState.fcn;
			break;
		case timem:
			curNum = "";
			inputState = GUIState.timeh;
			hour = 0;
			break;
		case times:
			curNum = "";
			inputState = GUIState.timem;
			min = 0;
			break;
		default:

		}
	}
	
	private void right() throws InvalidTimeException
	{
		switch (inputState) 
		{
		case fcn:
			enterCommand(currentScreen.writeTo());//this will put me in the correct state
			break;
		case num:
			if(curNum != "")
			{
				main.addRacer(ChronoTime.now(), Integer.parseInt(curNum));
				retWait();
				curNum = "";
				extraInput = false; 
			}
			break;
		case event:
			main.setType(ChronoTime.now(), currentScreen.writeTo());
			retWait();
			break;
		case print:
			//printArea.setText("");
			if(curNum != "")
				main.printRun(ChronoTime.now(), Integer.parseInt(curNum));
			else
				main.printRun(ChronoTime.now());
			curNum = "";
			extraInput = false;
			retWait();
			break;
			//later
		case export:
			if(curNum != "")
				main.exportRun(ChronoTime.now(), Integer.parseInt(curNum));
			else
				main.exportRun(ChronoTime.now());
			curNum = "";
			extraInput = false;
			retWait();
			break;
			//later
		case timeh:
			if(curNum != "")
			{
				inputState = GUIState.timem;
				hour = Integer.parseInt(curNum);
				curNum = "";
			}
		case timem:
			if(curNum != "")
			{
				inputState = GUIState.times;
				min = Integer.parseInt(curNum);
				curNum = "";
			}
		case times:
			if(curNum != "")
			{
				inputState = GUIState.wait;
				try{
					main.setTime(ChronoTime.now(), new ChronoTime(hour, min, Integer.parseInt(curNum), 0));	
				}
				catch (InvalidTimeException t)
				{
					System.out.println("time exception in setting time");
				}
				
				hour = min = 0;
				curNum = "";
				currentScreen = new Card();
				extraInput = false;
				
			}
		default:
			// do nothing
		}
	}
	
	private void star() throws InvalidTimeException
	{
		switch (inputState) 
		{
		case num:
		case print:
		case export:
		case timeh:
		case timem:
		case times:
			curNum = "";
			break;
		case fcn:
			inputState = GUIState.wait;
			currentScreen = main.getCard(ChronoTime.now());
			break;
		default:
			// do nothing
		}
	}
	
	private void retWait() throws InvalidTimeException
	{
		inputState = GUIState.wait;
		if(raceState)
			currentScreen = main.getCard(ChronoTime.now());
		else
			currentScreen = new Card();
	}
	
	
	
	/**
	 * this function handles function calls out of the Menu 
	 * @param command the command to parse
	 * @return false means no more information is needed and the command is completed, true means that the command is not completed, it needs more information
	 */
	private boolean enterCommand(String command)
	{
		try
		{
			switch (command)
			{
			case "NUM":
				inputState = GUIState.num;
				extraInput = true;
				return true;
			case "CLEAR":
				inputState = GUIState.wait;
				currentScreen = main.getCard(ChronoTime.now());
				//clear does nothing?
				return false;
			case "CANCEL":
				inputState = GUIState.wait;
				currentScreen = main.getCard(ChronoTime.now());
				main.cancel(ChronoTime.now());
				return false;
			case "DNF":
				inputState = GUIState.wait;
				currentScreen = main.getCard(ChronoTime.now());
				main.dnf(ChronoTime.now());
				return false;
			case "ENDRUN":
				inputState = GUIState.wait;
				currentScreen = new Card();
				raceState = false;
				main.finRun(ChronoTime.now());
				return false;
			case "EVENT":
				inputState = GUIState.event;
				currentScreen = new EventMenu();
				return true;
			case "PRINT":
				if(printerPower)
				{
					extraInput = true;
					inputState = GUIState.print;
					return true;
				}
				retWait();
				return false;
			case "EXPORT":
				inputState = GUIState.export;
				extraInput = true;
				return true;
			case "RESET":
				power();
				power();
				return false;
			case "NEWRUN":
				inputState = GUIState.wait;
				main.newRun(ChronoTime.now());
				currentScreen = main.getCard(ChronoTime.now());
				raceState = true;
				return false;
			case "TIME":
				extraInput = true;
				inputState = GUIState.timeh;
				return true;
			case "ENABLESERVER":
				SERVERENABLED = true;
				retWait();
				return false;
			case "DISABLESERVER":
				SERVERENABLED = true;
				retWait();
				return false;
			default:
				return false;
			}
		} catch (InvalidTimeException e) 
		{
			System.out.println("Misrepresented time... Crashing");
			return false;
		}
	}
}
