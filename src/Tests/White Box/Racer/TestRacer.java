import ChronoTimer.ChronoTime;
import ChronoTimer.Racer;
import Exceptions.InvalidTimeException;

import static org.junit.Assert.*;

import java.lang.IllegalArgumentException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

//NB: I am assuming we have a constructor that takes a bib number

public class TestRacer {
	private Racer racer, dummyRacer;
	private ChronoTime time1, time2, time3, zeroTime;

	@BeforeClass
	public static void classPreparation() {
		System.out.println("Prepared");
	}

	@Before
	public void setUp() throws Exception {
		racer = new Racer(1);
		dummyRacer = new Racer(-1);
		time1 = new ChronoTime(0,0,0,0);
		time2 = new ChronoTime(1,0,0,0);
		time3 = new ChronoTime(2,0,0,0);
		zeroTime = new ChronoTime(0, 0, 0, 0);
	}

	//MARK: Test Constructors
	@Test
	public void testConstructorNormalRacer() {
		//Racer should have number 1.
		assertEquals(1, racer.getNumber());
		//Status should default to QUEUED.
		assertEquals(Racer.Status.QUEUED, racer.getStatus());
	}

	@Test
	public void testConstructorDummyRacer() {
		//Racer should have number -1.
		assertEquals(-1, dummyRacer.getNumber());

		//Status should default to QUEUED.
		assertEquals(Racer.Status.QUEUED, dummyRacer.getStatus());
	}

	@Test
	public void testConstructorRacerMaximum() {
		racer = new Racer(9999);

		//Racer should have number 9999
		assertEquals(9999, racer.getNumber());

		//Status should default to QUEUED.
		assertEquals(Racer.Status.QUEUED, racer.getStatus());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNumber0() {
		new Racer(0);
	}

	@Test
	public void testConstructorDummyRacerMaximum() {
		dummyRacer = new Racer(-9999);

		//Racer should have number -9999
		assertEquals(-9999, dummyRacer.getNumber());

		//Status should default to QUEUED.
		assertEquals(Racer.Status.QUEUED, dummyRacer.getStatus());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructorInvalidNumberNegative() {
		new Racer(-10_000);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructorInvalidNumberPositive() {
		new Racer(10_000);
	}


	//Test Start
	@Test
	public void testSuccessfulStart() {
		assertEquals(null, racer.getStartTime());
		assertEquals(Racer.Status.QUEUED, racer.getStatus());

		racer.start(time1);

		assertEquals(time1, racer.getStartTime());
		assertEquals(Racer.Status.RACING, racer.getStatus());
	}

	@Test(expected = IllegalStateException.class)
	public void testDoubleStart() {
		//Starting a racer after the racer has already started.

		racer.start(time1);
		racer.start(time2);
	}

	@Test(expected = IllegalStateException.class)
	public void testStartAfterFinish() {
		//Start a racer after the racer has finished.
		racer.start(time1);
		try {
			racer.finish(time2);
		} catch (InvalidTimeException e) {
			fail("Finishing should not fail in this instance.");
		}
		racer.start(time1);
	}

	@Test(expected = IllegalStateException.class)
	public void testStartAfterDNF() {
		//Start a racer after the racer has DNFd
		racer.start(time1);
		racer.didNotFinish();
		racer.start(time1);
	}

	@Test
	public void testStartAfterCancel() {
		//Start the racer, cancel them, and start them again.
		racer.start(time1);
		racer.cancel();
		racer.start(time2);
	}


	//MARK: Finish
	@Test
	public void testSuccessfulFinish() {
		//The racer starts and finishes.
		racer.start(time1);
		try {
			racer.finish(time2);
		} catch (InvalidTimeException e) {
			fail("Finish should not fail in this instance.");
		}

		assertEquals(time1, racer.getStartTime());
		assertEquals(time2, racer.getEndTime());
		assertEquals(Racer.Status.FINISHED, racer.getStatus());
	}

	@Test(expected = InvalidTimeException.class)
	public void testFinishTimeBeforeStartTime() throws InvalidTimeException {
		//Racer start time is "after" the finish time
		racer.start(time2);
		racer.finish(time1);
	}

	@Test(expected = InvalidTimeException.class)
	public void testFinishTimeEqualsStartTime() throws InvalidTimeException {
		//Racer start time is "equal" the finish time
		racer.start(time1);
		racer.finish(time1);
	}

	@Test(expected = IllegalStateException.class)
	public void testFinishQueued() throws InvalidTimeException {
		//Finish a racer that is QUEUED (not racing)
		racer.finish(time1);
	}

	@Test(expected = IllegalStateException.class)
	public void testFinishFinishedRacer() throws InvalidTimeException {
		//Finish a racer and then finish them again.
		racer.start(time1);
		racer.finish(time2);
		racer.finish(time3);
	}

	@Test(expected = IllegalStateException.class)
	public void testFinishDNFRacer() throws InvalidTimeException {
		//Start racer, DNF them, and then finish them.
		racer.start(time1);
		racer.didNotFinish();
		racer.finish(time2);
	}

	//MARK: DNF
	@Test
	public void testSuccessfulDNF() {
		//The racer starts and DNF.
		racer.start(time1);
		racer.didNotFinish();

		assertEquals(time1, racer.getStartTime());
		assertEquals(null, racer.getEndTime());
		assertEquals(Racer.Status.DNF, racer.getStatus());
	}

	@Test(expected = IllegalStateException.class)
	public void testDNFQueued() throws InvalidTimeException {
		//DNF a racer that is QUEUED (not racing)
		racer.didNotFinish();
	}

	@Test(expected = IllegalStateException.class)
	public void testDNFdnfedRacer() throws InvalidTimeException {
		//start a racer and then dnf them twice.
		racer.start(time1);
		racer.didNotFinish();
		racer.didNotFinish();
	}

	@Test(expected = IllegalStateException.class)
	public void testDNFFinish() throws InvalidTimeException {
		//Start racer, DNF them, and then finish them.
		racer.start(time1);
		racer.finish(time2);
		racer.didNotFinish();
	}


	//MARK: Cancel
	@Test
	public void testSuccessfulCancel() {
		//Start racer and then cancel them.
		racer.start(time1);
		racer.cancel();

		assertEquals(null, racer.getStartTime());
		assertEquals(null, racer.getEndTime());
		assertEquals(Racer.Status.QUEUED, racer.getStatus());
	}

	@Test(expected = IllegalStateException.class)
	public void testCancelQueued() {
		racer.cancel();
	}

	@Test(expected = IllegalStateException.class)
	public void testCancelFinish() {
		racer.start(time1);

		try {
			racer.finish(time2);
		} catch (InvalidTimeException e) {
			fail("finish should not fail in this instance.");
		}

		racer.cancel();
	}

	@Test(expected = IllegalStateException.class)
	public void testCancelDNF() {
		racer.start(time1);
		racer.didNotFinish();
		racer.cancel();
	}

	@Test(expected = IllegalStateException.class)
	public void testCancelCancel() {
		racer.start(time1);
		racer.cancel();
		racer.cancel();
	}


	//MARK: Get Elapsed Time
	@Test
	public void getTimeBeforeStart() throws InvalidTimeException {
		assertEquals(zeroTime, racer.getElapsedTime());
	}

	@Test
	public void getTimeAfterEnd() throws InvalidTimeException {
		ChronoTime elapsed = time2.elapsedSince(time1);

		racer.start(time1);
		racer.finish(time2);

		assertEquals(elapsed, racer.getElapsedTime());
	}
}
