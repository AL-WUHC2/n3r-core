package org.n3r.core.text;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class RRandTest {
    @Test
    public void test1() {
        String str = RRand.randNum(10);
        assertTrue(StringUtils.isNumeric(str));
        assertFalse(RChinese.isChinese(str));
        
        str = RRand.randChinese(3);
        assertTrue(RChinese.isChinese(str));
    }
}
