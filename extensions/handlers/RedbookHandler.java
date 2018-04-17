package com.olytech.tika.extensions.handlers;

import com.olytech.tika.extensions.output.MetadataListener;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.TeeContentHandler;
import org.xml.sax.ContentHandler;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 11/26/12
 * Time: 12:28 PM
 * Capable of parsing a series of us-patent-grant objects, calling the MetadataListener each time
 * a new redbook patent is read in.
 */
public class RedbookHandler extends RepeatingElementHandler {

    protected static final String REDBOOK_ELEMENT = "us-patent-grant";
    protected Metadata                metadata;
    private   boolean                 inElement = false;
    private String apiURL;

    public ContentHandler getSubContentHandler(Metadata metadata) {
        return new TeeContentHandler(
                new ElementPathHandler(  metadata, "id",     "us-patent-grant/us-bibliographic-data-grant/publication-reference/document-id/doc-number"),
                new AttributePathHandler(metadata, "country",             "us-patent-grant", "country"),
                new ElementPathHandler(  metadata, "assignee_orgname",    "us-patent-grant/us-bibliographic-data-grant/assignees/assignee/addressbook/orgname"),
                new InventorsHandler(  metadata, "applicant_inventors",   "us-patent-grant/us-bibliographic-data-grant/parties/applicants/applicant"),
                new ElementPathHandler(  metadata, "invention_title_html","us-patent-grant/us-bibliographic-data-grant/invention-title"),
                new DateAttributePathHandler(metadata, "date_produced",   "us-patent-grant", "date-produced"),
                new DateAttributePathHandler(metadata, "date_publ",       "us-patent-grant", "date-publ"),
                new ClassificationHandler(metadata, "uspc",               "us-patent-grant/us-bibliographic-data-grant/classification-national/main-classification", apiURL),
                new ClassificationHandler(metadata, "cpc",                "us-patent-grant/us-bibliographic-data-grant/publication-reference/document-id/doc-number", apiURL),
                new ElementHtmlHandler(  metadata, "abstract_html",       "us-patent-grant/abstract"),
                new ElementHtmlHandler(  metadata, "description_html",    "us-patent-grant/description"),
                new ElementHtmlHandler(  metadata, "claims_html",         "us-patent-grant/claims")
                );
    }


    public RedbookHandler(MetadataListener listener, Metadata metadata, ParseContext context) {
        super(REDBOOK_ELEMENT, listener, metadata, context);

//        this.apiURL = context.get(ContextSettings.class).getApiURL();

        // Setup some default values for all the metadata objects.
        metadata.set(Metadata.CONTENT_TYPE, "application/xml");
        metadata.set("document_type", "patent");
    }


}
