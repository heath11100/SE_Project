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

	guis GUIState;
	String curNum;
	boolean race;
	boolean printerPower;
	UIPrint disp;
	Timer updater;
	int hour = 0, min = 0;
	boolean extraInput = false;
	
	final int TIMERDELAY = 100;
	final int STARTDELAY = 1000;
	

	public enum guis {
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
		//need to set the default printer as well here
		GUIState = guis.off;
		curNum = "";
		race = false;
		updater = new Timer(TIMERDELAY, new Listener(this, "UPDATE"));
		updater.setInitialDelay(100);
		updater.start();
		disp = new Card(0,0);
	}

	protected boolean issue(String command) 
	{
		if(command != "UPDATE") System.out.println(command);
		// switch each command to determine course of action
		try 
		{
			switch (command) 
			{
			case "UPDATE":
				disp.writeTo();
				break;

			/**
			 * if a run has been created allows the user to navigate the current
			 * runs list of participants if function has been pressed and allows
			 * the user to move up choosing a function
			 */
			case "UP":
				switch (GUIState) 
				{
				case fcn:
				case event:
				case wait:
					// should allow user to navigate the scroll view for this
					// run
					disp.up();
					break;
				default:

				}
				break;
			case "DOWN":
				switch (GUIState) 
				{
				case fcn:
				case event:
				case wait:
					disp.down();
					break;
				default:

				}
				break;

			/**
			 * exit out of a menu, 'go back'
			 */
			case "LEFT":
				switch (GUIState) 
				{
				case fcn:
					// maneuver back in menu
					GUIState = guis.wait;
					disp = main.getCard();
					break;
				case event:
					disp = new Menu(race);
				case num:
				case print:
				case export:
				case timeh:
					curNum = "";
					extraInput = false;
					GUIState = guis.fcn;
					break;
				case timem:
					curNum = "";
					GUIState = guis.timeh;
					break;
				case times:
					curNum = "";
					GUIState = guis.timem;
					break;
				default:

				}
				break;

			/**
			 * equivalent to an enter? only on function
			 */
			case "RIGHT":
			case "POUND":
				switch (GUIState) 
				{
				case fcn:
					enterCommand(disp.writeTo());//this will put me in the correct state
					break;
				case num:
					if(curNum != "")
					{
						main.addRacer(ChronoTime.now(), Integer.parseInt(curNum));
						GUIState = guis.wait;
						if(race)
							disp = main.getCard();
						else
							disp = new Card(0, 0);
						curNum = "";
						extraInput = false; 
					}
					break;
				case event:
					main.setType(ChronoTime.now(), disp.writeTo());
					GUIState = guis.wait;
					if(race)
						disp = main.getCard();
					else
						disp = new Card(0, 0);
					break;
				case print:
					if(curNum != "")
						main.printRun(ChronoTime.now(), Integer.parseInt(curNum));
					else
						main.printRun(ChronoTime.now());
					curNum = "";
					GUIState = guis.wait;
					extraInput = false;
					if(race)
						disp = main.getCard();
					else
						disp = new Card(0, 0);
					break;
					//later
				case export:
					if(curNum != "")
						main.exportRun(ChronoTime.now(), Integer.parseInt(curNum));
					else
						main.exportRun(ChronoTime.now());
					curNum = "";
					GUIState = guis.wait;
					extraInput = false;
					if(race)
						disp = main.getCard();
					else
						disp = new Card(0, 0);
					break;
					//later
				case timeh:
					if(curNum != "")
					{
						GUIState = guis.timem;
						hour = Integer.parseInt(curNum);
						curNum = "";
					}
				case timem:
					if(curNum != "")
					{
						GUIState = guis.times;
						min = Integer.parseInt(curNum);
						curNum = "";
					}
				case times:
					if(curNum != "")
					{
						GUIState = guis.wait;
						try{
							main.setTime(ChronoTime.now(), new ChronoTime(hour, min, Integer.parseInt(curNum), 0));	
						}
						catch (InvalidTimeException t)
						{
							System.out.println("time exception in setting time");
						}
						
						hour = min = 0;
						curNum = "";
						disp = new Card(0, 0);
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
				switch (GUIState) 
				{
				case wait:
					// start function protocol
					disp = new Menu(race);
					GUIState = guis.fcn;
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
					GUIState = guis.wait;
					if(race)
						disp = main.getCard();
					else
						disp = new Card(0, 0);
					break;
				default:

				}

				break;
			case "POWER":
				switch (GUIState) 
				{
				case off:
					main.powerOn(ChronoTime.now());
					disp = new SplashScreen();
					GUIState = guis.start;
					
					new java.util.Timer().schedule(new TimerTask() {
						
						@Override
						public void run() {
							disp = new Card(0, 0);
							GUIState = guis.wait;
						}
					}, STARTDELAY);
					
					break;
				default:
					main.powerOff(ChronoTime.now());
					GUIState = guis.off;
					curNum = "";
					race = false;
					hour = 0;
					min = 0;
					disp = new Card(0,0);
					printerPower = false;
					extraInput = false;
				}
				break;
			case "PRINTER POWER":
				switch (GUIState) 
				{
				case off:
					break;
				default:
					printerPower = !printerPower;	
				}
				break;
			case "STAR":
				switch (GUIState) 
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
					GUIState = guis.wait;
					disp = main.getCard();
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
				switch (GUIState) 
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
				switch (GUIState) 
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
				switch (GUIState) 
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
				switch (GUIState) 
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
				switch (GUIState) 
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
				switch (GUIState) 
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
				switch (GUIState) 
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
				switch (GUIState) 
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
				switch (GUIState) 
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
				switch (GUIState) 
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

			displayArea.setText(disp.getText());
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
				GUIState = guis.num;
				extraInput = true;
				return true;
			case "CLEAR":
				GUIState = guis.wait;
				disp = main.getCard();
				//clear does nothing?
				return false;
			case "CANCEL":
				GUIState = guis.wait;
				disp = main.getCard();
				main.cancel(ChronoTime.now());
				return false;
			case "DNF":
				GUIState = guis.wait;
				disp = main.getCard();
				main.dnf(ChronoTime.now());
				return false;
			case "ENDRUN":
				GUIState = guis.wait;
				disp = main.getCard();
				race = false;
				main.finRun(ChronoTime.now());
				return false;
			case "EVENT":
				GUIState = guis.event;
				disp = new EventMenu();
				return true;
			case "PRINT":
				if(printerPower)
				{
					extraInput = true;
					GUIState = guis.print;
					return true;
				}
				GUIState = guis.wait;
				if(race)
					disp = main.getCard();
				else
					disp = new Card(0, 0);
				return false;
			case "EXPORT":
				GUIState = guis.export;
				extraInput = true;
				return true;
			case "RESET":
				GUIState = guis.start;
				disp = new SplashScreen();
				main.powerOff(ChronoTime.now());
				main.powerOn(ChronoTime.now());
				new java.util.Timer().schedule(new TimerTask() {
					
					@Override
					public void run() {
						disp = new Card(0, 0);
						GUIState = guis.wait;
					}
				}, STARTDELAY);
				return false;
			case "NEWRUN":
				GUIState = guis.wait;
				main.newRun(ChronoTime.now());
				disp = main.getCard();
				race = true;
				return false;
			case "TIME":
				extraInput = true;
				GUIState = guis.timeh;
				return true;
			default:
				return false;
			}
		} catch (InvalidTimeException e) 
		{
			System.out.println("casey why?");
			return false;
		}
	}
}
