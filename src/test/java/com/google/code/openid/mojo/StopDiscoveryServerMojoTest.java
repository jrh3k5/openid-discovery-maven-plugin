package com.google.code.openid.mojo;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

/**
 * Unit tests for {@link StopDiscoveryServerMojo}.
 * 
 * @author JH016266
 * 
 */

public class StopDiscoveryServerMojoTest {
    /**
     * The {@link StopDiscoveryServerMojo#execute()} method should simply invoke its {@link StopDiscoveryServerMojo#stopServer() #stopServer()} method.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecute() throws Exception {
        final StopDiscoveryServerMojo mojo = mock(StopDiscoveryServerMojo.class);
        doCallRealMethod().when(mojo).execute();
        mojo.execute();
        verify(mojo).stopServer();
    }

    /**
     * If {@link StopDiscoveryServerMojo#stopServer()} throws an exception, it should be logged but not otherwise reported.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteError() throws Exception {
        final Exception thrown = new Exception();
        final Log logger = mock(Log.class);
        final StopDiscoveryServerMojo mojo = mock(StopDiscoveryServerMojo.class);
        doCallRealMethod().when(mojo).execute();
        doThrow(thrown).when(mojo).stopServer();
        when(mojo.getLog()).thenReturn(logger);

        mojo.execute();

        verify(logger).debug("Error stopping discovery server.", thrown);
    }
}
