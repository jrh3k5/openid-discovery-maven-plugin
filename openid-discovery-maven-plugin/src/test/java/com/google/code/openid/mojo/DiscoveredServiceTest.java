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
 * Unit tests for {@link DiscoveredService}.
 * 
 * @author JH016266
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { DiscoveredService.class, Pattern.class, Matcher.class })
public class DiscoveredServiceTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();
    private DiscoveredService service;

    /**
     * Set up the service for each test.
     */
    @Before
    public void setUp() {
        service = new DiscoveredService();
    }

    /**
     * Test the retrieval of a set local ID.
     */
    @Test
    public void testGetLocalId() {
        final String localId = "a.local.id";
        service.setLocalId(localId);
        assertThat(service.getLocalId()).isEqualTo(localId);
    }

    /**
     * Test the retrieval of the priority.
     */
    @Test
    public void testGetPriority() {
        final int priority = 1337;
        service.setPriority(priority);
        assertThat(service.getPriority()).isEqualTo(priority);
    }

    /**
     * Test the retrieval of the type.
     */
    @Test
    public void getTypes() {
        final String type = "a.type";
        service.setTypes(new String[] { type });
        assertThat(service.getTypes().iterator().next()).isEqualTo(type);
    }

    /**
     * Attempting to retrieve the type when it has not been set should fail, as it's a required field.
     */
    @Test
    public void getTypeNotSet() {
        expected.expect(IllegalStateException.class);
        expected.expectMessage("Type(s) has not yet been set prior to retrieval.");
        service.getTypes();
    }

    /**
     * Test the retrieval of the URI.
     */
    @Test
    public void testGetUri() {
        final String uri = "a.uri";
        service.setUri(uri);
        assertThat(service.getUri()).isEqualTo(uri);
    }

    /**
     * Attempting to retrieve the URI when it has not been set should fail, as it's a required field.
     */
    @Test
    public void testGetUriNotSet() {
        expected.expect(IllegalStateException.class);
        expected.expectMessage("URI has not yet been set prior to retrieval.");
        service.getUri();
    }

    /**
     * The determination of a match should be a direct result of a {@link Matcher} output.
     */
    @Test
    public void testMatchesHostRegex() {
        final String hostRegex = "a.host.regex";
        final String uriA = "a.uri";
        final String uriB = "b.uri";

        final Pattern pattern = mock(Pattern.class);
        mockStatic(Pattern.class);
        when(Pattern.compile(hostRegex)).thenReturn(pattern);

        final Matcher matcherA = mock(Matcher.class);
        when(pattern.matcher(uriA)).thenReturn(matcherA);
        when(matcherA.matches()).thenReturn(Boolean.TRUE);

        final Matcher matcherB = mock(Matcher.class);
        when(pattern.matcher(uriB)).thenReturn(matcherB);

        service.setHostRegex(hostRegex);
        assertThat(service.matchesHostRegex(uriA)).isTrue();
        assertThat(service.matchesHostRegex(uriB)).isFalse();
    }

    /**
     * If the host regex is not yet set, then matching comparisons should fail.
     */
    @Test
    public void testMatchesHostRegexNotSet() {
        expected.expect(IllegalStateException.class);
        expected.expectMessage("Host regex pattern has not been set prior to retrieval.");
        service.matchesHostRegex("wreklrje");
    }

    /**
     * Setting a {@code null} host regex should fail.
     */
    @Test
    public void testSetHostRegexNull() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Host regular expression cannot be null.");
        service.setHostRegex(null);
    }

    /**
     * Setting a {@code null} local ID should fail.
     */
    @Test
    public void testSetLocalIdNull() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Local ID cannot be null.");
        service.setLocalId(null);
    }

    /**
     * Setting a {@code null} type array should fail.
     */
    @Test
    public void testSetTypesNullArray() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Type array cannot be null.");
        service.setTypes(null);
    }

    /**
     * Setting an array containing a {@code null} type should fail.
     */
    @Test
    public void testSetTypesNullValue() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Type cannot be null.");
        service.setTypes(new String[] { null });
    }

    /**
     * Setting a {@code null} URI should fail.
     */
    @Test
    public void testSetUriNull() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("URI cannot be null.");
        service.setUri(null);
    }
}
