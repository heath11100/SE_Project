package ChronoTimer;

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
	
	/**
	 * Creates a Racer with a given number;
	 * @param number the identification number corresponding to the racer.
	 */
	public Racer(int number) {
		this.number = number;
	}
	
	/**
	 * Sets the Racer's status to RACING and sets the Racer's startTime to startTime
	 * @param startTime the time corresponding to when the Racer started a Race
	 * @precondition The Racer has status QUEUED
	 * @postcondition The Racer has status RACING
	 */
	void start(ChronoTime startTime) {
		if (this.status == Status.QUEUED) {
			this.status = Status.RACING;
			this.startTime = startTime;
		} else {
			System.out.println("Cannot start because racer does not have a QUEUED status");
		}
	}
	
	/**
	 * Sets the Racer's status to FINISHED and sets the Racer's endTime to endTime.
	 * @param finishTime the time corresponding to when the Racer finished a Race
	 * @precondition The Racer has status RACING
	 * @postcondition The Racer has status FINISHED
	 */
	void finish(ChronoTime endTime) {
		if (this.status == Status.RACING) {
			this.status = Status.FINISHED;
			this.endTime = endTime;
		} else {
			System.out.println("Cannot finish because racer does not have a RACING status");
		}
	}
	
	/**
	 * Sets the Racer's status to DNF and sets the Racer's endTime to null.
	 * The Racer's startTime is not altered.
	 * @precondition The Racer has status RACING
	 * @postcondition The Racer has status DNF
	 */
	void didNotFinish() {
		if (this.status == Status.RACING) {
			this.status = Status.DNF;
			this.endTime = null;
		} else {
			System.out.println("Cannot DNF because racer does not have a RACING status");
		}
	}
	
	
	/**
	 * Sets the Racer's status to QUEUED and sets the Raer's startTime and endTime to null.
	 * @precondition The Racer has status RACING
	 * @postcondition The Racer has status QUEUED
	 */
	void cancel() {
		if (this.status == Status.RACING) {
			this.status = Status.QUEUED;
			this.startTime = null;
			this.endTime = null;
		} else {
			System.out.println("Cannot CANCEL because racer does not have a RACING status");
		}
	}
	
	/**
	 * Returns the Racer's start time.
	 * @return the startTime of the Racer
	 */
	ChronoTime getStartTime() {
		return this.startTime;
	}
	
	/**
	 * Returns the Racer's end time.
	 * @return the endTime of the Racer.
	 */
	ChronoTime getEndTime() {
		return this.endTime;
	}
	
	/**
	 * Returns the Racer's number.
	 * @return the number of the Racer.
	 */
	int getNumber() {
		return this.number;
	}
	
	/**
	 * Returns the Racer's status
	 * @return the current status of the Racer
	 */
	Racer.Status getStatus() {
		return this.status;
	}
	
	enum Status {
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
