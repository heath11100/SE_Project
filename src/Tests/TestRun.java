package Tests;

import ChronoTimer.*;
import Exceptions.*;
import junit.framework.TestCase;

public class TestRun extends TestCase {
	private Run runIND;
	
	private int racerNumber1;
	
	ChronoTime time1, time2, time3;
	
	@Override 
	public void setUp() throws InvalidTimeException {
		runIND = new Run();
		
		racerNumber1 = 1234;
		
		time1 = new ChronoTime(0,0,0,0);
		time2 = new ChronoTime(1,0,0,0);
		time3 = new ChronoTime(2,0,0,0);
	}
	
	/**
	 * Tests to ensure that the constructor for Run(EventType eventType) properly sets the event type.
	 */
	public void testRunInitializerIND() {
		runIND = new Run(Run.EventType.IND);
		
		assertEquals(runIND.getEventType(), Run.EventType.IND);
	}
	
	/**
	 * Tests to ensure that the default constructor Run() properly sets the event type as IND.
	 */
	public void testRunDefaultInitializer() {
		runIND = new Run();
		
		assertEquals(runIND.getEventType(), Run.EventType.IND);
	}
	
	/**
	 * Ensures that the log for run is not null.
	 */
	public void testLogNotNull() {
		assertTrue(runIND.getLog() != null);
	}
	
	
	
	//TEST Set Event Type
	public void testEventTypeIND() {
		try {
			runIND.setEventType("IND");
			
			assertEquals(runIND.getEventType(), Run.EventType.IND);
		} catch (RaceException e) {
			fail("Should not fail for IND type.");
		}
	}
	
	public void testEventTypePARIND() {
		try {
			runIND.setEventType("PARIND");
			
			assertEquals(runIND.getEventType(), Run.EventType.PARIND);
		} catch (RaceException e) {
			fail("Should not fail for PARIND type.");
		}
	}
	
	public void testInvalidType() {
		try {
			runIND.setEventType("INDPAR");
			fail("Should fail for INDPAR type.");
		} catch (Exception e) {
			assertTrue(e instanceof RaceException);
		}
	}
	
	public void testEventTypeChangeAfterRacerStart() {	
		int laneNumber = -1;
		try {
			laneNumber = runIND.newLane();
		} catch (RaceException e) {
			fail("Could not create a new lane for runIND");
		}
		
		if (laneNumber == -1) {
			fail("Lane number was not properly initialized");
		}
		
		try {
			runIND.queueRacer(racerNumber1, laneNumber);
			
		} catch (RaceException e) {
			fail("Queueing racerNumber1 should Not fail in this instance.");
		}
		
		try {
			runIND.setEventType("PARIND");	
			fail("Should fail for queued racer.");
		} catch (RaceException e) {
		}
		
		
		try {
			runIND.startNextRacer(time1, laneNumber);
		} catch (InvalidTimeException | RaceException e) {
			fail("Starting next racer should NOT fail in this instance.");
		}
		
		try {
			runIND.setEventType("IND");	
			fail("Should fail for queued racer.");
		} catch (RaceException e) { }
	}
	
}
