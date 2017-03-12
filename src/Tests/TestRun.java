package Tests;

import ChronoTimer.*;
import Exceptions.*;
import junit.framework.TestCase;

public class TestRun extends TestCase {
	private Run runIND, runPARIND;
	
	private int racerNumber1, racerNumber2;
	
	ChronoTime time1, time2, time3;
	
	@Override 
	public void setUp() throws InvalidTimeException {
		runIND = new Run();
		runPARIND = new Run(Run.EventType.PARIND);
		
		racerNumber1 = 1234;
		racerNumber2 = 5678;
		
		time1 = new ChronoTime(0,0,0,0);
		time2 = new ChronoTime(1,0,0,0);
		time3 = new ChronoTime(2,0,0,0);
	}
	
	public void testHasStarted() {
		//Assert that runIND has not started (since no racer started)
		assertFalse(runIND.hasStarted());
		
		try {
			runIND.queueRacer(racerNumber1);
		} catch (RaceException e) {
			fail("Queueing racerNumber1 should Not fail in this instance.");
		}
		
		//Should not start when queueing racers.
		assertFalse(runIND.hasStarted());
		
		try {
			runIND.startNextRacer(time1);
		} catch (InvalidTimeException | RaceException e) {
			System.out.println(e.getMessage());
			fail("Starting next racer should NOT fail in this instance.");
		}
		
		//Run has started since first racer started.
		assertTrue(runIND.hasStarted());
	}
	
	public void testHasEnded() {
		//Assert that runIND has not started (since no racer started)
		assertFalse(runIND.hasEnded());
		
		try {
			runIND.queueRacer(racerNumber1);
		} catch (RaceException e) {
			fail("Queueing racerNumber1 should Not fail in this instance.");
		}
		
		//Should not end when queueing racers.
		assertFalse(runIND.hasEnded());
		
		try {
			runIND.startNextRacer(time1);
		} catch (InvalidTimeException | RaceException e) {
			fail("Starting next racer should NOT fail in this instance.");
		}
		
		//Should not end when starting a racer.
		assertFalse(runIND.hasEnded());
		
		try {
			runIND.finishNextRacer(time2);
		} catch (InvalidTimeException | RaceException e) {
			fail("Finishing next racer should NOT fail in this instance.");
		}
		
		//Run should not end when ending a racer, even if it is
		// the last racer.
		assertFalse(runIND.hasEnded());
		
		try {
			runIND.endRun(time3);
		} catch (RaceException | InvalidTimeException e) {
			fail("Ending run should NOT fail in this instance.");
		}
		
		assertTrue(runIND.hasEnded());
	}
	
	public void testHasRacerBegan() {
		//Assert that runIND hasRacerBegan() == false;
		assertFalse(runIND.hasRacerBegan());
		
		try {
			runIND.queueRacer(racerNumber1);
		} catch (RaceException e) {
			fail("Queueing racerNumber1 should Not fail in this instance.");
		}
		
		//Should not "hasRacerBegan" when queueing racers.
		assertFalse(runIND.hasRacerBegan());
		
		try {
			runIND.startNextRacer(time1);
		} catch (InvalidTimeException | RaceException e) {
			fail("Starting next racer should NOT fail in this instance.");
		}
		
		//Should hasracerbegan
		assertTrue(runIND.hasRacerBegan());
		
		try {
			runIND.finishNextRacer(time2);
		} catch (InvalidTimeException | RaceException e) {
			fail("Finishing next racer should NOT fail in this instance.");
		}
		
		assertTrue(runIND.hasRacerBegan());
		
		try {
			runIND.endRun(time3);
		} catch (RaceException | InvalidTimeException e) {
			fail("Ending run should NOT fail in this instance.");
		}
		
		assertTrue(runIND.hasRacerBegan());
	}
	
	//MARK: End Run Tests
	
	public void testEndRunInvalidTime() {
		try {
			runIND.queueRacer(racerNumber1);

			runIND.startNextRacer(time2);
			
		} catch (InvalidTimeException | RaceException e) {
			fail("Starting next racer should NOT fail in this instance.");
		}
		
		//Run has now started with startTime time2.
		//Test a bad endRun time.
		try {
			runIND.endRun(time1);
			
			fail("Should not pass when time is before the start time");
		} catch (InvalidTimeException e) {
			assertTrue("Time should fail because it is before the start time.",true);
		} catch (RaceException e) {
			fail("RaceException should not be thrown.");
		}
	}
	
	public void testEndRunWithNotStartedRun() {
		try {
			runIND.queueRacer(racerNumber1);
			
		} catch (RaceException e) {
			fail("Queueing next racer should NOT fail in this instance.");
		}
		
		//Run has a racer, but has NOT started.
		try {
			runIND.endRun(time1);
			
			fail("Should not pass when run has not started.");
		} catch (InvalidTimeException e) {
			fail("InvalidTimeException should not be thrown.");
		} catch (RaceException e) {
			assertTrue("Should fail because run has not started.",true);
		}
	}
	
	public void testEndRunAfterRunEnd() {
		try {
			runIND.queueRacer(racerNumber1);

			runIND.startNextRacer(time1);
			
		} catch (InvalidTimeException | RaceException e) {
			fail("Starting next racer should NOT fail in this instance.");
		}
		
		//Run has now started with startTime time2.
		//Test ending run after run has ended
		try {
			runIND.endRun(time2);
			
			runIND.endRun(time3);
			
			fail("Should not pass when time is before the start time");
		} catch (InvalidTimeException e) {
			fail("InvalidTimeException should not be thrown.");
		} catch (RaceException e) {
			assertTrue("Should fail because run has already ended.",true);
		}
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
	
	public void testEventTypeChangeAfterRacerStart() {		
		try {
			runIND.queueRacer(racerNumber1);
			
		} catch (RaceException e) {
			fail("Queueing racerNumber1 should Not fail in this instance.");
		}
		
		try {
			runIND.setEventType("PARIND");			
		} catch (RaceException e) {
			fail("Should not fail for queued racer.");
		}
		
		
		try {
			runIND.startNextRacer(time1);
		} catch (InvalidTimeException | RaceException e) {
			fail("Starting next racer should NOT fail in this instance.");
		}
		
		try {
			runIND.setEventType("IND");	
			fail("Should fail for queued racer.");
		} catch (RaceException e) { }
	}
	
}
