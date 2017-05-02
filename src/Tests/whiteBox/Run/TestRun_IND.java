package Tests.whiteBox.Run;

import junit.framework.TestCase;
import ChronoTimer.*;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.fail;

/**
 * Tests the specific implementation for IND type runs
 * - does nots include PARIND.
 * @author austinheath
 */
public class TestRun_IND {
	private Run run;
	
	private int racerNumber;
	
	ChronoTime time1, time2, time3;
	
	@Before
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
	@Test
	public void testCreateFirstLane() {
	}
	
	/**
	 * Tests to ensure failure when creating multiple lanes with IND.
	 */
	@Test
	public void testMultipleLanes() {
	}
	
	/**
	 * Tests to ensure that upon changing eventType from PARIND to IND that all but one lane is deleted.
	 */
	@Test
	public void testLaneDeletionAfterEventTypeChange() {
		int laneCount = -1;
		boolean isINDType = false;
	}
}
