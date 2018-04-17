package com.olytech.tika.extensions.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 11/29/12
 * Time: 3:09 PM
 * Silly little class to deserialize the cpc codes returned from the api.
 */

@XmlRootElement(name = "patent")
public class PatentToCPC {
/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 10/19/12
 * Time: 11:30 AM
 * Used to marshal patent-to-cpc mapping to xml.
 */

    private String id;
    private List<String> codes;

    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name="code")
    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

}
