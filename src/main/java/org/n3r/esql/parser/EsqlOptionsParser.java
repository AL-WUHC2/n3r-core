package org.n3r.esql.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.n3r.core.lang.RStr;

public class EsqlOptionsParser {
    private static Pattern OPTION_PATTERN = Pattern.compile("\\b(\\w+)\\b\\s*(=\\s*(\"?[^\"]+\"?))?");

    public Map<String, String> parseOptions(String optionsStr) {
        HashMap<String, String> options = new HashMap<String, String>();
        Matcher matcher = OPTION_PATTERN.matcher(optionsStr);
        int pos = 0;
        while (matcher.find(pos)) {
            String key = matcher.group(1);
            String option = matcher.group(3);
            pos = matcher.end();
            if (option != null && 
                    (option.startsWith("\"") && !option.endsWith("\"") ||  !option.startsWith("\""))) {
                int blankPos = RStr.indexOfBlank(option);
                if (blankPos >= 0) {
                    pos = matcher.start(3) + blankPos;
                    option = option.substring(0, blankPos);
                }
            }

            options.put(key, removeQuote(option));

        }
        return options;
    }

    private String removeQuote(String option) {
        if (option == null) return "";

        String ret = option;
        if (option.startsWith("\"")) ret = ret.substring(1);
        if (option.endsWith("\"")) ret = ret.substring(0, ret.length() - 1);

        return ret;
    }
}
