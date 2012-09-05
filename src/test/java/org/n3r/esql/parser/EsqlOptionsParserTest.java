package org.n3r.esql.parser;

import java.util.Map;

import org.junit.Test;
import org.n3r.esql.parser.EsqlOptionsParser;

import static org.junit.Assert.*;

public class EsqlOptionsParserTest {

    @Test
    public void test() {
        Map<String, String> options = new EsqlOptionsParser().parseOptions("immediate=true");
        assertEquals("{immediate=true}", options.toString());

        options = new EsqlOptionsParser().parseOptions("immediate=\"true abc\"");
        assertEquals("{immediate=true abc}", options.toString());

        options = new EsqlOptionsParser().parseOptions("immediate=\"true abc\" cached=1d");
        assertEquals("{cached=1d, immediate=true abc}", options.toString());

        options = new EsqlOptionsParser().parseOptions("immediate=\"true abc cached=1d");
        assertEquals("{cached=1d, immediate=true, abc=}", options.toString()); 
        assertEquals("", options.get("abc"));
        
        options = new EsqlOptionsParser().parseOptions("immediate=\"true");
        assertEquals("{immediate=true}", options.toString()); 
        options = new EsqlOptionsParser().parseOptions("onerr=resume immediate=true");
        assertEquals("{immediate=true, onerr=resume}", options.toString()); 
    }

}
