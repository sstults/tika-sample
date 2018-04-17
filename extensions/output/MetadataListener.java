package com.olytech.tika.extensions.output;

import org.apache.tika.metadata.Metadata;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 11/26/12
 * Time: 12:29 PM
 * Implement this interface to receive notification of newly created metadata objects
 * that contain patents.
 */
public interface MetadataListener {

    /**
     * Called each time a new patent metadata object is created.
     * @param meta
     */
    public void handle(Metadata meta);

    /**
     * Called after everything is completed.
     */
    void allDone();
}
