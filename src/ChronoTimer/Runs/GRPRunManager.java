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
     *  <br> Header: displays the current race time
     *  <br> Body: no information is displayed within the body
     *  <br> Footer: displays the last racer to finish
     * @param elapsedTime is the current elapsed time of the run. This is used to compute a current elapsed time for each running racer.
     * @return a valid card.
     */
    @Override
    public Card getCard(ChronoTime elapsedTime) {
        Card card = new Card();
        //Header
        //Running Time
        String headerString = "Race Time:\n";
        if (elapsedTime == null) {
            headerString += "Not started";

        } else {
            headerString += elapsedTime.toString();
        }
        card.setHeader(headerString);

        //Body
        //Nothing

        //Footer
        //Last Racer Finish Time
        Racer racer = ((LinkedList<Racer>)this.finishedRacers).peekLast();
        String footerString = "Last Racer to Finish:\n";

        if (racer != null) {
            footerString += racer.toString() + " " + racer.getElapsedTimeString();
        } else {
            footerString += "None";
        }
        card.setFooter(footerString);

        return card;
    }

    /**
     * This is called when the run is ended to inform the RunManager that the run is officially over.
     * Since GRP does not contain any queued racers or store any running racers, this call does not do anything in particular.
     */
    @Override
    public void endRun() {
        //Nothing can be done
        // There is not a list of "running" racers, so we cannot DNF anyone.
        //Anyone yet to finish just won't be able to finish.
    }

    /**
     * Returns a list of all racers within a run.
     * <i>This does NOT return the racers in any particular order.
     * @return a aggregated list of all racers.
     */
    @Override
    public ArrayList<Racer> getAllRacers() {
        ArrayList<Racer> allRacers = new ArrayList<>(this.finishedRacers);
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
        for (Racer racer : this.finishedRacers) {
            if (racer.getNumber() == racerNumber) {
                return true;
            }
        }

        return false;
    }

    /**
     * GRP does not support queueing racers.
     * @param  racerNumber corresponding to the racer's bib number
     * @throws RaceException on any call, GRP does not support queueing racers.
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
     * This method does not do anything in particular, as racers are not logged until they finish.
     * <br>
     * Preconditions:
     * <ul>
     *     <li> relativeTime is valid (not null, and set relative to the start of the run)</li>
     *     <li> the run has not yet ended</li>
     * </ul>
     *
     * @param relativeTime is ignored for GRP event type.
     * @param lane is ignored for GRP event type.
     * @throws RaceException is not thrown for GRP event type.
     */
    @Override
    public void startNext(ChronoTime relativeTime, int lane) throws RaceException {
        //Does nothing, all racers are "started",
        // but there is no need to add racers as they are dummy racers anyways.
    }


    /**
     * Adds another racer to the finished list.
     * Racers are added with a "dummy" number, which is distinct from any number that can be entered by a user.
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
     * @throws RaceException when the maximum number of racers to finish have already finished or if lane is not 1.
     */
    @Override
    public void finishNext(ChronoTime relativeTime, int lane) throws RaceException {
        if (this.finishedRacers.size() == MAX_RACERS) {
            //Note: we check for equality because adding another racer would put us OVER the max.
            throw new RaceException("Maximum number of racers have already finished.");

        } else if (lane != 1) {
            throw new RaceException("Racer can only finish in lane 1.");
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

            this.log.add("Finished " + newRacer + " " + newRacer.getElapsedTimeString());

        } catch (InvalidTimeException e) {
            //INVALID TIME!
            //Don't do anything.
        }
    }


    /**
     * Does nothing in particular. Cancel is not supported for GRP.
     * @param lane is ignored for GRP type
     * @throws RaceException on every call, GRP does not support cancel.
     */
    @Override
    public void cancelNextRacer(int lane) throws RaceException {
        //Does nothing as there is not a queue and no list of running racers.
        throw new RaceException("Cannot Cancel for GRP event");
    }


    /**
     * Does nothing in particular. DNF is not supported for GRP.
     * @param lane is ignored for GRP type
     * @throws RaceException on every call, GRP does not support DNF.
     */
    @Override
    public void didNotFinishNextRacer(int lane) throws RaceException {
        //Does nothing as there is not a list of running racers to DNF from
        //There is no way to discern what person DNFs.
        throw new RaceException("Cannot DNF for GRP event");
    }

    /**
     * Sets the bib number for the next *placeholder* racer that has finished.
     * @param racerNumber the bib number that will be set.
     * @throws RaceException when there is not a racer to be marked.
     */
    //TODO: Check to ensure we are not marking two racers with the same numbe!
    public void markNextRacer(int racerNumber) throws RaceException {
        LinkedList<Racer> linkedList = (LinkedList<Racer>)this.finishedRacers;
        if (this.nextRacerToMarkIndex < this.finishedRacers.size()) {
            Racer racer = linkedList.get(this.nextRacerToMarkIndex);

            String logString = "Marked " + racer.toString() + " as ";

            //Set the new number.
            racer.setNumber(racerNumber);
            this.nextRacerToMarkIndex++;

            logString += racer.toString();

            this.log.add(logString);
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


    public static class TestRunManager {
        private GRPRunManager runManager;

        private int racerNumber;

        private ChronoTime time1, time2, time3;

        public TestRunManager() throws InvalidTimeException {
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
            this.runManager.startNext(this.time1, 1);

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

            //Lane must be 1 for GRP.
            this.runManager.finishNext(this.time1, 1);

            //Finished racers should have 1 racers
            assertEquals(1 ,this.runManager.finishedRacers.size());
            Racer racer = this.runManager.finishedRacers.peek();

            assertTrue(racer != null);

            //Number will be -1 because it is a placeholder.
            assertEquals(-1, racer.getNumber());
        }

        /**
         * Tests that cancelling a running racer will do nothing.
         */
        @Test(expected = RaceException.class)
        public void testCancelRunningRacer() throws RaceException {
            //Finished racers should have 0 racers
            assertEquals(0,this.runManager.finishedRacers.size());

            //Lane should be ignored for GRP.
            this.runManager.cancelNextRacer(1);
        }

        /**
         * Tests that DNFing a racer will do nothing.
         */
        @Test(expected = RaceException.class)
        public void testDNFRunningRacer() throws RaceException {
            //Finished racers should have 0 racers
            assertEquals(0,this.runManager.finishedRacers.size());

            //Lane should be ignored for GRP.
            this.runManager.cancelNextRacer(1);

            //Finished racers should have 0 racers
            assertEquals(0,this.runManager.finishedRacers.size());
        }

    }
}
