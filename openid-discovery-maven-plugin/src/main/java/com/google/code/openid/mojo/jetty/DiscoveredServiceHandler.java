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

public class DiscoveredServiceHandler extends AbstractHandler {
    private final Collection<DiscoveredService> services;
    private final DiscoveredServiceWriter writer;
    private final String xriCanonicalId;
    
    public DiscoveredServiceHandler(String xriCanonicalId, Collection<DiscoveredService> services) {
        this(xriCanonicalId, services, new DiscoveredServiceWriter());
    }

    public DiscoveredServiceHandler(String xriCanonicalId, Collection<DiscoveredService> services,
            DiscoveredServiceWriter writer) {
        if(services == null)
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
    public void handle(String target, HttpServletRequest request, HttpServletResponse response,
            int dispatch) throws IOException, ServletException {
        // TODO: remove
        System.out.println("Target is: " + target);
        final List<DiscoveredService> matches = new LinkedList<DiscoveredService>();
        for(DiscoveredService service : services)
            if(service.getHostRegex().matcher(target).matches())
                matches.add(service);
        
        if(!matches.isEmpty()) {
            response.setHeader("content-type", "application/xrds+xml");
            response.setStatus(HttpServletResponse.SC_OK);
            writer.write(xriCanonicalId, matches, response.getOutputStream());
            ((Request) request).setHandled(true);
        }
    }
}
