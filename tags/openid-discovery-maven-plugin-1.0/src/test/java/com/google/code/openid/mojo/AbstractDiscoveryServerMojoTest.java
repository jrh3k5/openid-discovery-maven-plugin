package com.google.code.openid.mojo;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.google.code.openid.mojo.AbstractDiscoveryServerMojoTest.ConcreteMojo;
import com.google.code.openid.mojo.jetty.DiscoveredServiceHandler;

/**
 * Unit tests for {@link AbstractDiscoveryServerMojo}.
 * 
 * @author JH016266
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { ConcreteMojo.class, DiscoveredServiceHandler.class, Server.class })
public class AbstractDiscoveryServerMojoTest {
    private ConcreteMojo mojo;

    /**
     * Set up the mojo for each test.
     */
    @Before
    public void setUp() {
        mojo = new ConcreteMojo();
    }

    /**
     * Nullify the server reference so that the mock doesn't persist between tests.
     * 
     * @throws Exception
     *             If any errors occur during the teardown.
     */
    @After
    public void tearDown() throws Exception {
        final Field serverField = Whitebox.getField(AbstractDiscoveryServerMojo.class, "server");
        serverField.set(null, null);
    }

    /**
     * Test the starting of a server.
     * 
     * @throws Exception
     *             If any errors occur during the testing.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testStartServer() throws Exception {
        final int portA = 1338;
        final int portB = 1339;
        final List<DiscoveredService> services = mock(List.class);
        final List<DiscoveryCanonicalId> canonicalIds = mock(List.class);

        final Server serverA = mock(Server.class);
        whenNew(Server.class).withArguments(portA).thenReturn(serverA);

        final Server serverB = mock(Server.class);
        whenNew(Server.class).withArguments(portB).thenReturn(serverB);

        final DiscoveredServiceHandler handler = mock(DiscoveredServiceHandler.class);
        whenNew(DiscoveredServiceHandler.class).withArguments(canonicalIds, services).thenReturn(handler);

        mojo.startServer(portA, canonicalIds, services);

        verify(serverA).setHandler(handler);
        verify(serverA).start();
        verify(serverA, never()).stop();
        assertThat(mojo.getServer()).isEqualTo(serverA);

        // If the server is started again, the previous server should be stopped
        mojo.startServer(portB, canonicalIds, services);
        verify(serverA).stop();
        verify(serverB).start();
        assertThat(mojo.getServer()).isEqualTo(serverB);
    }

    /**
     * When a server is stopped, the reference should be nullified.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testStopServer() throws Exception {
        final int port = 1338;
        final List<DiscoveredService> services = mock(List.class);
        final List<DiscoveryCanonicalId> canonicalIds = mock(List.class);

        final Server server = mock(Server.class);
        whenNew(Server.class).withArguments(port).thenReturn(server);

        final DiscoveredServiceHandler handler = mock(DiscoveredServiceHandler.class);
        whenNew(DiscoveredServiceHandler.class).withArguments(canonicalIds, services).thenReturn(handler);

        mojo.startServer(port, canonicalIds, services);
        assertThat(mojo.getServer()).isEqualTo(server);
        mojo.stopServer();
        verify(server).stop();
        assertThat(mojo.getServer()).isNull();
    }

    /**
     * A concrete mojo to facilitate testing.
     * 
     * @author JH016266
     * 
     */
    public static class ConcreteMojo extends AbstractDiscoveryServerMojo {
        /**
         * {@inheritDoc}
         */
        public void execute() throws MojoExecutionException, MojoFailureException {
            // no-op
        }
    }
}
