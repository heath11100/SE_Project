package Tests.whiteBox;

import ChronoTimer.ChronoTrigger;
import Tests.whiteBox.ChronoTrigger.TestExport;
import Tests.whiteBox.ChronoTrigger.TestPrint;
import Tests.whiteBox.Run.Managers.TestAllManagers;
import Tests.whiteBox.Run.TestCancel;
import Tests.whiteBox.Run.TestDNF;
import Tests.whiteBox.Run.TestRun;
import Tests.whiteBox.Run.TestSwap;
import org.junit.runners.Suite;

import Tests.whiteBox.ChronoTime.TestChronoTime;
import Tests.whiteBox.ChronoTrigger.TestChronoTrigger;
import Tests.whiteBox.Racer.TestRacer;

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

        //Test Run Managers
        TestAllManagers.class,

        //Test Run
        TestRun.class,

        TestCancel.class,

        TestDNF.class,

        TestSwap.class,

        TestExport.class,

        TestPrint.class
})

/**
 * Created by austinheath on 4/14/17.
 */
public class TestAll { /*Placeholder class for the annotations above. */ }
