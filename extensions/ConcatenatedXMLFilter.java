package com.olytech.tika.extensions;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.regex.fast.FastRegexModifier;
import org.apache.commons.io.input.ReaderInputStream;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 11/25/12
 * Time: 11:52 PM
 * XML files that are concatenated together can be a real pain to pull apart.  This
 * will turn just such a file into a valix xml document by wrapping the contents in
 * a dummy tag and ripping out the doctype and xml delcarations..
 */
public class ConcatenatedXMLFilter {

    static final String charsetName = "UTF-8";

    public static InputStream filter(InputStream originalByteStream) throws UnsupportedEncodingException {

        String re1, re2, re3;  // Regular expressions
        Modifier mod1, mod2, mod3;  // Modifyers.
        ModifyingReader r1, r2, r3;  // Readers that modify the stream.

        // byte stream as character stream
        Reader originalReader = new InputStreamReader(originalByteStream, charsetName);

        // Create a regex that will remove all xml declerations.
        re1  ="<\\?xml version=\"1\\.0\" encoding=\"UTF-8\"\\?>";
        mod1 = new FastRegexModifier(re1, 0, "");

        // Create a regex that will remove all patent-grant doctype declerations.
        re2  ="<\\!DOCTYPE us-patent-grant SYSTEM \"us-patent-grant-v42-2006-08-23\\.dtd\" \\[ \\]>";
        mod2 = new FastRegexModifier(re2, 0, "");

        // Create a regex that will remove all sequence-cwu doctype declerations.
        re3  ="<\\!DOCTYPE sequence-cwu SYSTEM \"us-sequence-listing-2004-03-09\\.dtd\" \\[ \\]>";
        mod3 = new FastRegexModifier(re3, 0, "");

        // create the modifying reader that wraps the original reader
        r1 = new ModifyingReader(originalReader, mod1);
        r2 = new ModifyingReader(r1, mod2);
        r3 = new ModifyingReader(r2, mod3);

        // character stream as byte stream
        InputStream modifyingByteStream = new ReaderInputStream(r3, charsetName);

        // Wraps the stream in dummy tags.
        InputStream newStream = new SequenceInputStream(
                Collections.enumeration(Arrays.asList(
                        new InputStream[]{
                                new ByteArrayInputStream("<dummy>".getBytes()),
                                modifyingByteStream,
                                new ByteArrayInputStream("</dummy>".getBytes()),
                        }))
        );

        return(newStream);

    }
}