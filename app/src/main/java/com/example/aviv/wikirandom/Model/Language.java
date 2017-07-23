package com.example.aviv.wikirandom.Model;

/**
 * Created by Team Hurrange on 19/03/2017.
 */

//this enum describes the Language options
public enum Language
{
    HEBREW("he", 1),
    ENGLISH("en", 2),
    RUSSIAN("ru", 3);

    private String stringValue;
    private int intValue;

    Language(String stringValue, int intValue)
    {
        this.stringValue = stringValue;
        this.intValue = intValue;
    }

    @Override
    public String toString()
    {
        return stringValue;
    }
}