package Tests;

import junit.framework.TestCase;

// NB: I am assuming we have a constructor that takes no args

public class TestRace extends TestCase {
	
	/*	TODO: finish these tests
	Race ra;
	Racer r1,r2,r3,r4,r5;
	ChronoTime t1,t2,t3;

	@Override
	public void setUp() throws InvalidTimeException{
		ra = new Race();
		r1 = new Racer(1);
		r2 = new Racer(2);
		t1 = new ChronoTime(0,0,0,0);
		t2 = new ChronoTime(1,0,0,0);
		t2 = new ChronoTime(2,0,0,0);
	}
	
	public void testAddGetRemoveRacer() {
		
		try {
			ra.add(r1);
			ra.add(r2);
			assertEquals(ra.getRacer(1),r1);
			assertEquals(ra.getRacer(2),r2);
			
			ra.remove(1);
			assertEquals(ra.getRacer(1),null);
			
			ra.add(r1);
			assertEquals(ra.getRacer(1),r1);

		} catch (DuplicateRacerException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		//what happens if we try to remove a racer that's not there?
		//DISCUSS: currently it returns null, do we want it to throw an exception?
		ra.remove(3);
		ra.remove(-1);
		
		//Race can't cancel or DNF racers?
		// are we leaving this up to ChronoTrigger to get the last racer to start
		// and call the method directly on racer?
		
		r3 = new Racer(3);
		r3.start(t1);
		//add racer who has status other than QUEUE?
		//try {ra.add(r3);}
		//catch(Exception e){assertTrue(e instanceof ???);}
		
		//add null?
		//try {ra.add(null);}
		//catch(Exception e){assertTrue(e instanceof ???);}
	}
	
	
	public void testBeginAndFinish(){
		
		//ok to begin and end without racers?
		try {
			ra.beginRace(t1);
			ra.endRace(t2);
			
			ra = new Race();
			ra.add(r1);
			ra.nextRacerBegan(t1);
			assertEquals(r1.getStartTime(), t1);

		} catch (InvalidRaceStartException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (InvalidRaceEndException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (DuplicateRacerException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
public void testRacerBeginAndFinish(){
		
		//what happens if racer begins/finishes before race begins?
		//ra.nextRacerBegan(t1);
		//ra.nextRacerFinished(t1);
	
		//what happens if racer begins/finishes before race begins?
		//ra.beginRace(t1);
		//ra.endRace(t1);
		//ra.nextRacerBegan(t1);
		//ra.nextRacerFinished(t1);
	
		try {
			ra.beginRace(t1);
			ra.endRace(t2);
		} catch (InvalidRaceStartException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (InvalidRaceEndException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		//this is a normal scenario
		ra = new Race();
		try {
			ra.add(r1);
			ra.beginRace(t1);
			ra.nextRacerBegan(t2);
			ra.nextRacerFinished(t3);

			assertEquals(r1.getStartTime(), t2);
			assertEquals(r1.getEndTime(), t3);
			
		} catch (DuplicateRacerException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (InvalidRaceStartException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (InvalidRaceEndException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		//what happens if more began or finished than exist?
		//while (true)
		//	ra.nextRacerBegan(t1);
		//	ra.nextRacerFinished(t1);
	}
	*/
}
