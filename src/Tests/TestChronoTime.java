package Tests;
import java.time.Duration;
import java.time.LocalDateTime;

import ChronoTimer.ChronoTime;
import Exceptions.InvalidTimeException;
import junit.framework.TestCase;

/**
 * Tests for ChronoTime.
 * @author Casey Van Groll
 */
public class TestChronoTime extends TestCase{

	ChronoTime t1,t2,t3,t4,t5,t6;
	
	public void testConstructorToString() throws InvalidTimeException{
		t1 = new ChronoTime("00:00:00");
		t2 = new ChronoTime("01:01:01");
		t3 = new ChronoTime("23:59:59");
		t4 = new ChronoTime("00:00.00");
		t5 = new ChronoTime("01:01.01");
		t6 = new ChronoTime("1439:59.99");
		
		assertEquals("toString is wrong", t1.toString(), "00:00:00.00");
		assertEquals("getTimeStamp is wrong", t1.getTimeStamp(), "0:0.0");
		assertEquals("toString is wrong", t2.toString(), "01:01:01.00");
		assertEquals("getTimeStamp is wrong", t2.getTimeStamp(), "61:1.0");
		assertEquals("toString is wrong", t3.toString(), "23:59:59.00");
		assertEquals("getTimeStamp is wrong", t3.getTimeStamp(), "1439:59.0");
		
		assertEquals("toString is wrong", t4.toString(), "00:00:00.00");
		assertEquals("getTimeStamp is wrong", t4.getTimeStamp(), "0:0.0");
		assertEquals("toString is wrong", t5.toString(), "00:01:01.01");
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
	
	//Helpers
		public void testNowWithOffset() throws InvalidTimeException{
			t1 = new ChronoTime("12:00:00");
			Duration offset1 = Duration.ofSeconds(1000);//   16 min, 40 sec
			Duration offset2 = Duration.ofSeconds(-1000);// -16 min, 40 sec
			Duration offset3 = Duration.ofMinutes(100);//	 1 hour, 40 min
			Duration offset4 = Duration.ofMinutes(-100);//	-1 hour, 40 min
			Duration offset5 = Duration.ofHours(10);//		10 hour
			Duration offset6 = Duration.ofHours(-10);//	   -10 hour
			//System.out.println("now:    " + ChronoTime.now());
			//System.out.println("+16m40s " + ChronoTime.now(offset1));
			//System.out.println("-16m40s " + ChronoTime.now(offset2));
			//System.out.println("+1h40m  " + ChronoTime.now(offset3));
			//System.out.println("-1h40m  " +ChronoTime.now(offset4));
			//System.out.println("+10h    " +ChronoTime.now(offset5));
			//System.out.println("-10h    " +ChronoTime.now(offset6));
			
			int delta = 10; //accurate to within 10 hundredths of a second (to allow calculation time)
			ChronoTime t0 = ChronoTime.now();
			t1 = ChronoTime.now(offset1);
			t2 = ChronoTime.now(offset2);
			t3 = ChronoTime.now(offset3);
			t4 = ChronoTime.now(offset4);
			t5 = ChronoTime.now(offset5);
			t6 = ChronoTime.now(offset6);
			
			assertTrue(inRange(t0,t1,offset1,delta));
			assertTrue(inRange(t0,t2,offset2,delta));
			assertTrue(inRange(t0,t3,offset3,delta));
			assertTrue(inRange(t0,t4,offset4,delta));
			assertTrue(inRange(t0,t5,offset5,delta));
			assertTrue(inRange(t0,t6,offset6,delta));
		}
		
		private boolean inRange(ChronoTime original, ChronoTime offset, Duration dur, int delta) throws InvalidTimeException{
			return Math.abs(original.elapsedSince(offset).asHundredths() - Math.abs(dur.toMillis()/10)) < delta ||
					Math.abs(offset.elapsedSince(original).asHundredths() - Math.abs(dur.toMillis()/10)) < delta;}
		
}
