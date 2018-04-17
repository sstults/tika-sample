package com.olytech.tika.extensions.domain;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 11/30/12
 * Time: 1:12 PM
 *  Class useful for passing parameters to Content Handlers. See the Client class for how to set
 *  values.  Look in the handlers package for how they are used.
 *
 *  */
public class ContextSettings {

    private   static final String API_KEY     = "api";

    private String apiURL;

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

}
