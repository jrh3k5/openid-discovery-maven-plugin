package com.google.code.openid.mojo;

import java.util.Arrays;

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
     * The service information that should served out by the discovery server.
     * 
     * @parameter
     * @required
     */
    private DiscoveredService[] services;

    /**
     * The XRI canonical ID to be used when serving out discovery information with XRI identifiers.
     * 
     * @parameter
     */
    private String xriCanonicalId;

    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            startServer(serverPort, xriCanonicalId, Arrays.asList(services));
        } catch (Exception e) {
            throw new MojoExecutionException("Error starting discovery server.", e);
        }
    }

}
