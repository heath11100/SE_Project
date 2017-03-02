package Tests;

import junit.framework.TestCase;

//NB: I am assuming we have a constructor that takes a bib number

public class TestRacer extends TestCase {

	/*	TODO: finish these tests
	Racer r1,r2,r3,r4,r5;
	ChronoTime t1,t2,t3;

	@Override
	public void setUp(){
		r1 = new Racer(1);
		r2 = new Racer(2);
	}
	
	@Test
	public void testConstructorInvalidArgs(){
		//handle invalid bib numbers? (negative, maximum)?
		try	{r3 = new Racer(-1);}
		catch(Exception e){assertTrue(e instanceof IllegalArgumentException);}
//		try	{r4 = new Racer(83291294);}
//		catch(Exception e){assertTrue(e instanceof IllegalArgumentException);}
		
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
	public void testIStartAndFinishInvalidArgs() throws InvalidTimeException{
		//allow equal start and stop times?
		t1 = new ChronoTime(0,0,0,0);
		t2 = new ChronoTime(0,0,0,0);
		r1.start(t1);
		//try{r1.finish(t2);}
		//catch(Exception e){assertTrue(e instanceof ???);}
		
		
		// allow stop times that are before start times?
		// could be legitimate, but we need to decide
		t3 = new ChronoTime(1,0,0,0);
		r2.start(t3);
		// r2.finish(t1);  is this allowed???
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
	public void testInvalidOrder() throws InvalidTimeException{
		t1 = new ChronoTime(0,0,0,0);
		t2 = new ChronoTime(1,0,0,0);
		
		//finish, DNF or cancel without starting
		//try{r1.finish(t1)}
		//catch(Exception e){assertTrue(e instanceof ???);}
		//try{r1.didNotFinish();}
		//catch(Exception e){assertTrue(e instanceof ???);}
		//try{r1.cancel();}
		//catch(Exception e){assertTrue(e instanceof ???);}
		
		r1.start(t1);
		
		//start twice?
		//try{r1.start(t2);}
		//catch(Exception e){assertTrue(e instanceof ???);}
		
		r1.cancel();
		
		//cancel twice?
		//try{r1.cancel();}
		//catch(Exception e){assertTrue(e instanceof ???);}
		
		r1.start(t1);
		r1.finish(t2);
		//cancel after finish?
		//try{r1.cancel();}
		//catch(Exception e){assertTrue(e instanceof ???);}
		
		//DNF after finish?
		//try{r1.didNotFinish();}
		//catch(Exception e){assertTrue(e instanceof ???);}
	}
	*/
}
