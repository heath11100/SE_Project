package Tests.whiteBox.Run;

import ChronoTimer.ChronoTime;
import ChronoTimer.Racer;
import ChronoTimer.Run;
import Exceptions.InvalidTimeException;
import junit.framework.TestCase;

/**
 * @author vangrol2
 */
public class TestDNF extends TestCase {
	private Run run;
	private ChronoTime t1,t2,t3,t4,t5,t6;
	
	@Override
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
	public void testRunNotStarted(){
		
	}
	public void testNoRacersInRun(){
		
	}
	public void testNoRunningRacers(){
		
	}
	public void testInvalidLane(){
		
	}
	public void testDnfIND(){
		
	}
	public void testDnfPARIND(){
		
	}
	
	public void testDnfPARGRP(){
		
	}
	
	public void testDnfGRP(){
		//should do nothing
	}
	
	private void ensureDNF(int racerNumber){
		Racer racer = null;
		for (Racer r: run.getAllRacers())
			if (r.getNumber() == racerNumber)
				{racer = r;break;}
		assertTrue(racer.getStatus() == Racer.Status.DNF);
	}
	
	private void ensureNonDNF(int racerNumber){
		Racer racer = null;
		for (Racer r: run.getAllRacers())
			if (r.getNumber() == racerNumber)
				{racer = r;break;}
		assertTrue(racer.getStatus() != Racer.Status.DNF);
	}
}
