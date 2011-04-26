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
import com.google.code.openid.mojo.DiscoveryCanonicalId;
import com.google.code.openid.mojo.openid.DiscoveredServiceWriter;

/**
 * An {@link AbstractHandler} used to serve out information about a requested service.
 * 
 * @author jrh3k5
 * 
 */

public class DiscoveredServiceHandler extends AbstractHandler {
    private final Collection<DiscoveredService> services;
    private final List<DiscoveryCanonicalId> canonicalIds;
    private final DiscoveredServiceWriter writer;

    /**
     * Create a handler.
     * 
     * @param canonicalIds
     *            A {@link List} of {@link DiscoveryCanonicalId} objects representing the canonical IDs that could be written out.
     * @param services
     *            A {@link Collection} of {@link DiscoveredService} objects that represent the services to be handled by this handler.
     */
    public DiscoveredServiceHandler(List<DiscoveryCanonicalId> canonicalIds, Collection<DiscoveredService> services) {
        this(canonicalIds, services, new DiscoveredServiceWriter());
    }

    /**
     * Create a handler.
     * 
     * @param canonicalIds
     *            A {@link List} of {@link DiscoveryCanonicalId} objects representing the canonical IDs that could be written out.
     * @param services
     *            A {@link Collection} of {@link DiscoveredService} objects that represent the services to be handled by this handler.
     * @param writer
     *            A {@link DiscoveredServiceWriter} used to write out information about the services matching the request.
     */
    public DiscoveredServiceHandler(List<DiscoveryCanonicalId> canonicalIds, Collection<DiscoveredService> services, DiscoveredServiceWriter writer) {
        if (services == null)
            throw new IllegalArgumentException("Services cannot be null.");

        if (writer == null)
            throw new IllegalArgumentException("Writer cannot be null.");
        
        if(canonicalIds == null)
            throw new IllegalArgumentException("The canonical IDs cannot be null.");

        this.services = services;
        this.writer = writer;
        this.canonicalIds = canonicalIds;
    }

    /**
     * {@inheritDoc}
     */
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        final List<DiscoveredService> matches = new LinkedList<DiscoveredService>();
        for (DiscoveredService service : services)
            if (service.matchesHostRegex(target))
                matches.add(service);

        DiscoveryCanonicalId matchId = null;
        for(DiscoveryCanonicalId canonicalId : canonicalIds)
            if(canonicalId.matchesHostRegex(target)) {
                matchId = canonicalId;
                break;
            }
        
        if (!matches.isEmpty() || matchId != null) {            
            response.setHeader("content-type", "application/xrds+xml");
            response.setStatus(HttpServletResponse.SC_OK);
            writer.write(matchId, matches, response.getOutputStream());
            ((Request) request).setHandled(true);
        }
    }
}
