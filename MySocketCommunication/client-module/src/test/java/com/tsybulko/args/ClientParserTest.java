package com.tsybulko.args;

import com.tsybulko.dto.command.MapCommand;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/29/2016 13:38
 */
public class ClientParserTest extends ParserTest {

    @org.junit.Test
    public void testParsePort() throws Exception {
        String[] args = {"-serverHost", "host", "-serverPort", "4800", "clearAll"};
        ClientArgsContainer argumentContainer = ClientParser.getInstance().parse(args, new HashMap<String, String>());
        assertEquals(4800, argumentContainer.getPort());
    }

    @org.junit.Test
    public void testParseLogfile() throws Exception {
        String[] args = {"-serverHost", "host", "-serverPort", "4800", "clearAll", "-logfile", "someFile"};
        ClientArgsContainer argumentContainer = ClientParser.getInstance().parse(args, new HashMap<String, String>());
        assertEquals("someFile", argumentContainer.getLogFile());
    }

    @org.junit.Test
    public void testParseCommand() throws Exception {
        String[] args = {"-serverHost", "host", "-serverPort", "4800", "put", "key", "value"};
        ClientArgsContainer argumentContainer = ClientParser.getInstance().parse(args, new HashMap<String, String>());
        assertSame(MapCommand.put, argumentContainer.getCommandDTO().getCommand());
        assertEquals("key", argumentContainer.getCommandDTO().getKey());
        assertEquals("value", argumentContainer.getCommandDTO().getValue());
    }

}