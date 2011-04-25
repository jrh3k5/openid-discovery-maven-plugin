package com.google.code.openid.mojo.openid;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import com.google.code.openid.mojo.DiscoveredService;

/**
 * A utility object to write information out about a given set of services.
 * 
 * @author jrh3k5
 * 
 */

public class DiscoveredServiceWriter {
    private final Namespace xrdsNamespace;
    private final Namespace xrdNamespace;

    /**
     * Create a writer.
     */
    public DiscoveredServiceWriter() {
        this(Namespace.getNamespace("xri://$xrds"), Namespace.getNamespace("xri://$xrd*($v*2.0)"));
    }

    /**
     * Create a writer.
     * 
     * @param xrdsNamespace
     *            The {@link Namespace} of the {@code <XRDS />} element.
     * @param xrdNamespace
     *            The {@link Namespace} of the {@code <XRD />} element.
     * @throws IllegalArgumentException
     *             If either of the given namespaces are {@code null}.
     */
    public DiscoveredServiceWriter(Namespace xrdsNamespace, Namespace xrdNamespace) {
        if (xrdsNamespace == null)
            throw new IllegalArgumentException("XRDS namespace cannot be null.");

        if (xrdNamespace == null)
            throw new IllegalArgumentException("XRD namespace cannot be null.");

        this.xrdsNamespace = xrdsNamespace;
        this.xrdNamespace = xrdNamespace;
    }

    /**
     * Write out information about services.
     * 
     * @param xriCanonicalId
     *            The optional {@code <CanonicalID />} to be written out as part of the document.
     * @param services
     *            A {@link Collection} of {@link DiscoveredService} objects representing the service data to be written out.
     * @param outputStream
     *            The {@link OutputStream} to which the data is to be written.
     * @throws IOException
     *             If any errors occur during the write-out.
     */
    public void write(String xriCanonicalId, Collection<DiscoveredService> services, OutputStream outputStream) throws IOException {
        if (services == null)
            throw new IllegalArgumentException("Services cannot be null.");

        if (outputStream == null)
            throw new IllegalArgumentException("Output stream cannot be null.");

        final Element xrdElement = new Element("XRD", xrdNamespace);
        if (xriCanonicalId != null)
            xrdElement.addContent(asElement("CanonicalID", xrdNamespace, xriCanonicalId));

        for (DiscoveredService service : services) {
            final Element serviceElement = new Element("Service", xrdNamespace);
            final Integer servicePriority = service.getPriority();
            if (servicePriority != null)
                serviceElement.setAttribute("priority", servicePriority.toString());

            serviceElement.addContent(asElement("Type", xrdNamespace, service.getType()));
            serviceElement.addContent(asElement("URI", xrdNamespace, service.getUri()));

            if (service.getLocalId() != null)
                serviceElement.addContent(asElement("LocalID", xrdNamespace, service.getLocalId()));

            xrdElement.addContent(serviceElement);
        }

        final Element xrdsRoot = new Element("XRDS", xrdsNamespace);
        xrdsRoot.addContent(xrdElement);

        new XMLOutputter().output(new Document(xrdsRoot), outputStream);

    }

    // TODO: Javadoc
    private Element asElement(String name, Namespace namespace, String textValue) {
        final Element element = new Element(name, namespace);
        element.setText(textValue);
        return element;
    }
}
