package ChronoTimer;
	 /**__    __     ______     ______   ______      _____     ______     __    __     ______     __   __     ______                     
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
import java.util.Set;

import ChronoTimer.Race.EventType;

public class ChronoTrigger 
{
	private Channel[] channels = new Channel[8];
	private ChronoTime officialTime, startTime;
	private Race[] races = new Race[8];
	private int curRace = 0;

	//setup that allows you to set the Official Time
	public ChronoTrigger(String in)
	{
		try {
			officialTime = new ChronoTime(in);
		} catch (InvalidTimeException e) {
			System.out.println("invalid Start time");
		}
		try {
			startTime = new ChronoTime(in);
		} catch (InvalidTimeException e) {
			System.out.println("invalid Start time");
		}
	}
	//sets time
	public void setTime(int hor, int min, int sec, int hun)
	{
		try {
			officialTime = new ChronoTime(hor,min,sec,hun);
		} catch (InvalidTimeException e) {
			System.out.println("not valid time");
		}
	}
	//toggles channel
	public void toggle(int c)
	{
		channels[c].toggle();
	}
	//connects sensor to channel
	public void connectSensor(int c, String s)
	{
		channels[c].connect(s);
	}
	//disconnects sensor
	public void disSensor(int channel)
	{
		//for future
	}
	//triggers sensor
	public void triggerSensor(int c)
	{
		
		if(c == 1 &&channels[c].trigger())
		{
			try {
				races[curRace].startNextRacer(officialTime);
			} catch (InvalidRaceStateException e) {
				System.out.println("This race doesn't exist");
			}
		}
		if(c == 2 && channels[c].trigger())
		{
			try {
				races[curRace].finishNextRacer(officialTime);
			} catch (InvalidRaceStateException e) {
				System.out.println("This race doesn't exist");
			} catch (InvalidTimeException e) {
				System.out.println("This time is not valid");
			}
		}
	}
	public void newRace(EventType event)
	{
		races[curRace] = new Race(event);
	}
	public void changeRace(int i)
	{
		if(i < 9 && i > 0)
			curRace=i;
	}
	public void addRacer(int num)
	{
		try {
			races[curRace].add(num);
		} catch (DuplicateRacerException e) {
			System.out.println("Duplicate Racer");
		}
	}
	public void finRace()
	{
		try {
			races[curRace].endRace(this.officialTime);
		} catch (InvalidTimeException e) {
			System.out.println("the officialTime isnt working");
		}
	}
	public void printCurRace()
	{
		races[curRace].printRace();
	}
	//returns officialTime
	public ChronoTime getTime()
	{
		return this.officialTime;
	}

	//ends chronotrigger
	public void exit()
	{
		System.exit(0);
	}
	//hello
	
	private class Channel{
		private boolean on = false;
		private Set<String> validTypes;
		private String sensorType;
		
		private Channel(){
			validTypes.add("EYE");
			validTypes.add("GATE");
			validTypes.add("PAD");}
		
		private void toggle(){on = !on;}
		
		private void connect(String t) throws IllegalArgumentException{
			if (!validTypes.contains(t)) throw new IllegalArgumentException("Cannot connect sensor with type '"+t+"'");
			sensorType=t;}
		
		private boolean trigger(){
			return on && sensorType != null;}
	}
}
