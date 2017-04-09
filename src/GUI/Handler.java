package GUI;

import javax.swing.JTextArea;
import javax.swing.Timer;

import ChronoTimer.ChronoTime;
import ChronoTimer.ChronoTrigger;
import ChronoTimer.Menu;
import ChronoTimer.UIPrint;
import Exceptions.InvalidTimeException;

/*
 * Ryan Thorne
 * Matt Damon's ChronoTrigger
 */

public class Handler {

	JTextArea displayArea, printArea;
	ChronoTrigger main;

	guis GUIState;
	String curNum;
	boolean race;
	UIPrint disp;

	public enum guis {
		off, // synonymous with the cts.off state
		start, // extra, unused right now? for when we are powering up the
				// machine, similar to the off state.
		wait, // default state, either display race panel if race started, or
				// nothing? if no race
		fcn, // in the function menus
		num, // choosing a number for entry
		event // choosing an event type
	}

	public Handler(JTextArea d, JTextArea p) {
		displayArea = d;
		printArea = p;
		main = new ChronoTrigger();
		GUIState = guis.off;
		curNum = "";
		race = false;
		Timer updater = new Timer(1000, new Listener(this, "UPDATE"));
		updater.setInitialDelay(100);
		updater.start();
	}

	protected boolean issue(String command) 
	{
		displayArea.append(command);
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
					disp.up();
					break;
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
					disp.down();
					break;
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
					// maneuver up in menu
					GUIState = guis.wait;
					break;
				default:

				}
				break;

			/**
			 * equivalent to an enter? only on function
			 */
			case "RIGHT":
				// this and star should have roughly the same behavior?
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
				case fcn: 
				case num:
				case event:
					// start function protocol
					GUIState = guis.wait;
					disp = main.getCard();//need this method
					break;
				default:

				}

				break;
			case "POWER":
				switch (GUIState) 
				{
				case off:
					main.powerOn(ChronoTime.now());
					GUIState = guis.wait;
					break;
				default:
					main.powerOff(ChronoTime.now());
					GUIState = guis.off;
				}
				break;
			case "PRINTER POWER":
				// no idea what we are doing here, need another class for
				// printer state?
				// TODO @Friday
				break;
			case "STAR":
				switch (GUIState) 
				{
				case num:
					curNum = "";
					break;
				case fcn:
					// return to defaultState
				default:
					// do nothing
				}
				break;
			case "POUND":
				// enter key
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
				case num:
					if (curNum.length() < 4)
						curNum += "1";
					break;
				default:
					// do nothing
				}
				break;
			case "NUM 2":
				switch (GUIState) 
				{
				case num:
					if (curNum.length() < 4)
						curNum += "2";
					break;
				case event:

				default:
					// do nothing
				}
				break;
			case "NUM 3":
				switch (GUIState) 
				{
				case num:
					if (curNum.length() < 4)
						curNum += "3";
					break;
				default:
					// do nothing
				}
				break;
			case "NUM 4":
				switch (GUIState) 
				{
				case num:
					if (curNum.length() < 4)
						curNum += "4";
					break;
				default:
					// do nothing
				}

				break;
			case "NUM 5":
				switch (GUIState) 
				{
				case num:
					if (curNum.length() < 4)
						curNum += "5";
					break;
				default:
					// do nothing
				}

				break;
			case "NUM 6":
				switch (GUIState) 
				{
				case num:
					if (curNum.length() < 4)
						curNum += "6";
					break;
				default:
					// do nothing
				}

				break;
			case "NUM 7":
				switch (GUIState) 
				{
				case num:
					if (curNum.length() < 4)
						curNum += "7";
					break;
				default:
					// do nothing
				}

				break;
			case "NUM 8":
				switch (GUIState) 
				{
				case num:
					if (curNum.length() < 4)
						curNum += "8";
					break;
				default:
					// do nothing
				}

				break;
			case "NUM 9":
				switch (GUIState) 
				{
				case num:
					if (curNum.length() < 4)
						curNum += "9";
					break;
				default:
					// do nothing
				}

				break;
			case "NUM 0":
				switch (GUIState) 
				{
				case num:
					if (curNum.length() < 4 && curNum.length() > 0)// extra
																	// check so
																	// no 0023
																	// racer
						curNum += "0";
					break;
				default:
					// do nothing
				}

				break;
			default:

			}
			
			
			return true;
		} catch (InvalidTimeException e) 
		{
			System.out.println("casey why?");
		}
		// if we do not succesfully execute, return false
		return false;
	}
}
