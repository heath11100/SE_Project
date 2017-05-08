package Tests.whiteBox.ChronoTrigger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import ChronoTimer.ChronoTime;
import ChronoTimer.ChronoTrigger;
import ChronoTimer.Run;
import Exceptions.InvalidTimeException;
import junit.framework.TestCase;

/**
 * @author vangrol2
 */
public class TestExport extends TestCase{
	
	private ChronoTrigger ct;
	private ChronoTime t1,t2,t3,t4,t5,t6;
	
	@Override
	public void setUp() throws InvalidTimeException{
        try {
                assert 1/0 == 42 : "OK";
                System.err.println("Assertions must be enabled to use this test suite.");
                System.err.println("In Eclipse: add -ea in the VM Arguments box under Run>Run Configurations>Arguments");
                assertFalse("Assertions must be -ea enabled in the Run Configuration>Arguments>VM Arguments",true);
        } catch (ArithmeticException ex) {}
        t1 = new ChronoTime("1:0:0.0");
        t2 = new ChronoTime("2:0:0.0");
        t3 = new ChronoTime("3:0:0.0");
        t4 = new ChronoTime("4:0:0.0");
        t5 = new ChronoTime("5:0:0.0");
        t6 = new ChronoTime("6:0:0.0");
        ct = new ChronoTrigger(t1);
        ct.powerOn(t2);
        
        //delete run file if it exists
        try {Files.deleteIfExists(FileSystems.getDefault().getPath("run0.txt"));}
        catch (IOException e) {e.printStackTrace();}
	}

	public void testNoRuns(){
		ct.exportRun(t2);
		assertFalse(Files.exists(FileSystems.getDefault().getPath("run0.txt")));
	}
	
	public void testNoCurrentRun(){
		ct.newRun(t3);
		ct.addRacer(t3, 4);
		ct.toggle(t3, 1);ct.toggle(t3, 2);
		ct.triggerSensor(t3, 1);ct.triggerSensor(t4, 2);
		ct.finRun(t5);
		ct.exportRun(t6);
		assertTrue(Files.exists(FileSystems.getDefault().getPath("run0.txt")));
	}
	
	public void testUnfinishedRun(){
		ct.newRun(t3);
		ct.addRacer(t3, 4);
		ct.toggle(t3, 1);ct.toggle(t3, 2);
		ct.triggerSensor(t3, 1);
		ct.exportRun(t6);
		assertTrue(Files.exists(FileSystems.getDefault().getPath("run0.txt")));
	}
	
	public void testInvalidRunNumber(){
		ct.exportRun(t6,5);
		assertFalse(Files.exists(FileSystems.getDefault().getPath("run0.txt")));
		assertFalse(Files.exists(FileSystems.getDefault().getPath("run5.txt")));
	}
	
	public void testInvalidPrinter(){
		ct.newRun(t3);
		ct.addRacer(t3, 4);
		ct.toggle(t3, 1);ct.toggle(t3, 2);
		ct.triggerSensor(t3, 1);ct.triggerSensor(t4, 2);
		ct.finRun(t5);
		ct.exportRun(t6, 0, null);
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
}
