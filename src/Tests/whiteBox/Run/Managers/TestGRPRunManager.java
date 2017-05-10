package Tests.whiteBox.Run.Managers;

/**
 * Created by austinheath on 5/3/17.
 */

import ChronoTimer.Runs.GRPRunManager;
import ChronoTimer.Runs.INDRunManager;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GRPRunManager.TestRunManager.class
})

public class TestGRPRunManager { }
