package com.olytech.tika.extensions.handlers;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/*
 * Constructed to look for a very specific element, take all content within
 * that element (including it's tags) and place the whole thing in a metadata field.
 */
public class AttributePathHandler extends ElementPathHandler {

    protected String attribute;

    public AttributePathHandler(Metadata metadata, String name, String element, String attribute) {
        super(metadata, name, element);
        this.attribute = attribute;
    }

    public AttributePathHandler(Metadata metadata, Property property, String element, String attribute) {
        super(metadata, property, element);
        this.attribute = attribute;
    }

    @Override
    public void startElement(
            String uri, String localName, String name, Attributes attributes) throws SAXException {
        ancestors.push(localName);
        if(matches()) {
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getLocalName(i).equals(this.attribute)) {
                    addMetadata(attributes.getValue(i).trim());
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) {
        ancestors.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) {}
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) {}

}
