package Tests.whiteBox.ChronoTrigger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import ChronoTimer.ChronoTime;
import ChronoTimer.ChronoTrigger;
import ChronoTimer.Printer;
import Exceptions.InvalidTimeException;
import junit.framework.TestCase;

/**
 * @author vangrol2
 */
public class TestPrint extends TestCase{
	private ChronoTrigger ct;
	private ChronoTime t1,t2,t3,t4,t5,t6;
	private Printer dump;
	
	@Override
	public void setUp() throws InvalidTimeException{
        try {
                assert 1/0 == 42 : "OK";
                System.err.println("Assertions must be enabled to use this test suite.");
                System.err.println("In Eclipse: add -ea in the VM Arguments box under Run>Run Configurations>Arguments");
                assertFalse("Assertions must be -ea enabled in the Run Configuration>Arguments>VM Arguments",true);
        } catch (ArithmeticException ex) {}
        
        // We will route all irrelevant output to a print dump.
        dump = new Printer(new PrintStream(new ByteArrayOutputStream()));
        
        t1 = new ChronoTime("1:0:0.0");
        t2 = new ChronoTime("2:0:0.0");
        t3 = new ChronoTime("3:0:0.0");
        t4 = new ChronoTime("4:0:0.0");
        t5 = new ChronoTime("5:0:0.0");
        t6 = new ChronoTime("6:0:0.0");
        ct = new ChronoTrigger(t1);
        ct.setPrinter(dump);
        ct.powerOn(t2);
	}
	
	public void testNoRuns(){
		System.out.println("\nTry printing when no runs exist:");
		doPrint();
	}
	
	public void testNoCurrentRun(){
		System.out.println("\nTry printing when no current run exists (should print last run):");
		ct.newRun(t3);
		ct.addRacer(t3, 4);
		ct.toggle(t3, 1);ct.toggle(t3, 2);
		ct.triggerSensor(t3, 1);ct.triggerSensor(t4, 2);
		ct.finRun(t5);
		doPrint();
	}
	
	public void testUnfinishedRun(){
		System.out.println("\nTry printing when run isn't finished:");
		ct.newRun(t3);
		ct.addRacer(t3, 4);
		ct.toggle(t3, 1);ct.toggle(t3, 2);
		ct.triggerSensor(t3, 1);
		doPrint();
	}
	
	public void testInvalidRunNumber(){
		ct.newRun(t3);
		ct.addRacer(t3, 4);
		ct.toggle(t3, 1);ct.toggle(t3, 2);
		ct.triggerSensor(t3, 1);ct.triggerSensor(t4, 2);
		ct.finRun(t5);
		System.out.println("\nTry printing with invalid run number (1):");
		doPrint(45);
		System.out.println("\nTry printing with invalid run number (2):");
		doPrint(-1);
		System.out.println("\nTry printing with invalid run number (3):");
		doPrint(0);
	}
	
	public void testInvalidPrinter(){
		ct.newRun(t3);
		ct.addRacer(t3, 4);
		ct.toggle(t3, 1);ct.toggle(t3, 2);
		ct.triggerSensor(t3, 1);ct.triggerSensor(t4, 2);
		ct.finRun(t5);
		ct.setPrinter(null);
		ct.printRun(t6);
	}
	
	public void testExportIND(){
		// A Run is instantiated, 3 Racers are queued
	}
	
	public void testExportPARIND(){
		// A Run is instantiated, 3 Racers are queued
	}
	
	public void testExportGRP(){
		// A Run is instantiated, 3 Racers are queued
	}
	
	public void testExportPARGRP(){
		// A Run is instantiated, 3 Racers are queued
	}
	
	private void doPrint(){
		ct.setPrinter(new Printer(System.out));
		ct.printRun(t6);
		ct.setPrinter(dump);}
	
	private void doPrint(int num){
		ct.setPrinter(new Printer(System.out));
		ct.printRun(t6,num);
		ct.setPrinter(dump);}
}
