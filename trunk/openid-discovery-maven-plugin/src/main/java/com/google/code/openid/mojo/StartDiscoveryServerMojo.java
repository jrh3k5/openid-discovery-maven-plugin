package com.google.code.openid.mojo;

import java.util.Arrays;
import java.util.Collections;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * A mojo used to emulate an OpenID discovery server.
 * 
 * @author jrh3k5
 * 
 * @goal start
 * @phase pre-integration-test
 */

// TODO: unit test
public class StartDiscoveryServerMojo extends AbstractDiscoveryServerMojo {
    /**
     * The port on which the discovery server should receive requests.
     * 
     * @parameter expression="serverPort"
     * @required
     */
    private int serverPort;

    /**
     * The service information that should served out by the discovery server. The served-out discovery document will be an amalgamation of all services whose
     * {@link DiscoveredService#setHostRegex(String) hostRegex} match the request URI.
     * 
     * @parameter
     * @required
     */
    private DiscoveredService[] services;

    /**
     * The XRI canonical ID to be used when serving out discovery information with XRI identifiers. Unlike the {@link #services services} parameter, the served-out document will contain <i>only</i>
     * the first canonical ID that matches (if any).
     * 
     * @parameter
     */
    private DiscoveryCanonicalId[] canonicalIds;

    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            startServer(serverPort, canonicalIds == null ? Collections.<DiscoveryCanonicalId> emptyList() : Arrays.asList(canonicalIds), Arrays.asList(services));
        } catch (Exception e) {
            throw new MojoExecutionException("Error starting discovery server.", e);
        }
    }

}
