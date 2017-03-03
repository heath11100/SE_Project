package ChronoTimer;
import java.util.LinkedList;
import java.util.Queue;

import Exceptions.*;

public class Race {
	private Queue<Racer> queuedRacers;
	private Queue<Racer> racingRacers;
	private Queue<Racer> finishedRacers;
	
	private EventType eventType;
	
	private ChronoTime startTime;
	private ChronoTime endTime;
	
	/**
	 * Initializes a race with no racers.
	 * @param eventType the type of event this race will be.
	 */
	public Race(EventType eventType) {
		this.queuedRacers = new LinkedList<Racer>();
		this.racingRacers = new LinkedList<Racer>();
		this.finishedRacers = new LinkedList<Racer>();
		
		this.eventType = eventType;
	}
	
	/**
	 * Initializes a race with no racers and event type IND.
	 */
	public Race() {
		this(EventType.IND);
	}
	
	
	public void setEventType(EventType eType) {
		this.eventType = eType;
	}
	
	/**
	 * Adds a racer to the queue of racers yet to start. 
	 * This ensures that the racer is not a duplicate, if a duplicate racer is added an exception is thrown.
	 * @throws DuplicateRacerException if a racer with raacerNumber already exists
	 * @param racer to be added to the list
	 * @precondition the race has NOT began and racer does not already exist.
	 */
	public void add(int racerNumber) throws RaceException {
		Racer racer = new Racer(racerNumber);
		if (this.finishedRacers.contains(racer) || 
				this.racingRacers.contains(racer) || 
				this.queuedRacers.contains(racer)) {
			throw new RaceException("Racer with that number already exists!");
			
		} else {
			this.queuedRacers.add(racer);
		}
	}
	
	/**
	 * Finds the racer with the racerNumber and removes them from the list of racers yet to start.
	 * This will only remove a racer that is queued. Once a racer has started or finished they cannot be removed.
	 * @param racerNumber corresponding to the racer to be deleted.
	 * @return the Racer that was removed, or null if no racer was found
	 * @throws InvalidRacerException when a racer cannot be found with the given racerNumber.
	 */
	public Racer remove(int racerNumber) throws RaceException {
		Racer removedRacer = getRacer(racerNumber);
		if (removedRacer != null) {
			queuedRacers.remove(removedRacer);
		}
		else {
			throw new RaceException("Racer does not exist with that number.");
		}
		return removedRacer;
	}
	
	/**
	 * Finds a racer with a number equal to racerNumber of racers yet to start.
	 * @param racerNumber corresponding to the racer to be deleted.
	 * @return the Racer corresponding to racerNumber, or null if no racer was found
	 */
	public Racer getRacer(int racerNumber) {
		Racer returnRacer = null;
		for (Racer racer : queuedRacers) {
			if (racer.getNumber() == racerNumber) {
				returnRacer = racer;
				break;
			}
		}
		
		if (returnRacer == null) {
			for (Racer racer : racingRacers) {
				if (racer.getNumber() == racerNumber) {
					returnRacer = racer;
					break;
				}
			}
		}
		
		if (returnRacer == null) {
			for (Racer racer : finishedRacers) {
				if (racer.getNumber() == racerNumber) {
					returnRacer = racer;
					break;
				}
			}
		}
		return returnRacer;
	}
	
	/**
	 * Starts the race at the given time.
	 * Calling this method after the race has already begun will cause an exception to be thrown.
	 * @throws InvalidRaceStartException
	 * @param withTime corresponds to the time of the race starting.
	 */
	public void beginRace(ChronoTime startTime) throws InvalidTimeException {
		if (this.startTime == null) {
			this.startTime = startTime;
		} else {
			throw new InvalidTimeException("Race that already began - command canceled");
		}
	}
	
	/**
	 * Ends the race with the given time.
	 * @throws InvalidRaceEndException
	 * @param withTime corresponding to the time the race ends.
	 */
	public void endRace(ChronoTime endTime) throws InvalidTimeException{
		if (this.startTime == null) {
			throw new InvalidTimeException("Attempting to end a race that has not started.");
		} else if (this.endTime != null) {
			throw new InvalidTimeException("Attempting to end a race that has already ended.");
		} else {
			this.endTime = endTime;
		}
	}
	
	/**
	 * Prints a list of all racers and their respective data to the console.
	 */
	public void printRace() {
		//Queued Racers
		for (Racer racer : queuedRacers) {
			String racerOutput = "Racer[" + racer.getNumber() + "] - Queued";
			System.out.println(racerOutput);
		}
		
		//Current Racing Racers
		for (Racer racer : racingRacers) {
			String racerOutput = "Racer[" + racer.getNumber() + "] - Racing | Start time: ";
			racerOutput += racer.getStartTime().toString();
			
			System.out.println(racerOutput);
		}
		
		//Finished Racers
		for (Racer racer : finishedRacers) {
			String racerOutput = "Racer[" + racer.getNumber() + "] - Racing | Start time: ";
			racerOutput += racer.getStartTime().toString();
			
			racerOutput += " | End time: ";
			racerOutput += racer.getEndTime().toString();
			
			System.out.println(racerOutput);
		}
	}
	
	/**
	 * Adds the next queued racer to the race with start time atTime.
	 * @precondition the race has already began, there is a racer to start
	 * @param atTime the absolute time the next racer started
	 * @throws InvalidTimeException when the time the racer starts is before the race start time.
	 * @throws InvalidRaceException when there is not another racer to start OR the race has not started
	 */
	public void startNextRacer(ChronoTime atTime) throws RaceException, InvalidTimeException {
		Racer nextRacer = this.queuedRacers.remove();
		
		if (this.startTime == null) {
			throw new RaceException("Attempting to start a racer when the race has not started!");
		} else if (atTime.isBefore(this.startTime)) {
			//Illegal time because the time the racer is starting is before the race start time
			throw new InvalidTimeException("The Racer start time cannot be before the racer start time!");
		} else if (nextRacer == null) {
			throw new RaceException("Attempting to start next racer when there is no racer to start.");
		} else {
			/* Then: race has started
			 * Racer is starting at or after the time the race started
			 * There is another racer waiting in the queue.
			 */
			nextRacer.start(atTime);
			this.racingRacers.add(nextRacer);
		}
	}
	
	/**
	 * Sets the next racer in the racing queue to the finished racers queue with finish time atTime.
	 * 	 * @precondition the race has already began and there is at least one racer in the racingRacers queue
	 * @param atTime the absolute time the racer finished
	 * @throws InvalidRaceException when attempting to finish the next racer when there is not a racer in the race.
	 * @throws InvalidTimeException when the racer's status is not RACING
	 */
	public void finishNextRacer(ChronoTime atTime) throws RaceException, InvalidTimeException {
		Racer nextRacer = this.racingRacers.remove();
		if (nextRacer != null) {
			//Then there is a racer to finish.
			nextRacer.finish(atTime);
			this.finishedRacers.add(nextRacer);
		} else {
			throw new RaceException("Attempting to finish next racer when there is not a racer in the race.");
		}
	}
	
	enum EventType {
		IND;
	}
}
