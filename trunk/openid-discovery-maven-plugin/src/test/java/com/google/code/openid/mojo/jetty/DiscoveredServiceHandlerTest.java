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
import java.util.List;

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
import com.google.code.openid.mojo.DiscoveryCanonicalId;
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

    @Mock
    private List<DiscoveryCanonicalId> canonicalIds;
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
        handler = new DiscoveredServiceHandler(canonicalIds, services, writer);
    }

    /**
     * Construction with a {@code null} {@link List} of canonical IDs should fail.
     */
    @Test
    public void testConstructNullCanonicalIds() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("The canonical IDs cannot be null.");
        new DiscoveredServiceHandler(null, services, writer);
    }

    /**
     * Construction with {@code null} services should fail.
     */
    @Test
    public void testConstructNullServices() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Services cannot be null.");
        new DiscoveredServiceHandler(canonicalIds, null, writer);
    }

    /**
     * Construction with a {@code null} writer should fail.
     */
    @Test
    public void testConstructNullWriter() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Writer cannot be null.");
        new DiscoveredServiceHandler(canonicalIds, services, null);
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

        final DiscoveredService matchService = mock(DiscoveredService.class);
        when(matchService.matchesHostRegex(targetUri)).thenReturn(Boolean.TRUE);
        final DiscoveredService noMatchService = mock(DiscoveredService.class);
        when(services.iterator()).thenReturn(Arrays.asList(matchService, noMatchService).iterator());

        final DiscoveryCanonicalId matchId = mock(DiscoveryCanonicalId.class);
        when(matchId.matchesHostRegex(targetUri)).thenReturn(Boolean.TRUE);
        when(canonicalIds.iterator()).thenReturn(Arrays.asList(matchId).iterator());

        handler.handle(targetUri, request, response, 0);

        verify(request).setHandled(true);
        verify(response).setHeader("content-type", "application/xrds+xml");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        final ArgumentCaptor<Collection> writtenCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(writer).write(eq(matchId), writtenCaptor.capture(), eq(outputStream));
        assertThat(writtenCaptor.getValue()).containsOnly(matchService);
    }

    /**
     * If no canonical ID matches, then {@code null} should be passed to the writer.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testHandleNoCanonicalId() throws Exception {
        final String targetUri = "target/uri";

        final ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        final DiscoveredService matchService = mock(DiscoveredService.class);
        when(matchService.matchesHostRegex(targetUri)).thenReturn(Boolean.TRUE);
        when(services.iterator()).thenReturn(Arrays.asList(matchService).iterator());

        final DiscoveryCanonicalId noMatchId = mock(DiscoveryCanonicalId.class);
        when(canonicalIds.iterator()).thenReturn(Arrays.asList(noMatchId).iterator());

        handler.handle(targetUri, request, response, 0);

        verify(request).setHandled(true);
        verify(response).setHeader("content-type", "application/xrds+xml");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        final ArgumentCaptor<Collection> writtenCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(writer).write(eq((DiscoveryCanonicalId) null), writtenCaptor.capture(), eq(outputStream));
        assertThat(writtenCaptor.getValue()).containsOnly(matchService);
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
        when(canonicalIds.iterator()).thenReturn(Collections.<DiscoveryCanonicalId> emptyList().iterator());
        handler.handle("irrelevant", request, response, 0);
        verifyZeroInteractions(request);
        verifyZeroInteractions(response);
        verifyZeroInteractions(writer);
    }
}
