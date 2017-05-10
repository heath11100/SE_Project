package ChronoTimer.Runs;

import ChronoTimer.Card;
import ChronoTimer.Log;
import ChronoTimer.Racer;
import ChronoTimer.ChronoTime;
import Exceptions.RaceException;

import java.util.ArrayList;

/**
 * Created by austinheath on 4/30/17.
 */
public interface RunManager {
    //Is final because its in an interface.
    int MAX_RACERS = 9_999;

    /**
     * Returns a Log that contains a log of actions that occurred during the run.
     * @return a valid log.
     */
    Log getLog();

    /**
     * Returns a card that displays information relevant to the race.
     * The card contains three sections:
     * <br> Header
     * <br> Body
     * <br> Footer
     * @param elapsedTime is the current elapsed time of the run. This is used to compute a current elapsed time for each running racer.
     * @return a valid card.
     */
    Card getCard(ChronoTime elapsedTime);

    /**
     * This is called when the run has ended to inform the RunManager that the run is officially over.
     * Implementation specifics are dependent on each event type.
     */
    void endRun();

    /**
     * Returns a list of all racers within a run.
     * <i>This does NOT return the racers in any particular order.
     * @return a aggregated list of all racers.
     */
    ArrayList<Racer> getAllRacers();

    /**
     * Determines whether the racer exists with the given racerNumber.
     * This will check all racers within the run manager (queued, running, or finished).
     * @param racerNumber corresponding to a racer's bib number
     * @return true if a racer exists with the given racerNumber, false otherwise.
     */
    boolean doesRacerExist(int racerNumber);

    /**
     * Queues a racer to start with the given racerNumber.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> racerNumber is within bounds [1,9999]</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     * @param  racerNumber corresponding to the racer's bib number
     * @throws RaceException when a racer already exists with racerNumber
     */
    void queueRacer(int racerNumber) throws RaceException;

    /**
     * Removed a racer from the the queue.
     * Note: this will not remove a racer if they are running or have finished.
     * @param racerNumber corresponding to a racer's bib number, number must be in bounds [1,9999]
     * @throws RaceException when racer with racerNumber does not exist in the queue.
     */
    void deQueueRacer(int racerNumber) throws RaceException;

    /**
     * This method is called when the run should start the next racer(s) (dependent on the event type).
     * Implementation specifics are dependent on each event type.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> relativeTime is valid (not null, and set relative to the start of the run)</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     *
     * @param relativeTime corresponds to the start time, relative to the start of the run.
     * @param lane corresponds to the lane to start the next racer(s) from
     * @throws RaceException specifics are dependent on each event type
     */
    void startNext(ChronoTime relativeTime, int lane) throws RaceException;


    /**
     * This method is called when the run should finish the next racer(s) (dependent on the event type).
     * Implementation specifics are dependent on each event type.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> relativeTime is valid (not null, and set relative to the start of the run)</li>
     *     <li> the run has started</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     *
     * @param relativeTime corresponds to the finish time, relative to the start of the run.
     * @param lane corresponds to the lane to finish the next racer(s) from
     * @throws RaceException specifics are dependent on each event type
     */
    void finishNext(ChronoTime relativeTime, int lane) throws RaceException;


    /**
     * This method is called when the run should cancel the last racer to start running.
     * Implementation specifics are dependent on each event type.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> the run has started</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     *
     * @param lane corresponds to the lane to cancel the racer from
     * @throws RaceException specifics are dependent on each event type
     */
    void cancelNextRacer(int lane) throws RaceException;


    /**
     * This method is called when the run should set the next racer to start as Did Not Finish.
     * Implementation specifics are dependent on each event type.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> the run has started</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     *
     * @param lane corresponds to the lane to DNF the racer from
     * @throws RaceException specifics are dependent on each event type
     */
    void didNotFinishNextRacer(int lane) throws RaceException;
}
