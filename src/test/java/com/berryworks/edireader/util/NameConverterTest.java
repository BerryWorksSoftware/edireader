package com.berryworks.edireader.util;

import org.junit.Test;

import static com.berryworks.edireader.util.NameConverter.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NameConverterTest {

    @Test
    public void expandCamelCase_typical() {
        assertEquals("Abc Def", expandCamelCase("AbcDef"));
        assertEquals("Abc Def Ghi", expandCamelCase("AbcDefGhi"));
        assertEquals("Ab Def", expandCamelCase("AbDef"));
        assertEquals("Abc D", expandCamelCase("AbcD"));
    }

    @Test
    public void expandCamelCase_special() {
        assertNull(expandCamelCase(null));
        assertEquals("", expandCamelCase(""));
        assertEquals("A Def Ghi", expandCamelCase("ADefGhi"));
        assertEquals("Abc Def Ghi", expandCamelCase("Abc DefGhi"));
        assertEquals("My HTML Class", expandCamelCase("MyHTMLClass"));
        assertEquals("My HTML Clas", expandCamelCase("MyHTMLClas"));
        assertEquals("My HTML Cla", expandCamelCase("MyHTMLCla"));
        assertEquals("My HTML Cl", expandCamelCase("MyHTMLCl"));
        assertEquals("My HTMLC", expandCamelCase("MyHTMLC"));
        assertEquals("HTML Rocks", expandCamelCase("HTMLRocks"));
        assertEquals("My In-Laws Are Great", expandCamelCase("MyIn-LawsAreGreat"));
        assertEquals("My In-laws Are Great", expandCamelCase("MyIn-lawsAreGreat"));
    }

    @Test
    public void beforeFirst() {
        assertEquals("AK4", beforeFirstHyphen("AK4-1-1"));
        assertEquals("AK4", beforeFirstHyphen("AK4-1"));
        assertEquals("AK4", beforeFirstHyphen("AK4"));

        assertEquals("AK4", beforeFirstDot("AK4.1.1"));
        assertEquals("AK4", beforeFirstDot("AK4.1"));
        assertEquals("AK4", beforeFirstDot("AK4"));

        assertEquals("AK4", beforeFirstUnderscore("AK4_1_1"));
        assertEquals("AK4", beforeFirstUnderscore("AK4_1"));
        assertEquals("AK4", beforeFirstUnderscore("AK4"));
    }

    @Test
    public void beforeLast() {
        assertEquals("AK4-1", beforeLastHyphen("AK4-1-1"));
        assertEquals("AK4", beforeLastHyphen("AK4-1"));
        assertEquals("AK4", beforeLastHyphen("AK4"));

        assertEquals("AK4.1", beforeLastDot("AK4.1.1"));
        assertEquals("AK4", beforeLastDot("AK4.1"));
        assertEquals("AK4", beforeLastDot("AK4"));

        assertEquals("AK4_1", beforeLastUnderscore("AK4_1_1"));
        assertEquals("AK4", beforeLastUnderscore("AK4_1"));
        assertEquals("AK4", beforeLastUnderscore("AK4"));
    }

    @Test
    public void afterFirst() {
        assertEquals("1-1", afterFirstHyphen("AK4-1-1"));
        assertEquals("1", afterFirstHyphen("AK4-1"));
        assertEquals("AK4", afterFirstHyphen("AK4"));

        assertEquals("1.1", afterFirstDot("AK4.1.1"));
        assertEquals("1", afterFirstDot("AK4.1"));
        assertEquals("AK4", afterFirstDot("AK4"));

        assertEquals("1_1", afterFirstUnderscore("AK4_1_1"));
        assertEquals("1", afterFirstUnderscore("AK4_1"));
        assertEquals("AK4", afterFirstUnderscore("AK4"));
    }

    @Test
    public void afterLast() {
        assertEquals("1", afterLastHyphen("AK4-1-1"));
        assertEquals("1", afterLastHyphen("AK4-1"));
        assertEquals("AK4", afterLastHyphen("AK4"));

        assertEquals("1", afterLastDot("AK4.1.1"));
        assertEquals("1", afterLastDot("AK4.1"));
        assertEquals("AK4", afterLastDot("AK4"));

        assertEquals("1", afterLastUnderscore("AK4_1_1"));
        assertEquals("1", afterLastUnderscore("AK4_1"));
        assertEquals("AK4", afterLastUnderscore("AK4"));
    }

    @Test
    public void testFormXsdCompatibleName() {
        assertEquals("", formXsdCompatibleName(null));
        assertEquals("", formXsdCompatibleName(""));
        assertEquals("", formXsdCompatibleName("  "));
        assertEquals("Abc", formXsdCompatibleName("Abc  "));
        assertEquals("AbcDef", formXsdCompatibleName("abc def"));
        assertEquals("AbcDefEfg", formXsdCompatibleName("abc def  efg"));
        assertEquals("AbcDefEFg", formXsdCompatibleName("abc def  e fg"));
        assertEquals("AbcDef", formXsdCompatibleName("abc/def"));
        assertEquals("AbcDefEFg", formXsdCompatibleName("abc, def, e, fg"));
        assertEquals("AbcDefEFg", formXsdCompatibleName("abc,def, E,fg"));
        assertEquals("AbcDefEFg", formXsdCompatibleName("abc def (e Fg)"));
        assertEquals("AbcsDefEFg", formXsdCompatibleName("abc's def (e Fg)"));
        assertEquals("AbcDefEf-g", formXsdCompatibleName("abc def ef|g"));
        assertEquals("AbcDefEf-01", formXsdCompatibleName("abc def ef|01"));
    }

}
