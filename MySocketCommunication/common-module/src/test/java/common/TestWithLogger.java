package common;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/29/2016 12:59
 */
public abstract class TestWithLogger {

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();
    }

}
