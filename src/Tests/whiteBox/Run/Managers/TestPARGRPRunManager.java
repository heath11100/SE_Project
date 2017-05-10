package Tests.whiteBox.Run.Managers;

/**
 * Created by austinheath on 5/3/17.
 */

import ChronoTimer.Runs.GRPRunManager;
import ChronoTimer.Runs.PARGRPRunManager;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PARGRPRunManager.TestRunManager.class
})

public class TestPARGRPRunManager { }
