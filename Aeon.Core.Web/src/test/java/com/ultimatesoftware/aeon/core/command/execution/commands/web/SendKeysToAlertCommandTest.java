package com.ultimatesoftware.aeon.core.command.execution.commands.web;

import com.ultimatesoftware.aeon.core.framework.abstraction.drivers.IWebDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class SendKeysToAlertCommandTest {
    private SendKeysToAlertCommand sendKeysToAlertCommand;
    private String keysToSend = "testKey";

    @Mock
    private IWebDriver driver;

    @BeforeEach
    public void setup() {
        sendKeysToAlertCommand = new SendKeysToAlertCommand(keysToSend);
    }

    @Test
    public void commandDelegateSendKeysToAlertCommand() {
        //Arrange

        //Act
        sendKeysToAlertCommand.driverDelegate(driver);

        //Assert
        verify(driver, times(1)).sendKeysToAlert(keysToSend);
    }
}