package com.tsybulko.args;


import static org.junit.Assert.assertEquals;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/29/2016 13:16
 */
public class ServerParserTest extends ParserTest {

    @org.junit.Test
    public void testParsePort() throws Exception {
        String[] args = {"-port", "4800"};
        ServerArgsContainer argumentContainer = ServerParser.getInstance().parse(args);
        assertEquals(4800, argumentContainer.getPort());
    }

    @org.junit.Test
    public void testParseLogfile() throws Exception {
        String[] args = {"-port", "4800", "-logfile", "someFile"};
        ServerArgsContainer argumentContainer = ServerParser.getInstance().parse(args);
        assertEquals("someFile", argumentContainer.getLogFile());
    }

    @org.junit.Test
    public void testParseReverse() throws Exception {
        String[] args = {"-logfile", "someFile", "-serverPort", "4800"};
        ServerArgsContainer argumentContainer = ServerParser.getInstance().parse(args);
        assertEquals("someFile", argumentContainer.getLogFile());
        assertEquals(4800, argumentContainer.getPort());
    }
}