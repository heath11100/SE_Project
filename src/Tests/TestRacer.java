package Tests;

import ChronoTimer.*;
import Exceptions.*;

import junit.framework.TestCase;
import org.junit.Test;

//NB: I am assuming we have a constructor that takes a bib number

public class TestRacer extends TestCase {

	Racer r1,r2,r3,r4,r5;
	ChronoTime t1,t2,t3;

	@Override
	public void setUp(){
		r1 = new Racer(1);
		r2 = new Racer(2);
	}
	
	@Test
	public void testConstructorInvalidArgs(){
		/*
		 * Test Racer Numbers:
		 * - must be greater than 0 but no more than 4 digits
		 */
		
		
		/* No longer apply with dummy racers
		try	{
			r3 = new Racer(-1);
			assertTrue("Created a racer with a negative bib number",false);
		} catch(Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		
		try	{
			r3 = new Racer(0);
			assertTrue("Created a racer with a bib number of 0",false);
		} catch(Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}*/
		
		try {
			r4 = new Racer(10000);
			assertTrue("Error was not thrown for a number greater than 4 digits",false);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		
		try {
			r4 = new Racer(9999);
			assertTrue("Racer successfully created with largest 4-digit number", true);
		} catch (Exception e) {
			assertTrue("Racer could not be created with largest 4-digit number",false);
		}
		
		assertEquals(r1.getNumber(), 1);
		assertEquals(r1.getStatus(), Racer.Status.QUEUED);
		assertEquals(r2.getNumber(), 2);
		assertEquals(r2.getStatus(), Racer.Status.QUEUED);
	}
	
	@Test
	public void testStartAndFinish() throws InvalidTimeException{
		t1 = new ChronoTime(0,0,0,0);
		t2 = new ChronoTime(1,0,0,0);
		r1.start(t1);
		assertEquals(r1.getStartTime(), t1);
		assertEquals(r1.getStatus(), Racer.Status.RACING);
		r1.finish(t2);
		assertEquals(r1.getEndTime(), t2);
		assertEquals(r1.getStatus(), Racer.Status.FINISHED);
	}
	
	@Test
	public void testInvalidStartAndFinishInvalidArgs() throws InvalidTimeException{
		//allow equal start and stop times?
		t1 = new ChronoTime(0,0,0,0);
		t2 = new ChronoTime(0,0,0,0);
		r1.start(t1);
		
		try {
			r1.finish(t2);
			assertTrue("Failed to throw exception when throw exception when end time is equal to start time.", false);
		} catch (Exception e) {
			assertTrue(e instanceof InvalidTimeException);
		}		
		
		// allow stop times that are before start times?
		// could be legitimate, but we need to decide
		t3 = new ChronoTime(1,0,0,0);
		r2.start(t3);
		try {
			r2.finish(t1);
			assertTrue("Failed to throw exception when throw exception when end time is less than start time.", false);
		} catch (Exception e) {
			assertTrue(e instanceof InvalidTimeException);
		}	
	}
	
	@Test
	public void testDNF() throws InvalidTimeException{
		t1 = new ChronoTime(0,0,0,0);
		r1.start(t1);
		r1.didNotFinish();
		assertEquals(r1.getStartTime(), t1);
		assertEquals(r1.getStatus(), Racer.Status.DNF);
	}
	
	@Test
	public void testCancel() throws InvalidTimeException{
		t1 = new ChronoTime(0,0,0,0);
		r1.start(t1);
		r1.cancel();
		assertEquals(r1.getStartTime(), null);
		assertEquals(r1.getStatus(), Racer.Status.QUEUED);
	}
	
	@Test
	public void testInvalidOrder() throws InvalidTimeException {
		t1 = new ChronoTime(0,0,0,0);
		t2 = new ChronoTime(1,0,0,0);
		
		//finish, DNF or cancel without starting
		try {
			r1.finish(t1);
			assertTrue("Racer finishing before starting should throw exception.", false);
		} catch (Exception e) {
			
			assertTrue(e instanceof InvalidTimeException);
		}
		
		try {
			r1.didNotFinish();
			assertTrue("Racer DNF before starting should throw exception.", false);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
		
		try {
			r1.cancel();
			assertTrue("Racer canceling before starting should throw exception.", false);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
		
		r1.start(t1);
		
		//start twice?
		try {
			r1.start(t2);
			assertTrue("Racer starting twice should throw exception.", false);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
		
		r1.cancel();
		
		try {
			r1.cancel();
			assertTrue("Racer cancel twice should throw exception.", false);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
		
		r1.start(t1);
		r1.finish(t2);
		//cancel after finish?
		try {
			r1.cancel();
			assertTrue("Racer cancel after finish should throw exception.", false);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
		
		//DNF after finish?
		try {
			r1.didNotFinish();
			assertTrue("Racer DNF after finish should throw exception.", false);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
	}
	
}
