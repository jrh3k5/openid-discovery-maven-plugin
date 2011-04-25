package com.google.code.openid.mojo.jetty;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mortbay.jetty.Request;

import com.google.code.openid.mojo.DiscoveredService;
import com.google.code.openid.mojo.openid.DiscoveredServiceWriter;

/**
 * Unit tests for {@link DiscoveredServiceHandler}.
 * 
 * @author jrh3k5
 * 
 */

@RunWith(MockitoJUnitRunner.class)
public class DiscoveredServiceHandlerTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private final String xriCanonicalId = "xri.canonical.id";
    @Mock
    private Collection<DiscoveredService> services;
    @Mock
    private DiscoveredServiceWriter writer;
    @Mock
    private Request request;
    @Mock
    private HttpServletResponse response;
    private DiscoveredServiceHandler handler;

    /**
     * Set up the handler for each test.
     */
    @Before
    public void setUp() {
        handler = new DiscoveredServiceHandler(xriCanonicalId, services, writer);
    }

    /**
     * Construction with {@code null} services should fail.
     */
    @Test
    public void testConstructNullServices() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Services cannot be null.");
        new DiscoveredServiceHandler(xriCanonicalId, null, writer);
    }

    /**
     * Construction with a {@code null} writer should fail.
     */
    @Test
    public void testConstructNullWriter() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Writer cannot be null.");
        new DiscoveredServiceHandler(xriCanonicalId, services, null);
    }

    /**
     * Test the writing out of a matching service.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testHandle() throws Exception {
        final String targetUri = "target/uri";

        final ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        final DiscoveredService match = mock(DiscoveredService.class);
        when(match.matchesHostRegex(targetUri)).thenReturn(Boolean.TRUE);
        final DiscoveredService noMatch = mock(DiscoveredService.class);
        when(services.iterator()).thenReturn(Arrays.asList(match, noMatch).iterator());

        handler.handle(targetUri, request, response, 0);

        verify(request).setHandled(true);
        verify(response).setHeader("content-type", "application/xrds+xml");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        final ArgumentCaptor<Collection> writtenCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(writer).write(eq(xriCanonicalId), writtenCaptor.capture(), eq(outputStream));
        assertThat(writtenCaptor.getValue()).containsOnly(match);
    }

    /**
     * If there are no matches, then nothing should be written out or handled.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testHandleNoMatches() throws Exception {
        when(services.iterator()).thenReturn(Collections.<DiscoveredService> emptySet().iterator());
        handler.handle("irrelevant", request, response, 0);
        verifyZeroInteractions(request);
        verifyZeroInteractions(response);
        verifyZeroInteractions(writer);
    }
}
