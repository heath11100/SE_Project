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
	
	/**
	 * Successfully queues a racer (with raceNumber, in lane 1)
	 */
	private void queueRacer() throws RaceException {
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
}
