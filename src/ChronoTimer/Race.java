package ChronoTimer;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import Exceptions.*;

public class Race {
	private Queue<Racer> queuedRacers;
	private Queue<Racer> racingRacers;
	private Queue<Racer> finishedRacers;
	
	private EventType eventType;
	
	private ChronoTime startTime;
	private ChronoTime endTime;
	
	private Log log;
	
	/**
	 * Initializes a race with no racers.
	 * @param eventType the type of event this race will be.
	 * @throws InvalidTimeException 
	 */
	@Deprecated
	public Race(EventType eventType) throws InvalidTimeException {
		this.queuedRacers = new LinkedList<Racer>();
		this.racingRacers = new LinkedList<Racer>();
		this.finishedRacers = new LinkedList<Racer>();
		
		this.eventType = eventType;
		this.startTime = new ChronoTime(0, 0, 0, 0);
		
		this.log = new Log();
	}
	
	public Race(EventType eventType, ChronoTime startTime) {
		this.queuedRacers = new LinkedList<Racer>();
		this.racingRacers = new LinkedList<Racer>();
		this.finishedRacers = new LinkedList<Racer>();
		
		this.eventType = eventType;
		
		this.startTime = startTime;
		this.log = new Log();
	}
	
	/**
	 * Initializes a race with no racers and event type IND.
	 * @param startTime the time the race began
	 */
	public Race(ChronoTime startTime) {
		this(EventType.IND, startTime);		
	}
	
	/**
	 * Changes the eventType
	 * @param eType the type of event you would like to change it to
	 */
	public void setEventType(String type) throws RaceException {
		EventType eType = null;
		switch(type){
			case "IND":eType = EventType.IND;break;//all that's needed for now
		}
		
		
		if (this.racingRacers.size() > 0 || this.finishedRacers.size() > 0) {
			throw new RaceException("Cannot set event type after a racer has started");
		} else if (eType == null){
			throw new RaceException("Illegal event type");
		}
		else{
			this.eventType = eType;
			this.log.add("Changed event type to " + this.eventType);
		}
	}
	
	/**
	 * Adds a racer to the queue of racers yet to start. 
	 * This ensures that the racer is not a duplicate, if a duplicate racer is added an exception is thrown.
	 * @throws DuplicateRacerException if a racer with raacerNumber already exists
	 * @param racer to be added to the list
	 * @precondition the race has NOT began and racer does not already exist.
	 */
	public void add(int racerNumber) throws RaceException {
		Racer racer = new Racer(racerNumber);
		if (this.finishedRacers.contains(racer) || 
				this.racingRacers.contains(racer) || 
				this.queuedRacers.contains(racer)) {
			throw new RaceException("Duplicate racer");
		} else {
			this.queuedRacers.add(racer);
			this.log.add("Added Racer: " + racer);
		}
	}
	
	/**
	 * Finds the racer with the racerNumber and removes them from the list of racers yet to start.
	 * This will only remove a racer that is queued. Once a racer has started or finished they cannot be removed.
	 * @param racerNumber corresponding to the racer to be deleted.
	 * @return the Racer that was removed, or null if no racer was found
	 * @throws InvalidRacerException when a racer cannot be found with the given racerNumber.
	 */
	public Racer remove(int racerNumber) throws RaceException {
		Racer removedRacer = getRacer(racerNumber);
		if (removedRacer != null) {
			queuedRacers.remove(removedRacer);
			this.log.add("Removed Racer: " + removedRacer);
		}
		else {
			throw new RaceException("Invalid racer number");
		}
		return removedRacer;
	}
	
	/**
	 * Finds a racer with a number equal to racerNumber of racers yet to start.
	 * @param racerNumber corresponding to the racer to be deleted.
	 * @return the Racer corresponding to racerNumber, or null if no racer was found
	 */
	public Racer getRacer(int racerNumber) {
		Racer returnRacer = null;
		for (Racer racer : queuedRacers) {
			if (racer.getNumber() == racerNumber) {
				returnRacer = racer;
				break;
			}
		}
		
		if (returnRacer == null) {
			for (Racer racer : racingRacers) {
				if (racer.getNumber() == racerNumber) {
					returnRacer = racer;
					break;
				}
			}
		}
		
		if (returnRacer == null) {
			for (Racer racer : finishedRacers) {
				if (racer.getNumber() == racerNumber) {
					returnRacer = racer;
					break;
				}
			}
		}
		return returnRacer;
	}
	
	/**
	 * Starts the race at the given time.
	 * Calling this method after the race has already begun will cause an exception to be thrown.
	 * @throws InvalidRaceStartException
	 * @param withTime corresponds to the time of the race starting.
	 */
	@Deprecated
	public void beginRace(ChronoTime startTime) throws InvalidTimeException {
		if (this.startTime == null) {
			this.startTime = startTime;
		} else {
			throw new InvalidTimeException("Race that already began - command canceled");
		}
	}
	
	/**
	 * Ends the race with the given time.
	 * @throws InvalidRaceEndException
	 * @param withTime corresponding to the time the race ends.
	 */
	public void endRace(ChronoTime endTime) throws InvalidTimeException {
		if (this.endTime != null) {
			throw new InvalidTimeException("Race already ended");
		} else {
			this.endTime = endTime;
			this.log.add(endTime.getTimeStamp() + " Ended Race");
		}
	}
	
	public boolean isStarted()
	{
		return startTime == null ? false : true;
	}
	
	public boolean isEnded()
	{
		return endTime == null ? false : true;
	}
	
	/**
	 * Prints a list of all racers and their respective data to the console.
	 */
	@Deprecated
	public void printRace() {
		//Queued Racers
		for (Racer racer : queuedRacers) {
			String racerOutput = "Racer[" + racer.getNumber() + "] - Queued";
			System.out.println(racerOutput);
		}
		
		//Current Racing Racers
		for (Racer racer : racingRacers) {
			String racerOutput = "Racer[" + racer.getNumber() + "] - Racing | Start time: ";
			racerOutput += racer.getStartTime().toString();
			
			System.out.println(racerOutput);
		}
		
		//Finished Racers
		for (Racer racer : finishedRacers) {
			String racerOutput = "Racer[" + racer.getNumber() + "] - Racing | Start time: ";
			racerOutput += racer.getStartTime().toString();
			
			racerOutput += " | End time: ";
			racerOutput += racer.getEndTime().toString();
			
			System.out.println(racerOutput);
		}
	}
	
	/**
	 * Adds the next queued racer to the race with start time atTime.
	 * @precondition the race has already began, there is a racer to start
	 * @param atTime the absolute time the next racer started
	 * @throws InvalidTimeException when the time the racer starts is before the race start time.
	 * @throws InvalidRaceException when there is not another racer to start
	 */
	public void startNextRacer(ChronoTime atTime) throws RaceException, InvalidTimeException {
		Racer nextRacer;
		try {
			nextRacer = this.queuedRacers.remove();
		} catch (NoSuchElementException e) {
			throw new RaceException("No racer to start");
		}
		
		if (this.isEnded()) {
			throw new RaceException("Race has already ended");
		} else if (atTime.isBefore(this.startTime)) {
			//Illegal time because the time the racer is starting is before the race start time
			throw new InvalidTimeException("Invalid racer time");
		} else if (nextRacer == null) {
			throw new RaceException("No racer to start");
		} else {
			/* Then: race has started
			 * Racer is starting at or after the time the race started
			 * There is another racer waiting in the queue.
			 */
			ChronoTime elapsed = atTime.elapsedSince(startTime);
			nextRacer.start(elapsed);
			this.racingRacers.add(nextRacer);
			
			//TODO: Should time be elapsed or absolute?
			this.log.add(elapsed.getTimeStamp() + " | started racer: " + nextRacer);
		}
	}
	
	/**
	 * Sets the next racer in the racing queue to the finished racers queue with finish time atTime.
	 * 	 * @precondition the race has already began and there is at least one racer in the racingRacers queue
	 * @param atTime the absolute time the racer finished
	 * @throws InvalidRaceException when attempting to finish the next racer when there is not a racer in the race.
	 * @throws InvalidTimeException when the racer's status is not RACING
	 */
	public void finishNextRacer(ChronoTime atTime) throws RaceException, InvalidTimeException {
		Racer nextRacer = this.racingRacers.poll();
		if (this.isEnded()) {
			throw new RaceException("Race has already ended");
		} else if (this.endTime != null) {
			//Then the race has already ended
			throw new RaceException("Cannot finish racer after race ended");
			
		} else if (nextRacer != null) {
			//Then there is a racer to finish.
			ChronoTime elapsed = atTime.elapsedSince(startTime);
			nextRacer.finish(elapsed);
			this.finishedRacers.add(nextRacer);
			//TODO: Should time be elapsed or absolute?
			this.log.add(elapsed.getTimeStamp() + " | finished racer: " + nextRacer);
		} else {
			throw new RaceException("No racer to finish");
		}
	}
	
	/**
	 * Sets the racer corresponding to racer number.
	 * @throws RaceException when racerNumber is invalid (does not correspond to racer) or if a Racer has a DNF status
	 * @param racerNumber corresponding to the racer
	 */
	public void cancel() throws RaceException {
		Racer racer = this.racingRacers.poll();
		if (this.isEnded()) {
			throw new RaceException("Race has already ended");
		} else if (racer == null) {
			throw new RaceException("No Racer to cancel");
		} else {
			if (racer.getStatus() == Racer.Status.RACING) {
				this.queuedRacers.add(racer);
				racer.cancel();
			} else {
				throw new RaceException("Cannot cancel a racer that is " + racer.getStatus());
			}
			this.log.add("Cancelled racer: " + racer);
		}
	}
	
	/**
	 * Sets the racer corresponding to racer number to DNF.
	 * @param racerNumber corresponding to the racer
	 * @throws RaceException thrown if a racer cannot be found or if the racer is not DNF
	 */
	public void didNotFinish() throws RaceException {
		Racer racer = this.racingRacers.poll();
		
		if (this.isEnded()) {
			throw new RaceException("Race has already ended");
		} else if (racer == null) {
			throw new RaceException("No Racer available");
		} else {
			
			try {
				racer.didNotFinish();
				
				this.racingRacers.remove(racer);
				this.finishedRacers.add(racer);
				this.log.add("Racer: " + racer + " - did not finish");

			} catch (IllegalStateException e) {
				throw new RaceException("Cannot DNF racer that is not racing");
			}
		}
	}
	
	/**
	 * The log for the Race
	 * @return the log
	 */
	public Log getLog() {
		return this.log;
	}
	public boolean isOver()
	{
		if(this.endTime != null)
			return true;
		else
			return false;
	}
	enum EventType {
		IND;
	}
}
