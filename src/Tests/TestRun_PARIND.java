package Tests;

import junit.framework.TestCase;
import ChronoTimer.*;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;

/**
 * Tests the specific implementation for PARIND type runs
 * - does not include PARIND.
 * @author austinheath
 */
public class TestRun_PARIND extends TestCase {
	private Run run;
	
	private int racerNumber1, racerNumber2, racerNumber3, racerNumber4, racerNumber5;
	
	ChronoTime time1, time2, time3, time4;
	
	@Override 
	public void setUp() throws InvalidTimeException {
		run = new Run(Run.EventType.PARIND);
		
		racerNumber1 = 1234;
		racerNumber2 = 2345;
		racerNumber3 = 3456;
		racerNumber4 = 4567;
		racerNumber5 = 5678;
		
		time1 = new ChronoTime(0,0,0,0);
		time2 = new ChronoTime(1,0,0,0);
		time3 = new ChronoTime(2,0,0,0);
		time4 = new ChronoTime(3,0,0,0);
	}
	
	
	//Mark: Lane Testing
	/**
	 * Tests creating a single lane with PARIND.
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
	public void testMultipleLanes_MidCapactiy() {		
		try {
			int laneNumber;
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 1);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 2);

			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 3);
						
		} catch (RaceException e) {
			fail("This should not fail, not exceeding maximum lane capacity.");
		}
	}
	
	/**
	 * Ensures that multiple lanes can be added, up to capacity (8 lanes)
	 */
	public void testMultipleLanes_MaximumCapacity() {
		try {
			int laneNumber;
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 1);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 2);

			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 3);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 4);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 5);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 6);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 7);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 8);
						
		} catch (RaceException e) {
			fail("This should not fail, not exceeding maximum lane capacity.");
		}
	}
	
	public void testMultipleLanes_ExceedingMaximumCapacity() {
		int laneNumber = 0;

		try {
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 1);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 2);

			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 3);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 4);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 5);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 6);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 7);
			
			laneNumber = run.newLane();
			
			assertEquals(laneNumber, 8);
			
			laneNumber = run.newLane();
			
			fail("Should not be able to meet 9 lanes.");
						
		} catch (RaceException e) {
			assertTrue(laneNumber > 0 && laneNumber < 9);
		}
	}
	
	/**
	 * Test Running multiple racers with
	 */
	
	public void testFinishingRacers() {
		//Add new lane.
		try {
			run.newLane();
		} catch (RaceException e) {
			fail("Should not fail in this instance.");
		}
		
		//Add racer 1.
		try {
			run.queueRacer(racerNumber1);
		} catch (RaceException e) {
			fail("Should not fail in this instance.");
		}
		
		//Add racer 2.
		try {
			run.queueRacer(racerNumber2);
		} catch (RaceException e) {
			fail("Should not fail in this instance.");
		}
		
		//Add new lane.
		try {
			run.newLane();
		} catch (RaceException e) {
			fail("Should not fail in this instance.");
		}
		
		//Add new lane.
		try {
			run.newLane();
		} catch (RaceException e) {
			fail("Should not fail in this instance.");
		}
		
		//Add racer 3.
		try {
			run.queueRacer(racerNumber3);
		} catch (RaceException e) {
			fail("Should not fail in this instance.");
		}

		//Add racer 4.
		try {
			run.queueRacer(racerNumber4);
		} catch (RaceException e) {
			fail("Should not fail in this instance.");
		}
		
		//Start racer 1, lane 1
		try {
			run.startNextRacer(time1, 1);
		} catch (Exception e) {
			fail("Should not fail in this instance.");
		}
		
		//Start racer 2, lane 3
		try {
			run.startNextRacer(time2, 3);
		} catch (Exception e) {
			fail("Should not fail in this instance.");
		}
		
		//Finish racer 1, lane 1
		try {
			run.startNextRacer(time2, 3);
			run.finishNextRacer(time3, 1);
		} catch (Exception e) {
			fail("Should not fail in this instance.");
		}
		
		//Add racer 5.
		try {
			run.queueRacer(racerNumber5);
		} catch (RaceException e) {
			fail("Should not fail in this instance.");
		}
		
		//Start racer 3, lane 2
		try {
			run.startNextRacer(time4, 2);
		} catch (Exception e) {
			fail("Should not fail in this instance.");
		}
	}
}
