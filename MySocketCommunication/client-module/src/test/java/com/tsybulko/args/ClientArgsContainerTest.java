package com.tsybulko.args;

import com.tsybulko.dto.command.MapCommand;
import com.tsybulko.dto.command.MapCommandDTO;
import com.tsybulko.common.TestWithLogger;
import com.tsybulko.validate.ClientArgsValidator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 03/04/2016 14:29
 */
public class ClientArgsContainerTest extends TestWithLogger {

    ClientArgsContainer goodContainer;
    HashMap<String, String> errors = new HashMap<String, String>();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        goodContainer = new ClientArgsContainer();
        goodContainer.setPort(2000);
        goodContainer.setLogFile("logFileName.log");
        goodContainer.setServerHost("google.com");
        goodContainer.setCommandDTO(new MapCommandDTO(MapCommand.clearAll));
        errors.clear();
    }

    @Test
    public void testValidate() throws Exception {
        assertTrue(ClientArgsValidator.validate(goodContainer, errors).isEmpty());
    }

    @Test
    public void testValidateWrongPort() throws Exception {
        goodContainer.setPort(1000);
        assertTrue(!ClientArgsValidator.validate(goodContainer, errors).isEmpty());
    }

    @Test
    public void testValidateNegativePort() throws Exception {
        goodContainer.setPort(-42);
        assertTrue(!ClientArgsValidator.validate(goodContainer, errors).isEmpty());
    }

    @Test
    public void testValidateHost() throws Exception {
        goodContainer.setServerHost(null);
        assertTrue(!ClientArgsValidator.validate(goodContainer, errors).isEmpty());
    }

    @Test
    public void testValidateCommand() throws Exception {
        goodContainer.setCommandDTO(null);
        assertTrue(!ClientArgsValidator.validate(goodContainer, errors).isEmpty());
    }

    @Test
    public void testValidateWrongCommand() throws Exception {
        goodContainer.setCommandDTO(new MapCommandDTO(MapCommand.put));
        assertTrue(!goodContainer.getCommandDTO().isInitialised());
        assertTrue(!ClientArgsValidator.validate(goodContainer, errors).isEmpty());
    }


}