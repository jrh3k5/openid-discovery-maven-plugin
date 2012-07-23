package com.google.code.openid.mojo.openid;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import com.google.code.openid.mojo.DiscoveredService;
import com.google.code.openid.mojo.DiscoveryCanonicalId;

/**
 * A utility object to write information out about a given set of services.
 * 
 * @author jrh3k5
 * 
 */

public class DiscoveredServiceWriter {
    private final Namespace xrdNamespace = Namespace.getNamespace("xri://$xrd*($v*2.0)");
    private final Namespace xrdsNamespace = Namespace.getNamespace("xri://$xrds");

    /**
     * Write out information about services.
     * 
     * @param canonicalId
     *            The optional {@link DiscoveryCanonicalId} representing, if not
     *            {@code null}, the {@code <CanonicalID />} to be written out as
     *            part of the document.
     * @param services
     *            A {@link Collection} of {@link DiscoveredService} objects
     *            representing the service data to be written out.
     * @param outputStream
     *            The {@link OutputStream} to which the data is to be written.
     * @throws IOException
     *             If any errors occur during the write-out.
     */
    public void write(DiscoveryCanonicalId canonicalId, Collection<DiscoveredService> services,
            OutputStream outputStream) throws IOException {
        if (services == null)
            throw new IllegalArgumentException("Services cannot be null.");

        if (outputStream == null)
            throw new IllegalArgumentException("Output stream cannot be null.");

        final Element xrdElement = new Element("XRD", xrdNamespace);
        if (canonicalId != null)
            xrdElement.addContent(asElement("CanonicalID", xrdNamespace, canonicalId.getCanonicalId()));

        for (DiscoveredService service : services) {
            final Element serviceElement = new Element("Service", xrdNamespace);
            final Integer servicePriority = service.getPriority();
            if (servicePriority != null)
                serviceElement.setAttribute("priority", servicePriority.toString());

            for(String type : service.getTypes()) {
            	serviceElement.addContent(asElement("Type", xrdNamespace, type));
            }
            
            serviceElement.addContent(asElement("URI", xrdNamespace, service.getUri()));

            if (service.getLocalId() != null)
                serviceElement.addContent(asElement("LocalID", xrdNamespace, service.getLocalId()));

            xrdElement.addContent(serviceElement);
        }

        final Element xrdsRoot = new Element("XRDS", xrdsNamespace);
        xrdsRoot.addContent(xrdElement);

        new XMLOutputter().output(new Document(xrdsRoot), outputStream);

    }

    /**
     * Create a JDOM element out of the given data.
     * 
     * @param name
     *            The name of the element.
     * @param namespace
     *            The namespace of the element.
     * @param textValue
     *            The value to be inserted as the textual value of the element.
     * @return An {@link Element} representing the given data.
     */
    private Element asElement(String name, Namespace namespace, String textValue) {
        final Element element = new Element(name, namespace);
        element.setText(textValue);
        return element;
    }
}
