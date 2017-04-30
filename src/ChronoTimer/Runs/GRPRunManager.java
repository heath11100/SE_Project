package ChronoTimer.Runs;

import ChronoTimer.ChronoTime;
import ChronoTimer.Racer;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by austinheath on 4/30/17.
 */
public class GRPRunManager implements RunManager {
    private Queue<Racer> finishedRacers;
    private int nextRacerIndex;

    public GRPRunManager() {
        this.finishedRacers = new LinkedList<>();

        this.nextRacerIndex = 0;
    }

    /**
     * Returns a list of all racers within a run.
     * - This does NOT return the racers in any particular order.
     * @return a aggregated list of all racers.
     */
    @Override
    public ArrayList<Racer> getAllRacers() {
        ArrayList<Racer> allRacers = new ArrayList<>(this.finishedRacers);
        return allRacers;
    }

    /**
     * Determines whether the racer exists with the given racerNumber.
     * This will check all racers within the run (queued, running, or finished).
     *
     * @param racerNumber corresponding to a racer's bib number
     * @return true if a racer exists with the given racerNumber, false otherwise.
     */
    @Override
    public boolean doesRacerExist(int racerNumber) {
        for (Racer racer : this.finishedRacers) {
            if (racer.getNumber() == racerNumber) {
                return true;
            }
        }
        return false;
    }

    /**
     * Queues a racer with a given racerNumber.
     * @param racerNumber corresponding to a racer's bib number, number must be in bounds [1,9999]
     * @return if the racer was queued successfully, false otherwise.
     * @throws RaceException everytime because queueRacer is not supported for GRP.
     * @precondition the run has not already started,
     * racerNumber is valid (in bounds [1,9999])
     */
    @Override
    public boolean queueRacer(int racerNumber) throws RaceException {
        throw new RaceException("Cannot queue racer for GRP event");
    }

    /**
     * Removed a racer from the the queue.
     * Note: this will not remove a racer if they are running or have finished.
     *
     * @param racerNumber corresponding to a racer's bib number, number must be in bounds [1,9999]
     * @return true if the racer was de-queued successfully, false otherwise.
     * @throws RaceException every-time because of GRP type.
     */
    @Override
    public boolean deQueueRacer(int racerNumber) throws RaceException {
        throw new RaceException("Cannot de-queue a racer for GRP event");
    }

    /**
     * This method is called when the run should start the next racer, or next batch of racers, dependent on the eventType.
     *
     * @param relativeTime corresponds to the start time, relative to the start of the run.
     * @param lane         corresponds to the lane to start the next racer from. Note: this may be ignored for some eventTypes.
     * @return true if the next racer, or batch of racers, were started successfully, false otherwise.
     * @precondition atTime is valid (not null, and relative to the start of the run), the run has NOT already ended
     */
    @Override
    public boolean startNext(ChronoTime relativeTime, int lane) throws RaceException {
        //Does nothing, all racers are started,
        // but there is no need to add racers as they are dummy racers anyways.
        return true;
    }

    /**
     * This method is called when the run should finish the next racer, or next batch of racers, dependent ofn the eventType.
     *
     * @param relativeTime corresponds to the end time, relative to the start of the run.
     * @param lane         corresponds to the lane to start the next racer from. Note: this may be ignored for some eventTypes.
     * @return true if the next racer, or batch of racers, were finished successfully, false otherwise.
     * @throws RaceException see specific eventType implementations for conditions where this exception is thrown.
     * @precondition atTime is valid (not null, and relative to the start of the run), the run has NOT already ended
     */
    @Override
    public boolean finishNext(ChronoTime relativeTime, int lane) throws RaceException {
        if (this.finishedRacers.size() == MAX_RACERS) {
            throw new RaceException("Maximum number of racers have already finished.");
        }

        final int currentSize = this.finishedRacers.size();
        //Negative number denotes that it is a placeholder racer.
        int dummyNumber = -(currentSize+1);
        //Create racer with negative bib number because they are place holder.
        Racer newRacer = new Racer(dummyNumber);

        try {
            newRacer.start(new ChronoTime(0,0,0,0));
            newRacer.finish(relativeTime);

            this.finishedRacers.add(newRacer);

        } catch (InvalidTimeException e) {
            //INVALID TIME!
            //Don't do anything.
        }

        return true;
    }

    /**
     * Cancels the next racer to finish, in the corresponding lane, and places that racer back in the queue of racers yet to start.
     *
     * @param lane corresponding to the lane to cancel the racer from. Note: lane may not be used by all event types.
     * @return true if a racer is successfully placed into the queue, false otherwise.
     * @throws RaceException when eventType is GRP
     * @precondition race has started but not yet ended
     */
    @Override
    public boolean cancelNextRacer(int lane) throws RaceException {
        //Does nothing as there is not a queue and no list of running racers.
        return true;
    }

    /**
     * Marks the next racer to finish, in the corresponding lane, as a did not finish.
     *
     * @param lane corresponding to the lane to cancel the racer from. Note: lane may not be used by all event types.
     * @return true if a racer is successfully marked as a DNF, false otherwise.
     * @throws RaceException when eventType is GRP
     * @precondition race has started but not yet ended
     */
    @Override
    public boolean didNotFinishNextRacer(int lane) throws RaceException {
        //Does nothing as there is not a list of running racers to DNF from
        //There is no way to discern what person DNFs.
        return true;
    }
}
