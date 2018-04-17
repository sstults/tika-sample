package com.olytech.tika.extensions.handlers;

import com.olytech.HadoopJob;
import com.olytech.tika.extensions.domain.Classification;
import com.olytech.tika.extensions.domain.PatentToCPC;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.mockito.internal.util.ArrayUtils;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 11/27/12
 * Time: 3:00 PM
 * When parsing a document, will collect the given field and use it to look up information about
 * the classification, adding that new information into the metadata object.
 */
public class ClassificationHandler extends ElementPathHandler {

    private static Logger logger = Logger.getLogger(ClassificationHandler.class.getCanonicalName());
    private String apiURL;

    public ClassificationHandler(Metadata metadata, String name, String element, String apiURL) {
        super(metadata, name, element);
        this.apiURL = apiURL;
    }

    public ClassificationHandler(Metadata metadata, Property property, String element, String apiURL) {
        super(metadata, property, element);
        this.apiURL = apiURL;
    }


    @Override
    /**
     * This is the original output from the XSLT we converted from.  Roughtly speaking this is the metadata
     * and structure this method is meant to reproduce.
     "uspc_code":"D 1102",
     "uspc_code_fmt":["D 1","D 1/102","D 1/102."],
     "uspc_code_std":"D01102000",
     "uspc_facet":[
     "0/D01",
     "1/D01/D01101000",
     "2/D01/D01101000/D01102000"
     */
    protected void addMetadata(String key, String value) {
        Classification c;
        String std;
        String[] codes;

        try {
        if(key.equals(Classification.USPTO_TYPE)) {
            std = Classification.standardize(value);

            super.addMetadata(key + "_code", value);
            super.addMetadata(key + "_code_std", Classification.standardize(value));
            for (String f : Classification.uspcFormats(value)) {
                super.addMetadata(key + "_code_fmt", f);
            }
            c = getClassification(std);
            if(c == null) logger.warning("Unable to locate classification " + std);
            else addFacetMetadata(key, getClassification(std));
        } else if (key.equals(Classification.CPC_TYPE)) {
            codes = getCodeFromPatentId(value);
            if(codes != null) {
              for(String code : codes) {
                  super.addMetadata(key + "_code", code);
                  c = getClassification(code.replace("/", "-"));
                  if(c == null) logger.warning("Unable to locate classification " + code);
                  else addFacetMetadata(key, c);
              }
          }
        }
        } catch (Exception e) {
            logger.info("Exception Encountered :" + e.getMessage());
        }
    }

    private void addFacetMetadata(String key, Classification c) throws SAXException {
        for (String f : c.facets()) {
            super.addMetadata(key + "_facet", f);
        }
    }

    private Classification getClassification(String std) throws SAXException {
        if (!HadoopJob.classificationMap.containsKey(std)) { return null; }
        return HadoopJob.classificationMap.get(std);
    }

    private String[] getCodeFromPatentId(String patentId) throws SAXException {
        if (!HadoopJob.patentMap.containsKey(patentId)) { return null; }
        return HadoopJob.patentMap.get(patentId);
    }


}
