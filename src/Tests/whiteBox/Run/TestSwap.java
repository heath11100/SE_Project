package Tests.whiteBox.Run;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import ChronoTimer.ChronoTime;
import ChronoTimer.Racer;
import ChronoTimer.Run;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;

/**
 * @author vangrol2
 */
public class TestSwap{
	private Run run;
	private ChronoTime t1,t2,t3,t5;
	
	@Before
	public void setUp() throws InvalidTimeException{
        try {
                assert 1/0 == 42 : "OK";
                System.err.println("Assertions must be enabled to use this test suite.");
                System.err.println("In Eclipse: add -ea in the VM Arguments box under Run>Run Configurations>Arguments");
                assertFalse("Assertions must be -ea enabled in the Run Configuration>Arguments>VM Arguments",true);
        } catch (ArithmeticException ex) {}
        t1 = new ChronoTime("0:1:0.0");
        t2 = new ChronoTime("0:2:0.0");
        t3 = new ChronoTime("0:3:0.0");
        t5 = new ChronoTime("0:5:0.0");
        run = new Run();
	}
	
	@Test(expected = RaceException.class)
	public void testRunNotStarted() throws RaceException{
		run.queueRacer(1);
		run.swap();
	}
	
	@Test(expected = RaceException.class)
	public void testNoRacersInRun() throws RaceException{
		run.swap();
	}
	
	@Test(expected = RaceException.class)
	public void testNoRunningRacers() throws RaceException, InvalidTimeException{
		run.queueRacer(1);
		run.startNextRacer(t1, 1);
		run.finishNextRacer(t2, 1);
		run.swap();
	}
	
	@Test(expected = RaceException.class)
	public void testInvalidLane() throws RaceException, InvalidTimeException{
		run.queueRacer(1);
		run.startNextRacer(t1, 1);
		run.swap();
	}
	
	@Test(expected = RaceException.class)
	public void testNotEnoughRacersToSwap() throws RaceException, InvalidTimeException{
		run.queueRacer(1);
		run.startNextRacer(t1, 1);
		run.swap();
	}

	@Test
	public void testSwapIND() throws RaceException, InvalidTimeException{
		run.queueRacer(1);
		run.queueRacer(2);
		run.queueRacer(3);
		
		//start racers 1 and 2
		run.startNextRacer(t1, 1);
		run.startNextRacer(t1, 1);
		
		//swap so 2 is in front, and finish
		run.swap();
		run.finishNextRacer(t2, 1);
		ensureFinished(2);
		ensureNonFinished(1);
		
		//start racer 3
		run.startNextRacer(t3, 1);
		
		//swap so 3 is in front
		// racer 1 must be really slow...
		run.swap();
		//swap again, 1 is getting faster!
		run.swap();
		run.finishNextRacer(t5, 1);
		ensureFinished(1);
		ensureNonFinished(3);
	}

	@Test(expected = RaceException.class)
	public void testSwapPARIND() throws RaceException, InvalidTimeException{
		run.setEventType("PARIND");
		run.queueRacer(1);
		run.queueRacer(2);
		run.queueRacer(3);
		run.startNextRacer(t1, 1);
		//won't be allowed
		run.swap();
	}
	
	@Test(expected = RaceException.class)
	public void testSwapGRP() throws RaceException, InvalidTimeException{
		run.setEventType("GRP");
		run.startNextRacer(t1, 1);
		//won't be allowed
		run.swap();
	}
	
	@Test(expected = RaceException.class)
	public void testSwapPARGRP() throws RaceException, InvalidTimeException{
		run.setEventType("PARGRP");
		run.queueRacer(1);
		run.queueRacer(2);
		run.queueRacer(3);
		run.startNextRacer(t1, 1);
		//won't be allowed
		run.swap();
	}
	
	private void ensureFinished(int racerNumber){
		Racer racer = null;
		for (Racer r: run.getAllRacers())
			if (r.getNumber() == racerNumber)
				{racer = r;break;}
		assertTrue(racer.getStatus() == Racer.Status.FINISHED);
		assert racer.getEndTime() != null;
	}
	
	private void ensureNonFinished(int racerNumber){
		Racer racer = null;
		for (Racer r: run.getAllRacers())
			if (r.getNumber() == racerNumber)
				{racer = r;break;}
		assertTrue(racer.getStatus() != Racer.Status.FINISHED);
		assert racer.getEndTime() == null;
	}
}
