package com.olytech.tika.extensions.output;

import com.google.gson.Gson;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.metadata.Metadata;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mike
 * Date: 12/1/12
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringOutputTarget implements MetadataListener {

    private Metadata metadata;
    private SolrInputDocument doc = new SolrInputDocument();
    private String[] IGNORE_META = {"Content-Type", "resourceName"};
    private List<String> ignore = Arrays.asList(IGNORE_META);

    public Metadata getMetadata() {
        return metadata;
    }

    public SolrInputDocument getSolrInputDoc() {
        return doc;
    }

    @Override
    public void handle(Metadata meta) {
        metadata = meta;
        setSolrInputDocument(metadata);

    }

    @Override
    public void allDone() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return new Gson().toJson(doc);
    }

    public void setSolrInputDocument(Metadata metadata) {
        for (String o : metadata.names()) {
            //	System.out.println("JSON key:" + o.toString() + ", " + json.get(o).toString());
            if(!ignore.contains(o)) {
                doc.addField(o.toString(), metadata.get(o));
            }
        }
    }
}
