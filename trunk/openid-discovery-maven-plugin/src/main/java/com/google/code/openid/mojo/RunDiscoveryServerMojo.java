package com.google.code.openid.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * A mojo that runs a discovery server and blocks the current thread.
 * 
 * @author jrh3k5
 * 
 * @goal run
 */

// TODO: unit test
public class RunDiscoveryServerMojo extends StartDiscoveryServerMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();
        try {
            getServer().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
