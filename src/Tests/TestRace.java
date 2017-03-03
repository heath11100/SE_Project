package Tests;

import ChronoTimer.*;
import Exceptions.*;
import junit.framework.TestCase;

// NB: I am assuming we have a constructor that takes no args

public class TestRace extends TestCase {
	
	Race ra;
	//Racer numbers
	int rn1, rn2, rn3, rn4, rn5;
	ChronoTime t1,t2,t3;

	@Override
	public void setUp() throws InvalidTimeException{
		ra = new Race();
		rn1 = 1;
		rn2 = 2;
		t1 = new ChronoTime(0,0,0,0);
		t2 = new ChronoTime(1,0,0,0);
		t3 = new ChronoTime(2,0,0,0);
	}
	
	public void testAddGetRemoveRacer() {
		try {
			ra.add(rn1);
			ra.add(rn2);
			
			//Test that racer1 exists after being added
			Racer racer1 = ra.getRacer(1);
			Racer racer2 = ra.getRacer(2);
			
			//Assert that racer1 is not null
			assertTrue(racer1 != null);
			assertEquals(racer1.getNumber(), 1);
			
			//Assert that racer2 is not null
			assertTrue(racer2 != null);
			assertEquals(racer2.getNumber(), 2);
			
			ra.remove(1);
			racer1 = ra.getRacer(1);
			racer2 = ra.getRacer(2);
			
			//Assert that racer1 is null (it was removed)
			assertTrue(racer1 == null);
			
			//Assert that racer2 is not null (it was not removed)
			assertTrue(racer2 != null);
			assertEquals(racer2.getNumber(), 2);

		} catch (RaceException e) {
			assertTrue(false);
		}
		
		//what happens if we try to remove a racer that's not there?
		try {
			ra.remove(-1);
			assertTrue("Error should be thrown when removing a racer that does not exist", false);
		} catch (RaceException e) {
			
		}
	}
	
	
	public void testBeginAndFinish() throws InvalidTimeException {
		
		//ok to begin and end without racers?
		/*
		 * TODO: Should we be able to do this?
		 */
		setUp();
		try {
			ra.beginRace(t1);
			
			ra.add(rn1);
			ra.startNextRacer(t1);
			
			Racer racer = ra.getRacer(rn1);
			assertEquals(racer.getStartTime(), t1);
			
			ra.endRace(t2);

		} catch (RaceException e) {
			assertTrue(e.getMessage(), false);
			
		} catch (InvalidTimeException e) {
			assertTrue(e.getMessage(), false);
		}
	}
	
public void testRacerBeginAndFinish(){
		
		//what happens if racer begins/finishes before race begins?
		//ra.nextRacerBegan(t1);
		//ra.nextRacerFinished(t1);
	
		//what happens if racer begins/finishes after race ends?
		//ra.beginRace(t1);
		//ra.endRace(t1);
		//ra.nextRacerBegan(t1);
		//ra.nextRacerFinished(t1);
	
//		try {
//			ra.beginRace(t1);
//			ra.endRace(t2);
//		} catch (InvalidTimeException e) {
//			e.printStackTrace();
//			assertTrue(false);
//		}

		//this is a normal scenario
		ra = new Race();
		try {
			ra.add(rn1);
			ra.beginRace(t1);
			ra.startNextRacer(t2);
			ra.finishNextRacer(t3);
			
			Racer racer1 = ra.getRacer(rn1);
			assertEquals(racer1.getStartTime(), t2);
			assertEquals(racer1.getEndTime(), t3);
			
		} catch (RaceException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (InvalidTimeException e) {
			assertTrue(false);
		}
		
		//what happens if more began or finished than exist?
		//while (true)
		//	ra.nextRacerBegan(t1);
		//	ra.nextRacerFinished(t1);
	}
}
