package com.olytech.tika.extensions.parsers;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 11/25/12
 * Time: 8:11 PM
 * Best possible documentation on this can be found here: wiki.apache.org/tika/RecursiveMetadata
 */
public class RecursiveMetadataParser extends ParserDecorator {


        public RecursiveMetadataParser(Parser parser) {
            super(parser);
        }

        @Override
        public void parse(
                InputStream stream, ContentHandler ignore,
                Metadata metadata, ParseContext context)
                throws IOException, SAXException, TikaException {

            ContentHandler content = new DefaultHandler();
            super.parse(stream, content, metadata, context);
        }
}

