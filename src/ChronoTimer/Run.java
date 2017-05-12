package ChronoTimer;

import java.util.ArrayList;

import ChronoTimer.Runs.*;
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

	private RunManager runManager;

	private final int MIN_BIB_NUMBER = 1;
	private final int MAX_BIB_NUMBER = 9999;


	public Run(EventType eventType) {
		this.startTime = null;
		this.endTime = null;

		this.eventType = eventType;

		//We must set the runManager so when we set the event type we can access a valid log.
		this.runManager = new INDRunManager(new Log());

		//This will ensure that runManager correctly corresponds to the event type and not arbitrarily set.
		try {
			this.setEventType(eventType.toString());
		} catch (RaceException e) { /* should never reach this, eventType.toString() will be recognized */ }
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
	 * 	- A list of all running racers.
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
	 * Returns ana aggregated list of racers within the run. This includes queued racers, running racers, and finished racers.
	 * Racers are not sorted in any particular order.
	 * @return all of the racers within the run.
	 */
	public ArrayList<Racer> getAllRacers(){
		return  this.runManager.getAllRacers();
	}


	/**
	 * Ends the run, this action cannot be undone. This will prevent any further manipulation of racers.
	 * Any currently running racers will be set to DNF.
	 * <i>Note: if the run has not started this will set the start time to be equal to atTime and then end the run.</i>
	 * @param  atTime is the time the run ended
	 * @thows RaceException when the run has already ended or if atTime is before the run start time.
	 */
	public void endRun(ChronoTime atTime) throws RaceException {
		if (this.hasEnded()) {
			throw new RaceException("Run already ended.");

		} else if (!this.hasStarted()) {
			//Then the run has not yet started. Start and end time will be the same.
			this.startTime = atTime;
			this.endTime = atTime;

			this.runManager.endRun();

		} else if (!this.startTime.isBefore(atTime)) {
			throw new RaceException("End time cannot be before start time");

		} else {
			this.runManager.endRun();
			this.endTime = atTime;
		}

		this.getLog().add("Ended run at " + atTime);
	}


	/**
	 * Sets the event type to the new event type associated with newTypeString.
	 * <i>Note: newTypeString is tested case-sensitive</i>
	 * @param newTypeString is a string representation of the event type.
	 * @throws RaceException if the run has already started or if the run has already ended
	 * or if newTypeString is not a valid event type string
	 */
	public void setEventType(String newTypeString) throws RaceException {
		if (this.hasStarted()) {
			throw new RaceException("Cannot change event type once run started");

		} else if (this.hasEnded()) {
			throw new RaceException("Cannot change event type once run ended");
		}

		Log log = this.runManager.getLog();
		switch (newTypeString) {
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
			throw new RaceException("Invalid event type: " + newTypeString);
		}

		//Add to the log.
		log.add("Event type is " + newTypeString);
	}


	/**
	 * Attempts to queue a racer to start with racerNumber as the bib number.
	 * @param racerNumber associated with the racer's bib number
	 * @throws RaceException if racerNumber is not within bounds [1,9999] or if the run has already ended
	 * or under the following conditions:
	 * <ul>
	 *     <li>IND: if a racer already has racerNumber within the run</li>
	 *     <li>PARIND: if a racer already has racerNumber within the run</li>
	 *	   <li>GRP: always </li>
	 *     <li>PARGRP: if a racer already has racerNumber within the run or if there are already 8 racers</li>
	 * </ul>
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
	 * //TODO:
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

	/**
	 * Starts the next racer(s) as described for each event type:
	 * <ul>
	 *     <li>IND: starts the next racer in the queue</li>
	 *     <li>PARIND: starts the next racer in the given lane</li>
	 *     <li>GRP: starts the run</li>
	 *     <li>PARGRP: starts the run provided lane is 1</li>
	 * </ul>
	 * @param atTime corresponds to the start time
	 * @param lane corresponds to the lane to start the next racer(s) from
	 * @throws RaceException when atTime is null or is before the run start time (where applicable)
	 * or if the run has already ended or where conditions apply for each event type:
	 * <ul>
	 *     <li>IND: there is not a racer to start</li>
	 *     <li>PARIND: there is not a racer to start or lane is not 1 or 2</li>
	 *     <li>GRP: does not throw an error</li>
	 *     <li>PARGRP: if lane is not in bounds [1,8]</li>
	 * </ul>
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
	 * Finishes the next racer(s) as described for each event type:
	 * <ul>
	 *     <li>IND: finishes the next racer in the queue</li>
	 *     <li>PARIND: finishes the next racer in the given lane</li>
	 *     <li>GRP: adds a racer to the finished queue as a placeholder</li>
	 *     <li>PARGRP: finishes the racer in the given lane</li>
	 * </ul>
	 * @param atTime corresponds to the finish time
	 * @param lane corresponds to the lane to finish the next racer(s) from
	 * @throws RaceException if the run has not started or atTime is null or the run has ended or
	 * where conditions apply for each event type:
	 * <ul>
	 *     <li>IND: there is not a racer to start</li>
	 *     <li>PARIND: there is not a racer to start or lane is not 1 or 2</li>
	 *     <li>GRP: does not throw an error</li>
	 *     <li>PARGRP: if lane is not in bounds [1,8]</li>
	 * </ul>
	 */
	public void finishNextRacer(ChronoTime atTime, int lane) throws InvalidTimeException, RaceException {
		if (!this.hasStarted()) {
			throw new RaceException("Race has not started");

		} else if (atTime == null) {
			throw new InvalidTimeException("NULL is Not a valid time");

		} else if (this.hasEnded()) {
			throw new RaceException("Race has ended");

		} else {
			ChronoTime elapsedTime = atTime.elapsedSince(this.startTime);
			this.runManager.finishNext(elapsedTime, lane);
		}
	}


	/**
	 * Cancels racer(s) as described for each event type:
	 * <ul>
	 *     <li>IND: cancels last racer to start</li>
	 *     <li>PARIND: cancels the last racer to start - independent of the lane</li>
	 *     <li>GRP: cancel is not supported for GRP</li>
	 *     <li>PARGRP: cancels all running racers and restarts any finished racers</li>
	 * </ul>
	 * @param lane corresponds to the lane to cancel the racer from
	 * @throws RaceException if the run has ended or
	 * where conditions apply for each event type:
	 * <ul>
	 *     <li>IND: if there is not a racer to cancel</li>
	 *     <li>PARIND: if there is not a racer to cancel</li>
	 *     <li>GRP: exception is not thrown</li>
	 *     <li>PARGRP: exception is not thrown</li>
	 * </ul>
	 */
	public void cancelNextRacer(int lane) throws RaceException {
		if (this.hasEnded()) {
			throw new RaceException("Race has ended");
			
		} else if (this.eventType == EventType.PARGRP){
			this.runManager.cancelNextRacer(lane);
			//If we make it to this point then an error has not been thrown.
			this.startTime = null;

		} else {
			this.runManager.cancelNextRacer(lane);
		}
	}


	/**
	 * Sets the next racer to finish as a Did Not Finish as described for each event type:
	 * <ul>
	 *     <li>IND: DNFs next racer to finish</li>
	 *     <li>PARIND: DNFs the presumed next racer to finish</li>
	 *     <li>GRP: cancel is not supported for GRP</li>
	 *     <li>PARGRP: sets all racers currently running as DNF</li>
	 * </ul>
	 * @param lane corresponds to the lane to DNF the racer from
	 * @throws RaceException if the run has ended or
	 * where conditions apply for each event type:
	 * <ul>
	 *     <li>IND: if there is not a racer to DNF</li>
	 *     <li>PARIND: if there is not a racer to DNF</li>
	 *     <li>GRP: exception is always thrown - not supported for GRP</li>
	 *     <li>PARGRP: exception is not thrown</li>
	 * </ul>
	 */
	public void didNotFinishNextRacer(int lane) throws RaceException {
		 if (this.hasEnded()) {
			throw new RaceException("Race has ended");
		} else {
		 	this.runManager.didNotFinishNextRacer(lane);
		}
	}
	
	
	public ChronoTime getStartTime()
	{return startTime;}


	/**
	 * Swaps the next two racers to start for IND event type only.
	 * @throws RaceException if the run has not started or if run has ended
	 * or if there are not two racers to swap
	 */
	public void swap() throws RaceException {
		if (!this.hasStarted()) {
			throw new RaceException("Run has not yet started");

		} else if (this.hasEnded()) {
			throw new RaceException("Run has already ended");
		}

		//Race has started but has not ended.
		switch (this.eventType) {
			case IND:
				((INDRunManager)this.runManager).swap();
				break;

			default:
				throw new RaceException("Cannot swap in " + this.eventType.toString() + " event");
		}
	}
	
	public enum EventType {
		IND,
		PARIND,
		GRP,
		PARGRP
	}
}