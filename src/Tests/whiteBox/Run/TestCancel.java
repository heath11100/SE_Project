package Tests.whiteBox.Run;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.Before;

import ChronoTimer.ChronoTime;
import ChronoTimer.Racer;
import ChronoTimer.Run;
import Exceptions.InvalidTimeException;
import Exceptions.RaceException;

/**
 * @author vangrol2
 */
public class TestCancel {
	private Run run;
	private ChronoTime t1,t2,t3,t4,t5,t6;
	
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
        t4 = new ChronoTime("0:4:0.0");
        t5 = new ChronoTime("0:5:0.0");
        t6 = new ChronoTime("0:6:0.0");
        run = new Run();
	}
	
	@Test(expected = RaceException.class)
	public void testRunNotStarted() throws RaceException{
		run.queueRacer(1);
		run.cancelNextRacer(1);
	}
	
	@Test(expected = RaceException.class)
	public void testNoRacersInRun() throws RaceException{
		run.cancelNextRacer(1);
	}
	
	@Test(expected = RaceException.class)
	public void testNoRunningRacers() throws RaceException, InvalidTimeException{
		run.queueRacer(1);
		run.startNextRacer(t1, 1);
		run.finishNextRacer(t2, 1);
		run.cancelNextRacer(1);
	}
	
	@Test(expected = RaceException.class)
	public void testInvalidLane() throws RaceException, InvalidTimeException{
		run.setEventType("GRP");
		run.queueRacer(1);
		run.startNextRacer(t1, 1);
		run.cancelNextRacer(78);
	}
	
	@Test
	public void testCancelIND() throws RaceException, InvalidTimeException{
		run.queueRacer(1);
		run.queueRacer(2);
		run.queueRacer(3);
		
		run.startNextRacer(t1, 1);
		run.startNextRacer(t2, 2);
		ensureNonCancel(1);
		ensureNonCancel(2);
		
		run.cancelNextRacer(1);
		ensureCancel(2);
		ensureNonCancel(1);
		
		run.finishNextRacer(t2, 1);
		run.startNextRacer(t3, 1);
		run.cancelNextRacer(1);
		ensureCancel(2);
		
		run.startNextRacer(t4, 1);
		run.startNextRacer(t5, 1);
		run.cancelNextRacer(1);
		ensureCancel(3);
		ensureNonCancel(2);
		
		run.startNextRacer(t5, 1);
		run.cancelNextRacer(1);
		ensureCancel(3);
		ensureNonCancel(2);
	}
	
	@Test
	public void testCancelPARIND(){
		
	}
	
	@Test
	public void testCancelPARGRP(){
		
	}
	
	@Test
	public void testCancelGRP(){
		//should do nothing
	}
	
	private void ensureCancel(int racerNumber){
		Racer racer = null;
		for (Racer r: run.getAllRacers())
			if (r.getNumber() == racerNumber)
				{racer = r;break;}
		assertTrue(racer.getStatus() == Racer.Status.QUEUED);
	}
	
	private void ensureNonCancel(int racerNumber){
		Racer racer = null;
		for (Racer r: run.getAllRacers())
			if (r.getNumber() == racerNumber)
				{racer = r;break;}
		assertTrue(racer.getStatus() != Racer.Status.QUEUED);
	}
}
