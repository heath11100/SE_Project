package ChronoTimer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;
import ChronoTimer.Race.EventType;

import Exceptions.InvalidCommandException;
import Exceptions.InvalidTimeException;

/*
	 __    __     ______     ______   ______      _____     ______     __    __     ______     __   __     ______                     
	/\ "-./  \   /\  __ \   /\__  _\ /\__  _\    /\  __-.  /\  __ \   /\ "-./  \   /\  __ \   /\ "-.\ \   /\  ___\                    
	\ \ \-./\ \  \ \  __ \  \/_/\ \/ \/_/\ \/    \ \ \/\ \ \ \  __ \  \ \ \-./\ \  \ \ \/\ \  \ \ \-.  \  \ \___  \                   
	 \ \_\ \ \_\  \ \_\ \_\    \ \_\    \ \_\     \ \____-  \ \_\ \_\  \ \_\ \ \_\  \ \_____\  \ \_\\"\_\  \/\_____\                  
	  \/_/  \/_/   \/_/\/_/     \/_/     \/_/      \/____/   \/_/\/_/   \/_/  \/_/   \/_____/   \/_/ \/_/   \/_____/                  
																																	
	 ______     ______     ______     __   __     ______        ______   ______     __     ______     ______     ______     ______    
	/\  ___\   /\  == \   /\  __ \   /\ "-.\ \   /\  __ \      /\__  _\ /\  == \   /\ \   /\  ___\   /\  ___\   /\  ___\   /\  == \   
	\ \ \____  \ \  __<   \ \ \/\ \  \ \ \-.  \  \ \ \/\ \     \/_/\ \/ \ \  __<   \ \ \  \ \ \__ \  \ \ \__ \  \ \  __\   \ \  __<   
	 \ \_____\  \ \_\ \_\  \ \_____\  \ \_\\"\_\  \ \_____\       \ \_\  \ \_\ \_\  \ \_\  \ \_____\  \ \_____\  \ \_____\  \ \_\ \_\ 
	  \/_____/   \/_/ /_/   \/_____/   \/_/ \/_/   \/_____/        \/_/   \/_/ /_/   \/_/   \/_____/   \/_____/   \/_____/   \/_/ /_/ 
	*/
public class Simulator {
	//simulator class
	//Ryan Thorne
	//2/23/2017
	//ver 0.3
	//commit 
	static final String COMMANDFORMAT = "((POWER)|(EXIT)|(RESET)|(TIME)|(TOG)|(CONN)|(DISC)|(EVENT)|(NEWRUN)|(ENDRUN)|(PRINT)|(EXPORT)|(NUM)|(CLR)|(SWAP)|(DNF)|(CANCEL)|(TRIG)|(START)|(FINISH))";
	
	/**
	 * timeformat can only be in the format of <min>:<sec>.<hund>
	 * also i am unsure of how to express the max in this format so i'll just assume any number of minutes is ok and catch time exceptions
	 */
	static final String TIMEFORMAT = "([0-9]+.[0-5][0-9].[0-9]{1,2})|([0-9]+.[0-9][0-9].[0-5][0-9].[0-9]{1,2})";
	
	/**
	 * for later use
	 */
	static final String CHANNELFORMAT = "[1-8]";
	
	/**
	 * for later use
	 */
	static final String SENSORFORMAT = "EYE|GATE|PAD";
	
	/**
	 * allows up to 9999 runners
	 */
	static final String RUNNERFORMAT = "[0-9]{1,4}";//ensure a valid number of runners
	
	/**
	 * not used/assuming ind for now
	 */
	static final String EVENTFORMAT = "IND|PARIND|GRP|PARGRP";
	
	/**
	 * this allows me to scan for delimiters in the scanner. since the scanner usually uses whitespace,
	 * this allows me to scan for other delimiters specifically tabs and newlines, rather than plain spaces
	 */
	 
	static final String DELIMITERS = "\\n";
	
	static final int RUN = 0, END = 1;
	
	static final boolean _reportOn = false;
	
	public enum COMMAND
	{
		POWER ("POWER"),
		EXIT ("EXIT"),
		RESET ("RESET"),
		TIME ("TIME"),
		TOG ("TOG"),
		CONN ("CONN"),//not used in sprint 1
		DISC ("DISC"),//not used in sprint 1
		EVENT ("EVENT"),
		NEWRUN ("NEWRUN"),
		ENDRUN ("ENDRUN"),
		PRINT ("PRINT"),//not used in sprint 1
		EXPORT ("EXPORT"),//not used in sprint 1
		NUM ("NUM"),
		CLR ("CLR"),//not used in sprint 1
		SWAP ("SWAP"),//not used in sprint 1
		CANCEL ("CANCEL"),
		DNF ("DNF"),
		TRIG ("TRIG"),
		START ("START"),
		FINISH ("FINISH");
		
		public final String word;
		COMMAND(String w){word = w;}
		//@Overwrite
		public String toString(){return this.word;}
	}
	
	/**
	 * displays a message to the user on a failed instance on the SIMULATORS side
	 * should be handled by the simulaor, not the cronotrigger
	 */
	private static void warning(String message)
	{
		System.out.println("error encountered during argument read, issue encountered is:");
		System.out.println(message);
		System.out.println("expected: <filename>");
		System.exit(1);
	}
	
	private static void report(String message)
	{
		if(_reportOn)
			System.out.println(message);
	}
	
	public static void main(String args[])
	{
		Scanner input;
		boolean fread_m = false;	//tells the simulator whether to keep track of its own time or use the provided file times
		int state = RUN;	//changes so that we can terminate the simulator
		boolean fparse = false;	//built in detection of failed parse will try to parse that particular command in the next loop
		String cCmd = ""; //current command, more like the current parsing token, but i like ccmd so sue me
		String tokens[] = {""};
		int cToken = 0;
		Date t = new Date();
		
		ChronoTime cTime;
		cTime = null;
		
		ChronoTrigger sim = null;
		if(args.length != 1 && args.length != 0)
		{
			warning("improper number of arguments");
			input = new Scanner(System.in);
		}
		else if(args.length == 1)
		{
			try
			{
				input = new Scanner(new FileReader(args[0]));
				fread_m = true;
			}
			catch (FileNotFoundException e) 
			{
				warning("File not found: " + args[0]);
				return;
			}
		}
		else
		{
			report("using console commands");
			input = new Scanner(System.in);
		}
		
		input.useDelimiter(DELIMITERS);
		
		//simulator loop
		while (state != END)
		{
			cToken = 0;
			try
			{
				//cCmd = fparse ? tokens[tokens.length - 1] : input.next().trim().toUpperCase();
				cCmd = input.next().trim().toUpperCase();
				fparse = false;
			}
			catch (NoSuchElementException ex)
			{
				if(fread_m && !input.hasNext())//have we reached end of file?
					state = END;
				else
					warning("unhandled IOException");
			}
			tokens = cCmd.split("\\s");
			System.out.println(cCmd);
			if (fread_m && tokens.length < 2)
				warning("improper token matching in file read, did you forget a timestamp?");
			else if(tokens.length < 1)
				warning("not enough tokens");
			else
			{
				try
				{
					if(fread_m)
					{
						cTime = new ChronoTime(tokens[cToken++]);
					}
					else
					{ 
						cTime = ChronoTime.now();
					}
					switch(tokens[cToken++])
					{
						//may need to add a ".word" to each usage of COMMANDS.W/E note this
					case "POWER":
						report(COMMAND.POWER.word);
						//I think this is what we want
						if(sim == null)
						{
							sim = new ChronoTrigger(cTime);
							report("power on");
						}
						else
							sim = null;
						break;
					case "EXIT":
						report(COMMAND.EXIT.word);
						report("exiting simulator");
						state = 1;
						break;
					case "RESET":
						report(COMMAND.RESET.word);
						sim = new ChronoTrigger(cTime);
						break;
					case "TIME":
						report(COMMAND.TIME.word);
						if(tokens[cToken].matches(TIMEFORMAT))
							sim.setTime(cTime, new ChronoTime(tokens[cToken++]));
						else
							throw new InvalidCommandException("TimeFormat, time");
						break;
					case "TOG":
						report(COMMAND.TOG.word);
						if(tokens[cToken].matches(CHANNELFORMAT))
							sim.toggle(cTime, Integer.parseInt(tokens[cToken++]));
						else
							throw new InvalidCommandException("channel format, tog");
						break;
					case "CONN": //not used in sprint 1
						report(COMMAND.CONN.word);
						break;
					case "DISC": //not used in sprint 1
						report(COMMAND.DISC.word);
						break;
					case "EVENT": 
						report(COMMAND.EVENT.word);
						if(tokens[cToken].matches(EVENTFORMAT))
						{
							sim.setType(cTime, tokens[cToken]);//fix this
						}
						else
							throw new InvalidCommandException("event format, event");
						break;
					case "NEWRUN": 
						report(COMMAND.NEWRUN.word);
						sim.newRace(cTime);
						break;
					case "ENDRUN": 
						report(COMMAND.ENDRUN.word);
						sim.finRace(cTime);
						break;
					case "PRINT": //not used in sprint 1
						report(COMMAND.PRINT.word);
						sim.printCurRace(cTime);
						//test data conflicts with sprint 0 details again, discuss with group add race param or no? 
						break;
					case "EXPORT": //not used in sprint 1
						report(COMMAND.EXPORT.word);
						break;
					case "NUM":
						report(COMMAND.NUM.word);
						if(tokens[cToken].matches(RUNNERFORMAT))
						{
							sim.addRacer(cTime, Integer.parseInt(tokens[cToken++]));
						}
						else
							throw new InvalidCommandException("runner format, num");
						break;
					case "CLR":
						report(COMMAND.CLR.word);	//not a cancel command, not used ion sprint 1?
//						if(tokens[cToken].matches(RUNNERFORMAT))
//						{
//							//idk what this command should do
//						}
//						else
//							throw new InvalidCommandException("runner format, clr");
						break;
					case "SWAP": //not used in sprint 1
						report(COMMAND.SWAP.word);
						break;
					case "CANCEL":
						report(COMMAND.CANCEL.word);
						sim.cancel(cTime);// no information to give here I think?
						break;
					case "DNF": 
						report(COMMAND.DNF.word);
						sim.dnf(cTime);
						break;
					case "TRIG":
						report(COMMAND.TRIG.word);
						//I think toggle is the command used here?
						if(tokens[cToken].matches(CHANNELFORMAT))
							sim.triggerSensor(cTime, Integer.parseInt(tokens[cToken++]));
						else
							throw new InvalidCommandException("channel format, trig");
						break;
					case "START":
						report(COMMAND.START.word);
							//per instructions we will toggle 1 here we can change this to be using sim.start()
						sim.triggerSensor(cTime, 1);
						break;
					case "FINISH":
						report(COMMAND.FINISH.word);
						sim.triggerSensor(cTime, 2);
						break;
					default:
						report("Could not parse command");
						fparse = true;
					}
				}
				catch (ArrayIndexOutOfBoundsException ex)
				{
					report("Innapropriate number of tokens");
					fparse = true;
				}
				catch (NullPointerException ex)
				{
					report("Have not turned power on yet");
				} 
				catch (InvalidTimeException ex) {
					// TODO Auto-generated catch block
					report("error: incorrect time format");
				} 
				catch (InvalidCommandException ex) {
					// TODO Auto-generated catch block
					report("error: " + ex.getMessage());
					fparse = true;
				}
				if(cToken != tokens.length)
					report("error: too many characters");
			}
			
		}
		//after loop
		input.close();
	}
}
