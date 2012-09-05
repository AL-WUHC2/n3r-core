package org.n3r.esql.parser;

import org.junit.Test;
import org.n3r.esql.Esql;
import org.n3r.esql.ex.EsqlConfigException;

import static org.junit.Assert.*;

public class DuplicateSqlIdTest {
    @Test
    public void test1() {
        try {
            new Esql().select("getString");
            fail();
        }
        catch (EsqlConfigException e) {
            assertEquals("sqlid getString is duplicated", e.getMessage());
        }
    }
}
