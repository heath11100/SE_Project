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
     * Returns a card that will be displayed by the system.
     *
     * @param elapsedTime is the current elapsed time of the run.
     * @return a valid card.
     */
    @Override
    public Card getCard(ChronoTime elapsedTime) {
        Card card = new Card();
        //Header
        Queue<Racer> nextThreeRacers = new LinkedList<Racer>();
        //Puts the next three racers into the nextThreeQueue
        for (Racer racer : this.queuedRacers) {
            if (nextThreeRacers.size() == 3) {
                break;
            } else {
                nextThreeRacers.add(racer);
            }
        }

        //For loop exits when there are no longer racers to add OR there are 3 racers.
        if (nextThreeRacers.size() == 0) {
            card.setHeader("NO RACERS QUEUED");
        } else {
            card.setHeader(nextThreeRacers);
        }


        //Body
        //Set body as the list of running racers.
        String bodyString = "";

        for (Racer racer : this.runningRacers) {
            String elapsedTimeString = "";

            try {
                //Calculate the current elapsed time
                //This puts the current time (that is passed in) relative to the run start time.
                ChronoTime racerElapsed = elapsedTime.elapsedSince(racer.getStartTime());
                elapsedTimeString = racerElapsed.toString();

            } catch (InvalidTimeException e) {
                elapsedTimeString = "INVALID TIME";
            }

            bodyString += racer.toString() + " " + elapsedTimeString + "\n";
        }

        card.setBody(bodyString);

        //Footer
        Racer lastRacer = null;
        if (this.finishedRacers.size() > 0) {
            lastRacer = ((LinkedList<Racer>)this.finishedRacers).getLast();
        }

        if (lastRacer != null) {
            card.setFooter(lastRacer.toString() + " " + lastRacer.getElapsedTimeString());

        } else {
            card.setFooter("NO RACER FINISHED");
        }

        return card;
    }

    /**
     * This will move DNF any currently running racers.
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
     * Queues a racer with a given racerNumber.
     *
     * @param racerNumber corresponding to a racer's bib number, number must be in bounds [1,9999]
     * @return if the racer was queued successfully, false otherwise.
     * @throws RaceException with any of the following conditions:
     *                       1) Racer already exists with racerNumber
     *
     * @precondition the run has not already started, racerNumber is valid (in bounds [1,9999])
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
     * This method is called when the run should start the next racer, or next batch of racers, dependent on the eventType.
     *
     * @param relativeTime corresponds to the start time, relative to the start of the run.
     * @param lane         lane is ignored for IND race type.
     * @return true if the next racer, or batch of racers, were started successfully, false otherwise.
     * @throws RaceException if there is not a racer to start.
     * @precondition atTime is valid (not null, and relative to the start of the run), the run has NOT already ended
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
     * This method is called when the run should finish the next racer, or next batch of racers, dependent ofn the eventType.
     *
     * @param relativeTime corresponds to the end time, relative to the start of the run.
     * @param lane         corresponds to the lane to start the next racer from. Note: this may be ignored for some eventTypes.
     * @return true if the next racer, or batch of racers, were finished successfully, false otherwise.
     * @throws RaceException if there is not a racer to finish.
     * @precondition atTime is valid (not null, and relative to the start of the run), the run has NOT already ended
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

                this.log.add(relativeTime.getTimeStamp() +" "+racer+" finished with time "+racer.getElapsedTime().getTimeStamp());

            } catch (InvalidTimeException e) {
                //Relative time was invalid (probably before the start time for the racer.
            }

        } else {
            throw new RaceException("No racer to finish");
        }
    }

    /**
     * Cancels the next racer to finish, in the corresponding lane, and places that racer back in the queue of racers yet to start.
     *
     * @param lane corresponding to the lane to cancel the racer from. Note: lane may not be used by all event types.
     * @return true if a racer is successfully placed into the queue, false otherwise.
     * @throws RaceException when eventType is GRP OR there is no racer to cancel.
     * @precondition race has started but not yet ended
     */
    @Override
    public void cancelNextRacer(int lane) throws RaceException {
        Racer racer = this.runningRacers.peek();

        if (racer != null) {
            racer.cancel();

            //*Could* throw NoSuchElementException, although it should never throw this.
            //Since racer != null, there is at least one element in the running queue.
            this.runningRacers.remove();

            this.queuedRacers.add(racer);

            this.log.add(racer+" cancelled");

        } else {
            //Then there is not a racer to cancel.
            throw new RaceException("No racer to cancel");
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

    public static class TestINDRunManager {
        private INDRunManager runManager;

        private int racerNumber;

        private ChronoTime time1, time2, time3;

        public TestINDRunManager() throws InvalidTimeException {
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
        public void testCancelRunningRacer() throws RaceException {
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
