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
	final int STARTDELAY = 1000;
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
				break;
			case "DOWN":
				switch (inputState) 
				{
				case fcn:
				case event:
				case wait:
					currentScreen.down();
					break;
				default:

				}
				break;

			/**
			 * exit out of a menu, 'go back'
			 */
			case "LEFT":
				switch (inputState) 
				{
				case fcn:
					// maneuver back in menu
					inputState = GUIState.wait;
					currentScreen = main.getCard(ChronoTime.now());
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
					break;
				case times:
					curNum = "";
					inputState = GUIState.timem;
					break;
				default:

				}
				break;

			/**
			 * equivalent to an enter? only on function
			 */
			case "RIGHT":
			case "POUND":
				switch (inputState) 
				{
				case fcn:
					enterCommand(currentScreen.writeTo());//this will put me in the correct state
					break;
				case num:
					if(curNum != "")
					{
						main.addRacer(ChronoTime.now(), Integer.parseInt(curNum));
						inputState = GUIState.wait;
						if(raceState)
							currentScreen = main.getCard(ChronoTime.now());
						else
							currentScreen = new Card();
						curNum = "";
						extraInput = false; 
					}
					break;
				case event:
					main.setType(ChronoTime.now(), currentScreen.writeTo());
					inputState = GUIState.wait;
					if(raceState)
						currentScreen = main.getCard(ChronoTime.now());
					else
						currentScreen = new Card();
					break;
				case print:
					printArea.setText("");
					if(curNum != "")
						main.printRun(ChronoTime.now(), Integer.parseInt(curNum));
					else
						main.printRun(ChronoTime.now());
					curNum = "";
					inputState = GUIState.wait;
					extraInput = false;
					if(raceState)
						currentScreen = main.getCard(ChronoTime.now());
					else
						currentScreen = new Card();
					break;
					//later
				case export:
					if(curNum != "")
						main.exportRun(ChronoTime.now(), Integer.parseInt(curNum));
					else
						main.exportRun(ChronoTime.now());
					curNum = "";
					inputState = GUIState.wait;
					extraInput = false;
					if(raceState)
						currentScreen = main.getCard(ChronoTime.now());
					else
						currentScreen = new Card();
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
				
				// enter key
				break;

			/**
			 * has no function yet
			 */
			case "SWAP":
				break;

			/**
			 * starts up list, if list is already started, will cancel it?
			 */
			case "FUNCTION":
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
					inputState = GUIState.wait;
					if(raceState)
						currentScreen = main.getCard(ChronoTime.now());
					else
						currentScreen = new Card();
					break;
				default:

				}

				break;
			case "POWER":
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
				break;
			case "PRINTER POWER":
				switch (inputState) 
				{
				case off:
					break;
				default:
					printerPower = !printerPower;	
					if(printerPower)
						printArea.setText("printer On");
					else
						printArea.setText("");
				}
				break;
			case "STAR":
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
				break;
				
			// i wish i had the foresight to break this one up oh well
			case "TRIGGER 1":
				main.triggerSensor(ChronoTime.now(), 1);
				break;
			case "TRIGGER 2":
				main.triggerSensor(ChronoTime.now(), 2);
				break;
			case "TRIGGER 3":
				main.triggerSensor(ChronoTime.now(), 3);
				break;
			case "TRIGGER 4":
				main.triggerSensor(ChronoTime.now(), 4);
				break;
			case "TRIGGER 5":
				main.triggerSensor(ChronoTime.now(), 5);
				break;
			case "TRIGGER 6":
				main.triggerSensor(ChronoTime.now(), 6);
				break;
			case "TRIGGER 7":
				main.triggerSensor(ChronoTime.now(), 7);
				break;
			case "TRIGGER 8":
				main.triggerSensor(ChronoTime.now(), 8);
				break;
			case "PLUG 1 EYE":
				main.connectSensor(ChronoTime.now(), 1, "EYE");
				break;
			case "PLUG 2 EYE":
				main.connectSensor(ChronoTime.now(), 2, "EYE");
				break;
			case "PLUG 3 EYE":
				main.connectSensor(ChronoTime.now(), 3, "EYE");
				break;
			case "PLUG 4 EYE":
				main.connectSensor(ChronoTime.now(), 4, "EYE");
				break;
			case "PLUG 5 EYE":
				main.connectSensor(ChronoTime.now(), 5, "EYE");
				break;
			case "PLUG 6 EYE":
				main.connectSensor(ChronoTime.now(), 6, "EYE");
				break;
			case "PLUG 7 EYE":
				main.connectSensor(ChronoTime.now(), 7, "EYE");
				break;
			case "PLUG 8 EYE":
				main.connectSensor(ChronoTime.now(), 8, "EYE");
				break;
			case "PLUG 1 GATE":
				main.connectSensor(ChronoTime.now(), 1, "GATE");
				break;
			case "PLUG 2 GATE":
				main.connectSensor(ChronoTime.now(), 2, "GATE");
				break;
			case "PLUG 3 GATE":
				main.connectSensor(ChronoTime.now(), 3, "GATE");
				break;
			case "PLUG 4 GATE":
				main.connectSensor(ChronoTime.now(), 4, "GATE");
				break;
			case "PLUG 5 GATE":
				main.connectSensor(ChronoTime.now(), 5, "GATE");
				break;
			case "PLUG 6 GATE":
				main.connectSensor(ChronoTime.now(), 6, "GATE");
				break;
			case "PLUG 7 GATE":
				main.connectSensor(ChronoTime.now(), 7, "GATE");
				break;
			case "PLUG 8 GATE":
				main.connectSensor(ChronoTime.now(), 8, "GATE");
				break;
			case "PLUG 1 PAD":
				main.connectSensor(ChronoTime.now(), 1, "PAD");
				break;
			case "PLUG 2 PAD":
				main.connectSensor(ChronoTime.now(), 2, "PAD");
				break;
			case "PLUG 3 PAD":
				main.connectSensor(ChronoTime.now(), 3, "PAD");
				break;
			case "PLUG 4 PAD":
				main.connectSensor(ChronoTime.now(), 4, "PAD");
				break;
			case "PLUG 5 PAD":
				main.connectSensor(ChronoTime.now(), 5, "PAD");
				break;
			case "PLUG 6 PAD":
				main.connectSensor(ChronoTime.now(), 6, "PAD");
				break;
			case "PLUG 7 PAD":
				main.connectSensor(ChronoTime.now(), 7, "PAD");
				break;
			case "PLUG 8 PAD":
				main.connectSensor(ChronoTime.now(), 8, "PAD");
				break;
			case "PLUG 1 NONE":
				main.disSensor(ChronoTime.now(), 1);
				break;
			case "PLUG 2 NONE":
				main.disSensor(ChronoTime.now(), 2);
				break;
			case "PLUG 3 NONE":
				main.disSensor(ChronoTime.now(), 3);
				break;
			case "PLUG 4 NONE":
				main.disSensor(ChronoTime.now(), 4);
				break;
			case "PLUG 5 NONE":
				main.disSensor(ChronoTime.now(), 5);
				break;
			case "PLUG 6 NONE":
				main.disSensor(ChronoTime.now(), 6);
				break;
			case "PLUG 7 NONE":
				main.disSensor(ChronoTime.now(), 7);
				break;
			case "PLUG 8 NONE":
				main.disSensor(ChronoTime.now(), 8);
				break;
			case "TOGGLE 1":
				main.toggle(ChronoTime.now(), 1);
				break;
			case "TOGGLE 2":
				main.toggle(ChronoTime.now(), 2);
				break;
			case "TOGGLE 3":
				main.toggle(ChronoTime.now(), 3);
				break;
			case "TOGGLE 4":
				main.toggle(ChronoTime.now(), 4);
				break;
			case "TOGGLE 5":
				main.toggle(ChronoTime.now(), 5);
				break;
			case "TOGGLE 6":
				main.toggle(ChronoTime.now(), 6);
				break;
			case "TOGGLE 7":
				main.toggle(ChronoTime.now(), 7);
				break;
			case "TOGGLE 8":
				main.toggle(ChronoTime.now(), 8);
				break;
			case "NUM 1":
				switch (inputState) 
				{
				case export:
				case print:
				case num:
					if (curNum.length() < 4)
						curNum += "1";
					break;
				case timeh:
				case timem:
				case times:
					if (curNum.length() < 2)
						curNum += "1";
					break;
				default:
					// do nothing
				}
				break;
			case "NUM 2":
				switch (inputState) 
				{
				case export:
				case print:
				case num:
					if (curNum.length() < 4)
						curNum += "2";
					break;
				case timeh:
				case timem:
				case times:
					if (curNum.length() < 2)
						curNum += "2";
					break;

				default:
					// do nothing
				}
				break;
			case "NUM 3":
				switch (inputState) 
				{
				case export:
				case print:
				case num:
					if (curNum.length() < 4)
						curNum += "3";
					break;
				case timeh:
				case timem:
				case times:
					if (curNum.length() < 2)
						curNum += "3";
					break;
				default:
					// do nothing
				}
				break;
			case "NUM 4":
				switch (inputState) 
				{
				case export:
				case print:
				case num:
					if (curNum.length() < 4)
						curNum += "4";
					break;
				case timeh:
				case timem:
				case times:
					if (curNum.length() < 2)
						curNum += "4";
					break;
				default:
					// do nothing
				}

				break;
			case "NUM 5":
				switch (inputState) 
				{
				case export:
				case print:
				case num:
					if (curNum.length() < 4)
						curNum += "5";
					break;
				case timeh:
				case timem:
				case times:
					if (curNum.length() < 2)
						curNum += "5";
					break;
				default:
					// do nothing
				}

				break;
			case "NUM 6":
				switch (inputState) 
				{
				case export:
				case print:
				case num:
					if (curNum.length() < 4)
						curNum += "6";
					break;
				case timeh:
				case timem:
				case times:
					if (curNum.length() < 2)
						curNum += "6";
					break;
				default:
					// do nothing
				}

				break;
			case "NUM 7":
				switch (inputState) 
				{
				case export:
				case print:
				case num:
					if (curNum.length() < 4)
						curNum += "7";
					break;
				case timeh:
				case timem:
				case times:
					if (curNum.length() < 2)
						curNum += "7";
					break;
				default:
					// do nothing
				}

				break;
			case "NUM 8":
				switch (inputState) 
				{
				case export:
				case print:
				case num:
					if (curNum.length() < 4)
						curNum += "8";
					break;
				case timeh:
				case timem:
				case times:
					if (curNum.length() < 2)
						curNum += "8";
					break;
				default:
					// do nothing
				}

				break;
			case "NUM 9":
				switch (inputState) 
				{
				case export:
				case print:
				case num:
					if (curNum.length() < 4)
						curNum += "9";
					break;
				case timeh:
				case timem:
				case times:
					if (curNum.length() < 2)
						curNum += "9";
					break;
				default:
					// do nothing
				}

				break;
			case "NUM 0":
				switch (inputState) 
				{
				case export:
				case print:
				case num:
					if (curNum.length() < 4 && curNum.length() > 0)// extra
																	// check so
																	// no 0023
																	// racer
						curNum += "0";
					break;
				case timeh:
				case timem:
				case times:
					if (curNum.length() < 2 && curNum.length() > 0)
						curNum += "0";
					break;
				default:
					// do nothing
				}

				break;
			default:
				throw new NoSuchElementException("could not parse command");
			}

			displayArea.setText(currentScreen.getText());
			displayArea.append("\n");
			if(extraInput) displayArea.append("\n>");
			if(hour != 0) displayArea.append("" + hour + ": ");
			if(min != 0) displayArea.append("" + min + ": ");
			displayArea.append(curNum);
			
			return true;
		} catch (InvalidTimeException e) 
		{
			System.out.println("time exception");
		return false;
		}
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
				inputState = GUIState.wait;
				if(raceState)
					currentScreen = main.getCard(ChronoTime.now());
				else
					currentScreen = new Card();
				return false;
			case "EXPORT":
				inputState = GUIState.export;
				extraInput = true;
				return true;
			case "RESET":
				inputState = GUIState.start;
				currentScreen = new SplashScreen();
				main.powerOff(ChronoTime.now());
				main.powerOn(ChronoTime.now());
				new java.util.Timer().schedule(new TimerTask() {
					
					@Override
					public void run() {
						currentScreen = new Card();
						inputState = GUIState.wait;
					}
				}, STARTDELAY);
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
				inputState = GUIState.wait;
				SERVERENABLED = true;
				if(raceState)
					currentScreen = main.getCard(ChronoTime.now());
				else
					currentScreen = new Card();
				return false;
			case "DISABLESERVER":
				inputState = GUIState.wait;
				SERVERENABLED = true;
				if(raceState)
					currentScreen = main.getCard(ChronoTime.now());
				else
					currentScreen = new Card();
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
