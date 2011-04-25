package com.google.code.openid.mojo;

import java.util.Collection;

import org.apache.maven.plugin.AbstractMojo;
import org.mortbay.jetty.Server;

import com.google.code.openid.mojo.jetty.DiscoveredServiceHandler;

public abstract class AbstractDiscoveryServerMojo extends AbstractMojo {
    private static Server server;

    protected Server getServer() {
        return server;
    }

    protected void startServer(int port, String xriCanonicalId, Collection<DiscoveredService> services)
            throws Exception {
        stopServer();
        server = new Server(port);
        server.setHandler(new DiscoveredServiceHandler(xriCanonicalId, services));
        server.start();
    }

    protected void stopServer() throws Exception {
        if (server != null && !server.isStopping())
            server.stop();
    }
}
