package ChronoTimer.Runs;

import ChronoTimer.Card;
import ChronoTimer.ChronoTime;
import ChronoTimer.Racer;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;

import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

/**
 * Created by austinheath on 4/30/17.
 */
public class PARINDRunManager implements RunManager {
    private Queue<Racer> queuedRacers;
    //Always contains two running lanes
    private ArrayList<Queue<Racer>> runningLanes;
    private Queue<Racer> finishedRacers;

    public PARINDRunManager() {
        this.queuedRacers = new LinkedList<>();

        this.runningLanes = new ArrayList<>();
        this.runningLanes.add(new LinkedList<>());
        this.runningLanes.add(new LinkedList<>());

        this.finishedRacers = new LinkedList<>();
    }

    /**
     * Determines whether the given lane is valid.
     * PARIND only supports two lanes, lane 1 and lane 2.
     * @param lane to determine the validity of.
     * @return true if the lane is valid, false otherwise.
     */
    private boolean isValidLane(int lane) {
        return lane == 1 || lane == 2;
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
        Card card = new Card();

        //Header
        //Next pair to run (essentially next two racers).
        Queue<Racer> nextPair = new LinkedList<Racer>();

        for (Racer racer : this.queuedRacers) {
            if (nextPair.size() < 2) {
                nextPair.add(racer);
            } else {
                break;
            }
        }

        //For loop exits when there are no longer racers to add OR there are 2 racers.
        if (nextPair.size() > 0) {
            card.setHeader(nextPair);
        } else {
            //Then there are no racers paired to run.
            card.setHeader("NO RACERS QUEUED");
        }

        //Body
        //Nothing

        //Footer
        //Finish times of the last pair to finish (essentially last two racers).
        LinkedList<Racer> linkedList = (LinkedList<Racer>) this.finishedRacers;
        final int size = linkedList.size();

        if (size > 1) {
            //Then there is at least 2 racers (one pair)
            Racer lastRacer = linkedList.get(size-1);
            Racer secondLast = linkedList.get(size-2);

            card.setFooter(lastRacer.toString() + " " + lastRacer.getElapsedTimeString()+ ", " +
                    secondLast.toString() + " " + secondLast.getElapsedTimeString());

        } else if (size > 0) {
            //Then only one racer has finished.
            Racer lastRacer = linkedList.get(size-1);

            card.setFooter(lastRacer.toString() + " " + lastRacer.getElapsedTimeString());

        } else {
            //Then no one has finished.
            card.setFooter("NO PAIR HAS FINISHED");
        }

        return card;
    }

    /**
     * This will move DNF any currently running racers.
     */
    @Override
    public void endRun() {
        for (Queue<Racer> runningLane : this.runningLanes) {
            for (Racer racer : runningLane) {
                racer.didNotFinish();
                this.finishedRacers.add(racer);
            }
            runningLane.clear();
        }
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
     * @precondition the run has not already started,
     * racerNumber is valid (in bounds [1,9999])
     */
    @Override
    public void queueRacer(int racerNumber) throws RaceException {
        if (this.doesRacerExist(racerNumber)) {
            //Racer already exists, throw an exception.
            throw new RaceException("Racer already exists with number: " + racerNumber);

        } else {
            Racer newRacer = new Racer(racerNumber);
            this.queuedRacers.add(newRacer);
        }
    }

    /**
     * Removed a racer from the the queue.
     * Note: this will not remove a racer if they are running or have finished.
     *
     * @param racerNumber corresponding to a racer's bib number, number must be in bounds [1,9999]
     * @return true if the racer was de-queued successfully, false otherwise.
     * @throws RaceException with any of the following conditions:
     *                       1) racerNumber is not within bounds [1,9999]
     */
    @Override
    public void deQueueRacer(int racerNumber) throws RaceException {
        LinkedList<Racer> linkedList = (LinkedList<Racer>)this.queuedRacers;
        final int size = this.queuedRacers.size();

        for (int i = 0; i < size; i++) {
            if (linkedList.get(i).getNumber() == racerNumber) {
                linkedList.remove(i);
            }
        }

        throw new RaceException("Racer with number: " + racerNumber + " is not queued");
    }

    /**
     * This method is called when the run should start the next racer, or next batch of racers, dependent on the eventType.
     *
     * @param relativeTime corresponds to the start time, relative to the start of the run.
     * @param lane         corresponds to the lane to start the next racer from.
     * @return true if the next racer, or batch of racers, were started successfully, false otherwise.
     * @throws RaceException if lane is invalid (not 1 or 2).
     * @precondition atTime is valid (not null, and relative to the start of the run), the run has NOT already ended
     */
    @Override
    public void startNext(ChronoTime relativeTime, int lane) throws RaceException {
        if (!this.isValidLane(lane)) {
            //Not valid lane
            throw new RaceException("Invalid lane: " + lane);

        } else {
            Racer racer = this.queuedRacers.poll();

            if (racer != null) {
                //*Could* throw NoSuchElementException, although it should never throw this.
                //Since racer != null, there is at least one element in the queue.
                this.queuedRacers.remove();

                //Start the racer and add it to the running queue.
                racer.start(relativeTime);
                Queue<Racer> runningRacers = this.getRunningRacers(lane);
                runningRacers.add(racer);

            } else {
                throw new RaceException("No racer to start");
            }
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
        if (!this.isValidLane(lane)) {
            //Not valid lane
            throw new RaceException("Invalid lane: " + lane);

        } else {
            Queue<Racer> runningRacers = this.getRunningRacers(lane);
            Racer racer = runningRacers.poll();

            if (racer != null) {
                try {
                    racer.finish(relativeTime);
                    //Could successfully finish the racer.

                    //*Could* throw NoSuchElementException, although it should never throw this.
                    //Since racer != null, there is at least one element in the running queue.
                    runningRacers.remove();

                    //Add the racer to the finished queue.
                    this.finishedRacers.add(racer);

                } catch (InvalidTimeException e) {
                    //Relative time was invalid (probably before the start time for the racer.
                }

            } else {
                throw new RaceException("No racer to finish");
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
        if (!this.isValidLane(lane)) {
            //Not valid lane
            throw new RaceException("Invalid lane: " + lane);

        } else {
            Queue<Racer> runningRacers = this.getRunningRacers(lane);
            Racer racer = runningRacers.poll();

            if (racer != null) {
                racer.cancel();

                //*Could* throw NoSuchElementException, although it should never throw this.
                //Since racer != null, there is at least one element in the running queue.
                runningRacers.remove();

                this.queuedRacers.add(racer);

            } else {
                //Then there is not a racer to cancel.
                throw new RaceException("No racer to cancel");
            }
        }
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
            //Not valid lane
            throw new RaceException("Invalid lane: " + lane);

        } else {
            Queue<Racer> runningRacers = this.getRunningRacers(lane);
            Racer racer = runningRacers.poll();

            if (racer != null) {
                racer.didNotFinish();

                //*Could* throw NoSuchElementException, although it should never throw this.
                //Since racer != null, there is at least one element in the running queue.
                runningRacers.remove();

                this.finishedRacers.add(racer);

            } else {
                //Then there is not a racer to DNF.
                throw new RaceException("No racer to DNF");
            }
        }
    }
}
