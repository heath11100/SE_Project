package ChronoTimer.Runs;

import ChronoTimer.Card;
import ChronoTimer.ChronoTime;
import ChronoTimer.Log;
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
    private ArrayList<Racer> runningRacers;

    //This is the lane in which each racer finished.s

    private ArrayList<Racer> finishedRacers;

    private final int NUM_OF_LANES = 8;

    private Log log;

    private boolean hasRunEnded = false;

    public PARGRPRunManager(Log log) {

        this.queuedRacers = new LinkedList<>();

        this.runningRacers = new ArrayList<>();
        for (int i = 0; i < NUM_OF_LANES; i++) {
            runningRacers.add(null);
        }

        this.finishedRacers = new ArrayList<>();
        for (int i = 0; i < NUM_OF_LANES; i++) {
            finishedRacers.add(null);
        }

        this.log = log;
    }

    /**
     * Determines whether the given lane is valid.
     * PARIND only supports two lanes, lane 1 and lane 2.
     * @param lane to determine the validity of.
     * @return true if the lane is valid, false otherwise.
     */
    private boolean isValidLane(int lane) {
        return lane >= 1 && lane <= NUM_OF_LANES;
    }

    /**
     * Returns a card that will be displayed by the system.
     *
     * @param elapsedTime is the current elapsed time of the run.
     * @return a valid card.
     */
    @Override
    public Card getCard(ChronoTime elapsedTime) {
        Card card = new Card();

        String headerString;
        if (this.hasRunEnded) {
            headerString = "Run Finished at " + elapsedTime.toString();

        } else if (elapsedTime == null) {
            //Run has not ended, but has not started either.
            headerString = "Parallel Group Run\n";

            //Show Queued Racers
            int count = 1;
            String queuedString = "\nQueued Racers:\n";

            if (this.queuedRacers.size() > 0) {
                for (Racer racer : this.queuedRacers) {
                    queuedString += "Lane " + (count) + ": " + racer.toString() + "\n";
                    count++;
                }
            } else {
                queuedString += "None";
            }

            headerString += queuedString;

        } else {
            //Run has not ended, but has started.
            headerString = "Run Time:\n" + elapsedTime.toString();
        }
        card.setHeader(headerString);

        String bodyString = "\n";
        for (int i = 0; i < NUM_OF_LANES; i++) {
            Racer racer = this.runningRacers.get(i);

            if (racer == null) {
                //Then this lane does NOT have a racer in it that is running.
                //Check if there is a racer in this lane that has finished.
                racer = this.finishedRacers.get(i);
            }

            if (racer != null && racer.hasFinished()) {
                bodyString += "Lane " + (i+1) + ": " + racer.toString() + " " + racer.getElapsedTimeString() + "\n\n";

            } else if (racer != null) {
                //Racer still going.
                bodyString += "Lane " + (i+1) + ": " + racer.toString() + "\n\n";
            }
        }
        card.setBody(bodyString);

        //Footer
        //None

        return card;
    }

    /**
     * This will move DNF any currently running racers.
     */
    @Override
    public void endRun() {
        for (int i = 0; i < this.runningRacers.size(); i++) {
            Racer racer = this.runningRacers.get(i);
            if (racer != null) {
                racer.didNotFinish();

                this.finishedRacers.add(racer);
                this.runningRacers.set(i, null);
            }
        }

        this.hasRunEnded = true;
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

        allRacers.addAll(this.runningRacers);

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
            for (Racer racer : this.runningRacers) {
                if (racer != null && racer.getNumber() == racerNumber) {
                    doesExist = true;
                    break;
                }
            }
        }

        if (!doesExist) {
            for (Racer racer : this.finishedRacers) {
                if (racer != null && racer.getNumber() == racerNumber) {
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
    public void queueRacer(int racerNumber) throws RaceException {
        if (this.doesRacerExist(racerNumber)) {
            //Racer already exists, throw an exception.
            throw new RaceException("Racer already exists with number: " + racerNumber);

        } else if (this.queuedRacers.size() >= 8) {
            throw new RaceException("Maximum racers already queued.");

        } else {
            Racer newRacer = new Racer(racerNumber);
            this.queuedRacers.add(newRacer);

            this.log.add("Added " + newRacer.toString() + " to lane " + this.queuedRacers.size());
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
    public void deQueueRacer(int racerNumber) throws RaceException {
        LinkedList<Racer> linkedList = (LinkedList<Racer>)this.queuedRacers;
        final int size = this.queuedRacers.size();

        for (int i = 0; i < size; i++) {
            if (linkedList.get(i).getNumber() == racerNumber) {
                Racer racer = linkedList.remove(i);
                this.log.add("Removed " + racer);

                return;
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
    public void startNext(ChronoTime relativeTime, int lane) throws RaceException {
        //START ALL RACERS.
        if (!this.isValidLane(lane)) {
            throw new RaceException("Lane " + lane + " is invalid");

        } else {
            int runIndex = 0;

            //start all racers
            for (Racer racer : this.queuedRacers) {
                //Ensure that we are not getting a running lane that does not exist.
                if (runIndex < this.runningRacers.size()) {
                    this.runningRacers.add(runIndex, racer);

                    racer.start(relativeTime);
                    runIndex++;

                    this.log.add("Started " + racer + " at time " + relativeTime.getTimeStamp());

                } else {
                    throw new RaceException("INTERNAL INCONSISTENCY: Not enough run lanes.");
                }
            }

            //At this point: all runners have been added to the running queue.
            //Remove all racers from the queue.
            this.queuedRacers.clear();
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
    public void finishNext(ChronoTime relativeTime, int lane) throws RaceException {
        //Test if lane is valid [1,8]
        if (!this.isValidLane(lane)) {
            throw new RaceException("Lane " + lane + " is invalid");

        } else {
            final int laneIndex = lane-1;

            if (laneIndex >= 0 || laneIndex < this.runningRacers.size()) {
                //Lane is valid - indexed [0, number of running racers]

                Racer racer = this.runningRacers.get(laneIndex);

                if (racer != null) {
                    try {
                        racer.finish(relativeTime);

                        this.finishedRacers.set(laneIndex, racer);
                        this.runningRacers.set(laneIndex, null);

                        this.log.add("Finished " + racer + " at time " + relativeTime.getTimeStamp());

                    } catch (InvalidTimeException e) { /*Do nothing.*/ }
                }

            } else {
                throw new RaceException("Lane " + lane +" is invalid");
            }

        }
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
    public void cancelNextRacer(int lane) throws RaceException {
        for (int i = 0; i < this.runningRacers.size(); i++) {
            Racer racer = this.runningRacers.get(i);
            if (racer != null) {
                this.queuedRacers.add(racer);

                this.runningRacers.set(i, null);
            }
        }

        for (Racer racer : this.finishedRacers) {
            this.queuedRacers.add(racer);
        }
        this.finishedRacers.clear();

        this.log.add("Cancelled all racers");
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
    public void didNotFinishNextRacer(int lane) throws RaceException {
        if (!this.isValidLane(lane)) {
            throw new RaceException("Lane " + lane + " is invalid");

        } else {
            final int laneIndex = lane-1;

            Racer racer = this.runningRacers.get(laneIndex);

            if (racer != null) {
                racer.didNotFinish();
                this.finishedRacers.add(racer);
                this.runningRacers.set(laneIndex, null);

                this.log.add(racer.toString() + " did not finish");
            }
        }
    }


    @Override
    public String toString() {
        String outputString = "PARGRP RUN OUTPUT\nQueued:\n";

        for (Racer racer : this.queuedRacers) {
            outputString += racer.toString() + "\n";
        }

        outputString += "\nRunning:\n";
        for (int i = 0; i < NUM_OF_LANES; i++) {
            outputString += "\nLane " + (i+1) + ": ";
            Racer racer = this.runningRacers.get(i);

            if (racer != null) {
                outputString += racer.toString() + "\n";
            }

            outputString += "\n";
        }

        outputString += "\nFinished:\n";
        for (int i = 0; i < NUM_OF_LANES; i++) {
            outputString += "\nLane " + (i+1) + ": ";
            Racer racer = this.finishedRacers.get(i);

            if (racer != null) {
                outputString += racer.toString() + "\n";
            }

            outputString += "\n";
        }

        outputString += "\n\n\n";
        return outputString;
    }

}
