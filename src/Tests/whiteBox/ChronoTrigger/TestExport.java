package Tests.whiteBox.ChronoTrigger;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import ChronoTimer.ChronoTime;
import ChronoTimer.ChronoTrigger;
import Exceptions.InvalidTimeException;
import junit.framework.TestCase;

/**
 * @author vangrol2
 */
public class TestExport extends TestCase{
	
	private ChronoTrigger ct;
	private ChronoTime t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11;
	
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
        t7 = new ChronoTime("0:7:0.0");
        t8 = new ChronoTime("0:8:0.0");
        t9 = new ChronoTime("0:9:0.0");
        t10 = new ChronoTime("0:10:0.0");
        t11 = new ChronoTime("0:11:0.0");
        ct = new ChronoTrigger(t1);
        ct.powerOn(t2);
        
        //creating/deleting files takes time...
        // this method call ensures we don't cause file access collisions
        waitForFileSystem();
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
		assertFalse(Files.exists(FileSystems.getDefault().getPath("run0.txt")));
	}
	
	public void testExportIND(){
		ct.newRun(t3);
		ct.setType(t3, "IND");
		ct.addRacer(t3, 1);
		ct.addRacer(t3, 2);
		ct.addRacer(t3, 3);
		ct.toggle(t3, 1);ct.toggle(t3, 2);
		ct.triggerSensor(t3, 1);ct.triggerSensor(t4, 2);
		ct.triggerSensor(t5, 1);ct.triggerSensor(t6, 1);
		ct.triggerSensor(t7, 2);ct.triggerSensor(t8, 2);
		ct.finRun(t9);
		ct.exportRun(t10);
		assertTrue(Files.exists(FileSystems.getDefault().getPath("run0.txt")));
	}
	
	public void testExportPARIND(){
		
		ct.newRun(t3);
		ct.setType(t3, "PARIND");
		ct.addRacer(t3, 1);
		ct.addRacer(t3, 2);
		ct.addRacer(t3, 3);
		ct.toggle(t3, 1);ct.toggle(t3, 2);
		ct.toggle(t3, 3);ct.toggle(t3, 4);
		ct.triggerSensor(t3, 1);ct.triggerSensor(t4, 3);
		ct.triggerSensor(t5, 1);ct.triggerSensor(t6, 4);
		ct.triggerSensor(t7, 2);ct.triggerSensor(t8, 2);
		ct.finRun(t9);
		ct.exportRun(t10);
		assertTrue(Files.exists(FileSystems.getDefault().getPath("run0.txt")));
	}
	
	public void testExportGRP(){
		ct.newRun(t3);
		ct.setType(t3, "GRP");
		ct.addRacer(t3, 1);
		ct.addRacer(t3, 2);
		ct.addRacer(t3, 3);
		ct.toggle(t3, 1);ct.toggle(t3, 2);
		ct.triggerSensor(t3, 1);
		ct.triggerSensor(t4, 2);
		ct.triggerSensor(t5, 2);
		ct.triggerSensor(t6, 2);
		ct.finRun(t7);
		ct.exportRun(t11);
		assertTrue(Files.exists(FileSystems.getDefault().getPath("run0.txt")));
	}
	
	public void testExportPARGRP(){
		ct.newRun(t3);
		ct.setType(t3, "PARGRP");
		ct.addRacer(t3, 1);
		ct.addRacer(t3, 2);
		ct.addRacer(t3, 3);
		ct.addRacer(t3, 4);
		ct.addRacer(t3, 5);
		ct.addRacer(t3, 6);
		ct.addRacer(t3, 7);
		ct.addRacer(t3, 8);
		ct.toggle(t3, 1);ct.toggle(t3, 2);
		ct.toggle(t3, 3);ct.toggle(t3, 4);
		ct.toggle(t3, 5);ct.toggle(t3, 6);
		ct.toggle(t3, 7);ct.toggle(t3, 8);
		ct.triggerSensor(t3, 1);
		
		ct.triggerSensor(t4, 2);
		ct.triggerSensor(t5, 4);
		ct.triggerSensor(t6, 6);
		ct.triggerSensor(t7, 8);
		ct.triggerSensor(t8, 1);
		ct.triggerSensor(t9, 3);
		ct.triggerSensor(t10, 5);
		ct.triggerSensor(t11, 7);
		ct.finRun(t11);
		ct.exportRun(t11);
		assertTrue(Files.exists(FileSystems.getDefault().getPath("run0.txt")));
	}
	
	public void waitForFileSystem(){
		try{Thread.sleep(10);}
		catch(Exception e){}}
}
