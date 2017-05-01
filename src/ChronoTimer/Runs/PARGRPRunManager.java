package ChronoTimer.Runs;

import ChronoTimer.Card;
import ChronoTimer.ChronoTime;
import ChronoTimer.Racer;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by austinheath on 4/30/17.
 * QUESTION:
 * - When canceling, must I preserve the order in which racers were originally added?
 */
public class PARGRPRunManager implements RunManager {
    private Queue<Racer> queuedRacers;
    private ArrayList<Queue<Racer>> runningLanes;
    private Queue<Racer> finishedRacers;

    public PARGRPRunManager() {
        this.queuedRacers = new LinkedList<>();

        this.runningLanes = new ArrayList<>();

        this.finishedRacers = new LinkedList<>();
    }

    /**
     * Determines whether the given lane is valid.
     * PARIND only supports two lanes, lane 1 and lane 2.
     * @param lane to determine the validity of.
     * @return true if the lane is valid, false otherwise.
     */
    private boolean isValidLane(int lane) {
        return lane >= 1 && lane <= 8;
    }

    /**
     * Returns the running list for the given lane. Note: it is assumed that lane is valid, and is NOT indexed.
     * @param lane corresponding to the running lane to be returned.
     * @return the running lane.
     * @precondition lane is valid
     */
    private Queue<Racer> getRunningRacers(int lane) {
        return this.runningLanes.get(lane-1);
    }

    /**
     * Returns a card that will be displayed by the system.
     *
     * @param elapsedTime is the current elapsed time of the run.
     * @return a valid card.
     */
    @Override
    public Card getCard(ChronoTime elapsedTime) {
        return null;
    }

    /**
     * This will move DNF any currently running racers.
     */
    @Override
    public void endRun() {

    }

    /**
     * Returns a list of all racers within a run.
     * - This does NOT return the racers in any particular order.
     *
     * @return a aggregated list of all racers.
     */
    @Override
    public ArrayList<Racer> getAllRacers() {
        ArrayList<Racer> allRacers = new ArrayList<>();

        allRacers.addAll(this.queuedRacers);

        for (Queue<Racer> runningLane : this.runningLanes) {
            allRacers.addAll(runningLane);
        }

        allRacers.addAll(this.finishedRacers);

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
        boolean doesExist = false;

        for (Racer racer : this.queuedRacers) {
            if (racer.getNumber() == racerNumber) {
                doesExist = true;
                break;
            }
        }

        if (!doesExist) {
            for (Queue<Racer> runningLane : this.runningLanes) {
                //Loop through each running lane.
                for (Racer racer : runningLane) {
                    if (racer.getNumber() == racerNumber) {
                        doesExist = true;
                        break;
                    }
                }
            }
        }

        if (!doesExist) {
            for (Racer racer : this.finishedRacers) {
                if (racer.getNumber() == racerNumber) {
                    doesExist = true;
                    break;
                }
            }
        }

        return doesExist;
    }

    /**
     * Queues a racer with a given racerNumber.
     *
     * @param racerNumber corresponding to a racer's bib number, number must be in bounds [1,9999]
     * @return if the racer was queued successfully, false otherwise.
     * @throws RaceException with any of the following conditions:
     *                       1) Racer already exists with racerNumber
     *                       2) 8 Racers are already queued
     * @precondition the run has not already started,
     * racerNumber is valid (in bounds [1,9999])
     */
    @Override
    public boolean queueRacer(int racerNumber) throws RaceException {
        if (this.doesRacerExist(racerNumber)) {
            //Racer already exists, throw an exception.
            throw new RaceException("Racer already exists with number: " + racerNumber);

        } else if (this.queuedRacers.size() >= 8) {
            throw new RaceException("Maximum racers already queued.");

        } else {
            Racer newRacer = new Racer(racerNumber);
            return this.queuedRacers.add(newRacer);
        }
    }

    /**
     * Removed a racer from the the queue.
     * Note: this will not remove a racer if they are running or have finished.
     *
     * @param racerNumber corresponding to a racer's bib number, number must be in bounds [1,9999]
     * @return true if the racer was de-queued successfully, false otherwise.
     * @throws RaceException when racer with racerNumber does not exist in the queue.
     */
    @Override
    public boolean deQueueRacer(int racerNumber) throws RaceException {
        LinkedList<Racer> linkedList = (LinkedList<Racer>)this.queuedRacers;
        final int size = this.queuedRacers.size();

        for (int i = 0; i < size; i++) {
            if (linkedList.get(i).getNumber() == racerNumber) {
                linkedList.remove(i);
                return true;
            }
        }

        throw new RaceException("Racer with number: " + racerNumber + " is not queued");
    }

    /**
     * This method is called when the run should start the next racer, or next batch of racers, dependent on the eventType.
     *
     * @param relativeTime corresponds to the start time, relative to the start of the run.
     * @param lane         corresponds to the lane to start the next racer from. Note: this may be ignored for some eventTypes.
     * @return true if the next racer, or batch of racers, were started successfully, false otherwise.
     * @throws RaceException see specific eventType implementations for conditions where this exception is thrown.
     * @precondition atTime is valid (not null, and relative to the start of the run), the run has NOT already ended
     */
    @Override
    public boolean startNext(ChronoTime relativeTime, int lane) throws RaceException {
        //START ALL RACERS.
        if (lane < 1 || lane > 8) {
            throw new RaceException("Invalid lane number: " + lane);

        } else {
            int runIndex = 0;

            for (Racer racer : this.queuedRacers) {
                //Ensure that we are not getting a running lane that does not exist.
                if (runIndex < this.runningLanes.size()) {
                    this.runningLanes.get(runIndex).add(racer);
                    racer.start(relativeTime);
                } else {
                    throw new RaceException("INTERNAL INCONSISTENCY: Not enough run lanes.");
                }
            }

            //At this point: all runners have been added to the running queue.
            //Remove all racers from the queue.
            this.queuedRacers.clear();

            return true;
        }
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
        //Test if lane is valid [1,8]
        if (lane < 1 || lane > 8) {
            throw new RaceException("Invalid lane number: " + lane);

        } else {

            if (lane <= this.runningLanes.size()) {
                //Then there are enough lanes to grab it.
                LinkedList<Racer> runningLane = (LinkedList<Racer>)this.runningLanes.get(lane-1);

                Racer racer = runningLane.pollFirst();

                if (racer != null) {
                    try {
                        racer.finish(relativeTime);
                        return true;
                    } catch (InvalidTimeException e) {
                        //Do nothing.
                    }
                }
            }
        }

        return false;
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
        for (Queue<Racer> runningLane : this.runningLanes) {
            for (Racer racer : runningLane) {
                this.queuedRacers.add(racer);
            }
        }

        for (Racer racer : this.finishedRacers) {
            this.queuedRacers.add(racer);
        }

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
        if (lane < 1 || lane > 8) {
            throw new RaceException("Lane is not within bounds [1,8]");

        } else {
            final int adjustedLane = lane-1;

            if (adjustedLane < this.runningLanes.size()) {
                //Then the lane exists, attempt to pull the first racer (as there should only be one per lane).
                LinkedList<Racer> runningLane = (LinkedList<Racer>)this.runningLanes.get(adjustedLane);

                if (!runningLane.isEmpty()) {
                    //Then there is a racer.
                    Racer racer = runningLane.get(0);
                    racer.didNotFinish();
                    return true;
                }
            }
        }

        return false;
    }

}
