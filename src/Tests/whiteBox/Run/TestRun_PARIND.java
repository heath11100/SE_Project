package Tests.whiteBox.Run;

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

	}
	
	/**
	 * Tests to ensure failure when creating multiple lanes with IND.
	 */
	public void testMultipleLanes_MidCapactiy() {
	}
	
	/**
	 * Ensures that multiple lanes can be added, up to capacity (8 lanes)
	 */
	public void testMultipleLanes_MaximumCapacity() {

	}
	
	public void testMultipleLanes_ExceedingMaximumCapacity() {

	}
	
	/**
	 * Test Running multiple racers with
	 */
	
	public void testFinishingRacers() {

		
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
