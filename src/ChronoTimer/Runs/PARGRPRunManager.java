package ChronoTimer.Runs;

import ChronoTimer.Card;
import ChronoTimer.ChronoTime;
import ChronoTimer.Log;
import ChronoTimer.Racer;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
     * Returns a Log that contains a log of actions that occurred during the run.
     *
     * @return a valid log.
     */
    @Override
    public Log getLog() {
        return this.log;
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
     * Determines whether or not all racers are finished.
     * @return true if all racers are finished, false otherwise (or if there are no racers have finished).
     * @precondition the race has started but has not ended.
     */
    private boolean didAllFinish() {
        int count = 0;

        for (int i = 0; i < NUM_OF_LANES; i++) {
            Racer racer = this.runningRacers.get(i);

            if (racer != null) {
                //Then there is a racer that has not finished.
                return false;
            } else {
                racer = this.finishedRacers.get(i);
                if (racer != null) {
                    count++;
                }
            }
        }

        return count != 0;
    }

    /**
     * Returns a card that displays information relevant to the race. The card contains three sections:
     *  <br> Header: Displays information relative to the following conditions:
     *  <ul>
     *      <li> If the run has ended, through manual end, the run finish time is displayed
     *      <li> If the run has not ended, but all racers have ended, header says the last racer has finished</li>
     *      <li> If the run has not yet started, it will display all of the queued racers</li>
     *      <li> If the run has started, it simply lists the race time</li>
     *  </ul>
     *  <br> Body: displays information relative to the following conditions:
     *  <ul>
     *      <li> If a racer has not finished, it displays the lane number and racer number</li>
     *      <li> If a racer has finished, it displays the lane number and racer number and their elapsed time</li>
     *  </ul>
     *  <br> Footer: does not display any information.
     * @param elapsedTime is the current elapsed time of the run. If elapsedTime is null, it is assumed the run has not yet started.
     *                    This is used to compute a current elapsed time for each running racer.
     * @return a valid card.
     */
    @Override
    public Card getCard(ChronoTime elapsedTime) {
        Card card = new Card();

        String headerString;
        if (this.hasRunEnded) {
            headerString = "Run Finished at " + elapsedTime.toString();

        } else if (this.didAllFinish()) {
            headerString = "Last Racer Finished \n";

        } else if (elapsedTime == null) {
            //Run has not ended, but has not started either.
            headerString = "Parallel Group Run\n";

            //Show Queued Racers
            int count = 1;
            String queuedString = "\nQueued Racers:\n";

            if (this.queuedRacers != null && this.queuedRacers.size() > 0) {
                for (Racer racer : this.queuedRacers) {
                	if (racer == null) continue;
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
     * This is called when the run has ended to inform the RunManager that the run is officially over.
     * This will move DNF any currently running racers and ignore any racers within the queue waiting to start.
     */
    @Override
    public void endRun() {
        try {
            this.didNotFinishNextRacer(1);
        } catch (RaceException e) {
            //No.
        }

        this.hasRunEnded = true;
    }

    /**
     * Returns a list of all racers within a run.
     * <i>This does NOT return the racers in any particular order.
     * @return a aggregated list of all racers.
     */
    @Override
    public ArrayList<Racer> getAllRacers() {
        ArrayList<Racer> allRacers = new ArrayList<>();

        allRacers.addAll(this.queuedRacers);

        for (int i = 0; i < NUM_OF_LANES; i++) {
            Racer racer = this.runningRacers.get(i);
            if (racer != null) {
                allRacers.add(racer);
            }
        }

        for (int i = 0; i < NUM_OF_LANES; i++) {
            Racer racer = this.finishedRacers.get(i);
            if (racer != null) {
                allRacers.add(racer);
            }
        }

        return allRacers;
    }

    /**
     * Determines whether the racer exists with the given racerNumber.
     * This will check all racers within the run manager (queued, running, or finished).
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
            for (int i = 0; i < this.NUM_OF_LANES; i++) {
                Racer racer = this.runningRacers.get(i);
                if (racer != null && racer.getNumber() == racerNumber) {
                    doesExist = true;
                    break;
                }
            }
        }

        if (!doesExist) {
            for (int i = 0; i < this.NUM_OF_LANES; i++) {
                Racer racer = this.finishedRacers.get(i);
                if (racer != null && racer.getNumber() == racerNumber) {
                    doesExist = true;
                    break;
                }
            }
        }

        return doesExist;
    }

    /**
     * Determine whether or not a racer has started running or has finished running.
     * @return true if a racer is running or finished, false otherwise.
     */
    private boolean hasStarted() {
        boolean started = false;

        for (int i = 0; i < this.NUM_OF_LANES; i++) {
            Racer racer = this.runningRacers.get(i);
            if (racer != null) {
                started = true;
                break;
            }
        }

        if (!started) {
            for (int i = 0; i < this.NUM_OF_LANES; i++) {
                Racer racer = this.finishedRacers.get(i);
                if (racer != null) {
                    started = true;
                    break;
                }
            }
        }

        return started;
    }

    /**
     * Queues a racer to start with the given racerNumber. <i>Note: you can only queue 8 racers for PARGRP</i>
     * <br>
     * Preconditions:
     * <ul>
     *     <li> racerNumber is within bounds [1,9999]</li>
     *     <li> the run has not yet started</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     * @param  racerNumber corresponding to the racer's bib number
     * @throws RaceException when a racer already exists with racerNumber or when 8 racers are already queued
     */
    @Override
    public void queueRacer(int racerNumber) throws RaceException {
        if (this.doesRacerExist(racerNumber)) {
            //Racer already exists, throw an exception.
            throw new RaceException("Racer already exists with number: " + racerNumber);

        } else if (this.queuedRacers.size() >= NUM_OF_LANES) {
            throw new RaceException("Maximum racers already queued.");

        } else if (this.hasStarted()) {
            throw new RaceException("Cannot queue racer once run has started");
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
     * This starts all of the queued racers and places them into the lanes corresponding to their position within the queue.
     * For example, the racer at the head of the queue will go into lane 1, next in lane 2, etc.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> relativeTime is valid (not null, and set relative to the start of the run)</li>
     *     <li> the run has not yet ended</li>
     *     <li> lane is 1</li>
     * </ul>
     *
     * @param relativeTime corresponds to the start time, relative to the start of the run.
     * @param lane is ignored for PARGRP.
     * @throws RaceException when there is not a lane for a racer to be added to.
     */
    @Override
    public void startNext(ChronoTime relativeTime, int lane) throws RaceException {
        //START ALL RACERS.
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

    /**
     * Finishes the racer in the given lane.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> relativeTime is valid (not null, and set relative to the start of the run)</li>
     *     <li> the run has started</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     *
     * @param relativeTime corresponds to the finish time, relative to the start of the run.
     * @param lane corresponds to the lane to finish the next racer from
     * @throws RaceException when lane is invalid (not in bounds [1,8]) or there is not a racer to finish in the given lane
     */
    @Override
    public void finishNext(ChronoTime relativeTime, int lane) throws RaceException {
        //Test if lane is valid [1,8]
        if (!this.isValidLane(lane)) {
            throw new RaceException("Lane " + lane + " is invalid");

        } else {
            final int laneIndex = lane-1;
            //Lane is valid - indexed [0, number of running racers]

            Racer racer = this.runningRacers.get(laneIndex);

            if (racer != null) {
                try {
                    racer.finish(relativeTime);

                    this.finishedRacers.set(laneIndex, racer);
                    this.runningRacers.set(laneIndex, null);

                    this.log.add("Finished " + racer + " " + racer.getElapsedTimeString());

                } catch (InvalidTimeException e) { /*Do nothing.*/ }
            } else {
                throw new RaceException("No racer in lane " + lane + " to finish");
            }
        }
    }


    /**
     * Cancels all of the currently running racers and resets any racer that has finished into the queue to start.
     * The order of each racer is preserved.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> the run has started</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     *
     * @param lane is ignored for PARGRP type
     * @throws RaceException is not thrown for PARGRP.
     */
    @Override
    public void cancelNextRacer(int lane) throws RaceException {
        for (int i = 0; i < NUM_OF_LANES; i++) {
            Racer racer = this.runningRacers.get(i);

            if (racer == null) {
                //Then racer is not running, they might be finished.
                racer = this.finishedRacers.get(i);

                if (racer != null) {
                    //Then the racer is already finished.
                    Racer newRacer = new Racer(racer.getNumber());
                    //We need to create a new racer because we cannot cancel a finished racer.
                    this.queuedRacers.add(newRacer);
                    this.finishedRacers.set(i, null);

                } else {
                    //There is not a racer in this lane.
                }

            } else {
                //Then the racer in this lane is running.
                racer.cancel();
                this.queuedRacers.add(racer);
                this.runningRacers.set(i, null);
            }
        }

        this.log.add("Cancelled all racers");
    }

    /**
     * This will set any currently running racers as a Did Not Finish.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> the run has started</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     *
     * @param lane is ignored for PARGRP event type
     * @throws RaceException is not thrown for PARGRP event typ
     */
    @Override
    public void didNotFinishNextRacer(int lane) throws RaceException {
        if (!this.isValidLane(lane)) {
            throw new RaceException("Lane " + lane + " is invalid");

        } else {
            for (int i = 0; i < NUM_OF_LANES; i++) {
                Racer racer = this.runningRacers.get(i);

                if (racer != null) {
                    racer.didNotFinish();
                    this.finishedRacers.set(i, racer);
                    this.runningRacers.set(i, null);
                    this.log.add(racer.toString() + " did not finish");
                }
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



    public static class TestRunManager {
        private PARGRPRunManager runManager;

        private int racerNumber1, racerNumber2, racerNumber3,
                racerNumber4, racerNumber5, racerNumber6,
                racerNumber7, racerNumber8;

        private ChronoTime time1, time2;

        public TestRunManager() throws InvalidTimeException {
            this.runManager = new PARGRPRunManager(new Log());

            racerNumber1 = 1234;
            racerNumber2 = 2345;
            racerNumber3 = 3456;
            racerNumber4 = 4567;
            racerNumber5 = 5678;
            racerNumber6 = 6789;
            racerNumber7 = 7890;
            racerNumber8 = 8901;

            time1 = new ChronoTime(0,0,0,0);
            time2 = new ChronoTime(1,0,0,0);
        }

        @Test
        public void testInitialization() {
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(8, this.runManager.runningRacers.size());

            assertEquals(8, this.runManager.finishedRacers.size());

            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                Racer runningRacer = this.runManager.runningRacers.get(i);
                Racer finishedRacer = this.runManager.finishedRacers.get(i);

                assertTrue(runningRacer == null);
                assertTrue(finishedRacer == null);
            }
        }


        @Test
        public void testQueueRacer() throws RaceException {
            //Queue should be empty.
            assertEquals(0, this.runManager.queuedRacers.size());

            //Queue a racer with racerNumber.
            this.runManager.queueRacer(racerNumber1);

            //Queue should have 1 racer
            assertEquals(1, this.runManager.queuedRacers.size());

            Racer racer = this.runManager.queuedRacers.peek();
            //The racer should have the same racer number as originally provided.
            assertEquals(racer.getNumber(), this.racerNumber1);
        }


        /**
         * This tests that you cannot queue a racer with the same racer number as a racer in the queue.
         */
        @Test(expected = RaceException.class)
        public void testFailQueueRacer_1() throws RaceException {
            try {
                this.runManager.queueRacer(racerNumber1);
            } catch (RaceException e) {
                fail("Queueing failed when queueing first racer.");
            }

            //Should fail at this point, cannot double queue racer.
            this.runManager.queueRacer(racerNumber1);
        }

        /**
         * This tests that you cannot queue a racer with the same racer number as a racer running.
         */
        @Test(expected = RaceException.class)
        public void testFailQueueRacer_2() throws RaceException{
            try {
                this.runManager.queueRacer(racerNumber1);
                //Lane should be ignored for IND type.
                this.runManager.startNext(this.time1, 1);

            } catch (RaceException e) {
                fail("Queueing failed when queueing first racer.");
            }

            //Should fail at this point, cannot double queue racer.
            this.runManager.queueRacer(racerNumber1);
        }

        /**
         * This tests that you cannot queue a racer with the same racer number as a racer finished.
         */
        @Test(expected = RaceException.class)
        public void testFailQueueRacer_3() throws RaceException{
            try {
                this.runManager.queueRacer(racerNumber1);

                //Lane should be ignored for IND type.
                this.runManager.startNext(this.time1, 1);

                //Lane should be ignored for IND type.
                this.runManager.finishNext(this.time2, 1);
            } catch (RaceException e) {
                fail("Queueing failed when queueing first racer.");
            }

            //Should fail at this point, cannot double queue racer.
            this.runManager.queueRacer(racerNumber1);
        }


        /**
         * Tests that deQueueing a racer from the queue will remove them from the queue.
         */
        @Test
        public void testDeQueueing() throws RaceException {
            /*Sizes:
            Queue = 0
            Running = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());

            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Ensure finished racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.finishedRacers.get(i));
            }

            this.runManager.queueRacer(racerNumber1);

            /*Sizes:
            Queue = 1
            Running = 0
            Finished = 0
             */
            assertEquals(1, this.runManager.queuedRacers.size());
            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Ensure finished racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.finishedRacers.get(i));
            }

            this.runManager.deQueueRacer(racerNumber1);

            /*Sizes:
            Queue = 0
            Running = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());
            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Ensure finished racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.finishedRacers.get(i));
            }
        }

        /**
         * Tests that deQueuing a racer that does not exist will throw an exception.
         */
        @Test(expected = RaceException.class)
        public void testDeQueueingFail_1() throws RaceException {
            /*Sizes:
            Queue = 0
            Running = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());
            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Ensure finished racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.finishedRacers.get(i));
            }

            this.runManager.deQueueRacer(racerNumber1);
        }

        /**
         * Tests that deQueueing a racer does NOT remove a racer from the running list.
         */
        @Test(expected = RaceException.class)
        public void testDeQueueingFail_2() throws RaceException {
            try {
                this.runManager.queueRacer(racerNumber1);
                this.runManager.startNext(this.time1, 1);

                /*Sizes:
                Queue = 0
                Running = 1
                Finished = 0
                 */
                assertEquals(0, this.runManager.queuedRacers.size());
                assertEquals(racerNumber1, this.runManager.runningRacers.get(0).getNumber());

                //Ensure running racers is empty.
                for (int i = 1; i < this.runManager.NUM_OF_LANES; i++) {
                    assertEquals(null, this.runManager.runningRacers.get(i));
                }

                //Ensure finished racers is empty.
                for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                    assertEquals(null, this.runManager.finishedRacers.get(i));
                }
            } catch (RaceException e) {
                fail("Test should not fail until deQueueing");
            }

            this.runManager.deQueueRacer(racerNumber1);
        }

        /**
         * Tests that starting a racer, that is queued, will put them into the running list.
         */
        @Test
        public void testStartNext() throws RaceException {
            //Queue the racer with racerNumber1 - 8
            this.runManager.queueRacer(racerNumber1);
            this.runManager.queueRacer(racerNumber2);
            this.runManager.queueRacer(racerNumber3);
            this.runManager.queueRacer(racerNumber4);
            this.runManager.queueRacer(racerNumber5);
            this.runManager.queueRacer(racerNumber6);
            this.runManager.queueRacer(racerNumber7);
            this.runManager.queueRacer(racerNumber8);

            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Lane is ignored for PARGRP type.
            //This should put all racers form the queue to the running list.
            this.runManager.startNext(this.time1, 1);

            //Queued racers should have 0 at this point.
            assertEquals(0, this.runManager.queuedRacers.size());

            //Ensure that racers were put into appropriate queues.
            assertEquals(this.racerNumber1, this.runManager.runningRacers.get(0).getNumber());
            assertEquals(this.racerNumber2, this.runManager.runningRacers.get(1).getNumber());
            assertEquals(this.racerNumber3, this.runManager.runningRacers.get(2).getNumber());
            assertEquals(this.racerNumber4, this.runManager.runningRacers.get(3).getNumber());
            assertEquals(this.racerNumber5, this.runManager.runningRacers.get(4).getNumber());
            assertEquals(this.racerNumber6, this.runManager.runningRacers.get(5).getNumber());
            assertEquals(this.racerNumber7, this.runManager.runningRacers.get(6).getNumber());
            assertEquals(this.racerNumber8, this.runManager.runningRacers.get(7).getNumber());
        }

        /**
         * Tests that finishing a racer, that is running, will put them into the finished list.
         * This tests that lane 1 works correctly.
         */
        @Test
        public void startFinishNext_1() throws RaceException {
            //Queue the racer with racerNumber1 - 8
            this.runManager.queueRacer(racerNumber1);
            this.runManager.queueRacer(racerNumber2);
            this.runManager.queueRacer(racerNumber3);
            this.runManager.queueRacer(racerNumber4);
            this.runManager.queueRacer(racerNumber5);
            this.runManager.queueRacer(racerNumber6);
            this.runManager.queueRacer(racerNumber7);
            this.runManager.queueRacer(racerNumber8);

            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }
            //Lane is ignored for PARGRP type.
            //This should put all racers form the queue to the running list.
            this.runManager.startNext(this.time1, 1);

            this.runManager.finishNext(this.time2, 1);

            //Ensure that racers were put into appropriate queues.
            ArrayList<Racer> finished = this.runManager.finishedRacers;

            assertEquals(this.racerNumber1, finished.get(0).getNumber());
            assertEquals(null, finished.get(1));
            assertEquals(null, finished.get(2));
            assertEquals(null, finished.get(3));
            assertEquals(null, finished.get(4));
            assertEquals(null, finished.get(5));
            assertEquals(null, finished.get(6));
            assertEquals(null, finished.get(7));
        }

        /**
         * Tests that finishing a racer, that is running, will put them into the finished list.
         * This tests that lane 2 works correctly.
         */
        @Test
        public void startFinishNext_2() throws RaceException {
            //Queue the racer with racerNumber1 - 8
            this.runManager.queueRacer(racerNumber1);
            this.runManager.queueRacer(racerNumber2);
            this.runManager.queueRacer(racerNumber3);
            this.runManager.queueRacer(racerNumber4);
            this.runManager.queueRacer(racerNumber5);
            this.runManager.queueRacer(racerNumber6);
            this.runManager.queueRacer(racerNumber7);
            this.runManager.queueRacer(racerNumber8);

            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Lane is ignored for PARGRP type.
            //This should put all racers form the queue to the running list.
            this.runManager.startNext(this.time1, 1);


            this.runManager.finishNext(this.time2, 2);

            //Ensure that racers were put into appropriate queues.
            ArrayList<Racer> finished = this.runManager.finishedRacers;

            assertEquals(null, finished.get(0));
            assertEquals(this.racerNumber2, finished.get(1).getNumber());
            assertEquals(null, finished.get(2));
            assertEquals(null, finished.get(3));
            assertEquals(null, finished.get(4));
            assertEquals(null, finished.get(5));
            assertEquals(null, finished.get(6));
            assertEquals(null, finished.get(7));
        }

        /**
         * Tests that finishing a racer, that is running, will put them into the finished list.
         * This tests that lane 3 works correctly.
         */
        @Test
        public void startFinishNext_3() throws RaceException {
            //Queue the racer with racerNumber1 - 8
            this.runManager.queueRacer(racerNumber1);
            this.runManager.queueRacer(racerNumber2);
            this.runManager.queueRacer(racerNumber3);
            this.runManager.queueRacer(racerNumber4);
            this.runManager.queueRacer(racerNumber5);
            this.runManager.queueRacer(racerNumber6);
            this.runManager.queueRacer(racerNumber7);
            this.runManager.queueRacer(racerNumber8);

            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Lane is ignored for PARGRP type.
            //This should put all racers form the queue to the running list.
            this.runManager.startNext(this.time1, 1);


            this.runManager.finishNext(this.time2, 3);

            //Ensure that racers were put into appropriate queues.
            ArrayList<Racer> finished = this.runManager.finishedRacers;

            assertEquals(null, finished.get(0));
            assertEquals(null, finished.get(1));
            assertEquals(this.racerNumber3, finished.get(2).getNumber());
            assertEquals(null, finished.get(3));
            assertEquals(null, finished.get(4));
            assertEquals(null, finished.get(5));
            assertEquals(null, finished.get(6));
            assertEquals(null, finished.get(7));
        }

        /**
         * Tests that finishing a racer, that is running, will put them into the finished list.
         * This tests that lane 4 works correctly.
         */
        @Test
        public void startFinishNext_4() throws RaceException {
            //Queue the racer with racerNumber1 - 8
            this.runManager.queueRacer(racerNumber1);
            this.runManager.queueRacer(racerNumber2);
            this.runManager.queueRacer(racerNumber3);
            this.runManager.queueRacer(racerNumber4);
            this.runManager.queueRacer(racerNumber5);
            this.runManager.queueRacer(racerNumber6);
            this.runManager.queueRacer(racerNumber7);
            this.runManager.queueRacer(racerNumber8);

            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Lane is ignored for PARGRP type.
            //This should put all racers form the queue to the running list.
            this.runManager.startNext(this.time1, 1);


            this.runManager.finishNext(this.time2, 4);

            //Ensure that racers were put into appropriate queues.
            ArrayList<Racer> finished = this.runManager.finishedRacers;

            assertEquals(null, finished.get(0));
            assertEquals(null, finished.get(1));
            assertEquals(null, finished.get(2));
            assertEquals(this.racerNumber4, finished.get(3).getNumber());
            assertEquals(null, finished.get(4));
            assertEquals(null, finished.get(5));
            assertEquals(null, finished.get(6));
            assertEquals(null, finished.get(7));
        }

        /**
         * Tests that finishing a racer, that is running, will put them into the finished list.
         * This tests that lane 5 works correctly.
         */
        @Test
        public void startFinishNext_5() throws RaceException {
            //Queue the racer with racerNumber1 - 8
            this.runManager.queueRacer(racerNumber1);
            this.runManager.queueRacer(racerNumber2);
            this.runManager.queueRacer(racerNumber3);
            this.runManager.queueRacer(racerNumber4);
            this.runManager.queueRacer(racerNumber5);
            this.runManager.queueRacer(racerNumber6);
            this.runManager.queueRacer(racerNumber7);
            this.runManager.queueRacer(racerNumber8);

            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Lane is ignored for PARGRP type.
            //This should put all racers form the queue to the running list.
            this.runManager.startNext(this.time1, 1);


            this.runManager.finishNext(this.time2, 5);

            //Ensure that racers were put into appropriate queues.
            ArrayList<Racer> finished = this.runManager.finishedRacers;

            assertEquals(null, finished.get(0));
            assertEquals(null, finished.get(1));
            assertEquals(null, finished.get(2));
            assertEquals(null, finished.get(3));
            assertEquals(this.racerNumber5, finished.get(4).getNumber());
            assertEquals(null, finished.get(5));
            assertEquals(null, finished.get(6));
            assertEquals(null, finished.get(7));
        }

        /**
         * Tests that finishing a racer, that is running, will put them into the finished list.
         * This tests that lane 6 works correctly.
         */
        @Test
        public void startFinishNext_6() throws RaceException {
            //Queue the racer with racerNumber1 - 8
            this.runManager.queueRacer(racerNumber1);
            this.runManager.queueRacer(racerNumber2);
            this.runManager.queueRacer(racerNumber3);
            this.runManager.queueRacer(racerNumber4);
            this.runManager.queueRacer(racerNumber5);
            this.runManager.queueRacer(racerNumber6);
            this.runManager.queueRacer(racerNumber7);
            this.runManager.queueRacer(racerNumber8);

            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Lane is ignored for PARGRP type.
            //This should put all racers form the queue to the running list.
            this.runManager.startNext(this.time1, 1);


            this.runManager.finishNext(this.time2, 6);

            //Ensure that racers were put into appropriate queues.
            ArrayList<Racer> finished = this.runManager.finishedRacers;

            assertEquals(null, finished.get(0));
            assertEquals(null, finished.get(1));
            assertEquals(null, finished.get(2));
            assertEquals(null, finished.get(3));
            assertEquals(null, finished.get(4));
            assertEquals(this.racerNumber6, finished.get(5).getNumber());
            assertEquals(null, finished.get(6));
            assertEquals(null, finished.get(7));
        }

        /**
         * Tests that finishing a racer, that is running, will put them into the finished list.
         * This tests that lane 7 works correctly.
         */
        @Test
        public void startFinishNext_7() throws RaceException {
            //Queue the racer with racerNumber1 - 8
            this.runManager.queueRacer(racerNumber1);
            this.runManager.queueRacer(racerNumber2);
            this.runManager.queueRacer(racerNumber3);
            this.runManager.queueRacer(racerNumber4);
            this.runManager.queueRacer(racerNumber5);
            this.runManager.queueRacer(racerNumber6);
            this.runManager.queueRacer(racerNumber7);
            this.runManager.queueRacer(racerNumber8);

            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Lane is ignored for PARGRP type.
            //This should put all racers form the queue to the running list.
            this.runManager.startNext(this.time1, 1);


            this.runManager.finishNext(this.time2, 7);

            //Ensure that racers were put into appropriate queues.
            ArrayList<Racer> finished = this.runManager.finishedRacers;

            assertEquals(null, finished.get(0));
            assertEquals(null, finished.get(1));
            assertEquals(null, finished.get(2));
            assertEquals(null, finished.get(3));
            assertEquals(null, finished.get(4));
            assertEquals(null, finished.get(5));
            assertEquals(this.racerNumber7, finished.get(6).getNumber());
            assertEquals(null, finished.get(7));
        }

        /**
         * Tests that finishing a racer, that is running, will put them into the finished list.
         * This tests that lane 7 works correctly.
         */
        @Test
        public void startFinishNext_8() throws RaceException {
            //Queue the racer with racerNumber1 - 8
            this.runManager.queueRacer(racerNumber1);
            this.runManager.queueRacer(racerNumber2);
            this.runManager.queueRacer(racerNumber3);
            this.runManager.queueRacer(racerNumber4);
            this.runManager.queueRacer(racerNumber5);
            this.runManager.queueRacer(racerNumber6);
            this.runManager.queueRacer(racerNumber7);
            this.runManager.queueRacer(racerNumber8);

            //Ensure running racers is empty.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Lane is ignored for PARGRP type.
            //This should put all racers form the queue to the running list.
            this.runManager.startNext(this.time1, 1);


            this.runManager.finishNext(this.time2, 8);

            //Ensure that racers were put into appropriate queues.
            ArrayList<Racer> finished = this.runManager.finishedRacers;

            assertEquals(null, finished.get(0));
            assertEquals(null, finished.get(1));
            assertEquals(null, finished.get(2));
            assertEquals(null, finished.get(3));
            assertEquals(null, finished.get(4));
            assertEquals(null, finished.get(5));
            assertEquals(null, finished.get(6));
            assertEquals(this.racerNumber8, finished.get(7).getNumber());
        }

        /**
         * Tests that you cannot finishNext if a racer is not running.
         * Tests lane 1.
         */
        @Test(expected =  RaceException.class)
        public void testFinishNextFail_1_1() throws RaceException {
            //Lane should be ignored.
            this.runManager.finishNext(this.time1, 1);
        }

        /**
         * Tests that you cannot finishNext if a racer is not running.
         * Tests lane 2.
         */
        @Test(expected =  RaceException.class)
        public void testFinishNextFail_1_2() throws RaceException {
            //Lane should be ignored.
            this.runManager.finishNext(this.time1, 2);
        }

        /**
         * Tests that you cannot finishNext if a racer is not running.
         * Tests lane 3.
         */
        @Test(expected =  RaceException.class)
        public void testFinishNextFail_1_3() throws RaceException {
            //Lane should be ignored.
            this.runManager.finishNext(this.time1, 3);
        }

        /**
         * Tests that you cannot finishNext if a racer is not running.
         * Tests lane 4.
         */
        @Test(expected =  RaceException.class)
        public void testFinishNextFail_1_4() throws RaceException {
            //Lane should be ignored.
            this.runManager.finishNext(this.time1, 4);
        }

        /**
         * Tests that you cannot finishNext if a racer is not running.
         * Tests lane 5.
         */
        @Test(expected =  RaceException.class)
        public void testFinishNextFail_1_5() throws RaceException {
            //Lane should be ignored.
            this.runManager.finishNext(this.time1, 5);
        }

        /**
         * Tests that you cannot finishNext if a racer is not running.
         * Tests lane 6.
         */
        @Test(expected =  RaceException.class)
        public void testFinishNextFail_1_6() throws RaceException {
            //Lane should be ignored.
            this.runManager.finishNext(this.time1, 6);
        }

        /**
         * Tests that you cannot finishNext if a racer is not running.
         * Tests lane 7.
         */
        @Test(expected =  RaceException.class)
        public void testFinishNextFail_1_7() throws RaceException {
            //Lane should be ignored.
            this.runManager.finishNext(this.time1, 7);
        }

        /**
         * Tests that you cannot finishNext if a racer is not running.
         * Tests lane 8.
         */
        @Test(expected =  RaceException.class)
        public void testFinishNextFail_1_8() throws RaceException {
            //Lane should be ignored.
            this.runManager.finishNext(this.time1, 8);
        }


        /**
         * Tests that cancelling a running racer will move them to the queue.
         */
        @Test
        public void testCancelRunningRacer_1() throws RaceException {
            this.runManager.queueRacer(racerNumber1);
            this.runManager.startNext(this.time1, 1);

            //Running Racer should have 1 racer, Queued racers should have 0.
            assertEquals(racerNumber1, this.runManager.runningRacers.get(0).getNumber());
            assertEquals(0, this.runManager.queuedRacers.size());

            //Lane should be ignored for IND.
            this.runManager.cancelNextRacer(0);

            //Running Racer should have 0 racer, Queued racers should have 1.
            assertEquals(null, this.runManager.runningRacers.get(0));
            assertEquals(1, this.runManager.queuedRacers.size());
        }

        /**
         * Tests that cancelling a running racer will move them to the queue.
         */
        @Test
        public void testCancelRunningRacer_2() throws RaceException {
            this.runManager.queueRacer(racerNumber1);
            this.runManager.queueRacer(racerNumber2);
            this.runManager.queueRacer(racerNumber3);
            //Queue should be: 1) racerNumber[1234], 2) racerNumber2[4321], 3) racerNumber3[8080]

            this.runManager.startNext(this.time1, 1);
            //Running should be: 1) racerNumber[1234], 2) racerNumber2[4321], 3) racerNumber3[8080]

            //Running Racer should have 2 racer, Queued racers should have 1.
            assertEquals(racerNumber1, this.runManager.runningRacers.get(0).getNumber());
            assertEquals(racerNumber2, this.runManager.runningRacers.get(1).getNumber());
            assertEquals(racerNumber3, this.runManager.runningRacers.get(2).getNumber());

            //Lane is ignored for cancel.
            this.runManager.cancelNextRacer(1);

            //The last racer to start.
            LinkedList<Racer> queuedLinked = (LinkedList<Racer>)this.runManager.queuedRacers;
            assertEquals(racerNumber1, queuedLinked.get(0).getNumber());
            assertEquals(racerNumber2, queuedLinked.get(1).getNumber());
            assertEquals(racerNumber3, queuedLinked.get(2).getNumber());

            //Ensure all racers have been removed from running racers.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }
        }

        /**
         * Tests that cancelling a running racer will move them to the queue.
         * Even from the finished list.
         */
        @Test
        public void testCancelRunningRacer_3() throws RaceException {
            this.runManager.queueRacer(racerNumber1);
            this.runManager.queueRacer(racerNumber2);
            this.runManager.queueRacer(racerNumber3);
            //Queue should be: 1) racerNumber[1234], 2) racerNumber2[4321], 3) racerNumber3[8080]

            this.runManager.startNext(this.time1, 1);
            //Running should be: 1) racerNumber[1234], 2) racerNumber2[4321], 3) racerNumber3[8080]

            //Running Racer should have 2 racer, Queued racers should have 1.
            assertEquals(racerNumber1, this.runManager.runningRacers.get(0).getNumber());
            assertEquals(racerNumber2, this.runManager.runningRacers.get(1).getNumber());
            assertEquals(racerNumber3, this.runManager.runningRacers.get(2).getNumber());


            this.runManager.finishNext(time2, 2);
            assertEquals(null, this.runManager.runningRacers.get(1));
            assertEquals(racerNumber2, this.runManager.finishedRacers.get(1).getNumber());

            //Lane is ignored for cancel.
            this.runManager.cancelNextRacer(1);

            //The last racer to start.
            LinkedList<Racer> queuedLinked = (LinkedList<Racer>)this.runManager.queuedRacers;
            assertEquals(racerNumber1, queuedLinked.get(0).getNumber());
            assertEquals(racerNumber2, queuedLinked.get(1).getNumber());
            assertEquals(racerNumber3, queuedLinked.get(2).getNumber());

            //Ensure all racers have been removed from running racers.
            for (int i = 0; i < this.runManager.NUM_OF_LANES; i++) {
                assertEquals(null, this.runManager.runningRacers.get(i));
            }

            //Ensure that the racer was removed from the running queue.
            assertEquals(null, this.runManager.finishedRacers.get(1));

        }

        /**
         * Tests that DNFing a racer will move them to the finished queue.
         */
        @Test
        public void testDNFRunningRacer() throws RaceException {
//            this.runManager.queueRacer(racerNumber);
//            this.runManager.startNext(this.time1, 0);
//
//            //Finished racers should have 0
//            //Running racers should have 1
//            //Queued racers should have 0
//            assertEquals(0, this.runManager.finishedRacers.size());
//            assertEquals(1, this.runManager.runningRacers.size());
//            assertEquals(0, this.runManager.queuedRacers.size());
//
//            //Lane should be ignored for IND.
//            this.runManager.didNotFinishNextRacer(0);
//
//            //Finished racers should have 1
//            //Running racers should have 0
//            //Queued racers should have 0
//            assertEquals(1, this.runManager.finishedRacers.size());
//            assertEquals(0, this.runManager.runningRacers.size());
//            assertEquals(0, this.runManager.queuedRacers.size());
        }

        /**
         * Tests that DNFing a racer, when no are running, throws an exception.
         */
        @Test(expected = RaceException.class)
        public void testDNFFail() throws RaceException {
//            //Lane should be ignored.
            this.runManager.didNotFinishNextRacer(0);
        }
    }

}
