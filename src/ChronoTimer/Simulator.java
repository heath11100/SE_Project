package ChronoTimer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

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
	 * for export and print
	 */
	static final String RACEFORMAT = "[0-9]+";
	
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
	
	static final boolean _reportOn = true;
	
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
		System.out.println("Error encountered during argument read:"+message);
		System.out.println("Expected: <filename>");
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
		String cCmd = ""; //current command, more like the current parsing token, but i like ccmd so sue me
		String tokens[] = {""};
		int cToken = 0;
		boolean power = false;
		
		ChronoTime cTime = null;
		ChronoTrigger sim = null;
		
		//Set up Scanner
		if(args.length > 1)
		{
			warning("Improper number of arguments.");
			report("Using console commands...");
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
				report("Using console commands...");
				input = new Scanner(System.in);
			}
		}
		else
		{
			report("Using console commands...");
			input = new Scanner(System.in);
		}
		
		sim = new ChronoTrigger();
		
		//Simulator Loop
		input.useDelimiter(DELIMITERS);
		while (state != END)
		{
			
			//Read the next line
			cToken = 0;
			try
			{
				//cCmd = fparse ? tokens[tokens.length - 1] : input.next().trim().toUpperCase();
				cCmd = input.next().trim().toUpperCase();
			}
			catch (NoSuchElementException ex)
			{
				if(fread_m && !input.hasNext())//have we reached end of file?
					state = END;
				else
					warning("Unhandled IOException.");
			}
			
			//Split command into tokens
			tokens = cCmd.split("\\s");
			report(cCmd);
			if (fread_m && tokens.length < 2)
				warning("Improper token matching in file read, did you forget a timestamp?");
			else if(tokens.length < 1)
				warning("Not enough tokens.");
			else
			{
				
				try
				{
					//Set simulator time variable
					if(fread_m)
						cTime = new ChronoTime(tokens[cToken++]);
					else
						cTime = ChronoTime.now();
					
					//Branch based on command
					switch(tokens[cToken++])
					{
					case "POWER":
						if(power)
							sim.powerOff(ChronoTime.now());
						else{
							sim.powerOn(ChronoTime.now());
							System.out.println(" > ChronoTrigger is off.");
							sim = null;}
						break;
					case "EXIT":
						System.out.println("Exiting simulator.");
						state = 1;
						break;
					case "RESET":
						sim.powerOff(ChronoTime.now());
						sim.powerOn(ChronoTime.now());
						break;
					case "TIME":
						if(tokens[cToken].matches(TIMEFORMAT))
							sim.setTime(cTime, new ChronoTime(tokens[cToken++]));
						else
							throw new InvalidCommandException("TimeFormat, time");
						break;
					case "TOG":
						if(tokens[cToken].matches(CHANNELFORMAT))
							sim.toggle(cTime, Integer.parseInt(tokens[cToken++]));
						else
							throw new InvalidCommandException("Channel format, tog");
						break;
					case "CONN": //not used in sprint 1
						break;
					case "DISC": //not used in sprint 1
						break;
					case "EVENT": ;
						if(tokens[cToken].matches(EVENTFORMAT))
							sim.setType(cTime, tokens[cToken++]);//fix this
						else
							throw new InvalidCommandException("Event format, event");
						break;
					case "NEWRUN": 
						sim.newRun(cTime);
						break;
					case "ENDRUN": 
						sim.finRun(cTime);
						break;
					case "PRINT":
						if(cToken == tokens.length)
							sim.printRun(cTime);
						else
							if(tokens[cToken].matches(RACEFORMAT))
								sim.printRun(cTime, Integer.parseInt(tokens[cToken++]));
							else
								throw new InvalidCommandException("Race format, print");
						
						//must overload this to take no args OR race number
						break;
					case "EXPORT":
						if(cToken == tokens.length)
							sim.exportRun(cTime);
						else
							if(tokens[cToken].matches(RACEFORMAT))
								sim.exportRun(cTime, Integer.parseInt(tokens[cToken++]));
							else
								throw new InvalidCommandException("Race format, export");
						//must overload this to take no args OR race number
						break;
					case "NUM":
						if(tokens[cToken].matches(RUNNERFORMAT))
							sim.addRacer(cTime, Integer.parseInt(tokens[cToken++]));
						else
							throw new InvalidCommandException("Runner format, num");
						break;
					case "CLR":
						//no action yet provided
						break;
					case "SWAP": //not used in sprint 2
						break;
					case "CANCEL":
						sim.cancel(cTime);
						break;
					case "DNF": 
						sim.dnf(cTime);
						break;
					case "TRIG":
						if(tokens[cToken].matches(CHANNELFORMAT))
							sim.triggerSensor(cTime, Integer.parseInt(tokens[cToken++]));
						else
							throw new InvalidCommandException("Channel format, trig");
						break;
					case "START":
						sim.triggerSensor(cTime, 1);
						break;
					case "FINISH":
						sim.triggerSensor(cTime, 2);
						break;
					default:
						report("Could not parse command.");
					}
				}
				catch (ArrayIndexOutOfBoundsException ex)
				{
					report("Innapropriate number of tokens.");
				}
				catch (NullPointerException ex){report("Have not turned power on ye");}
				catch (InvalidTimeException ex) {report("Error: incorrect time format.");}
				catch (InvalidCommandException ex) {
					report("error: " + ex.getMessage());
				}
				if(cToken != tokens.length)
					report("Error: too many words in command.");
			}
			
		}
		//after loop
		input.close();
	}
}
