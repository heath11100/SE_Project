package ChronoTimer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
	//instructor sneakily formatted file commands wirdly so the previous simulator's logic loop was broken

	static final String COMMANDFORMAT = "((POWER)|(EXIT)|(RESET)|(TIME)|(TOG)|(CONN)|(DISC)|(EVENT)|(NEWRUN)|(ENDRUN)|(PRINT)|(EXPORT)|(NUM)|(CLR)|(SWAP)|(DNF)|(CANCEL)|(TRIG)|(START)|(FINISH))";
	
	/**
	 * timeformato can only be in the format of <min>:<sec>.<hund>
	 * also i am unsure of how to express the max in this format so i'll just assum any number of minutes is ok and catch time exceptions
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
	 
	static final String DELIMITERS = "\t\n";
	
	static final int RUN = 0, END = 1;
	
	public enum COMMAND
	{
		POWER ("POWER"),
		EXIT ("EXIT"),
		RESET ("RESET"),
		TIME ("TIME"),
		TOG ("TOG"),//not used in sprint 1
		CONN ("CONN"),//not used in sprint 1
		DISC ("DISC"),//not used in sprint 1
		EVENT ("EVENT"),//not used in sprint 1
		NEWRUN ("NEWRUN"),//not used in sprint 1
		ENDRUN ("ENDRUN"),//not used in sprint 1
		PRINT ("PRINT"),//not used in sprint 1
		EXPORT ("EXPORT"),//not used in sprint 1
		NUM ("NUM"),//not used in sprint 1
		CLR ("CLR"),//not used in sprint 1
		SWAP ("SWAP"),//not used in sprint 1
		CANCEL ("CANCEL"),
		DNF ("DNF"),
		TRIG ("TRIG"),
		START ("START"),
		FINISH ("FINISH");
		
		public final String word;
		COMMAND(String w){word = w;}
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
	
	public static void main(String args[])
	{
		Scanner input;
		boolean fread_m = false;	//tells the simulator whether to keep track of its own time or use the provided file times
		int state = RUN;	//changes so that we can terminate the simulator
		boolean fparse = false;	//built in detection of failed parse will try to parse that particular command in the next loop
		String cCmd = ""; //current command, more like the current parsing token, but i like ccmd so sue me
		String tokens[];
		int cToken = 0;
		ChronoTime cTime;
		ChronoTrigger sim;
		if(args.length != 1 || args.length != 0)
			warning("improper number of arguments");
		else if(args.length == 1)
		{
			try
			{
				input = new Scanner(new FileReader(args[0]));
			}
			catch (FileNotFoundException e) 
			{
				warning("File not found: " + args[0]);
				return;
			}
			//can't fail statement
			fread_m = true;
		}
		else
		{
			input = new Scanner(System.in);
		}
		
		input.useDelimiter(DELIMITERS);
		
		//simulator loop
		while (state != END)
		{
			cToken = 0;
			try
			{
				cCmd = fparse ? cCmd : input.next().trim().toUpperCase();
			}
			catch (IOException ex)
			{
				if(fread_m && !input.hasNext())//have we reached end of file?
					state = END;
				else
					warning("unhandled IOException");
			}
			tokens = cCmd.split(" ");
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
						cTime = new ChronoTime(new Time())//will redefine this
					}
					switch(tokens[cToken++])
					{
						//may need to add a ".word" to each usage of COMMANDS.W/E note this
					case COMMAND.POWER:
						report(COMMAND.POWER);
						//I think this is what we want
						if(sim == null)
							sim = new CronoTrigger(cTime);
						else
							sim = null;
						break;
					case COMMAND.EXIT:
						report(COMMAND.EXIT);
						report("exiting simulator");
						state = 1;
						break;
					case COMMAND.RESET:
						report(COMMAND.RESET);
						sim.reset();
						break;
					case COMMAND.TIME:
						report(COMMAND.TIME);
						if(tokens[cToken].matches(TIMEFORMAT))
							sim.setTime(new CronoTime(tokens[cToken++]);
						else
							report("invalid time format passed to parser");//find a better way to reuse code
						break;
						break;
					case COMMAND.TOG: //not used in sprint 1
						break;
					case COMMAND.CONN: //not used in sprint 1
						break;
					case COMMAND.DISC: //not used in sprint 1
						break;
					case COMMAND.EVENT: //not used in sprint 1
						break;
					case COMMAND.NEWRUN: //not used in sprint 1
						break;
					case COMMAND.ENDRUN: //not used in sprint 1
						break;
					case COMMAND.PRINT: //not used in sprint 1
						break;
					case COMMAND.EXPORT: //not used in sprint 1
						break;
					case COMMAND.NUM: //not used in sprint 1
						break;
					case COMMAND.CLR: //not used in sprint 1
						break;
					case COMMAND.SWAP: //not used in sprint 1
						break;
					case COMMAND.CANCEL
						report(COMMAND.CANCEL);
						sim.cancel();// no information to give here I think?
						break;
					case COMMAND.DNF: 
						report(COMMAND.DNF);
						sim.didNotFinish()//no extra parameters here idk what to give you
						break;
					case COMMAND.TRIG:
						report(COMMAND.TRIG);
						//I think toggle is the command used here?
						if(tokens[cToken].matches(CHANNELFORMAT))
							sim.toggle(Integer.parseInt(tokens[cToken++]));
						else
							report("could not accept channel");//find a better way to reuse code
						break;
					case COMMAND.START:
						report(COMMAND.START);
							//per instructions we will toggle 1 here we can change this to be using sim.start()
							toggle(1);
						break;
					case COMMAND.FINISH:
						report(COMMAND.FINISH);
							toggle(2);
						break;
					case default:
						report("Could not parse command");
					}
				}
				catch (ArrayIndexOutOfBoundsException ex)
				{
					report("Innapropriate number of tokens")
				}
				catch (NullPointerException ex)
				{
					report("Have not initialized cronotrigger yet")
				}
				if(cToken != tokens.length)
					report("too many characters");
			}
			
		}
	}
}
