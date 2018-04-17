package com.olytech.tika.extensions.parsers;

import com.olytech.tika.extensions.ConcatenatedXMLFilter;
import com.olytech.tika.extensions.handlers.RedbookHandler;
import com.olytech.tika.extensions.output.MetadataListener;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.EmbeddedContentHandler;
import org.apache.tika.sax.OfflineContentHandler;
import org.apache.tika.sax.TaggedContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 11/14/12
 * Time: 8:11 AM
 * A very basic tika parser, the real work happens int the Redbook handler.  About all that
 * happens here is the creation of the redbook handler and tie in with the redbook media type.
 */
public class RedbookParser extends AbstractParser {

    /** Serial version UID */
    private static final long    serialVersionUID = 4905318835463880819L;

    private MetadataListener     listener;
    public static final MediaType MEDIA_TYPE = MediaType.application("x-redbook+xml");

    private static final Set<MediaType> SUPPORTED_TYPES =
            Collections.unmodifiableSet(new HashSet<MediaType>(Arrays.asList(
                    MEDIA_TYPE)));

    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }

    public RedbookParser(MetadataListener listener) {
        this.listener = listener;
    }

    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context)
            throws IOException, SAXException, TikaException {
        if (metadata.get(Metadata.CONTENT_TYPE) == null) {
            metadata.set(Metadata.CONTENT_TYPE, "application/xml");
        }

//        InputStream newIn = ConcatenatedXMLFilter.filter(stream);
        TaggedContentHandler tagged = new TaggedContentHandler(handler);
        try {
            context.getSAXParser().parse(
                    new CloseShieldInputStream(stream),
                    new OfflineContentHandler(new EmbeddedContentHandler(
                            new RedbookHandler(this.listener, metadata, context))));
        } catch (SAXException e) {
            tagged.throwIfCauseOf(e);
            throw new TikaException("XML parse error", e);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
