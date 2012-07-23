package com.google.code.openid.test;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openid4java.discovery.Discovery;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.yadis.YadisException;

/**
 * Tests to verify basic discovery functionality.
 * 
 * @author jrh3k5
 * 
 */

public class BasicDiscoveryTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private final Discovery discovery = new Discovery();

    /**
     * Test discovery.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testDiscover() throws Exception {
        @SuppressWarnings("unchecked")
        final List<DiscoveryInformation> discovered = discovery.discover("http://localhost:1337/test");
        assertThat(discovered).hasSize(4);

        boolean hasTest1 = false;
        boolean hasTest2 = false;

        for (DiscoveryInformation info : discovered) {
            hasTest1 |= "http://test1".equals(info.getOPEndpoint().toExternalForm());
            hasTest2 |= "http://test2".equals(info.getOPEndpoint().toExternalForm());
        }

        assertThat(hasTest1).as("Test1 not found: " + discovered).isTrue();
        assertThat(hasTest2).as("Test2 not found: " + discovered).isTrue();
    }

    /**
     * Verify that a character range regex is supported.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCharacterRangeDiscovery() throws Exception {
        final List<DiscoveryInformation> acbDiscovered = discovery.discover("http://localhost:1337/regex/acb123");
        final List<DiscoveryInformation> accDiscovered = discovery.discover("http://localhost:1337/regex/acc123");
        assertThat(acbDiscovered).hasSize(2);
        assertThat(accDiscovered).hasSize(2);
        assertThat(acbDiscovered.get(0).getOPEndpoint().toExternalForm()).isEqualTo("http://regex-test/a-c+123");
        assertThat(accDiscovered.get(0).getOPEndpoint().toExternalForm()).isEqualTo("http://regex-test/a-c+123");
    }

    /**
     * Verify that wildcards work in the host regex.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testWildcardDiscovery() throws Exception {
        final List<DiscoveryInformation> dDiscovered = discovery.discover("http://localhost:1337/regex/abcdef");
        final List<DiscoveryInformation> eDiscovered = discovery.discover("http://localhost:1337/regex/abceef");
        assertThat(dDiscovered).hasSize(2);
        assertThat(eDiscovered).hasSize(2);
        assertThat(dDiscovered.get(0).getOPEndpoint().toExternalForm()).isEqualTo("http://regex-test/wildcard");
        assertThat(eDiscovered.get(0).getOPEndpoint().toExternalForm()).isEqualTo("http://regex-test/wildcard");
    }

    /**
     * Verify that wildcard characters can be escaped in the host regex.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testEscapedWildcardDiscovery() throws Exception {
        final List<DiscoveryInformation> discovered = discovery.discover("http://localhost:1337/regex/ghi.kl");
        assertThat(discovered).hasSize(2);
        assertThat(discovered.get(0).getOPEndpoint().toExternalForm()).isEqualTo("http://regex-test/escaped-wildcard");

        // The wildcard shouldn't be an actual wildcard
        expected.expect(YadisException.class);
        discovery.discover("http://localhost:1337/regex/ghijkl");
    }
}
