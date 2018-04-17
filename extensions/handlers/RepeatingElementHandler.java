package com.olytech.tika.extensions.handlers;

import com.olytech.tika.extensions.output.MetadataListener;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 11/26/12
 * Time: 2:33 PM
 * This allows us to take a single XML file, and create a series of Metadata documents from it, by
 * selecting a recurring element, and creating a new Metadata object each time this tag is encountered.
 * The actual work of handling the events once inside that tak is left to the "subContentHandler" which
 * is the only abstract method in this class.
 * If there is metadata defaults, or values you wish to see in all returned metadata, you can set it in the
 * metadata object provided in the constructor, and those values will be copied over.
 */
public abstract class RepeatingElementHandler extends DefaultHandler {

    protected ContentHandler handler;
    protected Metadata                origMeta;
    protected Metadata                metadata;
    protected MetadataListener listener;
    protected ParseContext            context;
    protected String element;
    private   boolean                 inElement;  // True if inside the element section.
    private   int                     count;

    /**
     * This method is called each time a new sub-section of the xml is encountered.
     * It will pass in a fresh metadata object to be populated with data.
     * @param metadata
     * @return
     */
    public abstract ContentHandler getSubContentHandler(Metadata metadata);

    /**
     * Constrcutor requires a listener, which will be notified as each new
     * metadata object is populated from the XML subsection.
     * @param element The local name of the element to use to generate metadata sections.
     * @param listener
     * @param metadata
     * @param context
     */
    public RepeatingElementHandler(String element, MetadataListener listener, Metadata metadata, ParseContext context) {
        this.origMeta = metadata;
        this.metadata = createMetadata();
        this.handler  = getSubContentHandler(this.metadata);
        this.listener = listener;
        this.context  = context;
        this.element  = element;
        this.count    = 0;
    }

    private Metadata createMetadata() {
        metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "application/xml");
        for(String name : origMeta.names())
            metadata.set(name, origMeta.get(name));
        return metadata;
    }

    /**
     * Sets the underlying content handler. All future SAX events will be
     * directed to this handler instead of the one that was previously used.
     *
     * @param handler content handler
     */
    protected void setContentHandler(ContentHandler handler) {
        assert handler != null;
        this.handler = handler;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        try {
            handler.startPrefixMapping(prefix, uri);
        } catch (SAXException e) {
            handleException(e);
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        try {
            handler.endPrefixMapping(prefix);
        } catch (SAXException e) {
            handleException(e);
        }
    }

    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        try {
            handler.processingInstruction(target, data);
        } catch (SAXException e) {
            handleException(e);
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        handler.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        try {
            handler.startDocument();
        } catch (SAXException e) {
            handleException(e);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            System.out.println("Total " + element + " encountered: " + count);
            handler.endDocument();
        } catch (SAXException e) {
            handleException(e);
        }
    }

    @Override
    public void startElement(
            String uri, String localName, String name, Attributes atts)
            throws SAXException {
        try {
            if(localName.equals(element)) {
                metadata  = createMetadata();
                handler   = getSubContentHandler(metadata);
                inElement = true;
                count++;
            }
            if (inElement == true) {
                handler.startElement(uri, localName, name, atts);
            }
        } catch (SAXException e) {
            handleException(e);
        }
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        try {
            if(localName.equals(element)) {
                listener.handle(metadata);
                inElement = false;
            }
            if(inElement == true) {
                handler.endElement(uri, localName, name);
            }
        } catch (SAXException e) {
            handleException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        try {
            handler.characters(ch, start, length);
        } catch (SAXException e) {
            handleException(e);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        try {
            handler.ignorableWhitespace(ch, start, length);
        } catch (SAXException e) {
            handleException(e);
        }
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        try {
            handler.skippedEntity(name);
        } catch (SAXException e) {
            handleException(e);
        }
    }

    @Override
    public String toString() {
        return handler.toString();
    }

    /**
     * Handle any exceptions thrown by methods in this class. This method
     * provides a single place to implement custom exception handling. The
     * default behaviour is simply to re-throw the given exception, but
     * subclasses can also provide alternative ways of handling the situation.
     *
     * @param exception the exception that was thrown
     * @throws org.xml.sax.SAXException the exception (if any) thrown to the client
     */
    protected void handleException(SAXException exception) throws SAXException {
        System.out.println("Repeating Element Handler encountered an exception: " + exception.getMessage());
        if (exception.getMessage().startsWith("Failed to lookup CPC code")){
        	System.out.println("ignoring missing CPC code.");
        }
        else {
        	throw exception;
        }
    }

}
