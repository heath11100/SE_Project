package Tests.whiteBox.Run;

import ChronoTimer.*;
import Exceptions.*;
import junit.framework.TestCase;

public class TestRun extends TestCase {
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
	 * Tests to ensure that the constructor for Run(EventType eventType) properly sets the event type.
	 */
	public void testRunInitializerIND() {
		run = new Run(Run.EventType.IND);
		
		assertEquals(run.getEventType(), Run.EventType.IND);
	}
	
	/**
	 * Tests to ensure that the default constructor Run() properly sets the event type as IND.
	 */
	public void testRunDefaultInitializer() {
		run = new Run();
		
		assertEquals(run.getEventType(), Run.EventType.IND);
	}
	
	/**
	 * Ensures that the log for run is not null.
	 */
	public void testLogNotNull() {
		assertTrue(run.getLog() != null);
	}
	
	
	
	//TEST Set Event Type
	public void testEventTypeIND() {
		try {
			run.setEventType("IND");
			
			assertEquals(run.getEventType(), Run.EventType.IND);
		} catch (RaceException e) {
			fail("Should not fail for IND type.");
		}
	}
	
	public void testEventTypePARIND() {
		try {
			run.setEventType("PARIND");
			
			assertEquals(run.getEventType(), Run.EventType.PARIND);
		} catch (RaceException e) {
			fail("Should not fail for PARIND type.");
		}
	}

	public void testEventTypeGRP() {
		try {
			run.setEventType("GRP");

			assertEquals(run.getEventType(), Run.EventType.GRP);
		} catch (RaceException e) {
			fail("Should not fail for GRP type.");
		}
	}
	
	public void testInvalidType() {
		try {
			run.setEventType("INDPAR");
			fail("Should fail for INDPAR type.");
		} catch (Exception e) {
			assertTrue(e instanceof RaceException);
		}
	}
	
	public void testEventTypeChangeAfterRacerStart() {	
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
			run.queueRacer(racerNumber);
			
		} catch (RaceException e) {
			fail("Queueing racerNumber should Not fail in this instance.");
		}
		
		try {
			run.setEventType("PARIND");	
		} catch (RaceException e) {
			fail("Should not fail for queued racer.");
		}
		
		
		try {
			run.startNextRacer(time1, laneNumber);
		} catch (InvalidTimeException | RaceException e) {
			fail("Starting next racer should NOT fail in this instance.");
		}
		
		try {
			run.setEventType("IND");	
			fail("Should fail for queued racer.");
		} catch (RaceException e) { }
	}
	
	/**
	 * Successfully queues a racer (with raceNumber, in lane 1)
	 */
	private void queueRacer() throws RaceException {
		run.newLane();
		run.queueRacer(racerNumber);
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
			run.queueRacer(racerNumber);

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
			run.queueRacer(racerNumber);
			
		} catch (RaceException e) {
			fail("Queueing next racer should NOT fail in this instance.");
		}
		
		//Run has a racer, but has NOT started.
		try {
			run.endRun(time1);
			assertTrue(run.hasStarted());
			assertTrue(run.hasEnded());

		} catch (InvalidTimeException e) {
			fail("InvalidTimeException should not be thrown.");
		} catch (RaceException e) {
			fail("Should not throw error because endTime and startTime are now the same.");
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
			run.queueRacer(racerNumber);

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
	
}
