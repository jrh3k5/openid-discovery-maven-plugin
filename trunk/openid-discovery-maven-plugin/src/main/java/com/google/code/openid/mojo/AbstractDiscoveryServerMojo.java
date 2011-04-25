package com.google.code.openid.mojo;

import java.util.Collection;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.mortbay.jetty.Server;

import com.google.code.openid.mojo.jetty.DiscoveredServiceHandler;

/**
 * Abstract skeleton of a mojo used to manage a discovery server.
 * 
 * @author jrh3k5
 * 
 */

public abstract class AbstractDiscoveryServerMojo extends AbstractMojo {
    private static Server server;

    /**
     * Get the server in use.
     * 
     * @return A {@link Server} reference to the server being used as a discovery server; if {@code null}, then no server has been started.
     */
    protected Server getServer() {
        return server;
    }

    /**
     * Start the discovery server. If one has been previously started, then it will be stopped first.
     * 
     * @param port
     *            The port on which the server is to listen.
     * @param canonicalIds
     *            A {@link List} of {@link DiscoveryCanonicalId} objects representing the canonical IDs that could be written out.
     * @param services
     *            A {@link Collection} of {@link DiscoveredService} objects representing the services to be hosted by this discovery service.
     * @throws Exception
     *             If any errors occur during the startup of the server.
     */
    protected void startServer(int port, List<DiscoveryCanonicalId> canonicalIds, Collection<DiscoveredService> services) throws Exception {
        stopServer();
        server = new Server(port);
        server.setHandler(new DiscoveredServiceHandler(canonicalIds, services));
        server.start();
    }

    /**
     * Stop the discovery server if it has been started.
     * 
     * @throws Exception
     *             If any errors occur during the shutdown of the server.
     */
    protected void stopServer() throws Exception {
        if (server != null && !server.isStopping())
            server.stop();
    }
}
