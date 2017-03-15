package Tests;

import junit.framework.TestCase;
import ChronoTimer.*;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;

/**
 * Tests the specific implementation for IND type runs
 * - does nots include PARIND.
 * @author austinheath
 */
public class TestRun_IND extends TestCase {
	private Run run;
	
	private int racerNumber;
	
	ChronoTime time1, time2, time3;
	
	@Override 
	public void setUp() throws InvalidTimeException {
		run = new Run();
		
		racerNumber = 1234;
		
		time1 = new ChronoTime(0,0,0,0);
		time2 = new ChronoTime(1,0,0,0);
		time3 = new ChronoTime(2,0,0,0);
	}
	
	/**
	 * Successfully queues a racer (with raceNumber, in lane 1)
	 */
	private void queueRacer() throws RaceException {
		run.newLane();
		run.queueRacer(racerNumber, 1);
	}
	
	/**
	 * Starts one racer in lane 1 with racerNumber.
	 * @throws RaceException
	 */
	private void startRacer() throws RaceException {
		queueRacer();
		try {
			run.startNextRacer(time1, 1);
		} catch (InvalidTimeException e1) {
			fail("Time shoudl not be invalid in this instance.");
		}
	}
	
	
	//MARK: Has Ended Testing.
	/**
	 * Tests that hasEnded returns false right after the Run is instantiated.
	 */
	public void testHasEndedAfterInstantiation() {
		assertFalse(run.hasEnded());
	}
	
	/**
	 * Tests that hasEnded returns false after the first racer starts.
	 */
	public void testHasEndedAfterRacerStarts() {
		try {
			startRacer();
		} catch (RaceException e) {
			fail("Should not fail in this instance.");
		}
		
		assertFalse(run.hasEnded());
	}
	
	/**
	 * Tests that hasEnded returns false after the first racer ends.
	 */
	public void testHasEndedAfterFirstRacerEnds()  {
		try {
			startRacer();
		} catch (RaceException e) {
			fail("Should not fail in this instance.");
		}
		
		assertFalse(run.hasEnded());
	}
	
	/**
	 * Tests that hasEnded returns true after the race has ended.
	 */
	public void testHasEndedAfterEndRun() {
		try {
			startRacer();
		} catch (RaceException e) {
			fail("Should not fail in this instance.");
		}
		
		try {
			run.endRun(time2);
		} catch (Exception e) {
			fail("Should not fail in this instance.");
		}
		
		assertTrue(run.hasEnded());
	}
	
	//MARK: Has Started Testing.
	/**
	 * Tests that hasStarted returns false right after the Run is instantiated.
	 */
	public void testHasStartedAfterInstantiation() {
		assertFalse(run.hasStarted());
	}
	
	/**
	 * Tests that hasStarted returns false after queuing a racer.
	 */
	public void testHasStartedAfterQueuingRacer() {
		try {
			queueRacer();
		} catch (RaceException e) {
			fail("Should not fail when queuing a racer in this instance.");
		}
		
		assertFalse(run.hasStarted());
	}
	
	/**
	 * Tests that hasStarted returns true after the first racer starts.
	 */
	public void testHasStartedAfterRacerStarts() {
		try {
			queueRacer();
		} catch (RaceException e) {
			fail("Should not fail when queuing a racer in this instance.");
		}
		
		try {
			run.startNextRacer(time1, 1);
		} catch (InvalidTimeException e) {
			fail("Time shoudl not be invalid in this instance.");

		} catch (RaceException e) {
			fail("Starting racer should not fail in this instance.");
		}
		
		assertTrue(run.hasStarted());
	}
	
	
	//MARK: Test End Run
	
	public void testEndRunInvalidTime() {
		int laneNumber = -1;
		try {
			laneNumber = run.newLane();
		} catch (RaceException e) {
			fail("Could not create a new lane for run");
		}
		
		if (laneNumber == -1) {
			fail("Lane number was not properly initialized");
		}
		
		try {
			run.queueRacer(racerNumber, laneNumber);

			run.startNextRacer(time2, laneNumber);
			
		} catch (InvalidTimeException | RaceException e) {
			fail("Starting next racer should NOT fail in this instance.");
		}
		
		//Run has now started with startTime time2.
		//Test a bad endRun time.
		try {
			run.endRun(time1);
			
			fail("Should not pass when time is before the start time");
		} catch (InvalidTimeException e) {
			assertTrue("Time should fail because it is before the start time.",true);
		} catch (RaceException e) {
			fail("RaceException should not be thrown.");
		}
	}
	
	public void testEndRunWithNotStartedRun() {
		int laneNumber = -1;
		try {
			laneNumber = run.newLane();
		} catch (RaceException e) {
			fail("Could not create a new lane for run");
		}
		
		if (laneNumber == -1) {
			fail("Lane number was not properly initialized");
		}
		
		try {
			run.queueRacer(racerNumber, laneNumber);
			
		} catch (RaceException e) {
			fail("Queueing next racer should NOT fail in this instance.");
		}
		
		//Run has a racer, but has NOT started.
		try {
			run.endRun(time1);
			
			fail("Should not pass when run has not started.");
		} catch (InvalidTimeException e) {
			fail("InvalidTimeException should not be thrown.");
		} catch (RaceException e) {
			assertTrue("Should fail because run has not started.",true);
		}
	}
	
	public void testEndRunAfterRunEnd() {
		int laneNumber = -1;
		try {
			laneNumber = run.newLane();
		} catch (RaceException e) {
			fail("Could not create a new lane for run");
		}
		
		if (laneNumber == -1) {
			fail("Lane number was not properly initialized");
		}
		
		try {
			run.queueRacer(racerNumber, laneNumber);

			run.startNextRacer(time1, laneNumber);
			
		} catch (InvalidTimeException | RaceException e) {
			fail("Starting next racer should NOT fail in this instance.");
		}
		
		//Run has now started with startTime time2.
		//Test ending run after run has ended
		try {
			run.endRun(time2);
			
			run.endRun(time3);
			
			fail("Should not pass when time is before the start time");
		} catch (InvalidTimeException e) {
			fail("InvalidTimeException should not be thrown.");
		} catch (RaceException e) {
			assertTrue("Should fail because run has already ended.",true);
		}
	}
	
	//Mark: Lane Testing
	/**
	 * Tests creating a single lane with IND.
	 */
	public void testCreateFirstLane() {
		try {
			run.newLane();
			
		} catch (RaceException e) {
			fail("Should not throw error on first lane creation");
		}
	}
	
	/**
	 * Tests to ensure failure when creating multiple lanes with IND.
	 */
	public void testMultipleLanes() {
		int laneCount = 0;
		try {
			run.newLane();
			laneCount++;
			
			run.newLane();
			fail("Should not reach this point, should fail at the newLane directly above");
			
		} catch (RaceException e) {
			if (laneCount == 0) {
				fail("Should not throw error on first lane creation");
			} //else it is not an error, because IND can only support 1 lane.
		}
	}
	
	/**
	 * Tests to ensure that upon changing eventType from PARIND to IND that all but one lane is deleted.
	 */
	public void testLaneDeletionAfterEventTypeChange() {
		int laneCount = -1;
		boolean isINDType = false;
		try {
			run.setEventType("PARIND");
			run.newLane();
			run.newLane();
			
			laneCount = 2; //PARIND supports multiple lanes.
						
			run.setEventType("IND");
			
			//Event Type should now be IND.
			isINDType = true;
			
			run.newLane();
			//Creating a newLane should fail because run should have kept
			// one lane after the event type switch.
			fail("Should not reach this point, should fail at the newLane directly above");
			
		} catch (RaceException e) {
			if (laneCount == 2 && !isINDType) {
				fail("Should not throw error yet.");
			} //else it is not an error, because IND can only support 1 lane.
		}
	}
}
