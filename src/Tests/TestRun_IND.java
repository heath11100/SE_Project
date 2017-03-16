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
