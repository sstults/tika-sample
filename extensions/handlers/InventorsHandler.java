package com.olytech.tika.extensions.handlers;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

/*
 * Constructed to look for a very specific element, take all content within
 * that element (including it's tags) and place the whole thing in a metadata field.
 * Provided the full element path to an applicants node, will process all applicants with
  * in that node and will add the following nodes into the metadata object:
  * 1) applicant_inventors_short:  ex. Doriano, Z etal.
  * 2) applicant_inventors: ex Dorano, Zaneletti and Funk, Dan (All the authors in a single formatted field)
  * 3) applicant_inventor_facet: a multi-value field of last name, first initial.
  *
           <applicants>
               <applicant sequence="001" app-type="applicant-inventor" designation="us-only">
                    <addressbook>
                        <last-name>Zanaletti</last-name>
                        <first-name>Doriano</first-name>
                        <address>
                            <city>Lainate</city>
                            <country>IT</country>
                        </address>
                    </addressbook>
                    <nationality>
                        <country>omitted</country>
                    </nationality>
                    <residence>
                        <country>IT</country>
                    </residence>
                </applicant>

 */
public class InventorsHandler extends ElementPathHandler {

    protected boolean isFirst = true;
    protected String currentName = "";

    private List<String> firstNames;
    private List<String> lastNames;

    public InventorsHandler(Metadata metadata, String name, String element) {
        super(metadata, name, element);
        firstNames = new ArrayList<String>();
        lastNames  = new ArrayList<String>();
    }

    public InventorsHandler(Metadata metadata, Property property, String element) {
        super(metadata, property, element);
        firstNames = new ArrayList<String>();
        lastNames  = new ArrayList<String>();
    }

    @Override
    public void startElement(
            String uri, String localName, String name, Attributes attributes) {
        if(!matches()) ancestors.push(localName);
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if(matches()) {
            if(localName.equals("first-name"))
                this.firstNames.add(buffer.toString().trim());
            if(localName.equals("last-name"))
                this.lastNames.add(buffer.toString().trim());
            if(localName.equals("applicants"))
                handleMetadata();
            this.buffer.delete(0,this.buffer.length()); // clear the buffer.
        } else {
            ancestors.pop();
        }
    }

    private void handleMetadata() throws SAXException {
        StringBuffer allAuthors = new StringBuffer();
        String firstInitial;
        if (lastNames.size() != firstNames.size())
            throw new SAXException("I was expecting that all inventors would have both a first and last name.");
        if(lastNames.size() > 0) {
          metadata.add("applicant_inventors_short", lastNames.get(0) + ", " + firstNames.get(0).substring(0,1) + ". etal.");
            for(int i=0; i<lastNames.size(); i++) {
                if (i == 0) {
                    // do nothing.
                } else if (i < lastNames.size() - 1 && i > 0) {
                    allAuthors.append("; ");
                } else {
                    allAuthors.append(" and ");
                }
                allAuthors.append(lastNames.get(i) + ", " + firstNames.get(i));
                metadata.add("applicant_inventor_facet", lastNames.get(i) + ", " + firstNames.get(i).substring(0,1));
            }
            metadata.add("applicant_inventors", allAuthors.toString());
        }
    }
}
