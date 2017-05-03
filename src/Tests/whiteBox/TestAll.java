package Tests.whiteBox;

import ChronoTimer.ChronoTrigger;
import org.junit.runners.Suite;

import Tests.whiteBox.ChronoTime.TestChronoTime;
import Tests.whiteBox.ChronoTrigger.TestChronoTrigger;
import Tests.whiteBox.Racer.TestRacer;
import Tests.whiteBox.Run.TestAllRun;

import org.junit.runner.RunWith;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        //Test ChronoTime
        TestChronoTime.class,

        //Test ChronoTrigger
        TestChronoTrigger.class,

        //Other ChronoTrigger stuff.
        ChronoTrigger.TestCT.class,

        //Test Racer
        TestRacer.class,

        //Test All Runs
        TestAllRun.class
})

/**
 * Created by austinheath on 4/14/17.
 */
public class TestAll { /*Placeholder class for the annotations above. */ }
