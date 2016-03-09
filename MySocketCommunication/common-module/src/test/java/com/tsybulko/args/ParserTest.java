package com.tsybulko.args;

import com.tsybulko.common.TestWithLogger;
import org.junit.Test;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/29/2016 13:39
 */
public abstract class ParserTest extends TestWithLogger {

    @Test
    public abstract void testParsePort() throws Exception;

    @Test
    public abstract void testParseLogfile() throws Exception;
}