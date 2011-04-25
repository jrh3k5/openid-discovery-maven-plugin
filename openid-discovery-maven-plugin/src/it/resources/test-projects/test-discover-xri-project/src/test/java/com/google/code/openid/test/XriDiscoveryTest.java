package com.google.code.openid.test;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

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

    /**
     * Test the discovery of a document serving out an XRI identifier.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testDiscoverXri() throws Exception {
        @SuppressWarnings("unchecked")
        final List<DiscoveryInformation> discovered = discovery.discover("http://localhost:1337/xri");
        assertThat(discovered).hasSize(1);
        assertThat(discovered.get(0).getOPEndpoint()).isEqualTo("http://xri-test");
    }
}
