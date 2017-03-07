package ChronoTimer;

import java.util.LinkedList;
import java.util.Queue;

import ChronoTimer.Race.EventType;
import Exceptions.*;

/* Questions:
 * 1) Should we have a "initialized time" for Run (currently startTime)
 *  AND a "startTime" that corresponds to the time the first racer began?
 *  
 * 2) Should we be able to add racers to the queue after the the first racer has started?
 * 
 * 3) Should we be able to remove racers from the queue after the first racer has started?
 * 
 * 4) What should happen when the last racer finishes?
 * 
 * 5) What happens when ending a run before all of the racers have completed?
 *  Should we not allow a run to be ended, but instead end it when all racers have finished or DNF'd?
 *  
 *  6) Should we be passing a copy of log to ChronoTrigger? If not, we might as well make it a public variable
 *  
 *  7) When do we allow the eventType to be changed? 
 *  (Currently you cannot change it once a racer has been put into the race)
 */

public class Run {
	private ChronoTime startTime;
	private ChronoTime endTime;
	
	private EventType eventType;
	
	private Queue<Racer> queuedRacers;
	private Queue<Racer> runningRacers;
	private Queue<Racer> finishedRacers;
	
	private Log log;
	
	Run(ChronoTime startTime, EventType eventType) {
		this.startTime = startTime;
		this.eventType = eventType;
		
		this.queuedRacers = new LinkedList<>();
		this.runningRacers = new LinkedList<>();
		this.finishedRacers = new LinkedList<>();
		
		this.log = new Log();
	}
	
	Run(ChronoTime startTime) {
		this(startTime, EventType.IND);
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
	private boolean hasStarted() {
		return this.startTime != null;
	}
	
	/**
	 * Determines whether or not the run has ended.
	 * @return true if the run has ended, false otherwise.
	 */
	private boolean hasEnded() {
		return this.endTime != null;
	}
	
	/**
	 * Returns a racer with a number equal to racerNumber.
	 * This searches all racers, regardless of status (i.e., queued, running, and finished)
	 * @param racerNumber corresponding to the racer being requested
	 * @return the racer corresponding to racerNumber, or null if a racer could not be found
	 */
	private Racer getRacer(int racerNumber) {
		return null;
	}
	
	/**
	 * Get the current event type of the run.
	 * @return eventType
	 */
	public EventType getEventType() {
		return this.eventType;
	}
	
	/**
	 * Sets the event type to a new event type.
	 * @param newEventType is a string representation of the eventType
	 * @throws RaceException when attempting to change the event type after a racer has started (or finished)  OR
	 * if newEventType does not correspond to a valid event type
	 */
	public void setEventType(String newEventType) throws RaceException {
		
	}
	
	/**
	 * Queues a racer, identified with racerNumber, to the queue of racers yet to begin the run.
	 * TODO: Can Racer be added after run has started?
	 * @param racerNumber is used to identify the racer, no other racer may have this number, 
	 * number must be 1 to 4 digits [1,9999]
	 * @throws RaceException when a racer exists with racerNumber 
	 * or when the racerNumber does not fit within the bounds [1,9999]
	 */
	public void queueRacer(int racerNumber) throws RaceException {
		
	}
	
	/**
	 * Removes a racer, identified with racerNumber, from the queue of racers yet to begin the run.
	 *	TODO: Should you be able to remove racers once the race has began?
	 * @param racerNumber is used to identify the racer, racer must exist, number must be in bounds [1,9999]
	 * @throws RaceException when a queued racer does not exist with racerNumber 
	 * or when the racerNumber is not within bounds [1,9999]
	 */
	public void removeRacer(int racerNumber) throws RaceException {
		
	}
	
	/**
	 * Starts the next racer in the queue.
	 * @param atTime is the absolute time the racer began, 
	 * time cannot be less than the run start time OR the previous racer's start time
	 * @throws InvalidTimeException when atTime is less than the run's start time OR 
	 * when the next racer's startTime is less than the previous racer's startTime
	 * @throws RaceException when there is not a racer left to start OR
	 * when the race has already ended
	 */
	public void startNextRacer(ChronoTime atTime) throws InvalidTimeException, RaceException {
		
	}
	
	/**
	 * Finishes the next racer that is currently racing.
	 * - TODO: What should happen when the last racer finishes? Should the Run end? Should it notify ChronoTrigger?
	 * @param atTime is the absolute time the racer finished, 
	 * time cannot be less than the run start time OR the previous racer's start time
	 * @throws InvalidTimeException when atTime is less than the run's start time OR 
	 * when the next racer's startTime is less than the previous racer's startTime
	 * @throws RaceException when there is not a racer left to finish OR
	 * when the race has already ended
	 */
	public void finishNextRacer(ChronoTime atTime) throws InvalidTimeException, RaceException {
		
	}
	
	/**
	 * Cancels the next racer to finish putting that racer at the end of the queued racers.
	 * @throws RaceException when there is not a racer to cancel OR
	 * when the race has already ended
	 */
	public void cancelNextRacer() throws RaceException {
		
	}
	
	/**
	 * Sets the next racer, to finish, as a Did Not Finish (DNF) finish type.
	 * @throws RaceException when there is not a racer to set DNF for OR
	 * when the race has already ended
	 */
	public void didNotFinishNextRacer() throws RaceException {
		
	}
	
	enum RunType {
		IND,
		PARIND;
	}
}
