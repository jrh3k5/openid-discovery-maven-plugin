package com.google.code.openid.mojo;

import static org.fest.assertions.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for {@link DiscoveryCanonicalId}.
 * 
 * @author JH016266
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { DiscoveryCanonicalId.class, Matcher.class, Pattern.class })
public class DiscoveryCanonicalIdTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();
    private DiscoveryCanonicalId discoveredId;

    /**
     * Set up the canonical ID for each test.
     */
    @Before
    public void setUp() {
        discoveredId = new DiscoveryCanonicalId();
    }

    /**
     * Test the retrieval of the canonical ID.
     */
    @Test
    public void testGetCanonicalId() {
        final String canonicalId = "blech";
        discoveredId.setCanonicalId(canonicalId);
        assertThat(discoveredId.getCanonicalId()).isEqualTo(canonicalId);
    }

    /**
     * Retrieval of the canonical ID should fail in the event of it not being set.
     */
    @Test
    public void testGetCanonicalIdNotSet() {
        expected.expect(IllegalStateException.class);
        expected.expectMessage("Canonical ID has not yet been set prior to retrieval.");
        discoveredId.getCanonicalId();
    }

    /**
     * The matching against a host regex should be reflective of the underlying {@link Pattern} object's {@link Matcher}.
     */
    @Test
    public void testMatchesHostRegex() {
        final String requestA = "requestA";
        final String requestB = "requestB";

        final String hostRegex = "host-regex";
        final Pattern pattern = mock(Pattern.class);
        mockStatic(Pattern.class);
        when(Pattern.compile(hostRegex)).thenReturn(pattern);

        final Matcher requestAMatcher = mock(Matcher.class);
        when(requestAMatcher.matches()).thenReturn(Boolean.TRUE);
        when(pattern.matcher(requestA)).thenReturn(requestAMatcher);

        final Matcher requestBMatcher = mock(Matcher.class);
        when(pattern.matcher(requestB)).thenReturn(requestBMatcher);

        discoveredId.setHostRegex(hostRegex);

        assertThat(discoveredId.matchesHostRegex(requestA)).isTrue();
        assertThat(discoveredId.matchesHostRegex(requestB)).isFalse();
    }

    /**
     * Attempting to match when the host regex hasn't been set should fail.
     */
    @Test
    public void testMatchesHostRegexNotSet() {
        expected.expect(IllegalStateException.class);
        expected.expectMessage("Host regex has not yet been set prior to retrieval.");
        discoveredId.matchesHostRegex("anything");
    }

    /**
     * Set the canonical ID to {@code null} should fail.
     */
    @Test
    public void testSetCanonicalIdNull() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Canonical ID cannot be null.");
        discoveredId.setCanonicalId(null);
    }

    /**
     * Setting the host regex to {@code null} should fail.
     */
    @Test
    public void testSetHostRegexNull() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Host regex cannot be null.");
        discoveredId.setHostRegex(null);
    }
}
