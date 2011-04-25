package com.google.code.openid.mojo;

import java.util.regex.Pattern;

/**
 * Definition of a service that is served as a service in a discovery document.
 * <p />
 * The following parameters are <b>required</b> to be set:
 * <ul>
 * <li>{@link #setHostRegex(String) hostRegex}</li>
 * <li>{@link #setUri(String) uri}</li>
 * <li>{@link #setType(String) type}</li>
 * </ul>
 * 
 * @author jrh3k5
 * 
 */

// TODO: unit tests
public class DiscoveredService {
    private Pattern hostRegex;
    private String uri;
    private String type;
    private Integer priority;
    private String localId;

    /**
     * Get the local ID.
     * 
     * @return The local ID. This can be {@code null}.
     */
    public String getLocalId() {
        return localId;
    }

    /**
     * Get the priority of the service.
     * 
     * @return The priority of the service; if {@code null}, this has not been set.
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Get the type URL associated with the service.
     * 
     * @return The type URL.
     * @throws IllegalStateException
     *             If the type URL has not yet been {@link #setType(String) set}.
     */
    public String getType() {
        if (type == null)
            throw new IllegalStateException("Type has not yet been set prior to retrieval.");

        return type;
    }

    /**
     * Get the URI for the discovered service.
     * 
     * @return The URI of the service.
     * @throws IllegalStateException
     *             If the URI has not yet been {@link #setUri(String)}.
     */
    public String getUri() {
        if (uri == null)
            throw new IllegalStateException("URI has not yet been set prior to retrieval.");

        return uri;
    }

    /**
     * Determine whether or not the given URI matches the stored host regex.
     * 
     * @param uri
     *            The URI to be matched against the host regex.
     * @return {@code true} if the given URI matches the host regex; {@code false} if not.
     * @throws IllegalStateException
     *             If the host regex has not yet been {@link #setHostRegex(String) set}.
     */
    public boolean matchesHostRegex(String uri) {
        if (hostRegex == null)
            throw new IllegalStateException("Host regex pattern has not been set prior to retrieval.");

        return hostRegex.matcher(uri).matches();
    }

    /**
     * Set the regular expression used to match against the request against the server.
     * 
     * @param hostRegex
     *            The regular expression to match against the request path.
     * @throws IllegalArgumentException
     *             If the given expression is {@code null}.
     */
    public void setHostRegex(String hostRegex) {
        if (hostRegex == null)
            throw new IllegalArgumentException("Host regular expression cannot be null.");

        this.hostRegex = Pattern.compile(hostRegex);
    }

    /**
     * Set the content of the {@code <LocalID />} element in a service XRDS element.
     * 
     * @param localId
     *            The local ID.
     * @throws IllegalArgumentException
     *             If the given local ID is {@code null}.
     */
    public void setLocalId(String localId) {
        if (localId == null)
            throw new IllegalArgumentException("Local ID cannot be null.");

        this.localId = localId;
    }

    /**
     * Set the priority of the service.
     * 
     * @param priority
     *            The priority of the service.
     */
    public void setPriority(int priority) {
        this.priority = Integer.valueOf(priority);
    }

    /**
     * Set the type URL of the discovered service.
     * 
     * @param type
     *            The type URL.
     * @throws IllegalArgumentException
     *             If the given URL is {@code null}.
     */
    public void setType(String type) {
        if (type == null)
            throw new IllegalArgumentException("Type cannot be null.");

        this.type = type;
    }

    /**
     * Set the value of the {@code <URI />} tag for a service.
     * 
     * @param uri
     *            The URI of the service.
     * @throws IllegalArgumentException
     *             If the given URI is {@code null}.
     */
    public void setUri(String uri) {
        if (uri == null)
            throw new IllegalArgumentException("URI cannot be null.");

        this.uri = uri;
    }
}
