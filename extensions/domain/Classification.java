package com.olytech.tika.extensions.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.beans.Field;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: dan
 * Date: 4/4/12
 * Time: 12:53 PM
 * Shamlessly copied over from the code that generates this object
 * in the API.  I'm not taking the time to share the libraries so
 * that this would be unnecessary.
 */
@XmlRootElement(name = "classification")
public class Classification {

    private static Logger logger = Logger.getLogger(Classification.class.getCanonicalName());

    // Possible classification types.
    public static final String UNKOWN_TYPE = "unkown";
    public static final String USPTO_TYPE  = "uspc";
    public static final String CPC_TYPE    = "cpc";

    private String code;
    private String oldCode; // references from CPC to IPC Code
    private int    level;
    private String type = UNKOWN_TYPE;
    private String name;
    private String description;
    private String facet;
    protected List<Classification> children = new ArrayList<Classification>();
    protected Classification parent;

    /**
     * Formats the given raw classification read from the xml and coverts it to something
     * we can use for a lookup.
     * Converts something like "D 1102" to "D01102000" a 9 digit value without spaces.
     *
     * @param classification
     * @return
     */
    public static String standardize(String classification) {
        String c;
        c = StringUtils.rightPad(classification, 9, "0");
        c = c.replace(" ", "0");
        return c;

    }
    /**
     * Formats the given raw classification read from the xml and coverts it to the standard format
     * for display used at the USPTO. Since formats are hierachical, this is an array rather than just one value.
     * It can be used for searching classifications in Solr.
     * Converts something like "D 11021" to an array of "D 1", D 1/102", "D 1/102.1"
     *
     * @param classification
     * @return
     */
    public static List<String> uspcFormats(String classification) {
        List<String> formats = new ArrayList<String>();
        String main, s1;

        if (classification.length() <= 3) {
            formats.add(classification);
        } else {
            main = classification.substring(0, 3);
            formats.add(main);
            if (classification.length() > 6) {
                s1 = main + "/" + classification.substring(3, 6);
                formats.add(s1);
                formats.add(s1 + "." + classification.substring(6));
            } else {
                formats.add(main + "/" + classification.substring(3));
            }
        }
        return formats;
    }

    /**
     * Turns a facet string, as provided by the API into a set of
     * facets that would match anywhere along the hierarchy.
     * To be specific, it takes something like this:
     *     2/D01/D01101000/D01102000
     * And turns it into:
     *      "0/D01",
     *      "1/D01/D01101000",
     *      "2/D01/D01101000/D01102000"
     */
    public List<String> facets() {
        List<String> sections = new ArrayList<String>();
        String[] subc = this.facet.split("/");
        subc[0] = ""; // clear off the leading number.
        for(int i = 0; i < subc.length - 1; i++ ) {
            sections.add(i + StringUtils.join(subc, "/", 0, i + 2));
        }
        return sections;
    }


    /**
     * Returns this classification and all it's decendents as a single List.
     * @return
     */
    public List<Classification> asList() {
        List<Classification> cs = new ArrayList<Classification>();

        cs.add(this);
        for(Classification c : this.children) {
            cs.addAll(c.asList());
        }

        return cs;
    }

    public void addChild(Classification c) {
        this.children.add(c);
    }

    @XmlAttribute
    public String getCode() {
        return code;
    }

    @Field("id") // Will also be available under "code" when searching, at is copied over during indexing
    public void setCode(String code) {
        this.code = code;
    }

    @XmlAttribute
    public int getLevel() {
        return level;
    }

    @Field
    public void setLevel(int level) {
        this.level = level;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    @Field
    public void setType(String type) {
        this.type = type;
    }

    @XmlElement
    public String getDescription() {
        return description;
    }

    @Field
    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    @Field
    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getFacet() {
        return facet;
    }

    @Field
    public void setFacet(String facet) {
        this.facet = facet;
    }

    public List<Classification> getChildren() {
        return children;
    }


    public void setChildren(List<Classification> children) {
        this.children = children;
    }

    public Classification getParent() {
        return parent;
    }

    public void setParent(Classification parent) {
        this.parent = parent;
    }

    public String getOldCode() {
        return oldCode;
    }

    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    @Override
    public String toString() {
        return "Classification{" +
                "code='" + code + '\'' +
                ", level=" + level +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", facet='" + getFacet() + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
