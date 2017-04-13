package ChronoTimer;

import java.time.Duration;
import java.time.ZonedDateTime;

import Exceptions.InvalidTimeException;

/**
 * The Class ChronoTime.
 * @author - Casey Van Groll
 */
public class ChronoTime {
	
	/** The current time in hundredths of seconds
		Range: [0, MAX_TIME]. */
	private int _currentTime;
	private static final int MAX_TIME = 8639999; // [0:0:0.0, 23:59:59.99]
	
	/**
	 * Instantiates a new time.
	 * @param s the time to create in one of two formats:
	 * 		1: "<HR>:<MIN>:<SEC>"
	 * 		2: "<MIN>:<SEC>.<HUN>"
	 * @throws InvalidTimeException if parameter is malformed
	 */
	public ChronoTime(String s) throws InvalidTimeException {
		String[] tokens = s.split(":");
		int hours=0, minutes=0, seconds=0, hundredths=0;
		
		try{
		//format 1
		if (tokens.length == 3){
			hours = Integer.parseInt(tokens[0]);
			minutes = Integer.parseInt(tokens[1]);
			String[] t2 = tokens[2].split("\\.");
			if (t2.length == 1)
				seconds = Integer.parseInt(tokens[2]);
			else{
				seconds = Integer.parseInt(t2[0]);
				hundredths = Integer.parseInt(t2[1]);
			}
		}
		//format 2
		else if (tokens.length == 2){
			String[] t2 = tokens[1].split("\\.");
			if (t2.length != 2) throw new Exception("Invalid number of components.");
			tokens = new String[]{tokens[0], t2[0], t2[1]};
			minutes = Integer.parseInt(tokens[0]);
			seconds = Integer.parseInt(tokens[1]);
			hundredths = Integer.parseInt(tokens[2]);
		}
		//invalid format
		else throw new Exception("Invalid number of components.");
		
		// we will restrict parameters to be non-negative,
		// and their converted sum in hundredths to be in range [0,MAX_TIME]
		if (hours<0 || minutes<0 || seconds<0 || hundredths<0) throw new Exception("Negative value.");
		minutes += hours*60;
		seconds += minutes*60;
		hundredths += seconds*100;
		if (hundredths > MAX_TIME) throw new Exception("Calculated time: "+hundredths+" but range is [0, "+MAX_TIME+"]");
		_currentTime = hundredths;
		}
		catch(Exception e){throw new InvalidTimeException("Malformed String passed to ChronoTime constructor: "+e.getMessage());}
	}

	/**
	 * Instantiates a new time.
	 *
	 * @param hours the hours
	 * @param minutes the minutes
	 * @param seconds the seconds
	 * @param hundredths the hundredths
	 * @throws InvalidTimeException if invalid parameters
	 */
	public ChronoTime(int hours, int minutes, int seconds, int hundredths) throws InvalidTimeException {
		this((minutes+hours*60)+":"+seconds+"."+hundredths);
	}

	/**
	 * Calculates how much time has elapsed since the parameter time.
	 * If this is before startTime we will assume a time-wrap occurred between the two.
	 * 
	 * @param startTime the start time
	 * @return time elapsed since startTime
	 */
	public ChronoTime elapsedSince(ChronoTime startTime) throws InvalidTimeException{
		int start = startTime._currentTime;
		int end = _currentTime;
		
		if (end < start)
		end += MAX_TIME + 1;
		return new ChronoTime(0,0,0,end-start);
	}
	
	/**
	 * Calculates if one time is before another.
	 * 
	 * @param other the time to compare against
	 * @return true if this time is before other time
	 */
	public boolean isBefore(ChronoTime other){
		return _currentTime < other._currentTime;}
	

	/**
	 * Gets this time as a timestamp.
	 * @return the time stamp in form "<MIN>:<SEC>.<HUN>"
	 */
	public String getTimeStamp() {
		int remaining = _currentTime;
		//calculate minutes and subtract from remaining
		int minutes = remaining / 6000;
		remaining%= 6000;
		//calculate seconds and subtract from remaining
		int seconds = remaining / 100;
		remaining%=100;
		return minutes+":"+seconds+"."+remaining;
	}

	/**
	 * Gets this time as a string.
	 * @return the time in form "<HR>:<MIN>:<SEC>.<HUN>"
	 */
	@Override
	public String toString() {
		int remaining = _currentTime;
		//calculate hours and subtract from remaining
		int hours = remaining / 360000;
		remaining %= 360000;
		//calculate minutes and subtract from remaining
		int minutes = remaining / 6000;
		remaining%= 6000;
		//calculate seconds and subtract from remaining
		int seconds = remaining / 100;
		remaining%=100;
		
		return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, remaining);
	}
	
	public int asHundredths(){return _currentTime;}

	public static ChronoTime now() throws InvalidTimeException{
		ZonedDateTime t = ZonedDateTime.now();
		return new ChronoTime(t.getHour(), t.getMinute(), t.getSecond(), t.getNano()/10000000);}
	
	//Allows to get real-time events after setting CT system time
	public static ChronoTime now(Duration offset) throws InvalidTimeException{
		ZonedDateTime t = ZonedDateTime.now().plus(offset);
		return new ChronoTime(t.getHour(), t.getMinute(), t.getSecond(), t.getNano()/10000000);}
	
	/**
	 * We will consider two ChronoTimes as equal if their times are equal.
	 * We won't consider their tick amounts or whether they are running or not.
	 */
	@Override
	public boolean equals(Object o){
		if (!(o instanceof ChronoTime)) return false;
		ChronoTime t = (ChronoTime) o;
		return t._currentTime == _currentTime;
	}
	
	
	@Override
	protected ChronoTime clone() {
		try {return new ChronoTime(getTimeStamp());}
		catch (InvalidTimeException e) {e.printStackTrace();return null;}
	}
}