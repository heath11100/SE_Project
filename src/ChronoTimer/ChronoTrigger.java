package ChronoTimer;



import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import Exceptions.InvalidTimeException;
import Exceptions.RaceException;
import junit.framework.TestCase;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.google.gson.Gson;
//hellollssss
public class ChronoTrigger 
{
	private Channel[] channels;
	private ChronoTime officialTime;
	private int offset;
	private ArrayList<Run> runs = new ArrayList<>();
	private int curRun = -1;
	private boolean logTimes = false;
	private Log history = new Log();
	private Printer printer = new Printer();
	private Printer runprinter = new Printer();
	private String eventType;
	private int[] lanes = new int[8];
	private boolean power;
	private String[] temps = new String[20];
	/**
	 * Default Constructor
	 * 
	 */
	public ChronoTrigger()
	{
		//set official time
		power = false;
		try {officialTime = ChronoTime.now();}
		catch (InvalidTimeException e) {history.add(e.getMessage());}
		offset = 0;
		//create channels
		channels = new Channel[8];
		for(int j =0; j < 8; j++){
			channels[j] = new Channel();
			channels[j].connect("EYE");}
		for(int k = 0; k < 8; k++)
			lanes[k] = k+1;
		
		String tk1 = "";
		File fil = new File("./src/ChronoTimer/racerNames");
		Scanner inFile = null;
		try {
			inFile = new Scanner(fil);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i = 0;
		while(inFile.hasNext())
		{
			tk1 =  inFile.next();
			temps[i] = (tk1);
			i++;
		}
		inFile.close();
	}
	/**
	 * Constructor with time parameter
	 * @param t
	 */
	public ChronoTrigger(ChronoTime t)
	{
		//set official time
		officialTime = t;
		power = false;
		offset = 0;
		
		
		
		
		//create channels
		channels = new Channel[8];
		for(int i = 0; i < 8; i++){
				channels[i] = new Channel();
				channels[i].connect("EYE");}
		for(int k = 0; k < 8; k++)
			lanes[k] = k+1;
		
		String tk1 = "";
		File fil = new File("./src/ChronoTimer/racerNames");
		Scanner inFile = null;
		try {
			inFile = new Scanner(fil);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i = 0;
		while(inFile.hasNext())
		{
			tk1 =  inFile.next();
			temps[i] = (tk1);
			i++;
		}
		inFile.close();
	}
	
	public void setPrinter(Printer s)
	{
		runprinter = s;
	}
	/**
	 * Sets the officialTime of the race
	 * @param commandTime
	 * @param newOfficialTime
	 */
	public void setTime(ChronoTime commandTime, ChronoTime newOfficialTime)
	{
		if(power)
		{
			offset = newOfficialTime.asHundredths()-commandTime.asHundredths();
			officialTime = newOfficialTime;
			history.add( (logTimes? officialTime+" | " : "") +"Set time to " + newOfficialTime.toString());
			flush();
		}
	}
	
	/**
	 * @return officialTime
	 */
	public ChronoTime getTime(){
		if(power)
		{try {
			return officialTime.withOffset(offset);
		} catch (InvalidTimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
		return null;
	}
	/**
	 * Toggles channel giveny by parameter int c. Adds only if channel
	 * exists
	 */
	public void toggle(ChronoTime commandTime, int c)
	{
		if(power)
		{
		try {
			officialTime = commandTime.withOffset(offset);
		} catch (InvalidTimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//if valid channel..
		if(c>=0 && c< 8){
			channels[c].toggle();
			history.add( (logTimes? officialTime+" | " : "") +"Toggled channel " +c);}
		else
			history.add("Cannot toggle: Channel "+c+" doesn't exist.");
		}
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
		if(power)
		{
		try {
			officialTime = commandTime.withOffset(offset);
		} catch (InvalidTimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//if valid channel..
		if(c>=0 && c< 8){
			try{
				channels[c].connect(type);
				history.add( (logTimes? officialTime+" | " : "") +"Connected "+type+" sensor to channel "+c);}
			//handle illegal sensor type exception
			catch (IllegalArgumentException e){history.add(e.getMessage());}}
		else
			history.add("Cannot connect: Channel "+c+" doesn't exist.");
		}
		
		flush();
	}
	
	/**
	 * Disconnects Sensor c
	 * @param commandTime
	 * @param c
	 */
	public void disSensor(ChronoTime commandTime, int c)
	{
		if(power)
		{
		try {
			officialTime = commandTime.withOffset(offset);
		} catch (InvalidTimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//if valid channel..
		if(c>=0 && c< 8){
			channels[c].disconnect();
			history.add( (logTimes? officialTime+" | " : "") +"Disconnected sensor from channel " +c);}
		else
			history.add("Cannot disconnect: Channel "+c+" doesn't exist.");
		}
		flush();
	}
	/**
	 * turn power on
	 */
	public void powerOn(ChronoTime commandTime)
	{
		power = true;
		try {
			officialTime = commandTime.withOffset(offset);
			history.add( (logTimes? officialTime+" | " : "") +"ChronoTrigger is on.");
			flush();
		} catch (InvalidTimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//create channels
		channels = new Channel[8];
		for(int j =0; j < 8; j++){
			channels[j] = new Channel();
			channels[j].connect("EYE");}
		for(int k = 0; k < 8; k++)
			lanes[k] = k+1;
		
		String tk1 = "";
		File fil = new File("./src/ChronoTimer/racerNames");
		Scanner inFile = null;
		try {
			inFile = new Scanner(fil);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i = 0;
		while(inFile.hasNext())
		{
			tk1 =  inFile.next();
			temps[i] = (tk1);
			i++;
		}
		inFile.close();
	}
	/**
	 * turn power off
	 */
	public void powerOff(ChronoTime commandTime)
	{
		power = false;
		try {
			officialTime = commandTime.withOffset(offset);
			history.add( (logTimes? officialTime+" | " : "") +"ChronoTrigger is off.");
			printer.flush(history);
		} catch (InvalidTimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		channels = null;
		runs = new ArrayList<Run>();
		offset = 0;
		curRun = -1;
	}
	/**
	 * This will set the type of the current race to String type.
	 * If there is no current race, creates a new one
	 * @param commandTime
	 * @param type
	 */
	public void setType(ChronoTime commandTime, String type)
	{
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (runs.isEmpty()||runs.get(curRun).hasEnded())	//need to check here if valid type - share checkValid(runType) method with Run?
				eventType = type;
			else{
				try {runs.get(curRun).setEventType(type);}
				catch (RaceException e) {history.add(e.getMessage());}
				catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
				if(type == "IND")
				{
					lanes[0] = 1;
					lanes[1] = 1;
				}
				if(type == "PAR")
				{
					lanes[0] = 1;
					lanes[1] = 2;
				}
			}
		}
		flush();
	}
	
	/**
	 * Will create a new Run
	 * @param commandTime
	 */
	public void newRun(ChronoTime commandTime)
	{
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(runs.isEmpty() || runs.get(curRun).hasEnded()){
				runs.add(new Run());
				curRun++;
				history.add( (logTimes? officialTime+" | " : "") +"Created race "+curRun+".");
			
				if(eventType != null){
					try {runs.get(curRun).setEventType(eventType);}
					catch (RaceException e) {history.add(e.getMessage());}
					catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
				}
				eventType = null;}//why this?
			else
				history.add("No race was created- already have current race.");
		}
		flush();
	}
	
	
	/**
	 * adds racer with int parameter. Will check if a race is created
	 * @param commandTime
	 * @param num
	 */
	public void addRacer(ChronoTime commandTime, int num)
	{
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (runs.isEmpty())
				history.add("Cannot add racer before race is created.");
			else{
				try {runs.get(curRun).queueRacer(num);}
				catch (RaceException e) {history.add(e.getMessage());}
				catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
			}
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
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//if valid channel..
			if(c>=0 && c< 8){
				//if trigger is successful and there's a current race
				if (channels[c].trigger() && !runs.isEmpty()){
					if(c == 1)
					{
						try {
							runs.get(curRun).startNextRacer(officialTime, lanes[0]);
							}
						catch(RaceException e) {history.add(e.getMessage());}
						catch(InvalidTimeException e){history.add(e.getMessage());}
						catch(NoSuchElementException e){history.add(e.getMessage());}
						catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
					}
					if(c == 2)
					{
						try {runs.get(curRun).finishNextRacer(officialTime, lanes[0]);}
						catch(RaceException e) {history.add(e.getMessage());}
						catch(InvalidTimeException e){history.add(e.getMessage());}
						catch(NoSuchElementException e){history.add(e.getMessage());}
						catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
					}
					if(c == 3)
					{
						try {runs.get(curRun).startNextRacer(officialTime, lanes[1]);}
						catch(RaceException e) {history.add(e.getMessage());}
						catch(InvalidTimeException e){history.add(e.getMessage());}
						catch(NoSuchElementException e){history.add(e.getMessage());}
						catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
					}
					if(c == 4)
					{
						try {runs.get(curRun).finishNextRacer(officialTime, lanes[1]);}
						catch(RaceException e) {history.add(e.getMessage());}
						catch(InvalidTimeException e){history.add(e.getMessage());}
						catch(NoSuchElementException e){history.add(e.getMessage());}
						catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
					}
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
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (!runs.isEmpty()){
				try {(runs.get(curRun)).didNotFinishNextRacer(1);}
				catch(RaceException e) {history.add(e.getMessage());}
				catch(NoSuchElementException e){history.add(e.getMessage());}
				catch(Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
			}
		}
		flush();
	}
	
	/**
	 * Next Racer will be canceled
	 * @param commandTime
	 */
	public void cancel(ChronoTime commandTime)
	{
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (!runs.isEmpty()){
				try {runs.get(curRun).cancelNextRacer(1);}
				catch(RaceException e) {history.add(e.getMessage());}
				catch(NoSuchElementException e){history.add(e.getMessage());}
				catch(Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
			}
		}
		flush();
	}
	
	/**
	 * Ends Race. Currently need a method for this in Run
	 * @param commandTime
	 */
	public void finRun(ChronoTime commandTime)
	{
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (!runs.isEmpty()){
				
				try {runs.get(curRun).endRun(this.officialTime);}
				catch (RaceException e) {history.add(e.getMessage());}
				catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
			}
		}
		flush();
	}
	
	//prints the current race
	public void printCurRace(ChronoTime commandTime)
	{
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (!runs.isEmpty()){
				try {runs.get(curRun).endRun(this.officialTime);}//endRace method in run
				catch (RaceException e) {history.add(e.getMessage());}
				catch (Exception e){System.out.println("Unexpected exception...");e.printStackTrace();}
			}
		}
		flush();

	}
	
	/**
	 * Default, prints the current Run
	 */
	public void printRun(ChronoTime commandTime)
	{
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (!runs.isEmpty())
				runprinter.print(runs.get(curRun).getLog());
			else {
				Log log = new Log();
				log.add("No run to print.");
				runprinter.print(log);
			}
		}
	}
	
	/**
	 * Prints run given by runNum
	 * @param commandTime
	 * @param runNum
	 */
	public void printRun(ChronoTime commandTime, int runNum)
	{
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (!runs.isEmpty() && runs.size() > runNum)
				runprinter.print(runs.get(runNum).getLog());
			else
				history.add("runNum " + runNum+ " was invalid");
		}
	}
	
	
	
	/**
	 * Exports run to .txt, default set to curRun
	 * @param commandTime
	 */
	public void exportRun(ChronoTime commandTime)
	{
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (!runs.isEmpty())
				printer.export(curRun, runs.get(curRun));
		}
	}
	
	/**
	 * Exports to .txt. uses the run given by runNum
	 * @param commandTime
	 * @param runNum
	 */
	public void exportRun(ChronoTime commandTime, int runNum)
	{
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (!runs.isEmpty() && runNum <= curRun)
				runprinter.export(runNum, runs.get(runNum));
			else
				history.add("runNum " + runNum+ " was invalid");
		}
	}
	
	public void exportRun(ChronoTime commandTime, int runNum, Printer curPrint)
	{
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (!runs.isEmpty())
				curPrint.print(runs.get(runNum).getLog());
			else
				history.add("runNum " + runNum+ " was invalid");
		}
	}
	/**
	 * posts to server
	 */
	private static void post(Object o){

		if(!(o instanceof String) && !(o instanceof NamedRacer))
		{System.out.println("Cannot POST object of type "+o.getClass());return;}

		try{
			// Client will connect to this location
			URL site = new URL("http://localhost:8000/sendresults");
			HttpURLConnection conn = (HttpURLConnection) site.openConnection();

			// create a POST request
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());

			// write String (Command) or Employee prepended with "ADD"
			if (o instanceof String)
				out.writeBytes((String) o);
			else{
				// write Employee JSON string to output buffer for message
				NamedRacer e = (NamedRacer) o;
				out.writeBytes("ADD " + new Gson().toJson(e));}

			out.flush();
			out.close();
			System.out.print("Sent POST to server // ");

			InputStreamReader inputStr = new InputStreamReader(conn.getInputStream());

			// string to hold the result of reading in the response
			StringBuilder sb = new StringBuilder();

			// read the characters from the request byte by byte and build up
			// the Response
			int nextChar;
			while ((nextChar = inputStr.read()) > -1) {
				sb = sb.append((char) nextChar);
			}
			System.out.println("Return String: " + sb);
		}

		catch (Exception e) {e.printStackTrace();}
	}
	/**
	 * exports to server
	 * @param commandTime
	 */
	public void exportToServer(ChronoTime commandTime)
	{
		if(power)
		{
			try {
				officialTime = commandTime.withOffset(offset);
			} catch (InvalidTimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			post("CLEAR");
			if(!runs.isEmpty())
			{
				ArrayList<Racer> temp = (runs.get(curRun)).getAllRacers();
				ArrayList<NamedRacer> racers = new ArrayList<NamedRacer>();
				int k =0;
				for(Racer r : temp)
				{
					String fname, lname;
					try{
					fname = temps[k];
					lname = temps[k+1];
					}
					catch(NullPointerException e)
					{
						fname = "undefined";
						lname = "un";
					}
					racers.add(new NamedRacer(r, fname, lname));
					k = k+2;
				}
				for(int i = 0; i < racers.size(); i++)
				{
					try{post("ADD " + (new Gson().toJson(racers.get(i))));}
					catch (Exception e){}//muffle if server isn't running
				}
				
			}
		}
	}
	/**
	 * 
	 */
	public void flush()
	{
		if(power)
		{
			//try to flush race if it exists
			if (!runs.isEmpty())
				printer.flush(runs.get(curRun).getLog());
			
			//flush chronotrigger history
			printer.flush(history);
		}
	}
	/**
	 * Returns the Card of the current race
	 * @return card of current run
	 */
	public Card getCard(ChronoTime currentTime)
	{
		try {
			officialTime = currentTime.withOffset(offset);
		} catch (InvalidTimeException e1) {
			e1.printStackTrace();
		}

		return runs.get(curRun).getCard(officialTime);
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
			t1 = ChronoTime.now();
			t2 = ChronoTime.now();
			t3 = ChronoTime.now();
			t4 = ChronoTime.now();
			ct = new ChronoTrigger();
			ct.powerOn(t1);
		}
		
		public void testConstructors(){
			//constructor 1
			ct = new ChronoTrigger();
			ct.powerOn(t1);
			assertFalse(ct.channels == null);
			for (int i=0; i< 8; i++){
				assertFalse(ct.channels[i] ==null);
				assertFalse(ct.channels[i].trigger());}
			assertFalse(ct.history == null);
			assertFalse(ct.printer == null);
			assertFalse(ct.officialTime == null);
			
			//constructor 2
			ct = new ChronoTrigger(t1);
			ct.powerOn(t1);
			assertFalse(ct.channels == null);
			for (int i=0; i< 8; i++){
				assertFalse(ct.channels[i] ==null);
				assertFalse(ct.channels[i].trigger());}
			assertFalse(ct.history == null);
			assertFalse(ct.printer == null);
			assertEquals(t1, ct.officialTime);
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

