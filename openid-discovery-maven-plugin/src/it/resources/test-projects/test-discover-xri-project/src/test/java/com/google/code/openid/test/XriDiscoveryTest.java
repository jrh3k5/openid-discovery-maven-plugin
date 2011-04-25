package com.google.code.openid.test;

import static org.fest.assertions.Assertions.assertThat;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Test;
import org.openid4java.discovery.Discovery;
import org.openid4java.discovery.DiscoveryInformation;

/**
 * Tests for discovery involving XRI identifiers.
 * 
 * @author jrh3k5
 * 
 */

public class XriDiscoveryTest {
    private final Discovery discovery = new Discovery();
    private final Namespace xrdNamespace = Namespace.getNamespace("xri://$xrd*($v*2.0)");

    /**
     * Shut down all HTTP clients.
     * 
     * @throws Exception
     *             If any errors occur during the teardown.
     */
    @After
    public void tearDown() throws Exception {
        MultiThreadedHttpConnectionManager.shutdownAll();
    }

    /**
     * Test the discovery of a document serving out an XRI identifier with a canonical ID.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testDiscoverXri() throws Exception {
        final String discoveryUrl = "http://localhost:1337/xri";
        // Verify basic ability to discover OP endpoints
        @SuppressWarnings("unchecked")
        final List<DiscoveryInformation> discovered = discovery.discover(discoveryUrl);
        assertThat(discovered).hasSize(1);
        assertThat(discovered.get(0).getOPEndpoint().toExternalForm()).isEqualTo("http://xri-test");

        // Verify the canonical ID returned as part of the document
        final GetMethod get = new GetMethod(discoveryUrl);
        new HttpClient().executeMethod(get);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final InputStream responseStream = get.getResponseBodyAsStream();
        try {
            final Document document = new SAXBuilder().build(responseStream);
            final Element xrdElement = document.getRootElement().getChild("XRD", xrdNamespace);
            assertThat(xrdElement.getChildText("CanonicalID", xrdNamespace)).isEqualTo("xri-canonical-id-return");
        } finally {
            responseStream.close();
        }
    }

    /**
     * Verify that the canonical ID return is the first that one matches the regex.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetCanonicalIdFirstMatch() throws Exception {
        final GetMethod get = new GetMethod("http://localhost:1337/abc123");
        new HttpClient().executeMethod(get);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final InputStream responseStream = get.getResponseBodyAsStream();
        try {
            final Document document = new SAXBuilder().build(responseStream);
            final Element xrdElement = document.getRootElement().getChild("XRD", xrdNamespace);
            assertThat(xrdElement.getChildText("CanonicalID", xrdNamespace)).isEqualTo("abc123-return");
        } finally {
            responseStream.close();
        }
    }
}
