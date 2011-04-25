package com.google.code.openid.mojo.jetty;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

import com.google.code.openid.mojo.DiscoveredService;
import com.google.code.openid.mojo.openid.DiscoveredServiceWriter;

/**
 * An {@link AbstractHandler} used to serve out information about a requested service.
 * 
 * @author jrh3k5
 * 
 */

public class DiscoveredServiceHandler extends AbstractHandler {
    private final Collection<DiscoveredService> services;
    private final DiscoveredServiceWriter writer;
    private final String xriCanonicalId;

    /**
     * Create a handler.
     * 
     * @param xriCanonicalId
     *            The optional canonical ID to be used in the served-out discovery document; this can be {@code null}.
     * @param services
     *            A {@link Collection} of {@link DiscoveredService} objects that represent the services to be handled by this handler.
     */
    public DiscoveredServiceHandler(String xriCanonicalId, Collection<DiscoveredService> services) {
        this(xriCanonicalId, services, new DiscoveredServiceWriter());
    }

    /**
     * Create a handler.
     * 
     * @param xriCanonicalId
     *            The optional canonical ID to be used in the served-out discovery document; this can be {@code null}.
     * @param services
     *            A {@link Collection} of {@link DiscoveredService} objects that represent the services to be handled by this handler.
     * @param writer
     *            A {@link DiscoveredServiceWriter} used to write out information about the services matching the request.
     */
    public DiscoveredServiceHandler(String xriCanonicalId, Collection<DiscoveredService> services, DiscoveredServiceWriter writer) {
        if (services == null)
            throw new IllegalArgumentException("Services cannot be null.");

        if (writer == null)
            throw new IllegalArgumentException("Writer cannot be null.");

        this.services = services;
        this.writer = writer;
        this.xriCanonicalId = xriCanonicalId;
    }

    /**
     * {@inheritDoc}
     */
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        final List<DiscoveredService> matches = new LinkedList<DiscoveredService>();
        for (DiscoveredService service : services)
            if (service.matchesHostRegex(target))
                matches.add(service);

        if (!matches.isEmpty()) {
            response.setHeader("content-type", "application/xrds+xml");
            response.setStatus(HttpServletResponse.SC_OK);
            writer.write(xriCanonicalId, matches, response.getOutputStream());
            ((Request) request).setHandled(true);
        }
    }
}
