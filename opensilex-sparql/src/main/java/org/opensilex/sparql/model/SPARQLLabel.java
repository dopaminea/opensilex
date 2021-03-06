/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensilex.sparql.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vmigot
 */
public class SPARQLLabel {
    
    private String defaultValue;
    
    private String defaultLang;
    
    private Map<String, String> translations;

    public SPARQLLabel() {
        translations = new HashMap<>();
    }

    public SPARQLLabel(String value, String lang) {
        this();
        setDefaultValue(value);
        setDefaultLang(lang);
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }
    
    public Map<String, String> getAllTranslations() {
        Map<String, String> allTranslations = new HashMap<>();
        allTranslations.putAll(translations);
        allTranslations.put(defaultLang, defaultValue);
        return allTranslations;
    }
    
    public void addTranslation(String value, String lang) {
        translations.put(lang, value);
    }

    @Override
    public String toString() {
        return defaultValue;
    }
    
    
}
