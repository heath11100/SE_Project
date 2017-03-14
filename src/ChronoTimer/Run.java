package ChronoTimer;

import java.util.ArrayList;
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
 *  
 *  
 *  NEW QUESTIONS:
 *  9) Can racers have the same number in different lanes? I implemented it so they cannot.
 *  
 *  10) Should I add lane creation to the log?
 *  
 *  11) When switching between a PARIND to IND, what should happen to all of the queues?
 */

public class Run {
	private ChronoTime startTime;
	private ChronoTime endTime;
	
	private EventType eventType;
	
	private ArrayList<Queue<Racer>> queuedLists;
	private ArrayList<Queue<Racer>> runningLists;
	private ArrayList<Queue<Racer>> finishedLists;
	
	private Log log;
	
	public Run(EventType eventType) {
		this.eventType = eventType;
		
		this.queuedLists = new ArrayList<>();
		this.runningLists = new ArrayList<>();
		this.finishedLists = new ArrayList<>();
		
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
	 */
	public int newLane() throws RaceException {
		//Verify event type & lane count.
		if (this.queuedLists.size() >= 1 && this.getEventType() == EventType.IND) {
			throw new RaceException("Cannot create more than one lane with event type IND");
		}
		
		this.queuedLists.add(new LinkedList<Racer>());
		this.runningLists.add(new LinkedList<Racer>());
		this.finishedLists.add(new LinkedList<Racer>());
		
		if (this.queuedLists.size() != this.runningLists.size() && 
				this.runningLists.size() != this.finishedLists.size()) {
			throw new IllegalStateException("The lists are not synchronized, sizes are not all equal.");
		}
		
		return this.queuedLists.size();
	}
	
	/**
	 * Removes the last lane from the list.
	 * @return the number of the removed lane.
	 */
	public int removeLane() throws RaceException {
		int size = this.queuedLists.size();
		
		if (size == 0) {
			throw new RaceException("No lane to remove");
		}
		
		this.queuedLists.remove(size - 1);
		this.runningLists.remove(size - 1);
		this.finishedLists.remove(size - 1);
		
		if (this.queuedLists.size() != this.runningLists.size() && 
				this.runningLists.size() != this.finishedLists.size()) {
			throw new IllegalStateException("The lists are not synchronized, sizes are not all equal.");
		}
		
		return this.queuedLists.size()+1;
	}
	
	/**
	 * Determines whether or not a lane number is valid. A lane number is valid when it is in bounds [1,8] AND 
	 * the lane number is less than or equal to the number of lanes available.
	 * @param laneNumber corresponding to index+1 in the lists
	 * @return true if lane number is valid, false otherwise.
	 */
	private boolean isValidLane(int laneNumber) {
		return (laneNumber >= 1 && laneNumber <= 8) && (laneNumber <= this.queuedLists.size());
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
			
			
			for (int queueIndex = 0; queueIndex < this.queuedLists.size(); queueIndex++) {
				Queue<Racer> runningQueue = this.runningLists.get(queueIndex);
				
				for (Racer racer : runningQueue) {
					//Add the racer to the corresponding finished queue
					this.finishedLists.get(queueIndex).add(racer);
					racer.didNotFinish();
				}
				
				//Remove all racers that were running from the queue
				runningQueue.clear();
				
				this.log.add("Ended run at time: " + atTime);

			}
		}
	}
	
	/**
	 * Determines whether or not it is OK to change the event type.
	 * You can only change the event type BEFORE a racer is put into the run.
	 * @return true if you can change the event type, false otherwise.
	 */
	private boolean canChangeEventType() {
		boolean isValid = true;
		
		for (Queue<Racer> queue : this.queuedLists) {
			isValid = queue.size() == 0;
			
			if (!isValid) {
				break;
			}
		}
		
		if (isValid) {
			for (Queue<Racer> queue : this.runningLists) {
				isValid = queue.size() == 0;
				
				if (!isValid) {
					break;
				}
			}
		}
		
		if (isValid) {
			for (Queue<Racer> queue : this.finishedLists) {
				isValid = queue.size() == 0;
				
				if (!isValid) {
					break;
				}
			}
		}
		
		return isValid;
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
		}
		
		switch (newEventType) {
		case "IND":
			this.eventType = EventType.IND;
			
			//Remove all existing lanes
			this.queuedLists.clear();
			this.runningLists.clear();
			this.finishedLists.clear();
			
			//Add one lane back to each of the lists
			this.newLane();
			
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
	 * Determines whether or not a racer can be queued with a given racer number.
	 * Note: racer can only be queued if another racer does NOT exist with that racer's number 
	 * (regardless of the lane they are in)
	 * @param racerNumber corresponding to the racer's number
	 * @return true if the racer can be queued, false otherwise.
	 */
	private boolean canQueueRacer(int racerNumber) {
		//Only valid if there is
		boolean isValid = true;
		
		for (Queue<Racer> queue : this.queuedLists) {
			
			for (Racer racer : queue) {
				if (racer.getNumber() == racerNumber) {
					isValid = false;
				}
				
				if (!isValid) {
					break;
				}
			}
			
			if (!isValid) {
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
	public void queueRacer(int racerNumber, int lane) throws RaceException {		
		if (racerNumber < 1 || racerNumber > 9999) {
			throw new RaceException("Number must be within bounds [1,9999]");
			
		} else if (!this.isValidLane(lane)) {
			throw new RaceException("Invalid lane: " + lane);
			
		} else if (!this.canQueueRacer(racerNumber)) {
			throw new RaceException("Racer already exists with number: " + racerNumber);
			
		} else if (this.hasEnded()) { 
			throw new RaceException("Run has already ended");
			
		} else {
			Racer racer = new Racer(racerNumber);
			
			this.queuedLists.get(lane -1).add(racer);
						
			this.log.add("Queued racer");
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
			Queue<Racer> queuedQueue = this.queuedLists.get(lane - 1);
			Racer racer = null;
			for (Racer r : queuedQueue) {
				if (r.equals(racer)) {
					racer = r;
					break;
				}
			}
			
			if (racer == null) {
				throw new RaceException("No racer to remove");
				
			} else {
				queuedQueue.remove(racer);
				
				this.log.add("Removed racer");
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
			throw new RaceException("Invalid lane: " + lane);
			
		} else {
			Queue<Racer> queuedQueue = this.queuedLists.get(lane - 1);
			Racer nextRacer = queuedQueue.poll();
			
			if (nextRacer == null) {
				throw new RaceException("No racer to start");
				
			} else {
				this.startTime = _tempStartTime;
				queuedQueue.remove(nextRacer);
				this.runningLists.get(lane - 1).add(nextRacer);
								
				ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);
				nextRacer.start(elapsedTime);
				
				this.log.add("" + atTime + " Next racer started");
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
			
		} else {
			Queue<Racer> runningQueue = this.runningLists.get(lane - 1);
			Racer nextRacer = runningQueue.poll();
			
			if (nextRacer == null) {
				throw new RaceException("No racer to finish");
				
			} else {
				runningQueue.remove(nextRacer);
				this.finishedLists.get(lane - 1).add(nextRacer);
								
				ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);
				nextRacer.finish(elapsedTime);
				
				this.log.add("" + atTime + " Next racer finished");
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
			Queue<Racer> runningQueue = this.runningLists.get(lane - 1);
			Racer nextRacer = runningQueue.poll();
			
			if (nextRacer == null) {
				throw new RaceException("No racer to cancel");
				
			} else {
				runningQueue.remove(nextRacer);
				
				this.queuedLists.get(lane - 1).add(nextRacer);
				
				nextRacer.didNotFinish();
				
				this.log.add("Next racer did not finish");
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
			Queue<Racer> runningQueue = this.runningLists.get(lane - 1);
			Racer nextRacer = runningQueue.poll();
			
			if (nextRacer == null) {
				throw new RaceException("No racer to DNF");
				
			} else {
				runningQueue.remove(nextRacer);
				
				this.finishedLists.get(lane - 1).add(nextRacer);
				
				nextRacer.didNotFinish();
				
				this.log.add("Next racer did not finish");
			}
		}
	}
	
	public enum EventType {
		IND,
		PARIND;
	}
}
