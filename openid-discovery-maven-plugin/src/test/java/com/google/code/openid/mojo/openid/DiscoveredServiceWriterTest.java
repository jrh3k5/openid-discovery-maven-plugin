package com.google.code.openid.mojo.openid;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.code.openid.mojo.DiscoveredService;

/**
 * Unit tests for {@link DiscoveredServiceWriter}.
 * 
 * @author jrh3k5
 * 
 */

public class DiscoveredServiceWriterTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private final DiscoveredServiceWriter writer = new DiscoveredServiceWriter();
    private final Namespace xrdNamespace = Namespace.getNamespace("xri://$xrd*($v*2.0)");
    private final Namespace xrdsNamespace = Namespace.getNamespace("xri://$xrds");

    /**
     * Test the writing of services to an output stream.
     * 
     * @throws Exception
     */
    @Test
    public void testWrite() throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        final String type = "this is the type";
        final String uri = "this is the uri";
        final DiscoveredService service = new DiscoveredService();
        service.setType(type);
        service.setUri(uri);

        writer.write(null, Collections.singleton(service), out);

        final Collection<DiscoveredService> services = getServices(toDocument(out));
        assertThat(services).hasSize(1);

        final DiscoveredService discovered = services.iterator().next();
        assertThat(discovered.getUri()).isEqualTo(uri);
        assertThat(discovered.getType()).isEqualTo(type);
        assertThat(discovered.getPriority()).isNull();
        assertThat(discovered.getLocalId()).isNull();
    }

    /**
     * Writing with a {@code null} {@link Collection} of services should fail.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteNullServices() throws Exception {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Services cannot be null.");
        writer.write(null, null, new ByteArrayOutputStream());
    }

    /**
     * Writing with a {@code null} {@link OutputStream} should fail.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteNullOutputStream() throws Exception {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Output stream cannot be null.");
        writer.write(null, Collections.<DiscoveredService> emptySet(), null);
    }

    /**
     * Test writing out a document with a caonical ID.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteWithCanonicalId() throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        final String xriCanonicalId = "this is a canonical ID";
        writer.write(xriCanonicalId, Collections.<DiscoveredService> emptySet(), out);

        assertThat(getCanonicalId(toDocument(out))).isEqualTo(xriCanonicalId);
    }

    /**
     * Test the writing of an element with a local ID.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteWithLocalId() throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
    
        final String type = "this is the type";
        final String uri = "this is the uri";
        final String localId = "this is a local id";
        final DiscoveredService service = new DiscoveredService();
        service.setType(type);
        service.setUri(uri);
        service.setLocalId(localId);
    
        writer.write(null, Collections.singleton(service), out);
    
        final Collection<DiscoveredService> services = getServices(toDocument(out));
        assertThat(services).hasSize(1);
    
        final DiscoveredService discovered = services.iterator().next();
        assertThat(discovered.getUri()).isEqualTo(uri);
        assertThat(discovered.getType()).isEqualTo(type);
        assertThat(discovered.getLocalId()).isEqualTo(localId);
    }

    /**
     * Test the writing of a service with a priority.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteWithPriority() throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        final String type = "this is the type";
        final String uri = "this is the uri";
        final int priority = 1347;
        final DiscoveredService service = new DiscoveredService();
        service.setType(type);
        service.setUri(uri);
        service.setPriority(priority);

        writer.write(null, Collections.singleton(service), out);

        final Collection<DiscoveredService> services = getServices(toDocument(out));
        assertThat(services).hasSize(1);

        final DiscoveredService discovered = services.iterator().next();
        assertThat(discovered.getUri()).isEqualTo(uri);
        assertThat(discovered.getType()).isEqualTo(type);
        assertThat(discovered.getPriority()).isEqualTo(priority);
    }

    /**
     * Get the canonical ID from a discovery document.
     * 
     * @param document
     *            A {@link Document} object representing the discovery document.
     * @return The canonical ID, or {@code null} if none exists in the document.
     */
    private String getCanonicalId(Document document) {
        return document.getRootElement().getChild("XRD", xrdNamespace).getChildText("CanonicalID", xrdNamespace);
    }

    /**
     * Get the services from a discovery document.
     * 
     * @param document
     *            A {@link Document} object representing the discovery document.
     * @return A {@link Collection} of {@link DiscoveredService} objects representing the services parsed from the given document.
     */
    @SuppressWarnings("unchecked")
    private Collection<DiscoveredService> getServices(Document document) {
        final Element xrdsElement = document.getRootElement();
        assertThat(xrdsElement.getName()).isEqualTo("XRDS");
        assertThat(xrdsElement.getNamespace()).isEqualTo(xrdsNamespace);

        final Element xrdElement = document.getRootElement().getChild("XRD", xrdNamespace);
        final List<DiscoveredService> services = new LinkedList<DiscoveredService>();
        for (Element serviceElement : (List<Element>) xrdElement.getChildren("Service", xrdNamespace)) {
            final DiscoveredService service = new DiscoveredService();
            service.setUri(serviceElement.getChildText("URI", xrdNamespace));
            service.setType(serviceElement.getChildText("Type", xrdNamespace));

            final Attribute priority = serviceElement.getAttribute("priority");
            if (priority != null)
                service.setPriority(Integer.parseInt(priority.getValue()));

            final Element localIdElement = serviceElement.getChild("LocalID", xrdNamespace);
            if (localIdElement != null)
                service.setLocalId(localIdElement.getText());

            services.add(service);
        }

        return services;
    }

    /**
     * Convert an output stream containing the contents of a written-out discovery document to a JDOM document.
     * 
     * @param outputStream
     *            A {@link ByteArrayOutputStream} representing the written-out document.
     * @return A {@link Document} representing the written-out document.
     * @throws IOException
     *             If any errors occur during the read-in of the document.
     * @throws JDOMException
     *             If any errors occur during the parsing of the document.
     */
    private Document toDocument(ByteArrayOutputStream outputStream) throws IOException, JDOMException {
        return new SAXBuilder().build(new ByteArrayInputStream(outputStream.toByteArray()));
    }
}
