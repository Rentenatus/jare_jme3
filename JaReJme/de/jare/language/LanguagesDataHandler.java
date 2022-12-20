/*
 * Copyright (C) 2022 Janusch Rentenatus  
 */
package de.jare.language;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

/**
 *
 * @author Janusch Rentenatus  
 */
class LanguagesDataHandler extends DefaultHandler {

    private final Languages languages;
    private final String primary;
    private final String secondary;
    private Languages primaryLg;
    private Languages secondaryLg;
    private String entry;

    LanguagesDataHandler(Languages languages, String primary, String secondary) {
        this.languages = languages;
        this.primary = primary;
        this.secondary = secondary;
        if (primary.equals(secondary)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void startDocument() throws SAXException {
        primaryLg = null;
        secondaryLg = null;
        entry = null;
    }

    @Override
    public void endDocument() throws SAXException {
        primaryLg = null;
        secondaryLg = null;
        entry = null;
    }

    @Override
    public void startElement(String uri,
            String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equalsIgnoreCase("entry")) {
            entry = attributes.getValue("name");
        } else {
            primaryLg = null;
            secondaryLg = null;
            if (qName.equalsIgnoreCase(primary)) {
                primaryLg = languages;
            }
            if (qName.equalsIgnoreCase(secondary)) {
                secondaryLg = languages;
            }
        }
    }

    @Override
    public void endElement(String uri,
            String localName, String qName) throws SAXException {
        entry = null;
    }

    @Override
    public void characters(char ch[],
            int start, int length) throws SAXException {
        if (entry != null) {
            if (primaryLg != null) {
                primaryLg.appendPrimary(entry, new String(ch, start, length));
            }
            if (secondaryLg != null) {
                secondaryLg.appendSecondary(entry, new String(ch, start, length));
            }
        }
    }
}
