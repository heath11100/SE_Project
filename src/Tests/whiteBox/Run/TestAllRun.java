package Tests.whiteBox.Run;


import ChronoTimer.Runs.INDRunManager;
import org.junit.runners.Suite;
import org.junit.runner.RunWith;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestRun.class,
        TestRun_GRP.class,
        TestRun_IND.class,
        TestRun_PARIND.class,
        
        //MARK: Run Managers
        INDRunManager.TestINDRunManager.class
})

/**
 * Created by austinheath on 4/14/17.
 */
public class TestAllRun {
    // the class remains empty,
    // used only as a holder for the above annotation;
}
