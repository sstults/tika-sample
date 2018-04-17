package com.olytech.tika.extensions.handlers;

import org.apache.commons.lang.StringUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Borrowed heavily from Tika's XML parsers, is constructed to look for exactly one tag
 * in the document, find it's content, and stick it in a metadata field.
 * Date: 11/20/12
 * Time: 9:06 AM
 */
public class ElementPathHandler extends DefaultHandler {

    protected String name;
    protected Property      property;
    protected String element;
    protected Metadata      metadata;
    protected Stack<String> ancestors;

    /**
     * The buffer used to capture characters inside standard elements.
     */
    protected final StringBuilder buffer = new StringBuilder();


    public ElementPathHandler(Metadata metadata, String name, String element) {
        this.metadata  = metadata;
        this.name      = name;
        this.element   = element;
        this.ancestors = new Stack<String>();
    }

    public ElementPathHandler(Metadata metadata, Property property, String element) {
        this.metadata  = metadata;
        this.name      = property.getName();
        this.property  = property;
        this.ancestors = new Stack<String>();
    }

    protected boolean matches() {
       String ancestory;
       ancestory = StringUtils.join(ancestors, "/");
       return this.element.equals(ancestory);
    }

    @Override
    public void startElement(
        String uri, String localName, String name, Attributes attributes) throws SAXException {
        ancestors.push(localName);
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if(matches()) {
            addMetadata(buffer.toString().trim());
        }
        ancestors.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        // We need to append to both buffers since we don't if we're inside a bag until we're done
        if (matches()) {
             buffer.append(ch, start, length);
        }
    }

    @Override
    public void ignorableWhitespace(final char[] ch, final int start,
                                    final int length) {
        // we want the whitespace too.. just throw it to characters
        buffer.append(ch, start, length);
    }

    protected void addMetadata(String value) throws SAXException {
        addMetadata(this.name, value);
    }

    protected void addMetadata(String key, String value) throws SAXException {
        if (value == null || value.length() == 0) return;

        if (metadata.isMultiValued(key)) {

            // Add the value, assuming it's not already there
            List<String> previous = Arrays.asList(metadata.getValues(key));
            if (!previous.contains(value)) {
                //FIXME:  this isn't finished.
                if (property != null && key.equals("")) {
                    metadata.add(property, value);
                } else {
                    metadata.add(key, value);
                }
            }
        } else {
            // Set the value, assuming it's not already there
            String previous = metadata.get(key);
            if (previous != null && previous.length() > 0) {
                if (!previous.equals(value)) {
                    if (property != null && key.equals(property.getName())) {
                        if (property.isMultiValuePermitted()) {
                            metadata.add(property, value);
                        } else {
                            // Replace the existing value if isMultiValuePermitted is false
                            metadata.set(property, value);
                        }
                    } else {
                        metadata.add(key, value);
                    }
                }
            } else {
                if (property != null && key.equals(property.getName())) {
                    metadata.set(property, value);
                } else {
                    metadata.set(key, value);
                }
            }
        }

    }

}
