package ChronoTimer;

import Exceptions.InvalidTimeException;

public class Racer {
	/**
	 * The time the Racer starts. 
	 * If the Racer never started, startTime is null.
	 */
	private ChronoTime startTime;
	/**
	 * The time the Racer finished.
	 * If the Racer never finished, endTime is null.
	 */
	private ChronoTime endTime;
	
	/**
	 * The Racer's number (i.e., bib number).
	 */
	private int number;
	
	/**
	 * The current status of the Racer.
	 */
	private Status status;
	
	public boolean equals(Object other) {
		if (other instanceof Racer) {
			return this.number == ((Racer)other).number;
		}
		return false;
	}
	
	/**
	 * Creates a Racer with a given number.
	 * Number must be 1 or greater.
	 * @throws IllegalArgumentException when a number less than or equal to 0 is supplied.
	 * @param number the identification number corresponding to the racer.
	 */
	public Racer(int number) throws IllegalArgumentException {
		if (number < 1) {
			throw new IllegalArgumentException("Racer cannot have a number less than 1");
		} else if (number > 9999) {
			throw new IllegalArgumentException("Racer cannot have a number greater than 4 digits");
		} else {
			this.number = number;
			this.status = Status.QUEUED;
		}
	}
	
	/**
	 * Sets the Racer's status to RACING and sets the Racer's startTime to startTime
	 * @param startTime the time corresponding to when the Racer started a Race
	 * @precondition The Racer has status QUEUED
	 * @postcondition The Racer has status RACING
	 * @throws IllegalStateException when the status is not QUEUED. This can occur if you attempt to start a racer that already began. 
	 */
	public void start(ChronoTime startTime) throws IllegalStateException {
		if (this.status == Status.QUEUED) {
			this.status = Status.RACING;
			this.startTime = startTime;
		} else {
			throw new IllegalStateException("Cannot start because racer does not have a QUEUED status. Current status is " + this.status);
		}
	}
	
	/**
	 * Sets the Racer's status to FINISHED and sets the Racer's endTime to endTime.
	 * @param finishTime the time corresponding to when the Racer finished a Race
	 * @precondition The Racer has status RACING
	 * @postcondition The Racer has status FINISHED
	 * @throws InvalidTimeException when endTime is before startTime
	 */
	public void finish(ChronoTime endTime) throws InvalidTimeException, IllegalStateException {
		if (this.status != Status.RACING) {
			throw new InvalidTimeException("Cannot finish because the racer is not racing! Current status is " + this.status);
		} else if (endTime.isBefore(this.startTime) || endTime.equals(this.startTime)) {
			throw new InvalidTimeException("Start time is before end time which is an invalid state.");
		} else {
			this.status = Status.FINISHED;
			this.endTime = endTime;
		}
	}
	
	/**
	 * Sets the Racer's status to DNF and sets the Racer's endTime to null.
	 * The Racer's startTime is not altered.
	 * @precondition The Racer has status RACING
	 * @postcondition The Racer has status DNF
	 * @throws IllegalStateException when the status of the racer is not RACING.
	 */
	public void didNotFinish() throws IllegalStateException {
		if (this.status == Status.RACING) {
			this.status = Status.DNF;
			this.endTime = null;
		} else {
			throw new IllegalStateException("Cannot set racer as a DNF because racer does not have a status of RACING. Current status is " + this.status);
		}
	}
	
	
	/**
	 * Sets the Racer's status to QUEUED and sets the Raer's startTime and endTime to null.
	 * @precondition The Racer has status RACING
	 * @postcondition The Racer has status QUEUED
	 * @throws IllegalStateException when attempting to cancel a racer without status RACING
	 */
	public void cancel() throws IllegalStateException {
		if (this.status == Status.RACING) {
			this.status = Status.QUEUED;
			this.startTime = null;
			this.endTime = null;
		} else {
			throw new IllegalStateException("Cannot cancel racer because their status is not RACING. Current status is " + this.status);
		}
	}
	
	/**
	 * Returns the Racer's start time.
	 * @return the startTime of the Racer, null if there is not a start time (racer has not started)
	 */
	public ChronoTime getStartTime() {
		return this.startTime;
	}
	
	/**
	 * Returns the Racer's end time.
	 * @return the endTime of the Racer, null if there is not an end time (i.e., racer still racing or racer DNF, etc.)
	 */
	public ChronoTime getEndTime() {
		return this.endTime;
	}
	
	/**
	 * Returns the Racer's number.
	 * @return the number of the Racer.
	 */
	public int getNumber() {
		return this.number;
	}
	
	/**
	 * Returns the Racer's status
	 * @return the current status of the Racer
	 */
	public Racer.Status getStatus() {
		return this.status;
	}
	
	public enum Status {
		//When the Racer is created, but has not yet started
		QUEUED,
		//When the Racer has started, but has not yet finished.
		RACING,
		//When the Racer has finished successfully.
		FINISHED,
		//When the Racer did not finish
		DNF
	}
}
