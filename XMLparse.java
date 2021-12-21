package com.thermador.formation.util;
import com.thermador.formation.exception.GCEException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.*;

public class XMLparse {

    private final static String S_EMPTY = "";

    public Document cleanGCE140XML(final String chaine) throws GCEException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        String resultString = null;
        final XPathFactory xPathfactory = XPathFactory.newInstance();
        final XPath xpath = xPathfactory.newXPath();
        final XPathExpression expr;
        try {
            builder = factory.newDocumentBuilder();

            final Document document = builder.parse(new InputSource(new StringReader(chaine)));

            expr = xpath.compile("//xmlpres/text()");
            resultString = (String) expr.evaluate(document, XPathConstants.STRING);

        } catch (final Exception e) {
            throw new GCEException(e);
        }

        //String replace = "<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>";
        final String replace = "<\\?xml version ?= ?[\"']1.0[\"'] encoding ?= ?[\"']UTF-8[\"']\\?>";
        resultString = Objects.requireNonNull(resultString).replaceAll("&lt;", "<");
        resultString = Objects.requireNonNull(resultString).replaceAll("&amp;amp;", "&amp;");


        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            resultString = resultString.replaceAll(replace, S_EMPTY);
            doc = builder.parse(new InputSource(new StringReader(resultString)));

        } catch (final Exception e) {
            throw new GCEException(e);
        }

        return doc;
    }
    public String getNodeAttributValue(final Element node, final String attribute) {
        final NodeList nl = node.getElementsByTagName(attribute);
        String value = "";
        if (nl.getLength() == 0) {
            value = "";
        } else if (nl.getLength() > 1) {
            value = "";
        } else {
            value = node.getElementsByTagName(attribute).item(0).getTextContent().trim();
        }

        return value;

    }
}
