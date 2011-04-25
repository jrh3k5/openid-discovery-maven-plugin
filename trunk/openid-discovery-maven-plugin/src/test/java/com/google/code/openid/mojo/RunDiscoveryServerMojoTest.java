package com.google.code.openid.mojo;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

/**
 * Unit tests for {@link RunDiscoveryServerMojo}.
 * 
 * @author JH016266
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { RunDiscoveryServerMojo.class, Server.class })
public class RunDiscoveryServerMojoTest {

    /**
     * Test the execution of the mojo.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecute() throws Exception {
        final RunDiscoveryServerMojo mojo = new RunDiscoveryServerMojo();
        final int port = 1338;

        Whitebox.setInternalState(mojo, "serverPort", Integer.valueOf(port));
        Whitebox.setInternalState(mojo, "services", new DiscoveredService[] {});
        Whitebox.setInternalState(mojo, "canonicalIds", new DiscoveryCanonicalId[] {});

        final Server server = mock(Server.class);
        whenNew(Server.class).withArguments(Integer.valueOf(1338)).thenReturn(server);

        mojo.execute();
        verify(server).join();
    }

}
