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
//eee
public class ChronoTrigger 
{
	private Channel[] channels = new Channel[8];
	private ChronoTime officialTime, startTime;
	private Race[] races = new Race[8];
	private int curRace = 0;
	private Log history = new Log();
	private Printer printIt = new Printer();
	public ChronoTrigger()
	{
			try {
				officialTime = new ChronoTime(1,1,1,1);
			} catch (InvalidTimeException e) {
				
				history.add(e.toString());
			}
			try {
				startTime = new ChronoTime(1,1,1,1);
			} catch (InvalidTimeException e) {
				
				history.add(e.toString());
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
			history.add("Set time to " + t.toString());
	}
	//toggles channel
	public void toggle(ChronoTime t, int c)
	{
		officialTime = t;
		if(c>0)
			channels[c].toggle();
		history.add("Toggled " +c+" at "+ t.toString());
	}
	//connects sensor to channel
	public void connectSensor(ChronoTime t, int c, String s)
	{	
		if(c >0)
			channels[c].connect(s);
		officialTime = t;
		history.add("Connected " +c+" at "+ t.toString());
	}
	//disconnects sensor
	public void disSensor(ChronoTime t, int channel)
	{
		officialTime = t;
		//for future
		history.add("Disconnected " +channel+" at "+ t.toString());
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
				history.add(e.toString());
			}
			catch(InvalidTimeException e)
			{
				history.add(e.toString());
			}
		}
		if(c == 2 && channels[c].trigger())
		{
			try {
				races[curRace].finishNextRacer(officialTime);
			} catch (RaceException e) {
				history.add(e.toString());
			} catch (InvalidTimeException e) {
				history.add(e.toString());
			}
		}
		else
			history.add("This channel "+c+" couldnt be triggered");
	}

	public void setType(ChronoTime t, String s)
	{
		try {
			races[curRace].setEventType(s);
		} catch (RaceException e1) {
			history.add(e1.toString());
		}
		
	}
	public void newRace(ChronoTime t)
	{
		officialTime = t;
		curRace++;
		
	}
	public void addRacer(ChronoTime t, int num)
	{
		officialTime = t;
		try {
			races[curRace].add(num);
		} catch (RaceException e) {
			history.add(e.toString());
		}
		
	}
	public void finRace(ChronoTime t)
	{
		officialTime = t;
		try {
			races[curRace].endRace(this.officialTime);
		} catch (InvalidTimeException e) {
			history.add(e.toString());
		}
		
	}
	public void printCurRace(ChronoTime t)
	{
		officialTime = t;
		history.add(races[curRace].getLog().readAll());
		printIt.print(races[curRace].getLog().readAll());
		
	}
	public void flush()
	{
		history.add(races[curRace].getLog().flush());
		printIt.print(history.flush());
		
	}
	//returns officialTime
	public ChronoTime getTime()
	{
		
		return this.officialTime;
	}
	
	public void dnf(ChronoTime t)
	{
		try {
			races[curRace].didNotFinish();
		} catch (RaceException e) {
			history.add(e.toString());
		}
		
	}
	
	public void cancel(ChronoTime t)
	{
		try {
			races[curRace].cancel();
		} catch (RaceException e) {
			history.add(e.toString());
		}
		
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
