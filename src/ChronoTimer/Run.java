package ChronoTimer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import Exceptions.*;

/*
Questions:
	1) How will I give information to be displayed by the ChronoTrigger?
	2) When printing Racers, how do I distinguish between a dummy racer and an actual racer?

Notes:
	- Should the racer compute the "elapsed time" from two absolute times?
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
	private Card card = new Card(3,10);

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
		return (laneNumber >= 1 && laneNumber <= MAX_LANES) && (laneNumber <= this.runningLanes.size());
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
	 * @return null if the run has not yet started, a valid card otherwise.
     */
	public Card getCard()
	{
		final int header;
		final int footer;

		//Format card based on event type.
		switch (this.eventType) {
			case IND:
				header = 3;
				footer = 1;

				this.card = new Card(header, footer);

				//Header
				Queue<Racer> nextThreeRacers = new LinkedList<Racer>();

				for (Racer racer : this.queuedRacers) {
					if (nextThreeRacers.size() == 3) {
						break;
					} else {
						nextThreeRacers.add(racer);
					}
				}
				//For loop exits when there are no longer racers to add OR there are 3 racers.
				this.card.setHeader(nextThreeRacers);

				//Body
				//Set body as the list of running racers (first and only running queue).
				this.card.setBody(this.runningLanes.get(0));

				//Footer
				Racer lastRacer = ((LinkedList<Racer>)this.finishedRacers).getLast();

				if (lastRacer != null) {
					this.card.setFooter(lastRacer.toString());
				}

				break;

			case PARIND:
				header = 2;
				footer = 2;

				this.card = new Card(header, footer);

				//Header
				//Next pair to run (essentially next two racers).
				Queue<Racer> nextPair = new LinkedList<Racer>();

				for (Racer racer : this.queuedRacers) {
					if (nextPair.size() == 2) {
						break;
					} else {
						nextPair.add(racer);
					}
				}
				//For loop exits when there are no longer racers to add OR there are 2 racers.
				this.card.setHeader(nextPair);

				//Body
				//Nothing

				//Footer
				//Finish times of the last pair to finish (essentially last two racers).
				LinkedList<Racer> linkedList = (LinkedList<Racer>)this.finishedRacers;

				lastRacer = linkedList.getLast();
				int secondLastIndex = linkedList.size()-2;
				if (lastRacer != null && secondLastIndex >= 0) {
					//Then there is a valid second last racer.
					Racer secondLast = linkedList.get(secondLastIndex);

					try {
						this.card.setFooter("" + lastRacer.getElapsedTime() + ", " + secondLast.getElapsedTime());

					} catch (InvalidTimeException e) { /*Should not reach this */ }
				} else if (lastRacer != null) {
					//There is only a valid last racer.
					try {
						this.card.setFooter("" + lastRacer.getElapsedTime());

					} catch (InvalidTimeException e) { /*Should not reach this */ }

				} else {
					//There are not any racers that have finished.
				}

				break;

			case GRP:
				header = 1;
				footer = 1;

				this.card = new Card(header, footer);

				//Header
				//Running Time
				try {
					this.card.setHeader(this.getElapsedTime().toString());

				} catch (InvalidTimeException e) { /*Should not reach this point. */ }

				//Body
				//Nothing

				//Footer
				//Last Finish Time
				lastRacer = ((LinkedList<Racer>)this.finishedRacers).getLast();

				if (lastRacer != null) {
					try {
						this.card.setFooter(lastRacer.getElapsedTime().toString());

					} catch (InvalidTimeException e) { /*Should not reach this point. */ }
				}

				break;
		}

		return this.card;
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
		if (this.canAddLaneFor(this.getEventType())) {
			throw new RaceException("Cannot create another lane for run type " + this.getEventType());
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
				return this.runningLanes.size() >= 1;

			case PARIND:
				return this.runningLanes.size() >= 1 && this.runningLanes.size() <= 8;

			case GRP:
				return false;

			default:
				return false;
		}
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
	 * @throws RaceException when a racer exists with racerNumber
	 * OR when the racerNumber does not fit within the bounds [1,9999]
	 * OR when the run was ended
	 * OR when the event type is GRP
	 */
	public void queueRacer(int racerNumber) throws RaceException {		
		if (racerNumber < 1 || racerNumber > 9999) {
			throw new RaceException("Number must be 1 to 9999");

		} else if (this.eventType == EventType.GRP) {
			throw new RaceException("Cannot queue racer during GRP event");

		} else if (!this.canQueueRacer(racerNumber)) {
			throw new RaceException("Racer already exists with number: " + racerNumber);
			
		} else if (this.hasEnded()) { 
			throw new RaceException("Run has already ended");
			
		} else if (this.hasStarted() && this.eventType == EventType.GRP) {
			//Then we should mark the next racer.
			this.markNextRacer(racerNumber);

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
				queuedRacers.remove(racer);
				
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

				String racerLogString = "" + atTime +" "+nextRacer+" finished";

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

			newRacer.start(this.startTime);
			newRacer.finish(atTime);

			this.finishedRacers.add(newRacer);
		}
	}

	/**
	 * Marks the next racer in the finish queue with a bibnumber (versus the default number.
	 * @param racerNumber corresponding to the next racer to be marked.
	 * @throws RaceException when there is not another racer to mark with a bib number
	 * OR eventType is not GRP
     */
	public void markNextRacer(int racerNumber) throws RaceException {
		if (this.eventType == EventType.IND || this.eventType == EventType.PARIND) {
			throw new RaceException("Cannot mark next racer with event " + this.eventType);

		} else if (this.nextRacerToMarkIndex >= this.finishedRacers.size()) {
			throw new RaceException("No racer to mark");
		}

		Racer dummyRacer = ((LinkedList<Racer>)this.finishedRacers).get(this.nextRacerToMarkIndex);
		Racer newRacer = new Racer(dummyRacer.getNumber());

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
		
		if (!this.isValidLane(lane)) {
			throw new RaceException("Invalid lane: " + lane);
			
		} else if (this.hasEnded()) {
			throw new RaceException("Race has ended");
			
		} else if (this.eventType == EventType.GRP) {
			throw new RaceException("Cannot cancel next racer for GRP run");

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
	 * OR when eventType is GRP
	 */
	public void didNotFinishNextRacer(int lane) throws RaceException {
		if (!this.isValidLane(lane)) {
			throw new RaceException("Invalid lane: " + lane);
			
		} else if (this.hasEnded()) {
			throw new RaceException("Race has ended");
			
		} else if (this.eventType == EventType.GRP) {
			throw new RaceException("Cannot DNF next racer for GRP type");

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
		PARIND,
		GRP
	}
}
