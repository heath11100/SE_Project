package ChronoTimer;
	 import java.util.ArrayList;
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
	private ChronoTime officialTime;
	private ArrayList<Race> races = new ArrayList<>();
	private int curRace = -1;
	private boolean logTimes = false;
	private Log history = new Log();
	private Printer printer = new Printer();
	private String raceType;

	public ChronoTrigger()
	{
		//set official time
		try {officialTime = ChronoTime.now();}
		catch (InvalidTimeException e) {history.add(e.getMessage());}

		//create channels
		channels = new Channel[8];
		for(int j =0; j < 8; j++){
			channels[j] = new Channel();
			channels[j].connect("EYE");}
		
		history.add( (logTimes? officialTime+" | " : "") +"ChronoTrigger is on.");
		flush();
	}
	
	public ChronoTrigger(ChronoTime t)
	{
		//set official time
		officialTime = t;
		
		//create channels
		channels = new Channel[8];
		for(int i = 0; i < 8; i++){
				channels[i] = new Channel();
				channels[i].connect("EYE");}
			
		history.add( (logTimes? officialTime+" | " : "") +"ChronoTrigger is on.");
		flush();
	}
	
	//sets official time
	public void setTime(ChronoTime commandTime, ChronoTime newOfficialTime)
	{
			officialTime = newOfficialTime;
			history.add( (logTimes? officialTime+" | " : "") +"Set time to " + newOfficialTime.toString());
			flush();
	}
	
	//returns officialTime
	public ChronoTime getTime(){return officialTime;}
	
	//toggles channel
	public void toggle(ChronoTime commandTime, int c)
	{
		officialTime = commandTime;
		
		//if valid channel..
		if(c>=0 && c< 8){
			channels[c].toggle();
			history.add( (logTimes? officialTime+" | " : "") +"Toggled channel " +c);}
		else
			history.add("Cannot toggle: Channel "+c+" doesn't exist.");
		
		flush();
	}
	
	//connects sensor to channel
	public void connectSensor(ChronoTime commandTime, int c, String type)
	{	
		officialTime = commandTime;
		
		//if valid channel..
		if(c>=0 && c< 8){
			try{
				channels[c].connect(type);
				history.add( (logTimes? officialTime+" | " : "") +"Connected "+type+" sensor to channel "+c);}
			//handle illegal sensor type exception
			catch (IllegalArgumentException e){history.add(e.getMessage());}}
		else
			history.add("Cannot connect: Channel "+c+" doesn't exist.");

		
		flush();
	}
	
	//disconnects sensor from channel
	public void disSensor(ChronoTime commandTime, int c)
	{
		officialTime = commandTime;
		
		//if valid channel..
		if(c>=0 && c< 8){
			channels[c].disconnect();
			history.add( (logTimes? officialTime+" | " : "") +"Disconnected sensor from channel " +c);}
		else
			history.add("Cannot disconnect: Channel "+c+" doesn't exist.");
		
		flush();
	}
	
	//sets type of current race or next race to be created
	public void setType(ChronoTime commandTime, String type)
	{
		officialTime = commandTime;
		
		if (races.isEmpty())
			raceType = type;
		else{
			try {races.get(curRace).setEventType(type);}
			catch (RaceException e) {history.add(e.getMessage());}
			catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
		}
		
		flush();
	}
	
	//creates a new race
	public void newRace(ChronoTime commandTime)
	{
		officialTime = commandTime;
		
		if(races.isEmpty() || races.get(curRace).isOver()){
			races.add(new Race(commandTime));
			curRace++;
			history.add( (logTimes? officialTime+" | " : "") +"Created race "+curRace+".");
		
			if(raceType != null){
				try {races.get(curRace).setEventType(raceType);}
				catch (RaceException e) {history.add(e.getMessage());}
				catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
			}
			raceType = null;}//why this?
		else
			history.add("No race was created- already have current race.");
		
		flush();
	}
	
	
	//adds a racer to the current race
	public void addRacer(ChronoTime commandTime, int num)
	{
		officialTime = commandTime;
		
		if (races.isEmpty())
			history.add("Cannot add racer before race is created.");
		else{
			try {races.get(curRace).add(num);}
			catch (RaceException e) {history.add(e.getMessage());}
			catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
		}
		
		flush();
	}
	
	//triggers sensor
	public void triggerSensor(ChronoTime commandTime, int c)
	{
		officialTime = commandTime;
		
		//if valid channel..
		if(c>=0 && c< 8){
			//if trigger is successful and there's a current race
			if (channels[c].trigger() && !races.isEmpty()){
				if(c == 1)
				{
					try {races.get(curRace).startNextRacer(officialTime);}
					catch(RaceException e) {history.add(e.getMessage());}
					catch(InvalidTimeException e){history.add(e.getMessage());}
					catch(NoSuchElementException e){history.add(e.getMessage());}
					catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
				}
				else if(c == 2)
				{
					try {races.get(curRace).finishNextRacer(officialTime);}
					catch(RaceException e) {history.add(e.getMessage());}
					catch(InvalidTimeException e){history.add(e.getMessage());}
					catch(NoSuchElementException e){history.add(e.getMessage());}
					catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
				}
			}
		}
		else
			history.add("Cannot trigger: Channel "+c+" doesn't exist.");

		flush();
	}
	
	public void dnf(ChronoTime commandTime)
	{
		officialTime = commandTime;
		
		if (!races.isEmpty()){
			try {races.get(curRace).didNotFinish();}
			catch(RaceException e) {history.add(e.getMessage());}
			catch(NoSuchElementException e){history.add(e.getMessage());}
			catch(Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
		}
		
		flush();
	}
	
	public void cancel(ChronoTime commandTime)
	{
		officialTime = commandTime;
		
		if (!races.isEmpty()){
			try {races.get(curRace).cancel();}
			catch(RaceException e) {history.add(e.getMessage());}
			catch(NoSuchElementException e){history.add(e.getMessage());}
			catch(Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
		}
		
		flush();
	}
	
	//end the current race
	public void finRace(ChronoTime commandTime)
	{
		officialTime = commandTime;
		
		if (!races.isEmpty()){
			try {races.get(curRace).endRace(this.officialTime);}
			catch (RaceException e) {history.add(e.getMessage());}
			catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
		}
		
		flush();
	}
	
	//prints the current race
	public void printCurRace(ChronoTime commandTime)
	{
		officialTime = commandTime;
		
		if (!races.isEmpty())
			printer.print(races.get(curRace).getLog());
	}
	
	public void flush()
	{
		//try to flush race if it exists
		if (!races.isEmpty())
			printer.flush(races.get(curRace).getLog());
		
		//flush chronotrigger history
		printer.flush(history);
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
