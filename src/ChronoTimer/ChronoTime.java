package ChronoTimer;
/**
 * The Class ChronoTime.
 */
public class ChronoTime {
	
	/** The current time in hundredths of seconds
		Range: [0, MAX_TIME]. */
	private int _currentTime;
	private static final int MAX_TIME = 8639999; // [0:0:0.0, 23:59:59.99]
	
	/** The tick amount in hundredths of seconds.
	 * 	Defaults to 1. */
	private int _tickAmount = 1;
	
	/** Toggle to enable tick. */
	private boolean _running;
	
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
			seconds = Integer.parseInt(tokens[2]);}
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
	 * Sets the tick amount.
	 * @param hundredths the new tick amount
	 * @throws InvalidTimeException if parameter is outside of range [1-MAX_TIME]
	 */
	public void setTickAmount(int hundredths) throws InvalidTimeException {
		if (hundredths < 1 || hundredths > MAX_TIME) throw new InvalidTimeException("Invalid tick amount.");
		_tickAmount = hundredths;}

	/** Starts clock. */
	public void start() {_running=true;}

	/** Increased _currentTime by _tickAmount. */
	public void tick() {
		if (_running) _currentTime += _tickAmount;
		// wrap
		if (_currentTime > MAX_TIME) _currentTime %= (MAX_TIME + 1);}

	/** Stops clock. */
	public void stop() {_running=false;}

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
		return hours+":"+minutes+":"+seconds+"."+remaining;
	}

	
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