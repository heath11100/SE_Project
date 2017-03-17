package ChronoTimer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import Exceptions.*;

/* Questions:
 *  1) Can racers have the same number in different lanes? I implemented it so they cannot.
 *  	- NOPE
 *  2) Should I add lane creation to the log?
 *  	- NOPE
 *  3) When switching between a PARIND to IND, what should happen to all of the queues?
 *  	- WHAT is currently happening
 *  4) We start the run when the first racer begins. What happens if the first, and only, racer is cancelled
 *  	so they are moved back to the start queue?
 *  	- Run continues going
 *  
 *  5) At what point should we prevent a lane creation?
 *  	- Currently there is not a restriction
 *  	- I think it makes sense to prevent it after the race has started.
 *  	- Once race has started we should prevent lane creation.
 */

public class Run {
	private ChronoTime startTime;
	private ChronoTime endTime;
	
	private EventType eventType;
	
	private Queue<Racer> queuedRacers;
	private ArrayList<Queue<Racer>> runningLanes;
	private Queue<Racer> finishedRacers;
	
	private Log log;
	
	public Run(EventType eventType) {
		this.eventType = eventType;
		
		this.queuedRacers = new LinkedList<>();
		this.runningLanes = new ArrayList<>();
		this.finishedRacers = new LinkedList<>();
		
		this.log = new Log();
		
		this.log.add("Created run");
	}
	
	public Run() {
		this(EventType.IND);
	}
	
	/**
	 * Determines whether or not a lane number is valid. A lane number is valid when it is in bounds [1,8] AND 
	 * the lane number is less than or equal to the number of lanes available.
	 * @param laneNumber corresponding to index+1 in the lists
	 * @return true if lane number is valid, false otherwise.
	 */
	private boolean isValidLane(int laneNumber) {
		return (laneNumber >= 1 && laneNumber <= 8) && (laneNumber <= this.runningLanes.size());
	}
	
	/**
	 * Get the log the run maintains to keep track of all changes to the run.
	 * @return the log
	 */
	public Log getLog() {
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
	 * Get the current event type of the run.
	 * @return eventType
	 */
	public EventType getEventType() {
		return this.eventType;
	}
	
	/**
	 * Creates a new lane for runners to be added to.
	 * @return lane number corresponding to the lane just created.
	 * @throws RaceException when attempting to create a multiple lanes for an IND run
	 * OR if the run has started
	 * OR if the run has ended
	 * OR if the number of lanes will exceed 8 (after creating this new one)
	 */
	public int newLane() throws RaceException {
		//Verify event type & lane count.
		if (this.runningLanes.size() >= 1 && this.getEventType() == EventType.IND) {
			throw new RaceException("Cannot create more than one lane with event type IND");
		} else if (this.hasStarted()) {
			throw new RaceException("Cannot create new lane after run started");
		} else if (this.hasEnded()) {
			throw new RaceException("Cannot create new lane after run ended");
		} else if (this.runningLanes.size() >= 8) {
			throw new RaceException("Cannot have more than 8 lanes.");
		}
		
		this.runningLanes.add(new LinkedList<Racer>());
		
		return this.runningLanes.size();
	}
	
	/**
	 * Removes the last lane from the list.
	 * @return the number of the removed lane.
	 * @throws RaceException when there is not a lane to remove
	 * @throws IllegalStateException when the lists are not the same size (this would be an internal error)
	 */
	public int removeLane() throws RaceException {
		int size = this.runningLanes.size();
		
		if (size == 0) {
			throw new RaceException("No lane to remove");
		}
		
		this.runningLanes.remove(size - 1);
		
		return this.runningLanes.size()+1;
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
			this.startTime = atTime;
			this.endTime = atTime;
			//throw new RaceException("Run has not started.");
			
		} else if (!this.startTime.isBefore(atTime)) {
			throw new InvalidTimeException("Run has not started.");

		} else {
			this.endTime = atTime;
			
			for (Queue<Racer> queue : this.runningLanes) {
				//Iterate through every running queue
				for (Racer racer : queue) {
					//Add the racer to the corresponding finished queue
					this.finishedRacers.add(racer);
					racer.didNotFinish();
				}
				
				queue.clear();				
			}
		}
		this.log.add("Ended run at " + atTime);

	}
	
	/**
	 * Determines whether or not it is OK to change the event type.
	 * You can only change the event type BEFORE the race begins.
	 * @return true if you can change the event type, false otherwise.
	 */
	private boolean canChangeEventType() {		
		int runningSize = 0;
		
		for (Queue<Racer> queue : this.runningLanes) {
			runningSize += queue.size();
		}
		
		return runningSize == 0 && this.finishedRacers.size() == 0;		
	}
	
	/**
	 * Sets the event type to a new event type. The event type can be changed until a racer has been added to the run (queued).
	 * Note: When switching from PARIND to IND all lanes, except the first, are removed.
	 * @param newEventType is a string representation of the eventType
	 * @throws RaceException when attempting to change the event type after a racer is queued, running, or finished,  OR
	 * if newEventType does not correspond to a valid event type
	 */
	public void setEventType(String newEventType) throws RaceException {
		if (!this.canChangeEventType()) {
			throw new RaceException("Cannot change event type after a racer started");
			
		}  else if (this.hasStarted()) {
			throw new RaceException("Cannot change event type once run started");

		} else if (this.hasEnded()) {
			throw new RaceException("Cannot change event type once run ended");

		}
		
		switch (newEventType) {
		case "IND":
			this.eventType = EventType.IND;
			
			//Remove all existing lanes
			this.runningLanes.clear();
			
			//Add one lane back to each of the lists
			this.newLane();
			
			break;
			
		case "PARIND":
			this.eventType = EventType.PARIND;
			
			this.runningLanes.clear();
			
			//Add two lanes back to each of the lists
			this.newLane();
			this.newLane();
			
			break;
			
		default:
			throw new RaceException("Invalid event type: " + newEventType);
		}
		
		this.log.add("Event type is " + newEventType);
	}
	
	/**
	 * Determines whether or not a racer can be queued with a given racer number.
	 * Note: racer can only be queued if another racer does NOT exist with that racer's number 
	 * (regardless of the lane they are in)
	 * @param racerNumber corresponding to the racer's number
	 * @return true if the racer can be queued, false otherwise.
	 */
	private boolean canQueueRacer(int racerNumber) {
		//Only valid if there is not a racer with that number.
		boolean isValid = true;
				
		for (Racer racer : this.queuedRacers) {
			if (racer.getNumber() == racerNumber) {
				isValid = false;
				break;
			}
		}
		
		return isValid;
	}
	
	/**
	 * Queues a racer, identified with racerNumber, to the queue of racers yet to begin the run.
	 * @param racerNumber is used to identify the racer, no other racer may have this number, 
	 * number must be 1 to 4 digits [1,9999]
	 * @param lane is the lane the racer is registered with, lane must be in bounds of [1,8] and a lane that currently exists
	 * @throws RaceException when a racer exists with racerNumber 
	 * OR when the lane is invalid
	 * OR when the racerNumber does not fit within the bounds [1,9999]
	 * OR when the run was ended
	 */
	public void queueRacer(int racerNumber) throws RaceException {		
		if (racerNumber < 1 || racerNumber > 9999) {
			throw new RaceException("Number must be within bounds [1,9999]");
		}
			
		 else if (!this.canQueueRacer(racerNumber)) {
			throw new RaceException("Racer already exists with number: " + racerNumber);
			
		} else if (this.hasEnded()) { 
			throw new RaceException("Run has already ended");
			
		} else {
			Racer racer = new Racer(racerNumber);
			this.queuedRacers.add(racer);
						
			this.log.add("Queued "+racer);
		}
	}
	
	/**
	 * Removes a racer, identified with racerNumber, from the queue of racers yet to begin the run.
	 * @param racerNumber is used to identify the racer, racer must exist, number must be in bounds [1,9999]
	 * @param lane is the lane corresponding to the racer's lane
	 * @throws RaceException when a queued racer does not exist with racerNumber 
	 * or when the racerNumber is not within bounds [1,9999]
	 * OR when the lane is invalid
	 */
	public void removeRacer(int racerNumber, int lane) throws RaceException {
		if (racerNumber < 1 || racerNumber > 9999) {
			throw new RaceException("Number must be within bounds [1,9999]");
		} else if (!this.isValidLane(lane)) {
			throw new RaceException("Invalid lane: " + lane);
			
		} else {
			Racer racer = null;
			for (Racer r : this.queuedRacers) {
				if (r.equals(racer)) {
					racer = r;
					break;
				}
			}
			
			if (racer == null) {
				throw new RaceException("No racer to remove");
				
			} else {
				queuedRacers.remove(racer);
				
				this.log.add("Removed "+racer);
			}
		}
	}
	
	/**
	 * Starts the next racer in the queue.
	 * @param atTime is the absolute time the racer began, 
	 * time cannot be less than the run start time OR the previous racer's start time
	 * @param lane is the lane corresponding to the racer's lane
	 * @throws InvalidTimeException when atTime is less than the run's start time
	 * @throws RaceException when there is not a racer left to start OR
	 * when the race has already ended 
	 * OR when the race has not started
	 * OR when the lane is invalid
	 */
	public void startNextRacer(ChronoTime atTime, int lane) throws InvalidTimeException, RaceException {
		ChronoTime _tempStartTime = null;
		
		if (!this.hasStarted()) {
			//Race has not started, 
			//set the start time to be the same as the racers.
			_tempStartTime = atTime;
		} else {
			_tempStartTime = this.startTime;
		}
		
		if (atTime == null) {
			throw new InvalidTimeException("NULL is Not a valid time");
			
		} else if (this.hasEnded()) {
			throw new RaceException("Race has ended");
			
		} else if (atTime.isBefore(_tempStartTime)) {
			throw new InvalidTimeException("Time is before the run start time");
			
		} else if (!this.isValidLane(lane)) {
			return;
			// No longer throwing error for invalid lanes?
			//throw new RaceException("Invalid lane: " + lane);
			
		} else {
			Racer nextRacer = queuedRacers.poll();
			
			if (nextRacer == null) {
				throw new RaceException("No racer to start");
				
			} else {
				this.startTime = _tempStartTime;
				queuedRacers.remove(nextRacer);
				this.runningLanes.get(lane - 1).add(nextRacer);
								
				ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);
				nextRacer.start(elapsedTime);
							
				//Log which lane the racer started in for PARIND only
				if (this.eventType == EventType.PARIND) {
					this.log.add("" + atTime +" "+nextRacer+" started in lane " + lane);
				} else {
					this.log.add("" + atTime +" "+nextRacer+" started");
				}
			}
		}
	}
	
	/**
	 * Finishes the next racer that is currently racing.
	 * @param atTime is the absolute time the racer finished, 
	 * time cannot be less than the run start time OR the previous racer's start time
	 * @param lane is the lane the racer belongs to
	 * @throws InvalidTimeException when atTime is less than the run's start time
	 * OR if atTime is null
	 * @throws RaceException when there is not a racer left to finish 
	 * OR when the race has already ended 
	 * OR when the race has not started
	 * OR when the lane is invalid
	 */
	public void finishNextRacer(ChronoTime atTime, int lane) throws InvalidTimeException, RaceException {
		if (atTime == null) {
			throw new InvalidTimeException("NULL is Not a valid time");
			
		} else if (!this.hasStarted()) {
			throw new RaceException("Race has not started");
			
		} else if (this.hasEnded()) {
			throw new RaceException("Race has ended");
			
		} else if (atTime.isBefore(this.startTime)) {
			throw new InvalidTimeException("Time cannot be before the run start time");
			
		} else if (!this.isValidLane(lane)) {
			// No longer throwing error for invalid lanes?
			//throw new RaceException("Invalid lane: " + lane);
		} else {
			Queue<Racer> runningQueue = this.runningLanes.get(lane - 1);
			Racer nextRacer = runningQueue.poll();
			
			if (nextRacer == null) {
				throw new RaceException("No racer to finish");
				
			} else {
				runningQueue.remove(nextRacer);
				this.finishedRacers.add(nextRacer);
								
				ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);
				nextRacer.finish(elapsedTime);
				
				//Log which lane the racer ended in for PARIND only
				if (this.eventType == EventType.PARIND) {
					this.log.add("" + atTime +" "+nextRacer+" finished in lane " + lane);
				} else {
					this.log.add("" + atTime +" "+nextRacer+" finished");
				}
			}
		}
	}
	
	/**
	 * Cancels the next racer to finish putting that racer at the end of the queued racers.
	 * @throws RaceException when there is not a racer to cancel OR
	 * when the race has already ended
	 */
	public void cancelNextRacer(int lane) throws RaceException {
		
		if (!this.isValidLane(lane)) {
			throw new RaceException("Invalid lane: " + lane);
			
		} else if (this.hasEnded()) {
			throw new RaceException("Race has ended");
			
		} else {
			LinkedList<Racer> runningQueue = (LinkedList<Racer>) this.runningLanes.get(lane - 1);
			Racer lastRacer = runningQueue.get(0);
			
			if (lastRacer == null) {
				throw new RaceException("No racer to cancel");
				
			} else {
				runningQueue.remove(lastRacer);
				
				this.queuedRacers.add(lastRacer);
								
				lastRacer.cancel();
				
				this.log.add(lastRacer+" cancelled");
			}
		}
	}
	
	/** 
	 * Sets the next racer, to finish, as a Did Not Finish (DNF) finish type.
	 * @param lane is the lane the next racer to DNF is in
	 * @throws RaceException when there is not a racer to set DNF for
	 * OR when the race has already ended
	 * OR when the lane is not a valid lane.
	 */
	public void didNotFinishNextRacer(int lane) throws RaceException {
		if (!this.isValidLane(lane)) {
			throw new RaceException("Invalid lane: " + lane);
			
		} else if (this.hasEnded()) {
			throw new RaceException("Race has ended");
			
		} else {
			LinkedList<Racer> runningQueue = (LinkedList<Racer>) this.runningLanes.get(lane - 1);
			Racer lastRacer = runningQueue.get(0);
			
			if (lastRacer == null) {
				throw new RaceException("No racer to DNF");
				
			} else {
				runningQueue.remove(lastRacer);
				this.finishedRacers.add(lastRacer);
				
				lastRacer.didNotFinish();
				
				this.log.add(lastRacer+" did not finish");
			}
		}
	}
	
	public enum EventType {
		IND,
		PARIND;
	}
}
