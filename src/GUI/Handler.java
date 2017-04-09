package GUI;

import javax.swing.JTextArea;

import ChronoTimer.ChronoTime;
import ChronoTimer.ChronoTrigger;
import Exceptions.InvalidTimeException;

public class Handler {
	
	JTextArea displayArea, printArea;
	ChronoTrigger main;
	
	guis GUIState;
	String curNum;
	boolean race;
	
	enum guis
	{
		off,	//synonymous with the cts.off state
		start,	//extra, unused right now? for when we are powering up the machine, similar to the off state.
		wait,	//default state, either display race panel if race started, or nothing? if no race
		fcn,	//in the function menus
		num,	//choosing a number for entry
		event	//choosing an event type
	}
	
	public Handler(JTextArea d, JTextArea p)
	{
		displayArea=d;
		printArea=p;
		main = new ChronoTrigger();
		GUIState = guis.off;//??? TODO @ Friday meeting
		curNum = "";
		race = false;
	}
	
	protected boolean issue(String command){
		displayArea.append(command);
		//switch each command to determine course of action
		try{
			switch(command)
			{
				/**
				 * if a run has been created
				 * allows the user to navigate the current runs list of participants
				 * if function has been pressed and 
				 * allows the user to move up choosing a function
				 */
				case "UP":
					switch (GUIState)
					{
					case fcn:
						//maneuver up in menu
						break;
					case wait:
						//should allow user to navigate the scroll view for this run
						if(race)
						{
							
						}
					default:
					
					}
					break;
				case "DOWN":
					switch (GUIState)
					{
					case fcn:
						//maneuver up in menu
						break;
					case wait:
						//should allow user to navigate the scroll view for this run
						if(race)
						{
							
						}
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
						//maneuver up in menu
						GUIState = guis.wait;
						break;
					default:
					
					}
					break;
					
					/**
					 * equivalent to an enter? only on function
					 */
				case "RIGHT":
					//this and star should have roughly the same behavior?
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
						//start function protocol
						
						if(race)
						{
							
						}
						break;
					default:
					
					}
					
					break;
				case "POWER":
					switch (GUIState)
					{
					case off :
						main.powerOn(ChronoTime.now());
						GUIState = guis.wait;
						break;
					default :
						main.powerOff(ChronoTime.now());
						GUIState = guis.off;
					}
					break;
				case "PRINTER POWER":
					//no idea what we are doing here, need another class for printer state?
					//TODO @Friday
					break;
				case "STAR":
					switch (GUIState)
					{
					case num:
						curNum = "";
						break;
					case fcn:
						//return to defaultState
					default:
						//do nothing
					}
					break;
				case "POUND":
					//enter key
					break;
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
				case "PLUG 1":
					main.connectSensor(ChronoTime.now(), 1, "EYE");
					break;
				case "PLUG 2":
					main.connectSensor(ChronoTime.now(), 2, "EYE");
					break;
				case "PLUG 3":
					main.connectSensor(ChronoTime.now(), 3, "EYE");
					break;
				case "PLUG 4":
					main.connectSensor(ChronoTime.now(), 4, "EYE");
					break;
				case "PLUG 5":
					main.connectSensor(ChronoTime.now(), 5, "EYE");
					break;
				case "PLUG 6":
					main.connectSensor(ChronoTime.now(), 6, "EYE");
					break;
				case "PLUG 7":
					main.connectSensor(ChronoTime.now(), 7, "EYE");
					break;
				case "PLUG 8":
					main.connectSensor(ChronoTime.now(), 8, "EYE");
					break;
				case "UNPLUG 1":
					main.disSensor(ChronoTime.now(), 1);
					break;
				case "UNPLUG 2":
					main.disSensor(ChronoTime.now(), 2);
					break;
				case "UNPLUG 3":
					main.disSensor(ChronoTime.now(), 3);
					break;
				case "UNPLUG 4":
					main.disSensor(ChronoTime.now(), 4);
					break;
				case "UNPLUG 5":
					main.disSensor(ChronoTime.now(), 5);
					break;
				case "UNPLUG 6":
					main.disSensor(ChronoTime.now(), 6);
					break;
				case "UNPLUG 7":
					main.disSensor(ChronoTime.now(), 7);
					break;
				case "UNPLUG 8":
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
						if(curNum.length() < 4)
							curNum += "1";
						break;
					default:
						//do nothing
					}
					break;
				case "NUM 2":
					switch (GUIState)
					{
					case num:
						if(curNum.length() < 4)
							curNum += "2";
						break;
					case event:
						
					default:
						//do nothing
					}
					break;
				case "NUM 3":
					switch (GUIState)
					{
					case num:
						if(curNum.length() < 4)
							curNum += "3";
						break;
					default:
						//do nothing
					}
					break;
				case "NUM 4":
					switch (GUIState)
					{
					case num:
						if(curNum.length() < 4)
							curNum += "4";
						break;
					default:
						//do nothing
					}

					break;
				case "NUM 5":
					switch (GUIState)
					{
					case num:
						if(curNum.length() < 4)
							curNum += "5";
						break;
					default:
						//do nothing
					}

					break;
				case "NUM 6":
					switch (GUIState)
					{
					case num:
						if(curNum.length() < 4)
							curNum += "6";
						break;
					default:
						//do nothing
					}

					break;
				case "NUM 7":
					switch (GUIState)
					{
					case num:
						if(curNum.length() < 4)
							curNum += "7";
						break;
					default:
						//do nothing
					}

					break;
				case "NUM 8":
					switch (GUIState)
					{
					case num:
						if(curNum.length() < 4)
							curNum += "8";
						break;
					default:
						//do nothing
					}

					break;
				case "NUM 9":
					switch (GUIState)
					{
					case num:
						if(curNum.length() < 4)
							curNum += "9";
						break;
					default:
						//do nothing
					}

					break;
				case "NUM 0":
					switch (GUIState)
					{
					case num:
						if(curNum.length() < 4 && curNum.length() > 0)//extra check so no 0023 racer
							curNum += "0";
						break;
					default:
						//do nothing
					}

					break;
				default :
					
			}
			//update output? here or earlier?
			return true;
		}
		catch (InvalidTimeException e)
		{
			System.out.println("casey why?");
		}
		//if we do not succesfully execute, return false
		return false;
	}
}

