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
public class TestDNF {
	private Run run;
	private ChronoTime t1,t2;
	
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
        run = new Run();
	}
	
	@Test(expected = RaceException.class)
	public void testRunNotStarted() throws RaceException{
		run.queueRacer(1);
		run.didNotFinishNextRacer(1);
	}
	
	@Test(expected = RaceException.class)
	public void testNoRacersInRun() throws RaceException{
		run.didNotFinishNextRacer(1);
	}
	
	@Test(expected = RaceException.class)
	public void testNoRunningRacers() throws RaceException, InvalidTimeException{
		run.queueRacer(1);
		run.startNextRacer(t1, 1);
		run.finishNextRacer(t2, 1);
		run.didNotFinishNextRacer(1);
	}
	
	@Test(expected = RaceException.class)
	public void testInvalidLane() throws RaceException, InvalidTimeException{
		run.queueRacer(1);
		run.startNextRacer(t1, 1);
		run.finishNextRacer(t2, 1);
		run.didNotFinishNextRacer(78);
	}
	
	@Test
	public void testDnfIND() throws RaceException, InvalidTimeException{
		run.queueRacer(1);
		run.queueRacer(2);
		run.queueRacer(3);
		
		//start and dnf racer 1
		run.startNextRacer(t1, 1);
		run.didNotFinishNextRacer(1);
		ensureDNF(1);
		
		//start racers 2 and 3
		run.startNextRacer(t1, 1);
		run.startNextRacer(t1, 1);
		run.didNotFinishNextRacer(1);
		
		//dnf racer 2
		ensureDNF(2);
		ensureNonDNF(3);
		
		run.didNotFinishNextRacer(1);
		ensureDNF(3);
	}
	
	@Test
	public void testDnfPARIND() throws RaceException, InvalidTimeException{
		run.queueRacer(1);
		run.queueRacer(2);
		run.queueRacer(3);
		run.queueRacer(4);
		
		//start racers 1 and 2
		run.startNextRacer(t1, 1);
		run.startNextRacer(t1, 2);
		
		//dnf racer 1
		run.didNotFinishNextRacer(1);
		ensureDNF(1);
		ensureNonDNF(2);
		
		//start racers 3 and 4 in lane 1
		run.startNextRacer(t1, 1);
		
		//dnf racers 2 and 3
		run.didNotFinishNextRacer(2);
		run.didNotFinishNextRacer(1);
		ensureDNF(2);
		ensureDNF(3);
		ensureNonDNF(4);
	}
	
//	@Test
//	public void testDnfPARGRP() throws RaceException, InvalidTimeException{
//		run.setEventType("PARGRP");
//		run.queueRacer(1);
//		run.queueRacer(2);
//		run.queueRacer(3);
//		run.queueRacer(4);
//		run.queueRacer(5);
//		run.queueRacer(6);
//		run.queueRacer(7);
//		run.queueRacer(8);
//		run.startNextRacer(t1, 1);
//		run.didNotFinishNextRacer(1);
//		run.didNotFinishNextRacer(3);
//		run.didNotFinishNextRacer(8);
//		ensureDNF(1);
//		ensureNonDNF(2);
//		ensureDNF(3);
//		ensureNonDNF(4);
//		ensureNonDNF(5);
//		ensureNonDNF(6);
//		ensureNonDNF(7);
//		ensureDNF(8);
//	}
	
	@Test(expected = RaceException.class)
	public void testDnfGRP() throws RaceException, InvalidTimeException{
		run.setEventType("GRP");
		run.startNextRacer(t1, 1);
		//won't be allowed
		run.didNotFinishNextRacer(1);
	}
	
	private void ensureDNF(int racerNumber){
		Racer racer = null;
		for (Racer r: run.getAllRacers())
			if (r != null && r.getNumber() == racerNumber)
				{racer = r;break;}
		assertTrue(racer.getStatus() == Racer.Status.DNF);
		assert racer.getEndTime() == null;
	}
	
	private void ensureNonDNF(int racerNumber){
		Racer racer = null;
		for (Racer r: run.getAllRacers())
			if (r != null && r.getNumber() == racerNumber)
				{racer = r;break;}
		assertTrue(racer.getStatus() != Racer.Status.DNF);
	}
}
