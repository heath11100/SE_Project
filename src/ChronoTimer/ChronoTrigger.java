package ChronoTimer;
	 import java.util.HashSet;
import java.util.NoSuchElementException;
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
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;
//eeeezs
public class ChronoTrigger 
{
	private Channel[] channels;
	private ChronoTime officialTime, startTime;
	private Race[] races = new Race[8];
	private int curRace = -1;
	private Log history = new Log();
	private Printer printIt = new Printer();
	private String raceType;
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
			channels = new Channel[8];
			for(int i =0; i < 8; i++)
			{
			try {officialTime = new ChronoTime(0,0,0,0);}
			catch (InvalidTimeException e) {history.add(e.toString());}
			}
			for(int j =0; j < 8; j++)
			{
				channels[j] = new Channel();
				channels[j].connect("EYE");
				}
			
			history.add("ChronoTrigger is on.");
			flush();
	}
	//setup that allows you to set the Official Time
	public ChronoTrigger(ChronoTime t)
	{
			officialTime = t;

			startTime = t;
			channels = new Channel[8];
			for(int i = 0; i < 8; i++)
			{
				channels[i] = new Channel();
				channels[i].connect("EYE");
			}
			
			history.add("ChronoTrigger is on.");
			flush();
	}
	//sets time
	public void setTime(ChronoTime t, ChronoTime s)
	{
			officialTime = s;
			history.add("Set time to " + s.toString());
			flush();
	}
	//toggles channel
	public void toggle(ChronoTime t, int c)
	{
		officialTime = t;
		if(c>=0 && c< 8)
			channels[c].toggle();
		history.add("Toggled " +c+" at "+ t.toString());
		flush();
	}
	//connects sensor to channel
	public void connectSensor(ChronoTime t, int c, String s)
	{	
		if(c >=0&& c< 8)
			channels[c].connect(s);
		officialTime = t;
		history.add("Connected " +c+" at "+ t.toString());
		flush();
	}
	//dissconnects sensor
	public void disSensor(ChronoTime t, int channel)
	{
		officialTime = t;
		channels[channel].disconnect();
		history.add("Disconnected " +channel+" at "+ t.toString());
		flush();
	}
	//triggers sensor
	public void triggerSensor(ChronoTime t, int c)
	{
		officialTime = t;
		if (!channels[c].trigger())
			history.add("Channel "+c+" is not on or is not connected.");
		else if(c == 1)
		{
			try {races[curRace].startNextRacer(officialTime);}
			catch(ArrayIndexOutOfBoundsException e){history.add("Cannot trigger before race is created.");}
			catch (RaceException e) {history.add(e.toString());}
			catch(InvalidTimeException e){history.add(e.toString());}
			catch(NoSuchElementException a)
			{
				
			}
		}
		else if(c == 2)
		{
			try {races[curRace].finishNextRacer(officialTime);}
			catch(ArrayIndexOutOfBoundsException e){history.add("Cannot trigger before race is created.");}
			catch (RaceException e) {history.add(e.toString());}
			catch(InvalidTimeException e){history.add(e.toString());}
			catch(NoSuchElementException a)
			{
				
			}
		}
		flush();
	}

	public void setType(ChronoTime t, String s)
	{
		try {
			races[curRace].setEventType(s);
		} catch (RaceException e1) {
			
			history.add(e1.toString());
			
		}
		catch(ArrayIndexOutOfBoundsException e){
			raceType = s;
			}
		flush();
	}
	public void newRace(ChronoTime t)
	{
		officialTime = t;
		try{
		if(races[curRace].isOver())
		{
			races[++curRace] = new Race(t);
			if(raceType != null)
			{
				try {
					races[curRace].setEventType(raceType);
				} catch (RaceException e) {
					history.add(e.getMessage());
				}
				raceType = null;
			}
			history.add("Created race "+curRace+".");
		}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			races[++curRace] = new Race(t);
			if(raceType != null)
			{
				try {
					races[curRace].setEventType(raceType);
				} catch (RaceException k) {
					history.add(k.getMessage());
				}
				raceType = null;
			}
			history.add("Created race "+curRace+".");
		}
		
		flush();
	}
	public void addRacer(ChronoTime t, int num)
	{
		officialTime = t;
		try {
			races[curRace].add(num);
		} catch (RaceException e) {
			history.add(e.getMessage());
		}
		catch(ArrayIndexOutOfBoundsException e){history.add("Cannot add racer before race is created.");}
		flush();
	}
	public void finRace(ChronoTime t)
	{
		officialTime = t;
		try {
			races[curRace].endRace(this.officialTime);
		} catch (InvalidTimeException e) {
			history.add(e.getMessage());
		}
		catch(NullPointerException e){
			history.add("Cannot end race before race is created.");
		}
		flush();
	}
	public void printCurRace(ChronoTime t)
	{
		officialTime = t;
		try{
		history.add(races[curRace].getLog().readAll());
		printIt.print(races[curRace].getLog().readAll());
		}
		catch(ArrayIndexOutOfBoundsException e){history.add("Cannot print race before race is created.");}
		flush();
	}
	public void flush()
	{
		//try to flush race if it exists
		try{printIt.print(races[curRace].getLog().flush());}
		catch(Exception e){}
		printIt.print(history.flush());
	}
	//returns officialTimesas
	public ChronoTime getTime()
	{
		return this.officialTime;
	}
	
	public void dnf(ChronoTime t)
	{
		try {
			races[curRace].didNotFinish();
		} catch (RaceException e) {
			history.add(e.getMessage());
		}
		catch(NoSuchElementException a)
		{
			
		}
		catch(ArrayIndexOutOfBoundsException e){history.add("Cannot DNF before race is created.");}
		
	}
	
	public void cancel(ChronoTime t)
	{
		try {
			races[curRace].cancel();
		} catch (RaceException e) {
			history.add(e.getMessage());
		}
		catch(NoSuchElementException a)
		{
			
		}
		catch(ArrayIndexOutOfBoundsException e){history.add("Cannot cancel racer before race is created.");}
		
	}
	
	private class Channel{
		private boolean on = false;
		private Set<String> validTypes;
		private String sensorType;
		
		private Channel(){
			validTypes = new HashSet<>();
			validTypes.add("EYE");
			validTypes.add("GATE");
			validTypes.add("PAD");}
		
		private void toggle(){on = !on;}
		
		private void connect(String t) throws IllegalArgumentException{
			if (!validTypes.contains(t)) throw new IllegalArgumentException("Cannot connect sensor with type '"+t+"'");
			sensorType=t;}
		
		private void disconnect(){sensorType=null;}
		
		private boolean trigger(){
			return on && sensorType != null;}
	}
}
