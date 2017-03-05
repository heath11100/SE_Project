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
	private Log history = new Log();
	private Printer printIt = new Printer();
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
			history.add("ChronoTrigger()");
	}
	//setup that allows you to set the Official Time
	public ChronoTrigger(ChronoTime t)
	{
			officialTime = t;
			startTime = t;
			history.add("ChronoTrigger("+t.toString()+")");
	}
	//sets time
	public void setTime(ChronoTime t, ChronoTime s)
	{
			officialTime = s;
			history.add("setTime("+t.toString()+","+s.toString()+")");
	}
	//toggles channel
	public void toggle(ChronoTime t, int c)
	{
		officialTime = t;
		if(c<9)
		channels[c].toggle();
		history.add("toggle("+t.toString()+","+c+")");
	}
	//connects sensor to channel
	public void connectSensor(ChronoTime t, int c, String s)
	{
		channels[c].connect(s);
		officialTime = t;
		history.add("connectSensor("+t.toString()+","+c+","+s.toString()+")");
	}
	//disconnects sensor
	public void disSensor(ChronoTime t, int channel)
	{
		officialTime = t;
		//for future
		history.add("disSensor("+t.toString()+","+channel+")");
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
		history.add("triggerSensor("+t.toString()+","+c+")");
	}

	public void setType(ChronoTime t, EventType e)
	{
		try {
			races[curRace].setEventType(e);
		} catch (RaceException e1) {
			System.out.println(e1);
		}
		history.add("setType("+t.toString()+","+e.toString()+")");
	}
	public void changeRace(ChronoTime t, int i)
	{
		officialTime = t;
		if(i < 9 && i > 0)
			curRace=i;
		history.add("changeRace("+t.toString()+","+i+")");
	}
	public void addRacer(ChronoTime t, int num)
	{
		officialTime = t;
		try {
			races[curRace].add(num);
		} catch (RaceException e) {
			System.out.println(e);
		}
		history.add("setTime("+t.toString()+","+num+")");
	}
	public void finRace(ChronoTime t)
	{
		officialTime = t;
		try {
			races[curRace].endRace(this.officialTime);
		} catch (InvalidTimeException e) {
			System.out.println(e);
		}
		history.add("finRace("+t.toString()+")");
	}
	public void printCurRace(ChronoTime t)
	{
		officialTime = t;
		history.add(races[curRace].getLog().flush());
		printIt.print(races[curRace].getLog().flush());
		history.add("printCurRace("+t.toString()+")");
	}
	public void flush(ChronoTime t)
	{
		officialTime = t;
		history.add(races[curRace].getLog().flush());
		printIt.print(history.flush());
		history.add("flush("+t.toString()+")");
	}
	//returns officialTime
	public ChronoTime getTime()
	{
		history.add("getTime()");
		return this.officialTime;
	}
	
	public void dnf(ChronoTime t)
	{
		try {
			races[curRace].didNotFinish();
		} catch (RaceException e) {
			System.out.println(e);
		}
		history.add("dnf("+t.toString()+")");
	}
	
	public void cancel(ChronoTime t, int r)
	{
		try {
			races[curRace].cancel(r);
		} catch (RaceException e) {
			System.out.println(e);
		}
		history.add("cancel("+t.toString()+")");
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
