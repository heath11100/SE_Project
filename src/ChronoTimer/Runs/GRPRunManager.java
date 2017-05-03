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
 */
public class GRPRunManager implements RunManager {
    private Queue<Racer> finishedRacers;
    private int nextRacerToMarkIndex;

    private Log log;

    public GRPRunManager(Log log) {
        this.finishedRacers = new LinkedList<>();

        this.nextRacerToMarkIndex = 0;

        this.log = log;
    }

    /**
     * Returns a card that will be displayed by the system.
     *
     * @return a valid card.
     */
    @Override
    public Card getCard(ChronoTime elapsedTime) {
        Card card = new Card();
        //Header
        //Running Time
        card.setHeader("Race Time: " + elapsedTime.toString());

        //Body
        //Nothing

        //Footer
        //Last Finish Time
        LinkedList<Racer> linkedList = (LinkedList<Racer>)this.finishedRacers;
        final int size = linkedList.size();

        if (size > 0) {
            //Then there is a valid finish time.
            Racer lastRacer = linkedList.get(size-1);
            card.setFooter(lastRacer.toString() + " " + lastRacer.getElapsedTimeString());

        } else {
            //Then no one has finished.
            card.setFooter("NO RACER FINISHED");
        }

        return card;
    }

    /**
     * This will move DNF any currently running racers.
     */
    @Override
    public void endRun() {
        //Nothing can be done
        // There is not a list of "running" racers, so we cannot DNF anyone.
        //Anyone yet to finish just won't be able to finish.
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
    public void queueRacer(int racerNumber) throws RaceException {
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
    public void deQueueRacer(int racerNumber) throws RaceException {
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
    public void startNext(ChronoTime relativeTime, int lane) throws RaceException {
        //Does nothing, all racers are started,
        // but there is no need to add racers as they are dummy racers anyways.
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

            this.log.add("Finished " + newRacer);

        } catch (InvalidTimeException e) {
            //INVALID TIME!
            //Don't do anything.
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
        //Does nothing as there is not a queue and no list of running racers.
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
        //Does nothing as there is not a list of running racers to DNF from
        //There is no way to discern what person DNFs.
    }

    /**
     * Sets the bib number for the next *placeholder* racer that has finished.
     * @param racerNumber the bib number that will be set.
     * @throws RaceException when there is not a racer to be marked.
     */
    public void markNextRacer(int racerNumber) throws RaceException {
        LinkedList<Racer> linkedList = (LinkedList<Racer>)this.finishedRacers;
        if (this.nextRacerToMarkIndex < this.finishedRacers.size()) {
            linkedList.get(this.nextRacerToMarkIndex).setNumber(racerNumber);
            this.nextRacerToMarkIndex++;
        } else {
            throw new RaceException("No racer to mark bib number for");
        }
    }

    @Override
    public String toString() {
        String outputString = "GRP RUN OUTPUT\n";

        outputString += "\nFinished:\n";

        for (Racer racer : this.finishedRacers) {
            outputString += racer.toString() + "\n";
        }

        outputString += "\n\n\n";
        return outputString;
    }


    public static class TestINDRunManager {
        private GRPRunManager runManager;

        private int racerNumber;

        private ChronoTime time1, time2, time3;

        public TestINDRunManager() throws InvalidTimeException {
            this.runManager = new GRPRunManager(new Log());

            racerNumber = 1234;

            time1 = new ChronoTime(0,0,1,0);
            time2 = new ChronoTime(1,0,0,0);
            time3 = new ChronoTime(2,0,0,0);
        }

        @Test
        public void testInitialization() {
            assertEquals(0, this.runManager.finishedRacers.size());
        }

        /**
         * Test that queueing a racer throws an exception.
         */
        @Test(expected = RaceException.class)
        public void testQueueRacer() throws RaceException {
            this.runManager.queueRacer(racerNumber);
        }

        /**
         * Test that deQueueing a racer throws an exception.
         */
        @Test(expected = RaceException.class)
        public void testDeQueueRacer() throws RaceException {
            this.runManager.deQueueRacer(racerNumber);
        }

        /**
         * Tests that starting a racer does not affect the data fields within the RunManager
         */
        @Test
        public void testStartNext() throws RaceException {
            //Finished racers should have 0 racers
            assertEquals(0,this.runManager.finishedRacers.size());

            //Lane should be ignored for GRP.
            this.runManager.startNext(this.time1, 0);

            //Finished racers should have 0 racers
            assertEquals(0,this.runManager.finishedRacers.size());
        }

        /**
         * Tests that finishing a racer properly adds it to the finished list, and that the racer is a "dummy" or placeholder.
         */
        @Test
        public void startFinishNext() throws RaceException {

            /**
             * This method is called when the run should finish the next racer, or next batch of racers, dependent ofn the eventType.
             *
             * @param relativeTime corresponds to the end time, relative to the start of the run.
             * @param lane         corresponds to the lane to start the next racer from. Note: this may be ignored for some eventTypes.
             * @return true if the next racer, or batch of racers, were finished successfully, false otherwise.
             * @throws RaceException see specific eventType implementations for conditions where this exception is thrown.
             * @precondition atTime is valid (not null, and relative to the start of the run), the run has NOT already ended
             */

            //Finished racers should have 0 racers
            assertEquals(0,this.runManager.finishedRacers.size());

            //Lane should be ignored for GRP type
            this.runManager.finishNext(this.time1, 0);

            //Finished racers should have 1 racers
            assertEquals(1 ,this.runManager.finishedRacers.size());
            Racer racer = this.runManager.finishedRacers.peek();

            assertTrue(racer != null);

            //Number will be -1 because it is a placeholder.
            assertEquals(-1, racer.getNumber());
        }

        /**
         * Tests that cancelling a running racer will do nothing. Not even an error.
         */
        @Test
        public void testCancelRunningRacer() throws RaceException {
            //Finished racers should have 0 racers
            assertEquals(0,this.runManager.finishedRacers.size());

            //Lane should be ignored for GRP.
            this.runManager.cancelNextRacer(0);

            //Finished racers should have 0 racers
            assertEquals(0,this.runManager.finishedRacers.size());
        }

        /**
         * Tests that DNFing a racer will do nothing. Not even an error.
         */
        @Test
        public void testDNFRunningRacer() throws RaceException {
            //Finished racers should have 0 racers
            assertEquals(0,this.runManager.finishedRacers.size());

            //Lane should be ignored for GRP.
            this.runManager.cancelNextRacer(0);

            //Finished racers should have 0 racers
            assertEquals(0,this.runManager.finishedRacers.size());
        }

    }
}
