package ChronoTimer;

import java.util.LinkedList;
import java.util.Queue;

import Exceptions.*;

/* Questions:
 * 1) Should we have a "initialized time" for Run (currently startTime)
 *  AND a "startTime" that corresponds to the time the first racer began?
 *  - Should start when the first racer starts.
 *  
 * 2) Should we be able to add racers to the queue after the the first racer has started?
 *  - Yes, cannot add once run has ended.
 * 
 * 3) Should we be able to remove racers from the queue after the first racer has started?
 *  - Yes, cannot remove once run has ended.
 * 
 * 4) What should happen when the last racer finishes?
 *  - Nothing
 * 
 * 5) What happens when ending a run before all of the racers have completed?
 *  Should we not allow a run to be ended, but instead end it when all racers have finished or DNF'd?
 *  - DNF All racers, ASK DNF
 *  
 *  6) Should we be passing a copy of log to ChronoTrigger? If not, we might as well make it a public variable
 *  
 *  7) When do we allow the eventType to be changed? 
 *  (Currently you cannot change it once a racer has been put into the race)
 *  - Keep as current
 *  
 *  8) Should we pass a String for EventType in the constructor?
 */

public class Run {
	private ChronoTime startTime;
	private ChronoTime endTime;
	
	private EventType eventType;
	
	private Queue<Racer> queuedRacers;
	private Queue<Racer> runningRacers;
	private Queue<Racer> finishedRacers;
	
	private Log log;
	
	public Run(EventType eventType) {
		this.eventType = eventType;
		
		this.queuedRacers = new LinkedList<>();
		this.runningRacers = new LinkedList<>();
		this.finishedRacers = new LinkedList<>();
		
		this.log = new Log();
	}
	
	public Run() {
		this(EventType.IND);
	}
	
	/**
	 * Get the log the run maintains to keep track of all changes to the run.
	 * @return the log
	 */
	Log getLog() {
		return this.log;
	}
	
	/**
	 * Determines whether or not the run has started.
	 * @return true if the run has started, false otherwise.
	 */
	public boolean hasStarted() {
		return this.startTime != null;
	}
	
	/**
	 * Determines whether or not the run has ended.
	 * @return true if the run has ended, false otherwise.
	 */
	public boolean hasEnded() {
		return this.endTime != null;
	}
	
	/**
	 * Determines whether or not any racer has started the run.
	 * @return true if there is at least one racer running or at least one racer finished.
	 */
	public boolean hasRacerBegan() {
		return this.runningRacers.size() > 0 || this.finishedRacers.size() > 0;
	}
	
	/**
	 * Returns a racer with a number equal to racerNumber.
	 * This searches all racers, regardless of status (i.e., queued, running, and finished)
	 * @param racerNumber corresponding to the racer being requested
	 * @return the racer corresponding to racerNumber, or null if a racer could not be found
	 */
	private Racer getRacer(int racerNumber) {
		Racer racer = null;
		for (Racer r : this.queuedRacers) {
			if (r.equals(racer)) {
				racer = r;
				break;
			}
		}
		
		if (racer == null) {
			for (Racer r : this.runningRacers) {
				if (r.equals(racer)) {
					racer = r;
					break;
				}
			}
		}
		
		if (racer == null) {
			for (Racer r : this.finishedRacers) {
				if (r.equals(racer)) {
					racer = r;
					break;
				}
			}
		}
		
		return racer;
	}
	
	/**
	 * Get the current event type of the run.
	 * @return eventType
	 */
	public EventType getEventType() {
		return this.eventType;
	}
	
	/**
	 * Ends the current run. This cannot be undone. Any racers that are currently running will be set to DNF. 
	 * Queued racers are not affected.
	 * @param atTime is the time the run ended.
	 * @throws RaceException when attempting to end a run after a run has already been ended OR
	 * if the run has not started
	 * @throws InvalidTimeException when atTime is before the run start time
	 */
	public void endRun(ChronoTime atTime) throws RaceException, InvalidTimeException {		
		if (this.hasEnded()) {
			throw new RaceException("Run already ended.");
			
		} else if (!this.hasStarted()) {
			throw new RaceException("Run has not started.");
			
		} else if (!this.startTime.isBefore(atTime)) {
			throw new InvalidTimeException("Run has not started.");

		} else {
			this.endTime = atTime;
			
			for (Racer racer : this.runningRacers) {
				this.finishedRacers.add(racer);
				racer.didNotFinish();
			}
			//Remove all racers that were running.
			this.runningRacers.clear();
			
			this.log.add("Ended run at time: " + atTime);
		}
	}
	
	/**
	 * Sets the event type to a new event type.
	 * @param newEventType is a string representation of the eventType
	 * @throws RaceException when attempting to change the event type after a racer has started (or finished)  OR
	 * if newEventType does not correspond to a valid event type
	 */
	public void setEventType(String newEventType) throws RaceException {
		if (this.hasRacerBegan()) {
			throw new RaceException("Cannot change event type after a racer started");
		}
		
		switch (newEventType) {
		case "IND":
			this.eventType = EventType.IND;
			break;
			
		case "PARIND":
			this.eventType = EventType.PARIND;
			break;
			
		default:
			throw new RaceException("Invalid event type: " + newEventType);
		}
		
		this.log.add("Set event type to " + newEventType);
	}
	
	/**
	 * Queues a racer, identified with racerNumber, to the queue of racers yet to begin the run.
	 * TODO: Can Racer be added after run has started?
	 * @param racerNumber is used to identify the racer, no other racer may have this number, 
	 * number must be 1 to 4 digits [1,9999]
	 * @throws RaceException when a racer exists with racerNumber 
	 * or when the racerNumber does not fit within the bounds [1,9999] or when the run was ended
	 */
	public void queueRacer(int racerNumber) throws RaceException {
		Racer racer = getRacer(racerNumber);
		
		if (racerNumber < 1 || racerNumber > 9999) {
			throw new RaceException("Number must be within bounds [1,9999]");
		} else if (racer != null) {
			throw new RaceException("Racer already exists with number: " + racerNumber);
		} else if (this.hasEnded()) { 
			throw new RaceException("Run has already ended");
		} else {
			racer = new Racer(racerNumber);
			
			this.queuedRacers.add(racer);
			
			this.log.add("Queued racer");
		}
	}
	
	/**
	 * Removes a racer, identified with racerNumber, from the queue of racers yet to begin the run.
	 *	TODO: Should you be able to remove racers once the race has began?
	 * @param racerNumber is used to identify the racer, racer must exist, number must be in bounds [1,9999]
	 * @throws RaceException when a queued racer does not exist with racerNumber 
	 * or when the racerNumber is not within bounds [1,9999]
	 */
	public void removeRacer(int racerNumber) throws RaceException {
		Racer racer = null;
		for (Racer r : this.queuedRacers) {
			if (r.equals(racer)) {
				racer = r;
				break;
			}
		}
		
		if (racerNumber < 1 || racerNumber > 9999) {
			throw new RaceException("Number must be within bounds [1,9999]");
			
		} else if (racer == null) {
			throw new RaceException("Racer does not exist with number: " + racerNumber);
		} else {
			this.queuedRacers.remove(racer);	
			
			this.log.add("Removed racer");
		}
	}
	
	/**
	 * Starts the next racer in the queue.
	 * @param atTime is the absolute time the racer began, 
	 * time cannot be less than the run start time OR the previous racer's start time
	 * @throws InvalidTimeException when atTime is less than the run's start time
	 * @throws RaceException when there is not a racer left to start OR
	 * when the race has already ended OR when the race has not started
	 */
	public void startNextRacer(ChronoTime atTime) throws InvalidTimeException, RaceException {
		Racer nextRacer = this.queuedRacers.poll();
		ChronoTime _tempStartTime = null;
		
		if (!this.hasStarted()) {
			//Race has not started, 
			//set the start time to be the same as the racers.
			_tempStartTime = atTime;
		} else {
			_tempStartTime = this.startTime;
		}
		
		if (atTime == null) {
			throw new InvalidTimeException("NULL: Not valid time");
			
		} else if (this.hasEnded()) {
			throw new RaceException("Race has ended");
			
		} else if (atTime.isBefore(_tempStartTime)) {
			throw new InvalidTimeException("Time is before the run start time");
			
		} else if (nextRacer == null) {
			throw new RaceException("No racer to start");
			
		} else {
			this.startTime = _tempStartTime;
			
			this.queuedRacers.remove(nextRacer);
			this.runningRacers.add(nextRacer);
			
			ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);
			nextRacer.start(elapsedTime);
			
			this.log.add("" + atTime + " Next racer started");
		}
	}
	
	/**
	 * Finishes the next racer that is currently racing.
	 * - TODO: What should happen when the last racer finishes? Should the Run end? Should it notify ChronoTrigger?
	 * @param atTime is the absolute time the racer finished, 
	 * time cannot be less than the run start time OR the previous racer's start time
	 * @throws InvalidTimeException when atTime is less than the run's start time
	 * @throws RaceException when there is not a racer left to finish OR
	 * when the race has already ended OR when the race has not started
	 */
	public void finishNextRacer(ChronoTime atTime) throws InvalidTimeException, RaceException {
		Racer nextRacer = this.runningRacers.poll();
		
		if (atTime == null) {
			throw new InvalidTimeException("NULL: Not valid time");
			
		}  else if (!this.hasStarted()) {
			throw new RaceException("Race has not started");

		}else if (this.hasEnded()) {
			throw new RaceException("Race has ended");
			
		} else if (atTime.isBefore(this.startTime)) {
			throw new InvalidTimeException("Time is before the run start time");
			
		} else if (nextRacer == null) {
			throw new RaceException("No racer to finish");
			
		} else {
			this.runningRacers.remove(nextRacer);
			this.finishedRacers.add(nextRacer);
			
			ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);
			nextRacer.finish(elapsedTime);
			
			this.log.add("" + atTime + " Next racer finished");
		}
	}
	
	/**
	 * Cancels the next racer to finish putting that racer at the end of the queued racers.
	 * @throws RaceException when there is not a racer to cancel OR
	 * when the race has already ended
	 */
	public void cancelNextRacer() throws RaceException {
		Racer nextRacer = this.runningRacers.poll();
		
		if (this.hasEnded()) {
			throw new RaceException("Race has ended");

		} else if (nextRacer == null) {
			throw new RaceException("No racer to cancel");

		} else {
			this.runningRacers.remove(nextRacer);
			this.queuedRacers.add(nextRacer);
			
			nextRacer.cancel();
			
			this.log.add("Cancelled next racer");
		}
	}
	
	/**
	 * Sets the next racer, to finish, as a Did Not Finish (DNF) finish type.
	 * @throws RaceException when there is not a racer to set DNF for OR
	 * when the race has already ended
	 */
	public void didNotFinishNextRacer() throws RaceException {
		Racer nextRacer = this.runningRacers.poll();
		
		if (this.hasEnded()) {
			throw new RaceException("Race has ended");

		} else if (nextRacer == null) {
			throw new RaceException("No racer to DNF");

		} else {
			this.runningRacers.remove(nextRacer);
			this.finishedRacers.add(nextRacer);
			
			nextRacer.didNotFinish();
			
			this.log.add("Next racer did not finish");
		}
	}
	
	public enum EventType {
		IND,
		PARIND;
	}
}
