package com.olytech.tika.extensions.handlers;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.xml.sax.SAXException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Grabs a date string in the format YYYYMMDD from an xml attribute and formats it for
 * indexing in solr.
 */
public class DateAttributePathHandler extends AttributePathHandler {

    private final SimpleDateFormat formatInt   = new SimpleDateFormat("yyyyMMdd");
    private final SimpleDateFormat formatRFC   = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public DateAttributePathHandler(Metadata metadata, String name, String element, String attribute) {
        super(metadata, name, element, attribute);
    }

    public DateAttributePathHandler(Metadata metadata, Property property, String element, String attribute) {
        super(metadata, property, element, attribute);
    }

    public void addMetadata(String value) throws SAXException {

        Date date;
        try {
            date = formatInt.parse(value);
            this.metadata.add(name, formatRFC.format(date));
            this.metadata.add(name + "_int", value);
            for(String facet : facetDate(date)) {
                this.metadata.add(name + "_facet", facet);
            }
        } catch (ParseException e) {
            throw new SAXException("Date could not be parsed.");
        }


    }

    private List<String> facetDate(Date date) {
        // Date facets of the form [0/yyyy, 1/yyyy/mm]
        String dateString = formatInt.format(date);
        List<String> dateFacets = new ArrayList<String>();
        if (null == date) return dateFacets;
        dateFacets.add("0/" + dateString.substring(0, 4));
        dateFacets.add("1/" + dateString.substring(0, 4) + "/"
                + dateString.substring(4, 6));
        return dateFacets;
    }


}
