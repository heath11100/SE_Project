package Tests.whiteBox.Run.Managers;

/**
 * Created by austinheath on 5/3/17.
 */


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestINDRunManager.class,
        TestPARINDRunManager.class,
        TestGRPRunManager.class,
        TestPARGRPRunManager.class
})

public class TestAllManagers { }