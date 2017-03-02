package ChronoTimer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

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
	//ver 0.2

	static final String COMMANDFORMAT = "((POWER)|(EXIT)|(RESET)|(TIME)|(TOG)|(CONN)|(DISC)|(EVENT)|(NEWRUN)|(ENDRUN)|(PRINT)|(EXPORT)|(NUM)|(CLR)|(SWAP)|(DNF)|(CANCEL)|(TRIG)|(START)|(FINISH))";
	
	/**
	 * timeformat can only be in the format of <min>:<sec>.<hund>
	 * also i am unsure of how to express the max in this format so i'll just assume any number of minutes is ok and catch time exceptions
	 */
	static final String TIMEFORMAT = "[0-9]+.[0-5][0-9].[0-9]{1,2}";
	
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
		ChronoTime cTime;
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
			report("read token: " + cCmd);
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
						//WARNING this needs to be implemented in CronoTime b4 this will compile
						//cTime = new ChronoTime(new Time())//will redefine this
					}
					switch(tokens[cToken++])
					{
						//may need to add a ".word" to each usage of COMMANDS.W/E note this
					case "POWER":
						report(COMMAND.POWER.word);
						//I think this is what we want
						if(sim == null)
							;//sim = new CronoTrigger(cTime);
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
						//sim.reset();
						break;
					case "TIME":
						report(COMMAND.TIME.word);
						if(tokens[cToken++].matches(TIMEFORMAT))
							;//sim.setTime(new CronoTime(tokens[cToken++]);
						else
							throw new InvalidCommandException("TimeFormat, time");
						break;
					case "TOG":
						report(COMMAND.TOG.word);
						if(tokens[cToken++].matches(CHANNELFORMAT))
							;//sim.setTime(new CronoTime(tokens[cToken++]);
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
						if(tokens[cToken++].matches(EVENTFORMAT))
						{
							
						}
						else
							throw new InvalidCommandException("event format, event");
						break;
					case "NEWRUN": 
						report(COMMAND.NEWRUN.word);
						break;
					case "ENDRUN": 
						report(COMMAND.ENDRUN.word);
						break;
					case "PRINT": //not used in sprint 1
						report(COMMAND.PRINT.word);
						break;
					case "EXPORT": //not used in sprint 1
						report(COMMAND.EXPORT.word);
						break;
					case "NUM":
						report(COMMAND.NUM.word);
						if(tokens[cToken++].matches(RUNNERFORMAT))
						{
							
						}
						else
							throw new InvalidCommandException("runner format, num");
						break;
					case "CLR":
						report(COMMAND.CLR.word);	//not a cancel command
						if(tokens[cToken++].matches(RUNNERFORMAT))
						{
							
						}
						else
							throw new InvalidCommandException("runner format, clr");
						break;
					case "SWAP": //not used in sprint 1
						report(COMMAND.SWAP.word);
						break;
					case "CANCEL":
						report(COMMAND.CANCEL.word);
						//sim.cancel();// no information to give here I think?
						break;
					case "DNF": 
						report(COMMAND.DNF.word);
						//sim.didNotFinish();
						break;
					case "TRIG":
						report(COMMAND.TRIG.word);
						//I think toggle is the command used here?
						if(tokens[cToken++].matches(CHANNELFORMAT))
							;//sim.toggle(Integer.parseInt(tokens[cToken++]));
						else
							throw new InvalidCommandException("channel format, trig");
						break;
					case "START":
						report(COMMAND.START.word);
							//per instructions we will toggle 1 here we can change this to be using sim.start()
							//sim.toggle(1);
						break;
					case "FINISH":
						report(COMMAND.FINISH.word);
							//sim.toggle(2);
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
					report("incorrect time format");
				} 
				catch (InvalidCommandException ex) {
					// TODO Auto-generated catch block
					report("error: " + ex.getMessage());
					fparse = true;
				}
				if(cToken != tokens.length)
					report("too many characters");
			}
			
		}
		//after loop
		input.close();
	}
}
