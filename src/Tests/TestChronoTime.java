package Tests;
import ChronoTimer.ChronoTime;
import Exceptions.InvalidTimeException;
import junit.framework.TestCase;

public class TestChronoTime extends TestCase{

	ChronoTime t1,t2,t3,t4,t5,t6;
	
	public void testConstructorToString() throws InvalidTimeException{
		t1 = new ChronoTime("00:00:00");
		t2 = new ChronoTime("01:01:01");
		t3 = new ChronoTime("23:59:59");
		t4 = new ChronoTime("00:00.00");
		t5 = new ChronoTime("01:01.01");
		t6 = new ChronoTime("1439:59.99");
		
		assertEquals("toString is wrong", t1.toString(), "0:0:0.0");
		assertEquals("getTimeStamp is wrong", t1.getTimeStamp(), "0:0.0");
		assertEquals("toString is wrong", t2.toString(), "1:1:1.0");
		assertEquals("getTimeStamp is wrong", t2.getTimeStamp(), "61:1.0");
		assertEquals("toString is wrong", t3.toString(), "23:59:59.0");
		assertEquals("getTimeStamp is wrong", t3.getTimeStamp(), "1439:59.0");
		
		assertEquals("toString is wrong", t4.toString(), "0:0:0.0");
		assertEquals("getTimeStamp is wrong", t4.getTimeStamp(), "0:0.0");
		assertEquals("toString is wrong", t5.toString(), "0:1:1.1");
		assertEquals("getTimeStamp is wrong", t5.getTimeStamp(), "1:1.1");
		assertEquals("toString is wrong", t6.toString(), "23:59:59.99");
		assertEquals("getTimeStamp is wrong", t6.getTimeStamp(), "1439:59.99");
	}
	
	public void testConstructorBadParams(){
		//negative parameters
		try {t1 = new ChronoTime("-1:00:00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("00:-1:00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("00:00:-1");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("-1:00.00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("00:-1.00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("00:00.-1");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		
		//too large
		try {t1 = new ChronoTime("24:00:00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("23:60:00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("23:59:60");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("53:65:6134410");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("22:590:601");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("1440:00.00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("1439:60.00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("1439:59.100");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("14390:59.100");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("1439:5900.100");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		
		//malformed
		try {t1 = new ChronoTime("00:00:00:00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("00:00:00.00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("00.00:00:00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("00:00.00.00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("00.00:00.00");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
		try {t1 = new ChronoTime("matt damon rulez");}
		catch (Exception e){assertTrue(e instanceof InvalidTimeException);}
	}
	
	//Helpers
	public void testElapsed() throws InvalidTimeException{
		ChronoTime t;
		t1 = new ChronoTime(0,0,0,0);
		t2 = new ChronoTime(1,1,1,1);
		t3 = new ChronoTime(23,59,59,99);
		
		t = t2.elapsedSince(t1);
		assertEquals("elapsed is wrong", t, new ChronoTime(1,1,1,1));
		t = t1.elapsedSince(t2);
		assertEquals("elapsed is wrong", t, new ChronoTime(22,58,58,99));
		
		t = t3.elapsedSince(t1);
		assertEquals("elapsed is wrong", t, new ChronoTime(23,59,59,99));
		t = t1.elapsedSince(t3);
		assertEquals("elapsed is wrong", t, new ChronoTime(0,0,0,1));
	}
}
