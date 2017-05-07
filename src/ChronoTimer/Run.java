package ChronoTimer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ChronoTimer.Runs.*;
import Exceptions.*;

import javax.swing.filechooser.FileSystemView;

/**
 * PARGRP:
 * - DNF, DNF any racer that has not finished.
 * - Cancel, Cancel all racers.
 */

public class Run {
	private ChronoTime startTime;
	private ChronoTime endTime;
	
	private EventType eventType;

	private RunManager runManager;

	private final int MIN_BIB_NUMBER = 1;
	private final int MAX_BIB_NUMBER = 9999;

	//private Timer timer;

	public Run(EventType eventType) {
		this.startTime = null;
		this.endTime = null;

		this.eventType = eventType;

		//We must set the runManager so when we set the event type we can access a valid log.
		this.runManager = new INDRunManager(new Log());

//		this.timer = new Timer();
//		this.timer.schedule(new WriteState(this.runManager), 0, 500);

		//This will ensure that runManager correctly corresponds to the event type and not arbitrarily set.
		try {
			this.setEventType(eventType.toString());
		} catch (RaceException e) { /* should never reach this, eventType.toString() will be recognized */ }

	}

	public class WriteState extends TimerTask {
		private RunManager runManager;

		public WriteState(RunManager runManager) {
			super();
			this.runManager = runManager;
		}

		@Override
		public void run() {
			final String filePath = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + File.separator + "runState.txt";

			try {
				FileWriter fileWriter = new FileWriter(filePath, false);

				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

				bufferedWriter.write(this.runManager.toString());

				bufferedWriter.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Run() {
		this(EventType.IND);
	}
	
	/**
	 * Get the log the run maintains to keep track of all changes to the run.
	 * @return the log
	 */
	public Log getLog() {
		return this.runManager.getLog();
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

		if (this.hasStarted()) {
			try {
				ChronoTime elapsedTime = currentTime.elapsedSince(this.startTime);

				return this.runManager.getCard(elapsedTime);

			} catch (InvalidTimeException e) {
				return new Card();
			}
		} else {
			return this.runManager.getCard(null);
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
	 * Returns an ArrayList of all racers.
	 * @return the number of the removed lane.
	 * @throws RaceException when there is not a lane to remove
	 * @throws IllegalStateException when the lists are not the same size (this would be an internal error)
	 */
	public ArrayList<Racer> getAllRacers(){
		return  this.runManager.getAllRacers();
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

			this.getLog().add("Started run at " + atTime);

		} else if (!this.startTime.isBefore(atTime)) {
			throw new InvalidTimeException("Run has not started.");

		} else {
			this.runManager.endRun();
			this.endTime = atTime;
		}

		this.getLog().add("Ended run at " + atTime);
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

		Log log = this.runManager.getLog();
		switch (newEventType) {
			case "IND":
				this.eventType = EventType.IND;
				this.runManager = new INDRunManager(log);
				break;
			
			case "PARIND":
				this.eventType = EventType.PARIND;
				this.runManager = new PARINDRunManager(log);
				break;

			case "GRP":
				this.eventType = EventType.GRP;
				this.runManager = new GRPRunManager(log);
				break;

			case "PARGRP":
				this.eventType = EventType.PARGRP;
				this.runManager = new PARGRPRunManager(log);
				break;
			
		default:
			throw new RaceException("Invalid event type: " + newEventType);
		}

//		this.timer.cancel();
//		this.timer.purge();
//
//		this.timer = new Timer();
//
//		// And From your main() method or any other method
//		this.timer.schedule(new WriteState(this.runManager), 0, 500);

		//Add to the log.
		log.add("Event type is " + newEventType);
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
		if (racerNumber < MIN_BIB_NUMBER || racerNumber > MAX_BIB_NUMBER) {
			throw new RaceException("Number must be " + MIN_BIB_NUMBER + " to " + MAX_BIB_NUMBER);

		} else if (this.hasEnded()) {
			throw new RaceException("Run has already ended");

		} else if (this.eventType == EventType.GRP && this.hasStarted()) {
			//Then we should attempt to mark the next racer.
			((GRPRunManager)this.runManager).markNextRacer(racerNumber);

		} else {
			this.runManager.queueRacer(racerNumber);
		}
	}

	/**
	 * Removes a racer, identified with racerNumber, from the queue of racers yet to begin the run.
	 * @param racerNumber is used to identify the racer, racer must exist, number must be in bounds [1,9999]
	 * @throws RaceException when a queued racer does not exist with racerNumber
	 * or when the racerNumber is not within bounds [1,9999]
	 * OR when the lane is invalid
	 * OR when eventType is GRP
	 */
	public void removeRacer(int racerNumber) throws RaceException {
		if (racerNumber < MIN_BIB_NUMBER || racerNumber > MAX_BIB_NUMBER) {
			throw new RaceException("Number must be " + MIN_BIB_NUMBER + " to " + MAX_BIB_NUMBER);
		} else {
			this.runManager.deQueueRacer(racerNumber);
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

		} else {

			ChronoTime elapsedTime = atTime.elapsedSince(tempStartTime);

			this.runManager.startNext(elapsedTime, lane);

			this.startTime = tempStartTime;
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

		} else {
			ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);
			this.runManager.finishNext(elapsedTime, lane);
		}
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
			
		} else {
			this.runManager.cancelNextRacer(lane);
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
		} else {
		 	this.runManager.didNotFinishNextRacer(lane);
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