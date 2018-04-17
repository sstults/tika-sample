package com.olytech.tika.extensions.handlers;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/*
 * Constructed to look for a very specific element, take all content within
 * that element (including it's tags) and place the whole thing in a metadata field.
 */
public class ElementHtmlHandler extends ElementPathHandler {


    public ElementHtmlHandler(Metadata metadata, String name, String element) {
        super(metadata, name, element);
    }

    public ElementHtmlHandler(Metadata metadata, String element, Property property) {
        super(metadata, property, element);
    }


    private int depth = 0;

    @Override
  public void startElement(final String uri, final String localName,
      final String name, final Attributes attributes) {
        if (matches()) {
            depth++;
            char[] ele = ("<" + localName + ">").toCharArray();
            characters(ele, 0, ele.length);
            return;
        } else {
            ancestors.push(localName);
        }
  }

  @Override
  public void endElement(final String uri, final String localName,
      final String name) throws SAXException {
      if(matches()) {
          if (depth > 0) {
              char[] ele = ("</" + localName + ">").toCharArray();
              characters(ele, 0, ele.length);
              depth --;
          } else {
              addMetadata(buffer.toString().trim());
          }
      } else {
          ancestors.pop();
      }
  }
}
