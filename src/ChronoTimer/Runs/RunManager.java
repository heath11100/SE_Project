package ChronoTimer.Runs;

import ChronoTimer.Racer;
import ChronoTimer.ChronoTime;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;

import java.util.ArrayList;

/**
 * Created by austinheath on 4/30/17.
 *
 * Questions:
 * 1) If I get a call for StartNextRacer, should I throw an error or just handle it myself?
 */
public interface RunManager {
    int MAX_RACERS = 9_999;

    /**
     * Returns a list of all racers within a run.
     * - This does NOT return the racers in any particular order.
     * @return a aggregated list of all racers.
     */
    ArrayList<Racer> getAllRacers();

    /**
     * Determines whether the racer exists with the given racerNumber.
     * This will check all racers within the run (queued, running, or finished).
     * @param racerNumber corresponding to a racer's bib number
     * @return true if a racer exists with the given racerNumber, false otherwise.
     */
    boolean doesRacerExist(int racerNumber);

    /**
     * Queues a racer with a given racerNumber.
     * @precondition the run has not already started,
     * racerNumber is valid (in bounds [1,9999])
     *
     * @param racerNumber corresponding to a racer's bib number, number must be in bounds [1,9999]
     * @return if the racer was queued successfully, false otherwise.
     * @throws RaceException with any of the following conditions:
     * 1) Racer already exists with racerNumber
     */
    boolean queueRacer(int racerNumber) throws RaceException;


    /**
     * Removed a racer from the the queue.
     * Note: this will not remove a racer if they are running or have finished.
     * @param racerNumber corresponding to a racer's bib number, number must be in bounds [1,9999]
     * @return true if the racer was de-queued successfully, false otherwise.
     * @throws RaceException when racer with racerNumber does not exist in the queue.
     */
    boolean deQueueRacer(int racerNumber) throws RaceException;

    /**
     * This method is called when the run should start the next racer, or next batch of racers, dependent on the eventType.
     * @precondition atTime is valid (not null, and relative to the start of the run), the run has NOT already ended
     *
     * @param relativeTime corresponds to the start time, relative to the start of the run.
     * @param lane corresponds to the lane to start the next racer from. Note: this may be ignored for some eventTypes.
     * @return true if the next racer, or batch of racers, were started successfully, false otherwise.
     * @throws RaceException see specific eventType implementations for conditions where this exception is thrown.
     */
    boolean startNext(ChronoTime relativeTime, int lane) throws RaceException;

    /**
     * This method is called when the run should finish the next racer, or next batch of racers, dependent ofn the eventType.
     * @precondition atTime is valid (not null, and relative to the start of the run), the run has NOT already ended
     *
     * @param relativeTime corresponds to the end time, relative to the start of the run.
     * @param lane corresponds to the lane to start the next racer from. Note: this may be ignored for some eventTypes.
     * @return true if the next racer, or batch of racers, were finished successfully, false otherwise.
     * @throws RaceException see specific eventType implementations for conditions where this exception is thrown.
     */
    boolean finishNext(ChronoTime relativeTime, int lane) throws RaceException;

    /**
     * Cancels the next racer to finish, in the corresponding lane, and places that racer back in the queue of racers yet to start.
     * @precondition race has started but not yet ended
     * @param lane corresponding to the lane to cancel the racer from. Note: lane may not be used by all event types.
     * @return true if a racer is successfully placed into the queue, false otherwise.
     * @throws RaceException when eventType is GRP
     */
    boolean cancelNextRacer(int lane) throws RaceException;

    /**
     * Marks the next racer to finish, in the corresponding lane, as a did not finish.
     * @precondition race has started but not yet ended
     *
     * @param lane corresponding to the lane to cancel the racer from. Note: lane may not be used by all event types.
     * @return true if a racer is successfully marked as a DNF, false otherwise.
     * @throws RaceException when eventType is GRP
     */
    boolean didNotFinishNextRacer(int lane) throws RaceException;
}
