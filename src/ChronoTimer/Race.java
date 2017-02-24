package ChronoTimer;
import java.util.Queue;

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
		this.eventType = eventType;
	}
	
	/**
	 * Initializes a race with no racers and event type IND.
	 */
	public Race() {
		this.eventType = EventType.IND;
	}
	
	/**
	 * Adds a racer to the queue of racers yet to start. 
	 * This ensures that the racer is not a duplicate, if a duplicate racer is added an exception is thrown.
	 * @throws DuplicateRacerException
	 * @param racer to be added to the list
	 * @precondition the race has NOT began and racer does not already exist.
	 */
	void add(Racer racer) throws DuplicateRacerException {
		if (this.finishedRacers.contains(racer) || 
				this.racingRacers.contains(racer) || 
				this.queuedRacers.contains(racer)) {
			throw new DuplicateRacerException();
		} else {
			this.queuedRacers.add(racer);
		}
	}
	
	/**
	 * Finds the racer with the racerNumber and removes them from the list of racers yet to start.
	 * @param racerNumber corresponding to the racer to be deleted.
	 * @return the Racer that was removed, or null if no racer was found
	 */
	Racer remove(int racerNumber) {
		Racer removedRacer = getRacer(racerNumber);
		if (removedRacer != null) {
			queuedRacers.remove(removedRacer);
		}
		
		return removedRacer;
	}
	
	/**
	 * Finds a racer with a number equal to racerNumber of racers yet to start.
	 * @param racerNumber corresponding to the racer to be deleted.
	 * @return the Racer corresponding to racerNumber.
	 */
	Racer getRacer(int racerNumber) {
		Racer returnRacer = null;
		for (Racer racer : queuedRacers) {
			if (racer.getNumber() == racerNumber) {
				returnRacer = racer;
				break;
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
	void beginRace(ChronoTime startTime) throws InvalidRaceStartException {
		if (this.startTime == null) {
			this.startTime = startTime;
		} else {
			throw new InvalidRaceStartException("Attempting to begin a race that already started.");
		}
	}
	
	/**
	 * Ends the race with the given time.
	 * @throws InvalidRaceEndException
	 * @param withTime corresponding to the time the race ends.
	 */
	void endRace(ChronoTime endTime) throws InvalidRaceEndException{
		if (this.startTime == null) {
			throw new InvalidRaceEndException("Attempting to end a race that has not started.");
		} else if (this.endTime != null) {
			throw new InvalidRaceEndException("Attempting to end a race that has already ended.");
		} else {
			this.endTime = endTime;
		}
	}
	
	/**
	 * Prints a list of all racers and their respective data to the console.
	 */
	void printRace() {
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
	 * Called when the next racer in the queue starts.
	 * If there is not another Racer in the Queue nothing happens.
	 * @param atTime corresponds to the time the next racer in the queue began.
	 */
	void nextRacerBegan(ChronoTime atTime) throws InvalidRaceStartException {
		Racer nextRacer = this.queuedRacers.remove();
		if (nextRacer != null) {
			//Then there is a racer to start.
			nextRacer.start(atTime);
			this.racingRacers.add(nextRacer);
		} else {
			throw new InvalidRaceStartException("Attempting to start next racer when there is no racer to start.");
		}
	}
	
	/**
	 * Called when the next racer in the queue ends.
	 * If there is not another Racer in the Queue, the Race ends.
	 * @throws InvalidRaceEndException
	 * @param atTime corresponds to the time the next racer in the queue began.
	 */
	void nextRacerFinished(ChronoTime atTime) throws InvalidRaceEndException {
		Racer nextRacer = this.racingRacers.remove();
		if (nextRacer != null) {
			//Then there is a racer to finish.
			nextRacer.finish(atTime);
			this.finishedRacers.add(nextRacer);
		} else {
			throw new InvalidRaceEndException("Attempting to finish next racer when there is not a racer in the race.");
		}

	}
	
	enum EventType {
		IND;
	}
}

//MARK: Race Exceptions

class DuplicateRacerException extends Exception
{
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
      public DuplicateRacerException() {}

      //Constructor that accepts a message
      public DuplicateRacerException(String message)
      {
         super(message);
      }
 }

class InvalidRaceStartException extends Exception
{
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
      public InvalidRaceStartException() {}

      //Constructor that accepts a message
      public InvalidRaceStartException(String message)
      {
         super(message);
      }
 }

class InvalidRaceEndException extends Exception
{
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
      public InvalidRaceEndException() {}

      //Constructor that accepts a message
      public InvalidRaceEndException(String message)
      {
         super(message);
      }
 }