package com.google.code.openid.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * A mojo to stop the previously-started discovery server.
 * 
 * @author jrh3k5
 * 
 * @goal stop
 * @phase post-integration-test
 */

public class StopDiscoveryServerMojo extends AbstractDiscoveryServerMojo {
    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            stopServer();
        } catch (Exception e) {
            getLog().debug("Error stopping discovery server.", e);
        }
    }

}
