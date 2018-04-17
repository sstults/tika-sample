package com.olytech.tika.extensions.output;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.metadata.Metadata;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SolrOutputTarget implements MetadataListener {

    private String[] IGNORE_META = {"Content-Type", "resourceName"};
    private List<String> ignore;
    private int count;
    private int maxCount;

//    private final Meter documentMeter = Metrics.newMeter(SolrOutputTarget.class, "documentMeter", "documentMeter", TimeUnit.SECONDS);
//    private final Meter zipMeter = Metrics.newMeter(SolrOutputTarget.class, "zipMeter", "zipMeter", TimeUnit.MINUTES);

	private SolrServer solrServer;
	public SolrOutputTarget(String outputTargetSolr, int maxCount) {
        ignore = Arrays.asList(IGNORE_META);
		solrServer = new HttpSolrServer(outputTargetSolr);
        this.count = 0;
        this.maxCount = maxCount;
	}

	@Override
	public void handle (Metadata metadata) {
        try {
    		SolrInputDocument doc = this.convert(metadata);
	    	solrServer.add(doc);
//	    	documentMeter.mark();
            this.count ++;

            if(this.count % maxCount == 0) {
                System.out.println("Added " + this.count + " documents to solr. ");
                solrServer.commit();
//                zipMeter.mark();
            }

        } catch (IOException e) {
            System.out.println("IO EXCEPTION writing to SOLR. " + e.getLocalizedMessage());
        } catch (SolrServerException e) {
            System.out.println("SOLR EXCEPTION writing to SOLR. " + e.getLocalizedMessage());
        }
	}

    @Override
    public void allDone() {
        try {
            solrServer.commit();
//            zipMeter.mark();
        } catch (IOException e) {
            System.out.println("IO EXCEPTION writing to SOLR. " + e.getLocalizedMessage());
        } catch (SolrServerException e) {
            System.out.println("SOLR SERVER EXCEPTION writing to SOLR. " + e.getLocalizedMessage());
        }
        catch (org.apache.solr.common.SolrException e){
        	System.out.println("SOLR EXCEPTION writing to SOLR. " + e.getLocalizedMessage());
        }
    }

	public SolrInputDocument convert(Metadata metadata) throws IOException {
		SolrInputDocument doc = new SolrInputDocument();
		
		for (String o : metadata.names()) {
		//	System.out.println("JSON key:" + o.toString() + ", " + json.get(o).toString());
            if(!ignore.contains(o)) {
    			doc.addField(o.toString(), metadata.get(o));
	    	}
        }
  		return doc;
	}

}
