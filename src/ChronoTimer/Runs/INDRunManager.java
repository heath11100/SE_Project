package ChronoTimer.Runs;

import ChronoTimer.*;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by austinheath on 4/30/17.
 *
 * Questions:
 * 1) If we cannot finish a racer (due to invalid time) should we DNF them?
 */
public class INDRunManager implements RunManager{
    private Queue<Racer> queuedRacers;
    private Queue<Racer> runningRacers;
    private Queue<Racer> finishedRacers;

    private Log log;

    public INDRunManager(Log log) {
        this.queuedRacers = new LinkedList<>();
        this.runningRacers = new LinkedList<>();
        this.finishedRacers = new LinkedList<>();

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
     * Returns a card that displays information relevant to the race. The card contains three sections:
     *  <br> Header: displays the next three queued racers
     *  <br> Body: displays as many currently running racers as possible.
     *  <br> Footer: displays the last racer that finished.
     * @param elapsedTime is the current elapsed time of the run. This is used to compute a current elapsed time for each running racer.
     * @return a valid card.
     */
    @Override
    public Card getCard(ChronoTime elapsedTime) {
        Card card = new Card();
        //Header
        String headerString = "Next 3 Queued Racers:\n";
        //queueSize will be used to iterate through the queuedRacers beginning at queueSize to 0.
        //We are going backwards so the string will display the next racer to start at the bottom of the list.
        //Since we only want the first 3 racers, if queuedRacers.size() > 3, we only want 3, thus taking the minimum.
        //If the size is 2 or 1 we don't want to IndexOutOfBounds so we will use the size instead.
        //Finally, we get the queue size here so the body is able to determine the max number of racers to show.
        final int queueSize = Math.min(this.queuedRacers.size(), 3);

        if (this.queuedRacers.size() == 0) {
            //Then there are no racers to queue.
            //Add 2 new lines to compensate for
            headerString += "None";

        } else {
            LinkedList<Racer> linkedQueue = (LinkedList<Racer>)this.queuedRacers;
            for (int index = queueSize-1; index >= 0; index--) {
                Racer racer = linkedQueue.get(index);
                headerString += racer.toString() + "\n";
            }
        }
        card.setHeader(headerString);

        String bodyString = "Running Racers:";

        LinkedList<Racer> linkedListRunning = (LinkedList<Racer>)this.runningRacers;
        //Iterate through running racers backwards.
        if (this.runningRacers.size() > 0) {
            int lineCount = 0;
            //Body
            //Set body as the list of running racers.
            //From the max number of rows allowed, subtract the amount for the header (queueSize)
            //Note: I subtract 3 to account for the "Next 3 Queued Racers:" line, which is not counted in queueSize
            //  while also accounting for the "Last Racer to finish" and "None" in the footer.
            //Subtracting 4 to account for the double line spaces we have.
            final int maxBodyLine = Card.MAX_ROWS - queueSize - 3 - 4;
            final int runningQueueSize = this.runningRacers.size();

            bodyString += " (showing " + Math.min(maxBodyLine, runningQueueSize) + " of " + runningQueueSize + ")\n";

            final int sizeOffset = 1 + runningQueueSize - Math.min(maxBodyLine, runningQueueSize);
            for (int i = runningQueueSize-sizeOffset; i >= 0; i--) {
                Racer racer = linkedListRunning.get(i);

                String elapsedTimeString;
                try {
                    //Calculate the current elapsed time
                    //This puts the current time (that is passed in) relative to the run start time.
                    ChronoTime racerElapsed = elapsedTime.elapsedSince(racer.getStartTime());
                    elapsedTimeString = racerElapsed.toString();

                } catch (Exception e) {
                    //Most likely invalid time exception
                    //Could be a NulLPointerException because elapsed time *could* be a null pointer
                    //Although, it should not if there is a runner racing.
                    elapsedTimeString = "<INVALID TIME>";
                }

                bodyString += racer.toString() + " " + elapsedTimeString + "\n";
                lineCount++;

                if (lineCount >= maxBodyLine) {
                    //Then we have reached the cap on racers to add, break from adding racers.
                    break;
                }
            }
        } else {
            bodyString += "\nNone";
        }
        card.setBody(bodyString);

        //Footer
        String footerString = "Last Racer to Finish:\n";
        Racer lastRacer = ((LinkedList<Racer>)this.finishedRacers).peekLast();
        if (lastRacer != null) {
            footerString += lastRacer.toString() + " " + lastRacer.getElapsedTimeString();
        } else {
            footerString += "None";
        }
        card.setFooter(footerString);

        return card;
    }

    /**
     * This is called when the run has ended to inform the RunManager that the run is officially over.
     * This will move DNF any currently running racers and ignore any racers within the queue waiting to start.
     */
    @Override
    public void endRun() {
        //DNF Every running racer.
        //Do nothing with the queued racers.
        for (Racer racer : this.runningRacers) {
            racer.didNotFinish();
            this.finishedRacers.add(racer);
        }

        this.runningRacers.clear();
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
        allRacers.addAll(this.runningRacers);
        allRacers.addAll(this.finishedRacers);

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
            for (Racer racer : this.runningRacers) {
                if (racer.getNumber() == racerNumber) {
                    doesExist = true;
                    break;
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
     * @throws RaceException when racer with racerNumber does not exist in the queue.
     * @precondition racerNumber is in bounds [1,9999]
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
     * This method is called when the run should start the next racer.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> relativeTime is valid (not null, and set relative to the start of the run)</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     *
     * @param relativeTime corresponds to the start time, relative to the start of the run.
     * @param lane is ignored for IND event type.
     * @throws RaceException when there is not a racer to start.
     */
    @Override
    public void startNext(ChronoTime relativeTime, int lane) throws RaceException {
        Racer racer = this.queuedRacers.peek();

        if (racer != null) {
            //*Could* throw NoSuchElementException, although it should never throw this.
            //Since racer != null, there is at least one element in the queue.
            this.queuedRacers.remove();

            //Start the racer and add it to the running queue.
            racer.start(relativeTime);
            this.runningRacers.add(racer);

            this.log.add(relativeTime.getTimeStamp() + " " + racer +" started");

        } else {
            throw new RaceException("No racer to start");
        }
    }


    /**
     * Finishes the next racer in the running queue.
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
     * @throws RaceException when there is not a racer to finish.
     */
    @Override
    public void finishNext(ChronoTime relativeTime, int lane) throws RaceException {
        Racer racer = this.runningRacers.peek();

        if (racer != null) {
            try {
                racer.finish(relativeTime);
                //Could successfully finish the racer.

                //*Could* throw NoSuchElementException, although it should never throw this.
                //Since racer != null, there is at least one element in the running queue.
                this.runningRacers.remove();

                //Add the racer to the finished queue.
                this.finishedRacers.add(racer);

                this.log.add("Finished " + racer + " " + racer.getElapsedTimeString());

            } catch (InvalidTimeException e) {
                //Relative time was invalid (probably before the start time for the racer.
            }

        } else {
            throw new RaceException("No racer to finish");
        }
    }


    /**
     * Cancels the last racer to start and puts them at the head of the queue to start next.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> the run has started</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     *
     * @param lane is ignored for IND type
     * @throws RaceException when there is not a racer to cancel
     */
    @Override
    public void cancelNextRacer(int lane) throws RaceException {
        LinkedList<Racer> linkedRunning = (LinkedList<Racer>)this.runningRacers;

        //Peek last will get the last racer to start.
        Racer racer = linkedRunning.peekLast();

        if (racer != null) {
            racer.cancel();

            //*Could* throw NoSuchElementException, although it should never throw this.
            //Since racer != null, there is at least one element in the running queue.
            linkedRunning.removeLast();

            LinkedList<Racer> linkedQueued = (LinkedList<Racer>)this.queuedRacers;
            if (!linkedQueued.offerFirst(racer)) {
                //Racer could not be added, for whatever reason?
                throw new RaceException("INTERNAL ERROR: Could not add racer.");
            }

            this.log.add(racer+" cancelled");

        } else {
            //Then there is not a racer to cancel.
            throw new RaceException("No racer to cancel");
        }
    }

    /**
     * Sets the next racer to finish as a Did Not Finish.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> the run has started</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     *
     * @param lane is ignored for IND event type.
     * @throws RaceException when there is not a racer to DNF
     */
    @Override
    public void didNotFinishNextRacer(int lane) throws RaceException{
        Racer racer = this.runningRacers.peek();

        if (racer != null) {
            racer.didNotFinish();

            //*Could* throw NoSuchElementException, although it should never throw this.
            //Since racer != null, there is at least one element in the running queue.
            this.runningRacers.remove();

            this.finishedRacers.add(racer);

            this.log.add(racer+" did not finish");

        } else {
            //Then there is not a racer to DNF.
            throw new RaceException("No racer to DNF");
        }
    }

    /**
     * Swaps the next two racers to finish.
     * @throws RaceException if there are not two racers to swap.
     * @precondition run has started but has not ended.
     */
    public void swap() throws RaceException {
        if (this.runningRacers.size() < 2) {
            //Then there are not two racers to swap, throw an exception
            throw new RaceException("There are not two racers to swap");
        } else {
            //There are at least two racers.
            LinkedList<Racer> linkedRunning = (LinkedList<Racer>)this.runningRacers;
            Racer nextRacer = linkedRunning.get(0);
            Racer secondNextRacer = linkedRunning.get(1);

            linkedRunning.set(0, secondNextRacer);
            linkedRunning.set(1, nextRacer);
        }
    }

    @Override
    public String toString() {
        String outputString = "IND RUN OUTPUT\nQueued:\n";

        for (Racer racer : this.queuedRacers) {
            outputString += racer.toString() + "\n";
        }

        outputString += "\nRunning:\n";

        for (Racer racer : this.runningRacers) {
            outputString += racer.toString() + "\n";
        }

        outputString += "\nFinished:\n";

        for (Racer racer : this.finishedRacers) {
            outputString += racer.toString() + "\n";
        }

        outputString += "\n\n\n";
        return outputString;
    }

    public static class TestRunManager {
        private INDRunManager runManager;

        private int racerNumber;

        private ChronoTime time1, time2, time3;

        public TestRunManager() throws InvalidTimeException {
            this.runManager = new INDRunManager(new Log());

            racerNumber = 1234;

            time1 = new ChronoTime(0,0,0,0);
            time2 = new ChronoTime(1,0,0,0);
            time3 = new ChronoTime(2,0,0,0);
        }

        @Test
        public void testInitialization() {
            assertEquals(0, this.runManager.queuedRacers.size());

            assertEquals(0, this.runManager.runningRacers.size());

            assertEquals(0, this.runManager.finishedRacers.size());
        }


        @Test
        public void testQueueRacer() throws RaceException {
            //Queue should be empty.
            assertEquals(0, this.runManager.queuedRacers.size());

            //Queue a racer with racerNumber.
            this.runManager.queueRacer(racerNumber);

            //Queue should have 1 racer
            assertEquals(1, this.runManager.queuedRacers.size());

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
         */
        @Test(expected = RaceException.class)
        public void testFailQueueRacer_2() throws RaceException{
            try {
                this.runManager.queueRacer(racerNumber);
                //Lane should be ignored for IND type.
                this.runManager.startNext(this.time1, 0);
            } catch (RaceException e) {
                fail("Queueing failed when queueing first racer.");
            }

            //Should fail at this point, cannot double queue racer.
            this.runManager.queueRacer(racerNumber);
        }

        /**
         * This tests that you cannot queue a racer with the same racer number as a racer finished.
         */
        @Test(expected = RaceException.class)
        public void testFailQueueRacer_3() throws RaceException{
            try {
                this.runManager.queueRacer(racerNumber);

                //Lane should be ignored for IND type.
                this.runManager.startNext(this.time1, 0);

                //Lane should be ignored for IND type.
                this.runManager.finishNext(this.time2, 0);
            } catch (RaceException e) {
                fail("Queueing failed when queueing first racer.");
            }

            //Should fail at this point, cannot double queue racer.
            this.runManager.queueRacer(racerNumber);
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
            assertEquals(0, this.runManager.runningRacers.size());
            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.queueRacer(racerNumber);

            /*Sizes:
            Queue = 1
            Running = 0
            Finished = 0
             */
            assertEquals(1, this.runManager.queuedRacers.size());
            assertEquals(0, this.runManager.runningRacers.size());
            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.deQueueRacer(racerNumber);

            /*Sizes:
            Queue = 0
            Running = 0
            Finished = 0
             */
            assertEquals(0, this.runManager.queuedRacers.size());
            assertEquals(0, this.runManager.runningRacers.size());
            assertEquals(0, this.runManager.finishedRacers.size());
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
            assertEquals(0, this.runManager.runningRacers.size());
            assertEquals(0, this.runManager.finishedRacers.size());

            this.runManager.deQueueRacer(racerNumber);
        }

        /**
         * Tests that deQueueing a racer does NOT remove a racer from the running list.
         */
        @Test(expected = RaceException.class)
        public void testDeQueueingFail_2() throws RaceException {
            try {
                this.runManager.queueRacer(racerNumber);
                this.runManager.startNext(this.time1, -1);

                /*Sizes:
                Queue = 0
                Running = 1
                Finished = 0
                 */
                assertEquals(0, this.runManager.queuedRacers.size());
                assertEquals(1, this.runManager.runningRacers.size());
                assertEquals(0, this.runManager.finishedRacers.size());
            } catch (RaceException e) {
                fail("Test should not fail until deQueueing");
            }

            this.runManager.deQueueRacer(racerNumber);
        }

        /**
         * Tests that starting a racer, that is queued, will put them into the running list.
         */
        @Test
        public void testStartNext() throws RaceException {
            //Queue the racer with racerNumber.
            this.runManager.queueRacer(racerNumber);

            //Running racers should have 0 at this point.
            assertEquals(0, this.runManager.runningRacers.size());

            //Lane should be ignored for IND type.
            this.runManager.startNext(this.time1, 0);

            //Queued racers should have 0 at this point.
            assertEquals(0, this.runManager.queuedRacers.size());

            //Running racers should have 1 at this point.
            assertEquals(1, this.runManager.runningRacers.size());

            Racer race = this.runManager.runningRacers.peek();

            //Assert that the racer has the correct racer number.
            assertEquals(racerNumber, race.getNumber());
        }

        /**
         * Tests that you cannot startNext if a racer is not queued.
         */
        @Test(expected =  RaceException.class)
        public void testStartNextFail_1() throws RaceException {
            //Lane should be ignored.
            this.runManager.startNext(this.time1, 0);
        }


        /**
         * Tests that finishing a racer, that is running, will put them into the finished list.
         */
        @Test
        public void startFinishNext() throws RaceException {
            //Queue the racer with racerNumber.
            this.runManager.queueRacer(racerNumber);


            //Running racers should have 0 at this point.
            assertEquals(0, this.runManager.runningRacers.size());

            //Finished racers should have 0 at this point.
            assertEquals(0, this.runManager.finishedRacers.size());


            //Lane should be ignored for IND type.
            this.runManager.startNext(this.time1, 0);

            //Queued racers should have 0 at this point.
            assertEquals(0, this.runManager.queuedRacers.size());

            //Running racers should have 1 at this point.
            assertEquals(1, this.runManager.runningRacers.size());

            //Finished racers should have 0 at this point.
            assertEquals(0, this.runManager.finishedRacers.size());


            //Lane should be ignored for IND type.
            this.runManager.finishNext(this.time2, 0);

            //Queued racers should have 0 at this point.
            assertEquals(0, this.runManager.queuedRacers.size());

            //Running racers should have 0 at this point.
            assertEquals(0, this.runManager.runningRacers.size());

            //Finished racers should have 1 at this point.
            assertEquals(1, this.runManager.finishedRacers.size());


            Racer race = this.runManager.finishedRacers.peek();
            //Assert that the racer has the correct racer number.
            assertEquals(racerNumber, race.getNumber());
        }

        /**
         * Tests that you cannot finishNext if a racer is not running.
         */
        @Test(expected =  RaceException.class)
        public void testFinishNextFail_1() throws RaceException {
            //Lane should be ignored.
            this.runManager.finishNext(this.time1, 0);
        }

        /**
         * Tests that cancelling a running racer will move them to the queue.
         */
        @Test
        public void testCancelRunningRacer_1() throws RaceException {
            this.runManager.queueRacer(racerNumber);
            this.runManager.startNext(this.time1, 0);

            //Running Racer should have 1 racer, Queued racers should have 0.
            assertEquals(1, this.runManager.runningRacers.size());
            assertEquals(0, this.runManager.queuedRacers.size());

            //Lane should be ignored for IND.
            this.runManager.cancelNextRacer(0);

            //Running Racer should have 0 racer, Queued racers should have 1.
            assertEquals(0, this.runManager.runningRacers.size());
            assertEquals(1, this.runManager.queuedRacers.size());
        }

        /**
         * Tests that cancelling a running racer will move them to the queue.
         */
        @Test
        public void testCancelRunningRacer_2() throws RaceException {
            final int racerNumber2 = 4321;
            final int racerNumber3 = 8080;

            this.runManager.queueRacer(racerNumber);
            this.runManager.queueRacer(racerNumber2);
            this.runManager.queueRacer(racerNumber3);
            //Queue should be: 1) racerNumber[1234], 2) racerNumber2[4321], 3) racerNumber3[8080]

            this.runManager.startNext(this.time1, 0);
            this.runManager.startNext(this.time2, 0);
            //Queue should be: 1) racerNumber3[8080]
            //Running should be 1) racerNumber[1234], 2) racerNumber2[4321]

            //Running Racer should have 2 racer, Queued racers should have 1.
            assertEquals(1, this.runManager.queuedRacers.size());
            assertEquals(2, this.runManager.runningRacers.size());

            LinkedList<Racer> runningLinked = (LinkedList<Racer>)this.runManager.runningRacers;

            //The last racer to start.
            Racer racerToCancel = runningLinked.getLast();

            //Last racer to start should be racerNumber2[4321]
            assertEquals(4321, racerToCancel.getNumber());

            //Lane should be ignored for IND.
            this.runManager.cancelNextRacer(0);
            //Queue should be: 1) racerNumber2[4321], 2) racerNumber3[8080]
            //Running should be 1) racerNumber[1234]

            //Running Racer should have 1 racer, Queued racers should have 2.
            assertEquals(2, this.runManager.queuedRacers.size());
            assertEquals(1, this.runManager.runningRacers.size());

            //Peek is the head of the queue, or the next racer to start.
            Racer nextToStart = this.runManager.queuedRacers.peek();

            //Head of the queue should be racerNumber2[4321]
            assertEquals(racerToCancel.getNumber(), nextToStart.getNumber());
            //The last racer to start (racerToCancel) should be equal the the nextRacer to start at this point (cancelledRacer)
        }

        /**
         * Tests that cancelling a racer, when no are running, throws an exception.
         */
        @Test(expected = RaceException.class)
        public void testCancelFail() throws RaceException {
            //Lane should be ignored.
            this.runManager.cancelNextRacer(0);
        }


        /**
         * Tests that DNFing a racer will move them to the finished queue.
         */
        @Test
        public void testDNFRunningRacer() throws RaceException {
            this.runManager.queueRacer(racerNumber);
            this.runManager.startNext(this.time1, 0);

            //Finished racers should have 0
            //Running racers should have 1
            //Queued racers should have 0
            assertEquals(0, this.runManager.finishedRacers.size());
            assertEquals(1, this.runManager.runningRacers.size());
            assertEquals(0, this.runManager.queuedRacers.size());

            //Lane should be ignored for IND.
            this.runManager.didNotFinishNextRacer(0);

            //Finished racers should have 1
            //Running racers should have 0
            //Queued racers should have 0
            assertEquals(1, this.runManager.finishedRacers.size());
            assertEquals(0, this.runManager.runningRacers.size());
            assertEquals(0, this.runManager.queuedRacers.size());
        }

        /**
         * Tests that DNFing a racer, when no are running, throws an exception.
         */
        @Test(expected = RaceException.class)
        public void testDNFFail() throws RaceException {
            //Lane should be ignored.
            this.runManager.didNotFinishNextRacer(0);
        }
    }
}
