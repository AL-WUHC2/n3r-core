package org.n3r.esql.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.n3r.esql.ex.EsqlConfigException;
import org.n3r.esql.param.EsqlParamPlaceholder;
import org.n3r.esql.param.EsqlPlaceholderType;
import org.n3r.esql.res.EsqlDynamic;

import com.google.common.collect.Lists;

public class EsqlDynamicParser {
    private static Pattern DYNAMIC_PATTERN = Pattern.compile("\\$(.*?)\\$");
    private EsqlDynamic dynamicSql;
    private String rawSql;

    public EsqlDynamic parseRawSql(String rawSql) {
        this.rawSql = rawSql;
        this.dynamicSql = new EsqlDynamic();

        Matcher matcher = DYNAMIC_PATTERN.matcher(rawSql);
        List<String> placeHolders = Lists.newArrayList();
        List<String> sqlPieces = Lists.newArrayList();
        int startPos = 0;
        while (matcher.find()) {
            String placeHolder = matcher.group(1).trim();
            placeHolders.add(placeHolder);

            sqlPieces.add(rawSql.substring(startPos, matcher.start()));
            startPos = matcher.end();
        }

        if (startPos == 0) return null;

        sqlPieces.add(rawSql.substring(startPos));

        dynamicSql.setSqlPieces(sqlPieces);

        parsePlaceholders(placeHolders);

        return dynamicSql;
    }

    private void parsePlaceholders(List<String> placeHolders) {
        List<EsqlParamPlaceholder> paramPlaceholders = new ArrayList<EsqlParamPlaceholder>();

        EsqlPlaceholderType placeHoldertype = EsqlPlaceholderType.UNSET;
        for (String placeHolder : placeHolders) {
            EsqlParamPlaceholder paramPlaceholder = new EsqlParamPlaceholder();
            paramPlaceholders.add(paramPlaceholder);
            paramPlaceholder.setPlaceholder(placeHolder);

            if (placeHolder.length() == 0) paramPlaceholder.setPlaceholderType(EsqlPlaceholderType.AUTO_SEQ);
            else if (StringUtils.isNumeric(placeHolder)) {
                paramPlaceholder.setPlaceholderType(EsqlPlaceholderType.MANU_SEQ);
                paramPlaceholder.setSeq(Integer.valueOf(placeHolder));
            }
            else paramPlaceholder.setPlaceholderType(EsqlPlaceholderType.VAR_NAME);

            placeHoldertype = paramPlaceholder.getPlaceholderType();
        }

        for (EsqlParamPlaceholder paramPlaceholder : paramPlaceholders)
            if (placeHoldertype != paramPlaceholder.getPlaceholderType())
                throw new EsqlConfigException("[" + rawSql + "]中定义的SQL动态替换设置类型不一致");

        dynamicSql.setPlaceholdertype(placeHoldertype);
        dynamicSql.setPlaceholders(paramPlaceholders.toArray(new EsqlParamPlaceholder[0]));
    }
}
