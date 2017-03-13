package ChronoTimer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;
import junit.framework.TestCase;

public class ChronoTrigger 
{
	private Channel[] channels;
	private ChronoTime officialTime;
	private ArrayList<Run> races = new ArrayList<>();
	private int curRun = -1;
	private boolean logTimes = false;
	private Log history = new Log();
	private Printer printer = new Printer();
	private String raceType;
	/**
	 * Default Constructor
	 * 
	 */
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
	/**
	 * Constructor with time parameter
	 * @param startTime
	 */

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
	
	/**
	 * Sets the officialTime of the race
	 * @param commandTime
	 * @param newOfficialTimessaaa
	 */
	public void setTime(ChronoTime commandTime, ChronoTime newOfficialTime)
	{
			officialTime = newOfficialTime;
			history.add( (logTimes? officialTime+" | " : "") +"Set time to " + newOfficialTime.toString());
			flush();
	}
	
	/**
	 * @return officialTime
	 */
	public ChronoTime getTime(){return officialTime;}
	
	/**
	 * Toggles channel giveny by parameter int c. Adds only if channel
	 * exists
	 */
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
	
	/**
	 * connects Sensor to the race type given by parameter type
	 * @param commandTime
	 * @param c
	 * @param type
	 */
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
	
	/**
	 * Disconnects Sensor c
	 * @param commandTime
	 * @param c
	 */
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
	
	/**
	 * This will set the type of the current race to String type.
	 * If there is no current race, creates a new one
	 * @param commandTime
	 * @param type
	 */
	public void setType(ChronoTime commandTime, String type)
	{
		officialTime = commandTime;
		
		if (races.isEmpty())	//need to check here if valid type - share checkValid(runType) method with Run?
			raceType = type;
		else{
			try {races.get(curRun).setEventType(type);}
			catch (RaceException e) {history.add(e.getMessage());}
			catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
		}
		
		flush();
	}
	
	/**
	 * Will create a new Run
	 * @param commandTime
	 */
	public void newRun(ChronoTime commandTime)
	{
		officialTime = commandTime;
		
		if(races.isEmpty() || races.get(curRun).hasEnded()){
			races.add(new Run());
			curRun++;
			history.add( (logTimes? officialTime+" | " : "") +"Created race "+curRun+".");
		
			if(raceType != null){
				try {races.get(curRun).setEventType(raceType);}
				catch (RaceException e) {history.add(e.getMessage());}
				catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
			}
			raceType = null;}//why this?
		else
			history.add("No race was created- already have current race.");
		
		flush();
	}
	
	
	/**
	 * adds racer with int parameter. Will check if a race is created
	 * @param commandTime
	 * @param num
	 */
	public void addRacer(ChronoTime commandTime, int num)
	{
		officialTime = commandTime;
		
		if (races.isEmpty())
			history.add("Cannot add racer before race is created.");
		else{
			try {races.get(curRun).queueRacer(num);}
			catch (RaceException e) {history.add(e.getMessage());}
			catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
		}
		
		flush();
	}
	
	/**
	 * triggers Sensor given by c. Also analyzes what this trigger
	 * actually means as an instruction.
	 * @param commandTime
	 * @param c
	 */
	public void triggerSensor(ChronoTime commandTime, int c)
	{
		officialTime = commandTime;
		
		//if valid channel..
		if(c>=0 && c< 8){
			//if trigger is successful and there's a current race
			if (channels[c].trigger() && !races.isEmpty()){
				if(c == 1)
				{
					try {races.get(curRun).startNextRacer(officialTime);}
					catch(RaceException e) {history.add(e.getMessage());}
					catch(InvalidTimeException e){history.add(e.getMessage());}
					catch(NoSuchElementException e){history.add(e.getMessage());}
					catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
				}
				else if(c == 2)
				{
					try {races.get(curRun).finishNextRacer(officialTime);}
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
	
	/**
	 * Next Racer will not finish
	 * @param commandTime
	 */
	public void dnf(ChronoTime commandTime)
	{
		officialTime = commandTime;
		
		if (!races.isEmpty()){
			try {(races.get(curRun)).didNotFinishNextRacer();}
			catch(RaceException e) {history.add(e.getMessage());}
			catch(NoSuchElementException e){history.add(e.getMessage());}
			catch(Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
		}
		
		flush();
	}
	
	/**
	 * Next Racer will be canceled
	 * @param commandTime
	 */
	public void cancel(ChronoTime commandTime)
	{
		officialTime = commandTime;
		
		if (!races.isEmpty()){
			try {races.get(curRun).cancelNextRacer();}
			catch(RaceException e) {history.add(e.getMessage());}
			catch(NoSuchElementException e){history.add(e.getMessage());}
			catch(Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
		}
		
		flush();
	}
	
	/**
	 * Ends Race. Currently need a method for this in Run
	 * @param commandTime
	 */
	public void finRun(ChronoTime commandTime)
	{
		officialTime = commandTime;
		
		if (!races.isEmpty()){
			try {races.get(curRun).endRun(this.officialTime);}
			catch (RaceException e) {history.add(e.getMessage());}
			catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
		}
		
		flush();
	}
	
	//prints the current race
	public void printCurRace(ChronoTime commandTime)
	{
officialTime = commandTime;
		
		if (!races.isEmpty()){
			try {races.get(curRun).endRun(this.officialTime);}//endRace method in run
			catch (RaceException e) {history.add(e.getMessage());}
			catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
		}
		
		flush();

	}
	
	/**
	 * Default, prints the current Run
	 */
	public void printRun(ChronoTime commandTime)
	{
		officialTime = commandTime;
		
		if (!races.isEmpty())
			printer.print(races.get(curRun).getLog());
	}
	
	/**
	 * Prints run given by runNum
	 * @param commandTime
	 * @param runNum
	 */
	public void printRun(ChronoTime commandTime, int runNum)
	{
		officialTime = commandTime;
		
		if (!races.isEmpty())
			printer.print(races.get(runNum).getLog());
		else
			history.add("runNum " + runNum+ " was invalid");
	}
	
	/**
	 * Exports run to .txt, default set to curRun
	 * @param commandTime
	 */
	public void exportRun(ChronoTime commandTime)
	{
		officialTime = commandTime;
		
		if (!races.isEmpty())
			printer.export(curRun, races.get(curRun));
	}
	
	/**
	 * Exports to .txt. uses the run given by runNum
	 * @param commandTime
	 * @param runNum
	 */
	public void exportRun(ChronoTime commandTime, int runNum)
	{
		officialTime = commandTime;
		
		if (!races.isEmpty() && runNum < curRun)
			printer.export(runNum, races.get(runNum));
		else
			history.add("runNum " + runNum+ " was invalid");
	}
	
	/**
	 * 
	 */
	public void flush()
	{
		//try to flush race if it exists
		if (!races.isEmpty())
			printer.flush(races.get(curRun).getLog());
		
		//flush chronotrigger history
		printer.flush(history);
	}

	
	/**
	 * The Channel class.
	 * @author Casey Van Groll
	 */
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
	
	/**
	 * Tests for ChronoTrigger.
	 * @author Casey Van Groll
	 */
	public static class TestCT extends TestCase{
		private ChronoTrigger ct;
		private ChronoTime t1,t2,t3,t4;
		
		@Override
		public void setUp() throws InvalidTimeException{
			ct = new ChronoTrigger();
			t1 = ChronoTime.now();
			t2 = ChronoTime.now();
			t3 = ChronoTime.now();
			t4 = ChronoTime.now();
		}
		
		public void testConstructors(){
			//constructor 1
			ct = new ChronoTrigger();
			assertFalse(ct.channels == null);
			for (int i=0; i< 8; i++){
				assertFalse(ct.channels[i] ==null);
				assertFalse(ct.channels[i].trigger());}
			assertFalse(ct.history == null);
			assertFalse(ct.printer == null);
			assertFalse(ct.officialTime == null);
			
			//constructor 2
			ct = new ChronoTrigger(t1);
			assertFalse(ct.channels == null);
			for (int i=0; i< 8; i++){
				assertFalse(ct.channels[i] ==null);
				assertFalse(ct.channels[i].trigger());}
			assertFalse(ct.history == null);
			assertFalse(ct.printer == null);
			assertEquals(t1, ct.officialTime);
		}
		
		public void testSetGetTime(){
			ct.setTime(t1, t2);
			assertEquals(ct.getTime(),t2);
			ct.setTime(t1, t1);
			assertEquals(ct.getTime(),t1);
			ct.setTime(t4, t3);
			assertEquals(ct.getTime(),t3);
		}
		
		public void testChannel(){
			//off and connected
			for (int i=0; i< 8; i++){
				assertFalse(ct.channels[i].trigger());
				ct.channels[i].disconnect();}
			
			//on and disconnected
			for (int i=0; i< 8; i++){
				ct.channels[i].toggle();
				assertFalse(ct.channels[i].trigger());}
			
			//on and connected
			for (int i=0; i< 8; i++){
				ct.channels[i].connect("EYE");
				assertTrue(ct.channels[i].trigger());}
			
			//invalid sensor type
			try {ct.channels[0].connect("invalid");}
			catch (Exception e){assertTrue(e instanceof IllegalArgumentException);}
			
			//new connection replaces old
			assertTrue(ct.channels[0].sensorType.equals("EYE"));
			ct.channels[0].connect("PAD");
			assertTrue(ct.channels[0].sensorType.equals("PAD"));
		}
		
		public void testChannelThroughCT(){
			//off and connected
			for (int i=0; i< 8; i++){
				assertFalse(ct.channels[i].trigger());
				ct.disSensor(t1, i);}
			
			//on and disconnected
			for (int i=0; i< 8; i++){
				ct.toggle(t1, i);
				assertFalse(ct.channels[i].trigger());}
			
			//on and connected
			for (int i=0; i< 8; i++){
				ct.connectSensor(t1, i, "EYE");
				assertTrue(ct.channels[i].trigger());}
			
			//invalid sensor type
			ct.connectSensor(t1, 0, "invalid");
			assertTrue(ct.history.readAll().trim().endsWith("Cannot connect sensor with type 'invalid'"));
			
			//new connection replaces old
			assertTrue(ct.channels[0].sensorType.equals("EYE"));
			ct.connectSensor(t1, 0, "PAD");
			assertTrue(ct.channels[0].sensorType.equals("PAD"));
		}
		
		//officialTime should be updated by each of these commands
		public void setOfficialTimeThruCommands(){
			assertEquals(ct.officialTime,t1);
			ct.toggle(t2, 0);
			assertEquals(ct.officialTime,t2);
			ct.connectSensor(t3, 0, "EYE");
			assertEquals(ct.officialTime,t3);
			ct.disSensor(t4, 0);
			assertEquals(ct.officialTime,t4);
			ct.setType(t1, "IND");
			assertEquals(ct.officialTime,t1);
			ct.newRun(t2);
			assertEquals(ct.officialTime,t2);
			ct.addRacer(t3, 10);
			assertEquals(ct.officialTime,t3);
			ct.triggerSensor(t4, 0);
			assertEquals(ct.officialTime,t4);
			ct.dnf(t1);
			assertEquals(ct.officialTime,t1);
			ct.cancel(t2);
			assertEquals(ct.officialTime,t2);
			ct.finRun(t3);
			assertEquals(ct.officialTime,t3);
			ct.printCurRace(t4);
			assertEquals(ct.officialTime,t4);
		}
	}
}
