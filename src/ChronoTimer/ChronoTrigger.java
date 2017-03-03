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
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;

public class ChronoTrigger 
{
	private Channel[] channels = new Channel[8];
	private ChronoTime officialTime, startTime;
	private Race[] races = new Race[8];
	private int curRace = 0;
	private Printer log;
	public ChronoTrigger()
	{
			try {
				officialTime = new ChronoTime(1,1,1,1);
			} catch (InvalidTimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				startTime = new ChronoTime(1,1,1,1);
			} catch (InvalidTimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	//setup that allows you to set the Official Time
	public ChronoTrigger(ChronoTime t)
	{
			officialTime = t;
			startTime = t;
	}
	//sets time
	public void setTime(ChronoTime t, ChronoTime s)
	{
			officialTime = s;
	}
	//toggles channel
	public void toggle(ChronoTime t, int c)
	{
		officialTime = t;
		channels[c].toggle();
		
	}
	//connects sensor to channel
	public void connectSensor(ChronoTime t, int c, String s)
	{
		channels[c].connect(s);
		officialTime = t;
	}
	//disconnects sensor
	public void disSensor(ChronoTime t, int channel)
	{
		officialTime = t;
		//for future
	}
	//triggers sensor
	public void triggerSensor(ChronoTime t, int c)
	{
		officialTime = t;
		if(c == 1 && channels[c].trigger())
		{
			try {
				races[curRace].startNextRacer(officialTime);
			} catch (RaceException e) {
				System.out.println(e);
			}
			catch(InvalidTimeException e)
			{
				System.out.println(e);
			}
		}
		if(c == 2 && channels[c].trigger())
		{
			try {
				races[curRace].finishNextRacer(officialTime);
			} catch (RaceException e) {
				System.out.println(e);
			} catch (InvalidTimeException e) {
				System.out.println(e);
			}
		}
	}
	public void newRace(ChronoTime t)
	{
		officialTime = t;
		races[curRace] = new Race();
	}
	public void setType(ChronoTime t, EventType e)
	{
		races[curRace].setEventType(e);
	}
	public void changeRace(ChronoTime t, int i)
	{
		officialTime = t;
		if(i < 9 && i > 0)
			curRace=i;
	}
	public void addRacer(ChronoTime t, int num)
	{
		officialTime = t;
		try {
			races[curRace].add(num);
		} catch (RaceException e) {
			System.out.println(e);
		}
	}
	public void finRace(ChronoTime t)
	{
		officialTime = t;
		try {
			races[curRace].endRace(this.officialTime);
		} catch (InvalidTimeException e) {
			System.out.println(e);
		}
	}
	public void printCurRace(ChronoTime t)
	{
		officialTime = t;
		races[curRace].printRace();
	}
	//returns officialTime
	public ChronoTime getTime()
	{
		return this.officialTime;
	}
	
	public void dnf(ChronoTime t, int r)
	{
		races[curRace].didNotFinish(r);
	}
	
	public void cancel(ChronoTime t, int r)
	{
		races[curRace].cancel(r);
	}
	
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
