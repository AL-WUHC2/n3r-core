package org.n3r.esql.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RClassPath;
import org.n3r.core.lang.RStr;
import org.n3r.esql.Esql;
import org.n3r.esql.ex.EsqlConfigException;
import org.n3r.esql.impl.EsqlExecInfo;
import org.n3r.esql.res.EsqlItem;

import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

public class EsqlSqlParser {
    private static final String SQLTABLE_KEY = "_SQLTABLE_KEY_";
    private static final String SPLITTER = "__$__";
    private static final ThreadLocal<EsqlFileParserContext> context = new ThreadLocal<EsqlFileParserContext>() {
        @Override
        protected EsqlFileParserContext initialValue() {
            return new EsqlFileParserContext();
        }
    };

    public static String getErr(int lineOffset, String err) {
        return context.get().getErr(lineOffset, err);
    }

    private static Pattern SQLID_PATTERN = Pattern.compile("\\s*\\[\\s*([\\w\\.\\-\\d]+)\\b(.*)\\].*");
    private static LoadingCache<String, Map<String, EsqlItem>> sqlCache = CacheBuilder.newBuilder()
            .softValues()/*.maximumSize(0)*/
            .build(new CacheLoader<String, Map<String, EsqlItem>>() {
                @Override
                public Map<String, EsqlItem> load(String key) throws Exception {
                    return parseSqlFileWoCache(key);
                }
            });

    public static Map<String, EsqlItem> parseSqlFile(String classpathSql) {
        return sqlCache.getUnchecked(classpathSql);
    }

    public static Map<String, EsqlItem> parseSqlTable(String sqlfromdb, String connectionName) {
        return sqlCache.getUnchecked(SQLTABLE_KEY + sqlfromdb + SPLITTER + connectionName);
    }

    public static Map<String, EsqlItem> parseSqlFileWoCache(String key) {
        getContext().setFilePath(key);

        List<String> lines = key.startsWith(SQLTABLE_KEY) ? readSqlTableToLines(key) : RClassPath.toLines(key);

        return parseLines(lines);
    }

    private static List<String> readSqlTableToLines(String key) {
        String sqlfromdb = StringUtils.substringBetween(key, SQLTABLE_KEY, SPLITTER);
        String connectionName = StringUtils.substringAfterLast(key, SPLITTER);

        EsqlExecInfo execContext = Esql.getExecContextInfo();
        List<String[]> sqls = new Esql(connectionName).returnType(EsqlTableSqlMapper.class).execute(sqlfromdb);
        Esql.setExecContextInfo(execContext);

        List<String> result = Lists.newArrayList();
        Splitter splitter = Splitter.on('\n').omitEmptyStrings().trimResults();
        for (String[] sql : sqls) {
            result.add("[" + sql[0] + " " + RStr.toStr(sql[1]) + "]");
            for (String line : splitter.split(sql[2]))
                result.add(line);
        }

        return result;
    }

    private static Map<String, EsqlItem> parseLines(List<String> lines) {
        Map<String, EsqlItem> sqlFile = new HashMap<String, EsqlItem>();
        EsqlItem esqlItem = null;
        ArrayList<String> sqlLines = new ArrayList<String>();
        int lineNo = 0;

        for (String line : lines) {
            ++lineNo;

            line = RStr.trimRight(line);
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith("--")) continue;
            if (StringUtils.isEmpty(trimmedLine)) continue;

            Matcher matcher = SQLID_PATTERN.matcher(line);
            if (!matcher.matches()) {
                if (esqlItem == null) continue;

                if (trimmedLine.endsWith(esqlItem.getSqlSpitter())) {
                    sqlLines.add(line.substring(0, line.length() - 1));
                    addRawSql(esqlItem, sqlLines, lineNo);
                }
                else sqlLines.add(line);

                continue;
            }

            addRawSql(esqlItem, sqlLines, lineNo);

            esqlItem = new EsqlItem();
            String sqlId = matcher.group(1);
            if (sqlFile.containsKey(sqlId)) throw new EsqlConfigException("sqlid " + sqlId + " is duplicated");

            getContext().setSqlId(sqlId);
            sqlFile.put(sqlId, esqlItem);

            esqlItem.setSqlId(sqlId);
            esqlItem.setSqlOptions(new EsqlOptionsParser().parseOptions(matcher.group(2)));
        }

        addRawSql(esqlItem, sqlLines, lineNo);

        return sqlFile;
    }

    private static EsqlFileParserContext getContext() {
        return context.get();
    }

    private static void addRawSql(EsqlItem esqlItem, ArrayList<String> sqlLines, int endLine) {
        try {
            if (esqlItem == null || sqlLines.size() == 0) return;

            getContext().setStartLineNo(endLine - sqlLines.size());
            esqlItem.addSqlParts(sqlLines);
        }
        finally {
            sqlLines.clear();
        }
    }

}
