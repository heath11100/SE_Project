package ChronoTimer;
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
	
	final String COMMANDFORMAT = "^((POWER)|(EXIT)|(RESET)|(TIME)|(TOG)|(CONN)|(DISC)|(EVENT)|(NEWRUN)|(ENDRUN)|(PRINT)|(EXPORT)|(NUM)|(CLR)|(SWAP)|(DNF)|(TRIG)|(START)|(FINISH))[\t\f ].*";
	
	final String TIMEFORMAT = ".*[\t\f ][0-9]+.[0-9]{1,2}.[0-9]{1,2}";
	
	final String CHANNELFORMAT = ".*[\t\f ][1-8]";
	
	final String SENSORFORMAT = ".*[\t\f ]EYE|GATE|PAD";
	
	final String RUNNERFORMAT = ".*[\t\f ][0-9]{1,4}";//ensure a valid number of runners
	
	final String EVENTFORMAT = "IND|PARIND|GRP|PARGRP";
	
	public static void report(String message){System.out.println(message);}
	
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
		DNF ("DNF"),
		TRIG ("TRIG"),
		START ("START"),
		FINISH ("FINISH");
		
		public final String word;
		COMMANDS(String w){word = w;}
	}
	
private static warning(String message)
{
	System.out.println("error encountered during argument read, issue encountered is:");
	System.out.println(message);
	System.out.println("expected: <filename>");
	System.exit(1);
}
	
public static main(String args[])
{
	//note: ask TA in lab how input via command file is expected
	//need an arg to read from a file i/o
	Scanner input;
	if(args.length != 1 || args.length != 0)
		warning("improper number of arguments");
	else if(args.length = 1)
	{
		try
		{
			input = new Scanner(new FileReader(args[0]));
		}
		catch (FileNotFoundException e) 
		{
			System.out.println("File not found: " + args[0]);
			return;
		}
	}
	else
		input = new Scanner(System.in);
	
	int state = 0;
	String curLine = "";
	String curCommand = "";
	while(state != 1)
	{
			//read input
			curLine = input.next();
			//set capitalization
			curLine.toUpper();
			curLine = curLine.trim();
			
			//check valid command
			if(curLine.matches(COMMANDFORMAT))
			{
				try
				{
					curCommand = curLine.substring(0, curLine.indexOf(" "));
					switch(curCommand)
					{
					case COMMAND.POWER:
						break;
					case COMMAND.EXIT:
						state = 1;
						report("exiting simulator");
						break;
					case COMMAND.RESET:
						break;
					case COMMAND.TIME:
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
					case COMMAND.DNF:
						break;
					case COMMAND.TRIG:
						break;
					case COMMAND.START:
						break;
					case COMMAND.FINISH:
						break;
					case default:
						report("could not parse command");
					}
				}
				catch (InvalidCommandException ex)
				{
					report("InvalidCommandException, could complete command: " + curCommand + "in line" + curLine);
					report(ex.message);
				}
				catch (InvalidTimeException ex)
				{
					report("InvalidTimeException, could complete command: " + curCommand + "in line" + curLine);
					report(ex.message);
				}
				
			}

	}
}
