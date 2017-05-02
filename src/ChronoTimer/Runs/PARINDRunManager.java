package ChronoTimer.Runs;

import ChronoTimer.Card;
import ChronoTimer.ChronoTime;
import ChronoTimer.Log;
import ChronoTimer.Racer;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by austinheath on 4/30/17.
 */
public class PARINDRunManager implements RunManager {
    private Queue<Racer> queuedRacers;
    //Always contains two running lanes
    private ArrayList<Queue<Racer>> runningLanes;
    private Queue<Racer> finishedRacers;

    private Log log;

    public PARINDRunManager(Log log) {
        this.queuedRacers = new LinkedList<>();

        this.runningLanes = new ArrayList<>();
        this.runningLanes.add(new LinkedList<>());
        this.runningLanes.add(new LinkedList<>());

        this.finishedRacers = new LinkedList<>();

        this.log = log;
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

            this.log.add("Queued " + newRacer);
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
            Racer racer = this.queuedRacers.peek();

            if (racer != null) {
                //*Could* throw NoSuchElementException, although it should never throw this.
                //Since racer != null, there is at least one element in the queue.
                this.queuedRacers.remove();

                //Start the racer and add it to the running queue.
                racer.start(relativeTime);
                Queue<Racer> runningRacers = this.getRunningRacers(lane);
                runningRacers.add(racer);

                this.log.add(relativeTime.getTimeStamp() + " " + racer +" started in lane " + lane);

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
            Racer racer = runningRacers.peek();

            if (racer != null) {
                try {
                    racer.finish(relativeTime);
                    //Could successfully finish the racer.

                    //*Could* throw NoSuchElementException, although it should never throw this.
                    //Since racer != null, there is at least one element in the running queue.
                    runningRacers.remove();

                    //Add the racer to the finished queue.
                    this.finishedRacers.add(racer);

                    this.log.add(relativeTime.getTimeStamp() +" "+racer+" finished with time "+racer.getElapsedTime().getTimeStamp() + " in lane " + lane);

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
            Racer racer = runningRacers.peek();

            if (racer != null) {
                racer.cancel();

                //*Could* throw NoSuchElementException, although it should never throw this.
                //Since racer != null, there is at least one element in the running queue.
                runningRacers.remove();

                this.queuedRacers.add(racer);

                this.log.add(racer+" cancelled");

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
            Racer racer = runningRacers.peek();

            if (racer != null) {
                racer.didNotFinish();

                //*Could* throw NoSuchElementException, although it should never throw this.
                //Since racer != null, there is at least one element in the running queue.
                runningRacers.remove();

                this.finishedRacers.add(racer);

                this.log.add(racer+" did not finish");

            } else {
                //Then there is not a racer to DNF.
                throw new RaceException("No racer to DNF");
            }
        }
    }

    public static class TestINDRunManager {
        private PARINDRunManager runManager;

        private int racerNumber;

        private ChronoTime time1, time2, time3;

        public TestINDRunManager() throws InvalidTimeException {
            this.runManager = new PARINDRunManager(new Log());

            racerNumber = 1234;

            time1 = new ChronoTime(0,0,0,0);
            time2 = new ChronoTime(1,0,0,0);
            time3 = new ChronoTime(2,0,0,0);
        }

        @Test
        /**
         * Ensure that data objects are initialized correctly.
         */
        public void testInitialization() {
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            //Ensure that each running lane is empty.
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());
        }


        @Test
        public void testQueueRacer() throws RaceException {
            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            //Queue a racer with racerNumber.
            this.runManager.queueRacer(racerNumber);

            /* Sizes:
            Queue = 1
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(1, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());


            Racer racer = this.runManager.queuedRacers.peek();
            //The racer should have the same racer number as originally provided.
            assertEquals(racer.getNumber(), this.racerNumber);
        }


        /**
         * This tests that you cannot queue a racer with the same racer number as a racer in the queue.
         */
        @Test(expected = RaceException.class)
        public void testFailQueueRacer_1() throws RaceException {
            try {
                this.runManager.queueRacer(racerNumber);
            } catch (RaceException e) {
                fail("Queueing failed when queueing first racer.");
            }

            //Should fail at this point, cannot double queue racer.
            this.runManager.queueRacer(racerNumber);
        }

        /**
         * This tests that you cannot queue a racer with the same racer number as a racer running.
         * This uses lane = 1.
         */
        @Test(expected = RaceException.class)
        public void testFailQueueRacer_2() throws RaceException{
            final int lane = 1;
            try {
                this.runManager.queueRacer(racerNumber);

                this.runManager.startNext(this.time1, lane);
            } catch (RaceException e) {
                fail("Queueing failed when queueing first racer.");
            }

            //Should fail at this point, cannot double queue racer.
            this.runManager.queueRacer(racerNumber);
        }

        /**
         * This tests that you cannot queue a racer with the same racer number as a racer running.
         * This uses lane = 2.
         */
        @Test(expected = RaceException.class)
        public void testFailQueueRacer_3() throws RaceException{
            final int lane = 2;
            try {
                this.runManager.queueRacer(racerNumber);

                this.runManager.startNext(this.time1, lane);
            } catch (RaceException e) {
                fail("Queueing failed when queueing first racer.");
            }

            //Should fail at this point, cannot double queue racer.
            this.runManager.queueRacer(racerNumber);
        }


        /**
         * This tests that you cannot queue a racer with the same racer number as a racer finished.
         * This uses lane = 1.
         */
        @Test(expected = RaceException.class)
        public void testFailQueueRacer_4() throws RaceException{
            final int lane = 1;
            try {
                this.runManager.queueRacer(racerNumber);

                //Lane should be ignored for IND type.
                this.runManager.startNext(this.time1, lane);

                //Lane should be ignored for IND type.
                this.runManager.finishNext(this.time2, lane);
            } catch (RaceException e) {
                fail("Queueing failed when queueing first racer.");
            }

            //Should fail at this point, cannot double queue racer.
            this.runManager.queueRacer(racerNumber);
        }

        /**
         * This tests that you cannot queue a racer with the same racer number as a racer finished.
         * This uses lane = 2.
         */
        @Test(expected = RaceException.class)
        public void testFailQueueRacer_5() throws RaceException{
            final int lane = 2;
            try {
                this.runManager.queueRacer(racerNumber);

                //Lane should be ignored for IND type.
                this.runManager.startNext(this.time1, lane);

                //Lane should be ignored for IND type.
                this.runManager.finishNext(this.time2, lane);
            } catch (RaceException e) {
                fail("Queueing failed when queueing first racer.");
            }

            //Should fail at this point, cannot double queue racer.
            this.runManager.queueRacer(racerNumber);
        }

        /**
         * Tests that starting a racer in lane 1, that is queued, will put them into the running list for lane 1
         */
        @Test
        public void testStartNext_Lane1() throws RaceException {
            final int lane = 1;
            final int laneIndex = lane-1;
            //Queue the racer with racerNumber.
            this.runManager.queueRacer(racerNumber);

            /* Sizes:
            Queue = 1
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(1, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            //Lane should be ignored for IND type.
            this.runManager.startNext(this.time1, lane);

            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 1
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(1, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            Racer race = this.runManager.runningLanes.get(laneIndex).peek();
            //Assert that the racer has the correct racer number.
            assertEquals(racerNumber, race.getNumber());
        }

        /**
         * Tests that you cannot startNext if a racer is not queued.
         * This uses lane 1.
         */
        @Test(expected =  RaceException.class)
        public void testStartNextFail_1() throws RaceException {
            final int lane = 1;
            //Lane should be ignored.
            this.runManager.startNext(this.time1, lane);
        }

        /**
         * Tests that you cannot startNext if a racer is not queued.
         * This uses lane 2.
         */
        @Test(expected =  RaceException.class)
        public void testStartNextFail_2() throws RaceException {
            final int lane = 2;
            //Lane should be ignored.
            this.runManager.startNext(this.time1, lane);
        }

        /**
         * Tests that finishing a racer, that is running, will put them into the finished list.
         * This uses lane = 1.
         */
        @Test
        public void startFinishNext_Lane1() throws RaceException {
            final int lane = 1;
            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.queueRacer(racerNumber);

            /* Sizes:
            Queue = 1
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(1, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());


            //Start next for lane 1.
            this.runManager.startNext(this.time1, lane);

            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 1
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(1, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            //Finish racer in lane 1.
            this.runManager.finishNext(this.time2, lane);
            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 1
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(1, this.runManager.finishedRacers.size());

            Racer race = this.runManager.finishedRacers.peek();
            //Assert that the racer has the correct racer number.
            assertEquals(racerNumber, race.getNumber());
        }

        /**
         * Tests that finishing a racer, that is running, will put them into the finished list.
         * This uses lane = 2.
         */
        @Test
        public void startFinishNext_Lane2() throws RaceException {
            final int lane = 2;
            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.queueRacer(racerNumber);

            /* Sizes:
            Queue = 1
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(1, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());


            //Start next for lane 1.
            this.runManager.startNext(this.time1, lane);

            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 1
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(1, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            //Finish racer in lane 1.
            this.runManager.finishNext(this.time2, lane);

            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 1
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(1, this.runManager.finishedRacers.size());

            Racer race = this.runManager.finishedRacers.peek();
            //Assert that the racer has the correct racer number.
            assertEquals(racerNumber, race.getNumber());
        }

        /**
         * Tests that you cannot finishNext if a racer is not running.
         * This uses lane 1.
         */
        @Test(expected =  RaceException.class)
        public void testFinishNextFail_1() throws RaceException {
            final int lane = 1;
            //Lane should be ignored.
            this.runManager.finishNext(this.time1, lane);
        }

        /**
         * Tests that you cannot finishNext if a racer is not running.
         * This uses lane 2.
         */
        @Test(expected =  RaceException.class)
        public void testFinishNextFail_2() throws RaceException {
            final int lane = 2;
            //Lane should be ignored.
            this.runManager.finishNext(this.time1, lane);
        }


        /**
         * Tests that cancelling a running racer will move them to the queue.
         * This uses lane = 1.
         */
        @Test
        public void testCancelRunningRacer_Lane1() throws RaceException {
            final int lane = 1;
            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.queueRacer(racerNumber);

            /* Sizes:
            Queue = 1
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(1, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.startNext(this.time1, lane);

            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 1
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(1, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.cancelNextRacer(lane);

            /* Sizes:
            Queue = 1
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(1, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());
        }

        /**
         * Tests that cancelling a running racer will move them to the queue.
         * This uses lane = 2.
         */
        @Test
        public void testCancelRunningRacer_Lane2() throws RaceException {
            final int lane = 2;
            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.queueRacer(racerNumber);

            /* Sizes:
            Queue = 1
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(1, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.startNext(this.time1, lane);

            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 1
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(1, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.cancelNextRacer(lane);

            /* Sizes:
            Queue = 1
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(1, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());
        }

        /**
         * Tests that cancelling a racer, when no are running, throws an exception.
         * This is for lane = 1.
         */
        @Test(expected = RaceException.class)
        public void testCancelFail_1() throws RaceException {
            final int lane = 1;
            //Lane should be ignored.
            this.runManager.cancelNextRacer(lane);
        }

        /**
         * Tests that cancelling a racer, when no are running, throws an exception.
         * This is for lane = 2.
         */
        @Test(expected = RaceException.class)
        public void testCancelFail_2() throws RaceException {
            final int lane = 2;
            //Lane should be ignored.
            this.runManager.cancelNextRacer(lane);
        }


        /**
         * Tests that DNFing a racer will move them to the finished queue.
         * This is for lane = 1.
         */
        @Test
        public void testDNFRunningRacer_Lane1() throws RaceException {
            final int lane = 1;
            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.queueRacer(racerNumber);

            /* Sizes:
            Queue = 1
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(1, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.startNext(this.time1, lane);

            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 1
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(1, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.didNotFinishNextRacer(lane);

            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 1
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(1, this.runManager.finishedRacers.size());
        }

        /**
         * Tests that DNFing a racer will move them to the finished queue.
         * This is for lane = 2.
         */
        @Test
        public void testDNFRunningRacer_Lane2() throws RaceException {
            final int lane = 2;
            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.queueRacer(racerNumber);

            /* Sizes:
            Queue = 1
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 0
             */
            assertEquals(1, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.startNext(this.time1, lane);

            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 1
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(1, this.runManager.runningLanes.get(1).size());

            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.didNotFinishNextRacer(lane);

            /* Sizes:
            Queue = 0
            Running Lanes = 2
                - Lane 1 = 0
                - Lane 2 = 0
            Finished = 1
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(2, this.runManager.runningLanes.size());
            assertEquals(0, this.runManager.runningLanes.get(0).size());
            assertEquals(0, this.runManager.runningLanes.get(1).size());

            assertEquals(1, this.runManager.finishedRacers.size());
        }

        /**
         * Tests that DNFing a racer, when no are running, throws an exception.
         * This is for lane = 1.
         */
        @Test(expected = RaceException.class)
        public void testDNFFail_1() throws RaceException {
            final int lane = 1;
            //Lane should be ignored.
            this.runManager.didNotFinishNextRacer(lane);
        }

        /**
         * Tests that DNFing a racer, when no are running, throws an exception.
         * This is for lane = 2.
         */
        @Test(expected = RaceException.class)
        public void testDNFFail_2() throws RaceException {
            final int lane = 2;
            //Lane should be ignored.
            this.runManager.didNotFinishNextRacer(lane);
        }
    }
}
