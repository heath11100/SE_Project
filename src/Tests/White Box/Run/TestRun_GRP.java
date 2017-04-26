import ChronoTimer.ChronoTime;
import ChronoTimer.Run;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;

/**
 * Tests the specific implementation for IND type runs
 * - does nots include PARIND.
 * @author austinheath
 */
public class TestRun_GRP {
	private Run run;
	
	ChronoTime time1, time2, time3;

	@Before
	public void setUp() throws InvalidTimeException, RaceException {
		run = new Run();
		run.setEventType("GRP");
		
		time1 = new ChronoTime(0,0,0,0);
		time2 = new ChronoTime(1,0,0,0);
		time3 = new ChronoTime(2,0,0,0);
	}


	@Test(expected = RaceException.class)
	public void testCreateLane() throws RaceException {
		//Attempt to create a new lane
		//Should fail, because we don't have "lanes" for GRP.
		run.newLane();
	}

	@Test(expected = RaceException.class)
	public void testQueueRacer() throws RaceException{
		//Attempt to Queue a racer (which is not allowed for GRP)
		run.queueRacer(123);
	}


	//Test Start
	@Test
	public void testStartRun() throws InvalidTimeException, RaceException {
		run.startNextRacer(time1, 0);

		assertTrue(run.hasStarted());
	}

	@Test(expected = RaceException.class)
	public void testStartRunAfterStart() throws InvalidTimeException, RaceException {
		run.startNextRacer(time1, 0);

		run.startNextRacer(time2, 0);
	}

	@Test(expected = RaceException.class)
	public void testStartRunAfterEnd() throws InvalidTimeException, RaceException {
		run.startNextRacer(time1, 0);

		run.endRun(time2);

		run.startNextRacer(time3, 0);
	}


	//Test Finish
	@Test
	//Tests that you can finish multiple racers, even though they were not "queued"
	public void testFinishRun()  throws InvalidTimeException, RaceException {
		run.startNextRacer(time1, 0);

		for (int i = 0; i < 50; i++) {
			run.finishNextRacer(ChronoTime.now(), 0);
		}
	}

	@Test(expected = RaceException.class)
	public void testFinishAfterEnd() throws InvalidTimeException, RaceException {
		run.startNextRacer(time1, 0);

		run.endRun(time2);

		run.finishNextRacer(time3, 0);
	}

	@Test(expected = RaceException.class)
	public void testFinishOverMaximum() throws InvalidTimeException, RaceException {
		run.startNextRacer(time1, 0);

		for (int i = 0; i < 10_001; i++) {
			run.finishNextRacer(ChronoTime.now(), 0);
		}
	}


	//Test Marking
	@Test
	public void testMarkingFinishedRacers() throws InvalidTimeException, RaceException {
		run.startNextRacer(time1, 0);

		for (int i = 0; i < 50; i++) {
			run.finishNextRacer(ChronoTime.now(), 0);
		}

		//There should be 50 racers to mark.
		int racerNumber = 1;
		for (int i = 0; i < 50; i++) {
			run.queueRacer(racerNumber);
			racerNumber++;
		}
	}

	@Test(expected = RaceException.class)
	public void testMarkingRacersBeforeFinish() throws InvalidTimeException, RaceException {
		run.startNextRacer(time1, 0);

		//There should be 50 racers to mark.
		int racerNumber = 1;
		for (int i = 0; i < 50; i++) {
			run.queueRacer(racerNumber);
			racerNumber++;
		}
	}

	@Test(expected = RaceException.class)
	public void testMarkingMoreThanFinished() throws InvalidTimeException, RaceException {
		run.startNextRacer(time1, 0);

		for (int i = 0; i < 50; i++) {
			run.finishNextRacer(ChronoTime.now(), 0);
		}

		//There should be 50 racers to mark.
		//Error should be thrown after 50 have been marked.
		int racerNumber = 1;
		for (int i = 0; i < 55; i++) {
			run.queueRacer(racerNumber);
			racerNumber++;
		}
	}
}
