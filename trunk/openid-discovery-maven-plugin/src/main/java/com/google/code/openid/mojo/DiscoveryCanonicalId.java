package com.google.code.openid.mojo;

import java.util.regex.Pattern;

/**
 * A bean representing a canonical ID written out to a discovery document.
 * <p />
 * The following fields are required to be set:
 * <ul>
 * <li>{@link #setHostRegex(String) hostRegex}</li>
 * <li>{@link #setCanonicalId(String) canonicalId}</li>
 * </ul>
 * 
 * @author JH016266
 * 
 */

// TODO: unit test
public class DiscoveryCanonicalId {
    private Pattern hostRegex;
    private String canonicalId;

    /**
     * Get the canonical ID.
     * 
     * @return The canonical ID.
     * @throws IllegalStateException
     *             If the canonical ID has not yet been {@link #setCanonicalId(String) set}.
     */
    public String getCanonicalId() {
        if (canonicalId == null)
            throw new IllegalStateException("Canonical ID has not yet been set prior to retrieval.");

        return canonicalId;
    }

    /**
     * Determine whether or not the host regex matches the given URI.
     * 
     * @param uri
     *            The URI to be matched against the regex pattern.
     * @return {@code true} if the regex matches; {@code false} if not.
     * @throws IllegalStateException
     *             If the host regex has not yet been {@link #setHostRegex(String) set}.
     */
    public boolean matchesHostRegex(String uri) {
        if (hostRegex == null)
            throw new IllegalStateException("Host regex has not yet been set prior to retrieval.");
    
        return hostRegex.matcher(uri).matches();
    }

    /**
     * Set the canonical ID.
     * 
     * @param canonicalId
     *            The canonical ID.
     * @throws IllegalArgumentException
     *             If the given canonical ID is {@code null}.
     */
    public void setCanonicalId(String canonicalId) {
        if (canonicalId == null)
            throw new IllegalArgumentException("Canonical ID cannot be null.");
    
        this.canonicalId = canonicalId;
    }

    /**
     * Set the host regular expression, which determines what requests this canonical ID will match.
     * 
     * @param hostRegex
     *            The host regex.
     * @throws IllegalArgumentException
     *             If the given regex is {@code null}.
     */
    public void setHostRegex(String hostRegex) {
        if (hostRegex == null)
            throw new IllegalArgumentException("Host regex cannot be null.");

        this.hostRegex = Pattern.compile(hostRegex);
    }
}
