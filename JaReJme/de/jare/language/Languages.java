/*
 * Copyright (C) 2022 Janusch Rentenatus  
 */
package de.jare.language;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Janusch Rentenatus
 */
public class Languages implements LanguagesMap {

    public static Languages instance = new Languages();
    private String primaryLg = "EN";
    private String secondaryLg = null;
    private Map<String, String> primaryMap = new HashMap<>();
    private Map<String, String> secondaryMap = new HashMap<>();

    public static LanguagesMap getInstance() {
        return instance;
    }

    @Override
    public String getPrimaryLg() {
        return primaryLg;
    }

    @Override
    public void setPrimaryLg(String primaryLg) {
        this.primaryLg = primaryLg;
        primaryMap = new HashMap<>();
    }

    @Override
    public String getSecondaryLg() {
        return secondaryLg;
    }

    @Override
    public void setSecondaryLg(String secondaryLg) {
        this.secondaryLg = secondaryLg;
        secondaryMap = new HashMap<>();
    }

    public void putPrimary(String key, String text) {
        primaryMap.put(key, text);
    }

    public void putSecondary(String key, String text) {
        secondaryMap.put(key, text);
    }

    public void appendPrimary(String aKey, String text) {
        if (primaryMap.containsKey(aKey)) {
            primaryMap.put(aKey, primaryMap.get(aKey) + text);
        } else {
            primaryMap.put(aKey, text);
        }
    }

    public void appendSecondary(String aKey, String text) {
        if (secondaryMap.containsKey(aKey)) {
            secondaryMap.put(aKey, secondaryMap.get(aKey) + text);
        } else {
            secondaryMap.put(aKey, text);
        }
    }

    @Override
    public void load(String filename) {
        try {

            //Create file object.
            File inputFile = new File(filename);

            //Get SAXParserFactory instance.
            SAXParserFactory factory = SAXParserFactory.newInstance();

            //Get SAXParser object from SAXParserFactory instance.
            SAXParser saxParser = factory.newSAXParser();

            //Create StudentHandler object.
            DefaultHandler dataHandler = new LanguagesDataHandler(this, getPrimaryLg(), getSecondaryLg());

            //Parse the XML file.
            saxParser.parse(inputFile, dataHandler);

        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(String aKey) {
        if (primaryMap.containsKey(aKey)) {
            return primaryMap.get(aKey);
        } else if (secondaryMap.containsKey(aKey)) {
            return secondaryMap.get(aKey);
        } else {
            return '#' + aKey;
        }
    }
    
     @Override
    public String getOrEmpty(String aKey) {
        if (primaryMap.containsKey(aKey)) {
            return primaryMap.get(aKey);
        } else if (secondaryMap.containsKey(aKey)) {
            return secondaryMap.get(aKey);
        } else {
            return "";
        }
    }
}
