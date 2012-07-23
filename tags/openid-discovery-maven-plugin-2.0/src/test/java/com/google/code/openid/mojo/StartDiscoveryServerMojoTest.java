package com.google.code.openid.mojo;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link StartDiscoveryServerMojo}.
 * 
 * @author JH016266
 * 
 */

@RunWith(MockitoJUnitRunner.class)
public class StartDiscoveryServerMojoTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Mock
    private StartDiscoveryServerMojo mojo;

    /**
     * Set up the mojo for each test.
     * 
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @Before
    public void setUp() throws Exception {
        doCallRealMethod().when(mojo).execute();
    }

    /**
     * Test the execution of the mojo.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testExecute() throws Exception {
        final DiscoveryCanonicalId canonicalId = mock(DiscoveryCanonicalId.class);
        final DiscoveredService service = mock(DiscoveredService.class);
        final int port = 1338;

        Whitebox.setInternalState(mojo, "serverPort", Integer.valueOf(port));
        Whitebox.setInternalState(mojo, "canonicalIds", new DiscoveryCanonicalId[] { canonicalId });
        Whitebox.setInternalState(mojo, "services", new DiscoveredService[] { service });

        mojo.execute();

        final ArgumentCaptor<List> canonicalIdCaptor = ArgumentCaptor.forClass(List.class);
        final ArgumentCaptor<Collection> serviceCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(mojo).startServer(eq(port), canonicalIdCaptor.capture(), serviceCaptor.capture());

        assertThat(canonicalIdCaptor.getValue()).containsOnly(canonicalId);
        assertThat(serviceCaptor.getValue()).containsOnly(service);
    }

    /**
     * If no canonical IDs are set on the mojo, then an empty list should just be passed down to the server.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testExecuteNoCanonicalIds() throws Exception {
        final DiscoveredService service = mock(DiscoveredService.class);
        final int port = 1338;

        Whitebox.setInternalState(mojo, "serverPort", Integer.valueOf(port));
        Whitebox.setInternalState(mojo, "services", new DiscoveredService[] { service });

        mojo.execute();

        final ArgumentCaptor<List> canonicalIdCaptor = ArgumentCaptor.forClass(List.class);
        verify(mojo).startServer(eq(port), canonicalIdCaptor.capture(), any(Collection.class));
        assertThat(canonicalIdCaptor.getValue()).isEmpty();
    }

    /**
     * If the startup of the server throws an exception, it should be re-thrown as a {@link MojoExecutionException}.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteStartupError() throws Exception {
        final Exception thrown = new Exception();
        doThrow(thrown).when(mojo).startServer(anyInt(), any(List.class), any(Collection.class));

        expected.expect(MojoExecutionException.class);
        expected.expectMessage("Error starting discovery server.");
        mojo.execute();
    }
}
