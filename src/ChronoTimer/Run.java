package ChronoTimer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import Exceptions.*;

/**
 * PARGRP:
 * - DNF, DNF any racer that has not finished.
 * - Cancel, Cancel all racers.
 */

public class Run {
	private ChronoTime startTime;
	private ChronoTime endTime;
	
	private EventType eventType;
	
	private Queue<Racer> queuedRacers;
	private ArrayList<Queue<Racer>> runningLanes;
	private Queue<Racer> finishedRacers;
	private int nextRacerToMarkIndex = 0;
	
	private Log log;
	private Card card = new Card();

	private final int MIN_BIB_NUMBER = 1;
	private final int MAX_BIB_NUMBER = 9999;
	private final int MAX_RACERS = 9999;

	private final int MAX_LANES = 8;

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
		switch (this.eventType) {
			case IND:
				return laneNumber == 1;
			default:
				return (laneNumber >= 1 && laneNumber <= MAX_LANES) && (laneNumber <= this.runningLanes.size());
		}
	}
	
	/**
	 * Get the log the run maintains to keep track of all changes to the run.
	 * @return the log
	 */
	public Log getLog() {
		return this.log;
	}

	/**
	 * Get the card of the run which displays information relative to the event type.
	 * The card and the information it displays per event type is shown below:
	 *
	 * Event Type - IND:
	 * 	Header:
	 * 	- Next three racers to start
	 * 	Body:
	 * 	- Current racers racing
	 * 	Footer:
	 * 	- Last Racer to finish
	 *
	 *
	 * Event Type - PARIND:
	 * 	Header:
	 * 	- Next pair to run
	 * 	Body:
	 * 	- NOTHING
	 * 	Footer:
	 * 	- Finish times of the last pair to finish
	 *
	 *
	 * Event Type - GRP:
	 * 	Header:
	 * 	- Running time
	 * 	Body:
	 * 	- NOTHING
	 * 	Footer:
	 * 	- Last finish time
	 *
	 * @return a card with the information relevant to the current event type.
     */
	public Card getCard(ChronoTime currentTime) {
		//Format card based on event type.
		switch (this.eventType) {
			case IND:

				setINDcard(currentTime);
				break;

			case PARIND:
				setPARINDcard();
				break;

			case GRP:
				setGRPcard();
				break;
		}

		return this.card;
	}

	/**
	 * Sets this.card with the following information:
	 *
	 * Event Type - IND:
	 * 	Header:
	 * 	- Next three racers to start
	 * 	Body:
	 * 	- Current racers racing
	 * 	Footer:
	 * 	- Last Racer to finish
	 */
	private void setINDcard(ChronoTime currentTime) {
		this.card = new Card();
		//Header
		Queue<Racer> nextThreeRacers = new LinkedList<Racer>();
		//Puts the next three racers into the nextThreeQueue
		for (Racer racer : this.queuedRacers) {
			if (nextThreeRacers.size() == 3) {
				break;
			} else {
				nextThreeRacers.add(racer);
			}
		}

		//For loop exits when there are no longer racers to add OR there are 3 racers.
		if (nextThreeRacers.size() == 0) {
			this.card.setHeader("NO RACERS QUEUED");
		} else {
			this.card.setHeader(nextThreeRacers);
		}


		//Body
		//Set body as the list of running racers (first and only running queue).
		if (this.runningLanes.size() > 0) {
			String bodyString = "";

			for (Racer racer : this.runningLanes.get(0)) {
				String elapsedTimeString = "";

				try {
					//Calculate the current elapsed time
					//This puts the current time (that is passed in) relative to the run start time.
					ChronoTime currentElapsedTime = currentTime.elapsedSince(this.startTime);
					ChronoTime elapsedTime = currentElapsedTime.elapsedSince(racer.getStartTime());
					elapsedTimeString = elapsedTime.toString();

				} catch (InvalidTimeException e) {
					elapsedTimeString = "INVALID TIME";
				}

				bodyString += racer.toString() + " " + elapsedTimeString + "\n";
			}

			this.card.setBody(bodyString);
		}

		//Footer
		Racer lastRacer = null;
		if (this.finishedRacers.size() > 0) {
			lastRacer = ((LinkedList<Racer>)this.finishedRacers).getLast();
		}

		if (lastRacer != null) {
			this.card.setFooter(lastRacer.toString() + " " + lastRacer.getElapsedTimeString());

		} else {
			this.card.setFooter("NO RACER FINISHED");
		}
	}

	/**
	 * Sets this.card with the following information:
	 *
	 * Event Type - PARIND:
	 * 	Header:
	 * 	- Next pair to run
	 * 	Body:
	 * 	- NOTHING
	 * 	Footer:
	 * 	- Finish times of the last pair to finish
	 */
	private void setPARINDcard() {
		this.card = new Card();

		//Header
		//Next pair to run (essentially next two racers).
		Queue<Racer> nextPair = new LinkedList<Racer>();

		for (Racer racer : this.queuedRacers) {
			if (nextPair.size() < 2) {
				nextPair.add(racer);
			} else {
				break;
			}
		}

		//For loop exits when there are no longer racers to add OR there are 2 racers.
		if (nextPair.size() > 0) {
			this.card.setHeader(nextPair);
		} else {
			//Then there are no racers paired to run.
			this.card.setHeader("NO RACERS QUEUED");
		}

		//Body
		//Nothing

		//Footer
		//Finish times of the last pair to finish (essentially last two racers).
		LinkedList<Racer> linkedList = (LinkedList<Racer>) this.finishedRacers;
		final int size = linkedList.size();

		if (size > 1) {
			//Then there is at least 2 racers (one pair)
			Racer lastRacer = linkedList.get(size-1);
			Racer secondLast = linkedList.get(size-2);

			this.card.setFooter(lastRacer.toString() + " " + lastRacer.getElapsedTimeString()+ ", " +
					secondLast.toString() + " " + secondLast.getElapsedTimeString());

		} else if (size > 0) {
			//Then only one racer has finished.
			Racer lastRacer = linkedList.get(size-1);

			this.card.setFooter(lastRacer.toString() + " " + lastRacer.getElapsedTimeString());

		} else {
			//Then no one has finished.
			this.card.setFooter("NO PAIR HAS FINISHED");
		}
	}

	/**
	 * Sets this.card with the following information:
	 *
	 * Event Type - GRP:
	 * 	Header:
	 * 	- Running time
	 * 	Body:
	 * 	- NOTHING
	 * 	Footer:
	 * 	- Last finish time
	 */
	private void setGRPcard() {
		this.card = new Card();
		//Header
		//Running Time
		try {
			this.card.setHeader("Race Time: " + this.getElapsedTime().toString());

		} catch (InvalidTimeException e) {
			//We should never reach this point

			this.card.setHeader("INVALID RACE TIME");
		}

		//Body
		//Nothing

		//Footer
		//Last Finish Time
		LinkedList<Racer> linkedList = (LinkedList<Racer>)this.finishedRacers;
		final int size = linkedList.size();

		if (size > 0) {
			//Then there is a valid finish time.
			Racer lastRacer = linkedList.get(size-1);
			this.card.setFooter(lastRacer.toString() + " " + lastRacer.getElapsedTimeString());

		} else {
			//Then no one has finished.
			this.card.setFooter("NO RACER FINISHED");
		}
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
	 * The elapsed time of the Run
	 * @return the elapsed time
	 * @throws InvalidTimeException
	 */
	private ChronoTime getElapsedTime() throws InvalidTimeException {
		if (this.startTime == null) {
			//0 elapsed time.
			return new ChronoTime(0,0,0,0);

		} else if (this.endTime == null) {
			return ChronoTime.now().elapsedSince(this.startTime);

		} else {
			return this.endTime.elapsedSince(this.startTime);
		}
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
		if (!this.canAddLaneFor(this.getEventType())) {
			throw new RaceException("Cannot create another lane for run type " + this.getEventType());
		} else if (this.hasStarted()) {
			throw new RaceException("Cannot create new lane after run started");
		} else if (this.hasEnded()) {
			throw new RaceException("Cannot create new lane after run ended");
		} else if (this.runningLanes.size() >= 8) {
			throw new RaceException("Cannot have more than 8 lanes.");
		}
		
		this.runningLanes.add(new LinkedList<>());
		
		return this.runningLanes.size();
	}

	/**
	 * Determines whether or not you can add another lane for the given event type.
	 * The lane restrictions are different for each event type, and are ([min lanes, max lanes]):
	 * IND: [1, 1]
	 * PARIND: [1, 8]
	 * GRP: [0,0]
	 * @param eventType used to determine if another lane can be added
	 * @return true if another lane can be added, false otherwise.
     */
	private boolean canAddLaneFor(EventType eventType) {
		switch (eventType) {
			case IND:
				return this.runningLanes.size() == 0;

			case PARIND:
				return this.runningLanes.size() >= 0 && this.runningLanes.size() <= 7;

			case GRP:
				return false;

			case PARGRP:
				return this.runningLanes.size() >= 0 && this.runningLanes.size() <= 7;

			default:
				return false;
		}
	}
	
	/**
	 * Returns an ArrayList of all racers.
	 * @return the number of the removed lane.
	 * @throws RaceException when there is not a lane to remove
	 * @throws IllegalStateException when the lists are not the same size (this would be an internal error)
	 */
	public ArrayList<Racer> getAllRacers(){
		ArrayList<Racer> allRacers = new ArrayList<>();
		for (Racer r: queuedRacers)
			allRacers.add(r);
		for (Queue<Racer> q: runningLanes)
			for (Racer r: q)
				allRacers.add(r);
		for (Racer r: finishedRacers)
			allRacers.add(r);
		return allRacers;
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
			//Then the run has not yet started. Start and end time will be the same.
			this.startTime = atTime;
			this.endTime = atTime;

		} else if (!this.startTime.isBefore(atTime)) {
			throw new InvalidTimeException("Run has not started.");

		} else if (this.getEventType() == EventType.IND || this.getEventType() == EventType.PARIND){
			this.endTime = atTime;

			//Move any currently running racers into the finished list,
			//while setting every running racer to DNF
			for (Queue<Racer> queue : this.runningLanes) {
				//Iterate through every running queue
				for (Racer racer : queue) {
					//Add the racer to the corresponding finished queue
					this.finishedRacers.add(racer);
					racer.didNotFinish();
				}

				queue.clear();
			}

		} else {
			//Event type is GRP.
			this.endTime = atTime;
			//We do not move racers from the running queue because none exist.
		}

		this.log.add("Ended run at " + atTime);
	}
	
	/**
	 * Sets the event type to a new event type. The event type can be changed until a racer has been added to the run (queued).
	 * Note: When switching from PARIND to IND all lanes, except the first, are removed.
	 * @param newEventType is a string representation of the eventType
	 * @throws RaceException when attempting to change the event type after a racer is queued, running, or finished,
	 * OR
	 * if newEventType does not correspond to a valid event type
	 */
	public void setEventType(String newEventType) throws RaceException {
		if (this.hasStarted()) {
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

			case "GRP":
				this.eventType = EventType.GRP;

				//Remove any racers queued, and also any lanes.
				this.runningLanes.clear();
				this.queuedRacers.clear();
				this.finishedRacers.clear();

				break;

			case "PARGRP":
				this.eventType = EventType.PARGRP;

				//Remove any racers queued, and also any lanes.
				this.runningLanes.clear();
				this.queuedRacers.clear();
				this.finishedRacers.clear();

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
	private boolean doesRacerExist(int racerNumber) {
		//Only valid if there is not a racer with that number.
		boolean isValid = true;
				
		for (Racer racer : this.queuedRacers) {
			if (racer.getNumber() == racerNumber) {
				isValid = false;
				break;
			}
		}

		if (isValid) {
			for (Queue<Racer> runningLane : this.runningLanes) {
				for (Racer racer : runningLane) {
					if (racer.getNumber() == racerNumber) {
						isValid = false;
						break;
					}
				}
			}
		}

		if (isValid) {
			for (Racer racer : this.finishedRacers) {
				if (racer.getNumber() == racerNumber) {
					isValid = false;
					break;
				}
			}
		}
		
		return isValid;
	}
	
	/**
	 * Queues a racer, identified with racerNumber, to the queue of racers yet to begin the run.
	 * @param racerNumber is used to identify the racer, no other racer may have this number, 
	 * number must be 1 to 4 digits [1,9999]
	 * @throws RaceException when a racer exists with racerNumber
	 * OR when the racerNumber does not fit within the bounds [1,9999]
	 * OR when the run was ended
	 * OR when the event type is GRP
	 */
	public void queueRacer(int racerNumber) throws RaceException {
		if (racerNumber < 1 || racerNumber > 9999) {
			throw new RaceException("Number must be 1 to 9999");

		} else if (!this.doesRacerExist(racerNumber)) {
			throw new RaceException("Racer already exists with number: " + racerNumber);

		} else if (this.hasEnded()) {
			throw new RaceException("Run has already ended");

		} else if (this.eventType == EventType.GRP) {
			if (this.hasStarted()) {
				//Then we should attempt to mark the next racer.
				this.markNextRacer(racerNumber);
			} else {
				throw new RaceException("Cannot queue a racer for event type GRP.");
			}

		} else if (this.eventType == EventType.PARGRP) {
			if (this.queuedRacers.size() < 8) {
				//Then there is at least one spot for the racer.
				Racer racer = new Racer(racerNumber);
				this.queuedRacers.add(racer);

				//Can throw error, should never tho.
				this.newLane();

				//Log that the racer was added.
				this.log.add("Added " + racer.toString() + " to lane " + this.queuedRacers.size());
			}

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
	 * OR when eventType is GRP
	 */
	public void removeRacer(int racerNumber, int lane) throws RaceException {
		if (racerNumber < MIN_BIB_NUMBER || racerNumber > MAX_BIB_NUMBER) {
			throw new RaceException("Number must be within bounds [1,9999]");

		} else if (this.eventType == EventType.GRP) {
			throw new RaceException("Cannot remove a racer from GRP run.");

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
				this.queuedRacers.remove(racer);

				if (this.eventType == EventType.PARGRP) {
					//Remove a lane as well
					this.removeLane();
				}

				this.log.add("Removed "+racer);
			}
		}
	}
	
	/**
	 * For IND and PARIND event types this method starts the next racer in the queue.
	 * For GRP event type this will start the run.
	 * @param atTime For IND and PARIND this is the absolute time the racer began.
	 *               For GRP this is the start time of the run and every racer that finishes the run
	 * @param lane For IND and PARIND this is the lane corresponding to the racer's lane.
	 *             For GRP this is ignored.
	 * @throws InvalidTimeException when atTime is null
	 * OR atTime is before the run's start time
	 * @throws RaceException when run has already ended
	 * OR run type is IND or PARIND and there is not another racer to start
	 * OR run type is GRP and the run has already started
	 */
	public void startNextRacer(ChronoTime atTime, int lane) throws InvalidTimeException, RaceException {
		ChronoTime tempStartTime;

		if (!this.hasStarted()) {
			//Race has not started,
			//set the start time to be the same as the racers.
			tempStartTime = atTime;
		} else {
			tempStartTime = this.startTime;
		}

		if (atTime == null) {
			throw new InvalidTimeException("Invalid time to start: NULL");

		} else if (atTime.isBefore(tempStartTime)) {
			throw new InvalidTimeException("Time is before the run start time");

		} else if (this.hasEnded()) {
			throw new RaceException("Race has already ended");

		} else if ((this.eventType == EventType.IND || this.eventType == EventType.PARIND)
				&& this.isValidLane(lane)) {
			//EventType is IND OR PARIND, lane is valid

			if (this.eventType == EventType.IND && this.runningLanes.size() == 0) {
				this.newLane();
			}

			Racer nextRacer = queuedRacers.poll();

			if (nextRacer == null) {
				throw new RaceException("No racer to start");

			} else {
				this.startTime = tempStartTime;
				queuedRacers.remove(nextRacer);
				this.runningLanes.get(lane - 1).add(nextRacer);

				ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);
				nextRacer.start(elapsedTime);

				String racerLogString = "" + atTime +" "+nextRacer+" started";

				//Log which lane the racer started in for PARIND only
				if (this.eventType == EventType.PARIND) {
					racerLogString += " in lane " + lane;
				}

				this.log.add(racerLogString);
			}

		}  else if (this.eventType == EventType.GRP) {
			if (this.hasStarted()) {
				throw new RaceException("Run has already started");
			} else {
				//Then we set the start time of the run to be atTime.
				this.startTime = atTime;
				this.log.add("Started group run at " + atTime);
			}
		} else if (this.eventType == EventType.PARGRP) {
			if (lane < 1 || lane > 8) {
				throw new RaceException("Invalid lane number: " + lane);

			} else if (this.hasStarted()) {
				throw new RaceException("Run has already started");

			} else {
				int runIndex = 0;

				for (Racer racer : this.queuedRacers) {
					//Ensure that we are not getting a running lane that does not exist.
					if (runIndex < this.runningLanes.size()) {
						this.runningLanes.get(runIndex).add(racer);
						ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);
						racer.start(elapsedTime);
					} else {
						throw new RaceException("INTERNAL INCONSISTENCY: Not enough run lanes.");
					}
				}

				//At this point: all runners have been added to the running queue.
				//Remove all racers from the queue.
				this.queuedRacers.clear();

				this.startTime = atTime;
				this.log.add("Started run at " + atTime);
			}
		}
	}
	
	/**
	 * For IND and PARIND this finishes the next racer that is currently racing in the given lane.
	 * For GRP this marks the finish of another racer.
	 * @param atTime this is the absolute time the racer finished.
	 * @param lane for IND and PARIND this is the lane the racer belongs to. This is ignored for GRP.
	 * @throws InvalidTimeException when atTime is null
	 * OR when atTime is before the run's start time
	 * @throws RaceException when the run has not started
	 * OR when the run has already ended
	 * OR there is not another racer to finish for IND and PARIND event types
	 * OR the maximum number of runners have finished (9999) for GRP event type.
	 */
	public void finishNextRacer(ChronoTime atTime, int lane) throws InvalidTimeException, RaceException {

		if (!this.hasStarted()) {
			throw new RaceException("Race has not started");

		} else if (atTime == null) {
			throw new InvalidTimeException("NULL is Not a valid time");

		} else if (atTime.isBefore(this.startTime)) {
			throw new InvalidTimeException("Time cannot be before the run start time");

		} else if (this.hasEnded()) {
			throw new RaceException("Race has ended");

		} else if ((this.eventType == EventType.IND || this.eventType == EventType.PARIND)
				&& this.isValidLane(lane)) {
			//Event type is IND or PARIND and lane is valid

			Queue<Racer> runningQueue = this.runningLanes.get(lane - 1);
			Racer nextRacer = runningQueue.poll();

			if (nextRacer == null) {
				throw new RaceException("No racer to finish");

			} else {
				runningQueue.remove(nextRacer);
				this.finishedRacers.add(nextRacer);

				ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);
				nextRacer.finish(elapsedTime);

				String racerLogString = "" + atTime +" "+nextRacer+" finished with time "+nextRacer.getElapsedTime().getTimeStamp();

				//Log which lane the racer ended in for PARIND only
				if (this.eventType == EventType.PARIND) {
					racerLogString += " in lane " + lane;
				}
				this.log.add(racerLogString);
			}
		} else if (this.eventType == EventType.GRP) {
			if (this.finishedRacers.size() == MAX_RACERS) {
				throw new RaceException("Maximum number of racers have already finished.");
			}

			final int currentSize = this.finishedRacers.size();
			//Negative number denotes that it is a placeholder racer.
			int racerNumber = (currentSize+1) * (-1);
			//Create racer with negative bib number because they are place holder.
			Racer newRacer = new Racer(racerNumber);

			ChronoTime emptyTime = new ChronoTime(0,0,0,0);
            ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);

			newRacer.start(emptyTime);
			newRacer.finish(elapsedTime);

			this.finishedRacers.add(newRacer);

		}  else if (this.eventType == EventType.PARGRP) {

			//Test if lane is valid [1,8]
			if (lane < 1 || lane > 8) {
				throw new RaceException("Invalid lane number: " + lane);

			} else if (!this.hasStarted()) {
				//Run has NOT started, cannot finish the lane.
				throw new RaceException("Lane (" + lane + ") cannot finish before the run starts");

			} else {

				if (lane <= this.runningLanes.size()) {
					//Then there are enough lanes to grab it.
					LinkedList<Racer> runningLane = (LinkedList<Racer>)this.runningLanes.get(lane-1);

					Racer racer = runningLane.pollFirst();

					if (racer != null) {
						ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);
						racer.finish(elapsedTime);
					}
				}
			}

		}
	}

	/**
	 * Marks the next racer in the finish queue with a bibnumber (versus the default number.
	 * @param racerNumber corresponding to the next racer to be marked.
	 * @throws RaceException when there is not another racer to mark with a bib number
	 * OR eventType is not GRP
     */
	public void markNextRacer(int racerNumber) throws RaceException {
		if (this.eventType == EventType.IND ||
				this.eventType == EventType.PARIND ||
				this.eventType == EventType.PARGRP) {
			throw new RaceException("Cannot mark next racer with event " + this.eventType);

		} else if (this.nextRacerToMarkIndex >= this.finishedRacers.size()) {
			throw new RaceException("No racer to mark");
		}

		Racer dummyRacer = ((LinkedList<Racer>)this.finishedRacers).get(this.nextRacerToMarkIndex);
		Racer newRacer = new Racer(racerNumber);

		newRacer.start(dummyRacer.getStartTime());
		try {
			newRacer.finish(dummyRacer.getEndTime());
		} catch (InvalidTimeException e) {
			throw new RaceException("Racer to be marked had invalid finish time");
		}

		//Increment the bib marker
		//Bib marker index is used mark the next dummy racer
		this.nextRacerToMarkIndex++;
	}

	/**
	 * Cancels the next racer to finish putting that racer at the end of the queued racers.
	 * @throws RaceException when there is not a racer to cancel
	 * OR when the race has already ended
	 * OR when eventType is GRP
	 */
	public void cancelNextRacer(int lane) throws RaceException {
		
		if (this.hasEnded()) {
			throw new RaceException("Race has ended");
			
		} else if (!this.isValidLane(lane)) {
			throw new RaceException("Invalid lane: " + lane);

		} else if (this.eventType == EventType.GRP) {
			throw new RaceException("Cannot cancel next racer for GRP run");

		} else if (this.runningLanes.isEmpty() || this.runningLanes.get(lane-1)==null) {
			throw new RaceException("No racer to cancel");

		}else {
			LinkedList<Racer> runningQueue = (LinkedList<Racer>) this.runningLanes.get(lane - 1);
			Racer lastRacer = runningQueue.poll();
			
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
	 * OR when eventType is GRP
	 */
	public void didNotFinishNextRacer(int lane) throws RaceException {
		 if (this.hasEnded()) {
			throw new RaceException("Race has ended");
			
		} else if (!this.isValidLane(lane)) {
			throw new RaceException("Invalid lane: " + lane);

		} else if (this.eventType == EventType.GRP) {
			throw new RaceException("Cannot DNF next racer for GRP type");

		} else if (this.runningLanes.isEmpty() || lane <= this.runningLanes.size()) {
			throw new RaceException("No racer to DNF");

		}  else if (this.eventType == EventType.PARGRP) {

		 	if (lane < 1 || lane > 8) {
		 		throw new RaceException("Lane is not within bounds [1,8]");

			} else {
		 		final int adjustedLane = lane-1;

		 		if (adjustedLane < this.runningLanes.size()) {
		 			//Then the lane exists, attempt to pull the first racer (as there should only be one per lane).
					LinkedList<Racer> runningLane = (LinkedList<Racer>)this.runningLanes.get(adjustedLane);

					if (!runningLane.isEmpty()) {
						//Then there is a racer.
						Racer racer = runningLane.get(0);
						racer.didNotFinish();
					}
				}
			}

		 } else {
			LinkedList<Racer> runningQueue = (LinkedList<Racer>) this.runningLanes.get(lane - 1);
			Racer lastRacer = runningQueue.poll();
			
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

	/**
	 * Swaps the next two racers to finish.
	 */
	public void swap() throws RaceException {
		//TODO:
	}
	
	public enum EventType {
		IND,
		PARIND,
		GRP,
		PARGRP
	}
}
