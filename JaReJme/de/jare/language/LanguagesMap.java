/*
 * Copyright (C) 2022 Janusch Rentenatus  
 */
package de.jare.language;

/**
 *
 * @author Janusch Rentenatus
 */
public interface LanguagesMap {

    public String getPrimaryLg();

    public void setPrimaryLg(String primaryLg);

    public String getSecondaryLg();

    public void setSecondaryLg(String secondaryLg);

    public void load(String filename);

    public String get(String aKey);

    public String getOrEmpty(String aKey);
}
