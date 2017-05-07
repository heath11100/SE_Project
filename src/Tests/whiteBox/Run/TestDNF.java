package Tests.whiteBox.Run;

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
public class TestDNF extends TestCase {
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
	}
	
	/*
		A Run is instantiated, 3 Racers are queued
	
	IND/PARIND/PARGRP
	Run hasn't started
	No racers in run
	No running racers
	Invalid lane
	DNF 1-4 running racers
	GRP
	Try dnf
*/
	
}
