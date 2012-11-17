package org.n3r.esql.util;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.n3r.core.lang.RStr;
import org.n3r.esql.param.EsqlParamsParser;
import org.n3r.esql.res.EsqlItem;
import org.n3r.esql.res.EsqlPart;
import org.n3r.esql.res.EsqlSub;
import org.n3r.esql.res.EsqlSub.EsqlType;

public class EsqlUtils {
    private static Pattern FIRST_WORD = Pattern.compile("\\b(\\w+)\\b");

    public static boolean isProcedure(EsqlType sqlType) {
        return RStr.in(sqlType, EsqlType.CALL, EsqlType.DECLARE, EsqlType.BEGIN);
    }

    public static EsqlType parseSqlType(String rawSql) {
        Matcher matcher = FIRST_WORD.matcher(rawSql);
        matcher.find();
        try {
            String firstWord = matcher.group(1).toUpperCase();
            return EsqlType.valueOf(firstWord);
        } catch (IllegalStateException ex) {
            throw ex;
        }
    }

    public static String getSqlClassPath(int num) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String callerClassName = stackTraceElements[num].getClassName();
        return "/" + callerClassName.replace('.', '/') + ".esql";
    }

    public static List<EsqlSub> createSqlSubs(Object bean, List<List<EsqlPart>> sqlRawSubs, EsqlItem sqlItem) {
        ArrayList<EsqlSub> sqlSubs = new ArrayList<EsqlSub>();
        EsqlSub lastSelectSql = null;
        for (List<EsqlPart> sqlSub : sqlRawSubs) {
            String sql = buildSql(bean, sqlSub);
            EsqlSub esqlSub = new EsqlParamsParser().parseRawSql(sql, sqlItem);
            sqlSubs.add(esqlSub);
            if (esqlSub.getSqlType() == EsqlType.SELECT) lastSelectSql = esqlSub;
        }
        if (lastSelectSql != null) lastSelectSql.setLastSelectSql(true);

        return sqlSubs;
    }

    public static String buildSql(Object bean, List<EsqlPart> sqlSub) {
        StringBuilder sql = new StringBuilder();
        for (EsqlPart sqlPart : sqlSub)
            sql.append(sqlPart.getSqlPart(bean)).append("\r\n");
        return StringUtils.trim(sql.toString());
    }

    public static String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
        String name = resultSetMetaData.getColumnLabel(columnIndex);
        if (name == null || name.length() < 1) name = resultSetMetaData.getColumnName(columnIndex);
        return name;
    }

    public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
        Object obj = rs.getObject(index);
        String className = null;
        if (obj != null) className = obj.getClass().getName();
        if (obj instanceof Blob) obj = rs.getBytes(index);
        else if (obj instanceof Clob) obj = rs.getString(index);
        else if (className != null &&
                ("oracle.sql.TIMESTAMP".equals(className) ||
                "oracle.sql.TIMESTAMPTZ".equals(className))) obj = rs.getTimestamp(index);
        else if (className != null && className.startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);
            if ("java.sql.Timestamp".equals(metaDataClassName) ||
                    "oracle.sql.TIMESTAMP".equals(metaDataClassName)) obj = rs.getTimestamp(index);
            else obj = rs.getDate(index);
        }
        else if (obj instanceof java.sql.Date) {
            if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) obj = rs.getTimestamp(index);
        }
        else if (obj instanceof String) obj = ((String) obj).trim();

        return obj;
    }

    public static Object getResultSetValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
        if (requiredType == null) return getResultSetValue(rs, index);

        Object value = null;
        boolean wasNullCheck = false;

        // Explicitly extract typed value, as far as possible.
        if (String.class.equals(requiredType)) value = StringUtils.trim(rs.getString(index));
        else if (boolean.class.equals(requiredType) || Boolean.class.equals(requiredType)) {
            value = rs.getBoolean(index);
            wasNullCheck = true;
        }
        else if (byte.class.equals(requiredType) || Byte.class.equals(requiredType)) {
            value = rs.getByte(index);
            wasNullCheck = true;
        }
        else if (short.class.equals(requiredType) || Short.class.equals(requiredType)) {
            value = rs.getShort(index);
            wasNullCheck = true;
        }
        else if (int.class.equals(requiredType) || Integer.class.equals(requiredType)) {
            value = rs.getInt(index);
            wasNullCheck = true;
        }
        else if (long.class.equals(requiredType) || Long.class.equals(requiredType)) {
            value = rs.getLong(index);
            wasNullCheck = true;
        }
        else if (float.class.equals(requiredType) || Float.class.equals(requiredType)) {
            value = rs.getFloat(index);
            wasNullCheck = true;
        }
        else if (double.class.equals(requiredType) || Double.class.equals(requiredType) ||
                Number.class.equals(requiredType)) {
            value = rs.getDouble(index);
            wasNullCheck = true;
        }
        else if (byte[].class.equals(requiredType)) value = rs.getBytes(index);
        else if (java.util.Date.class.equals(requiredType)) {
            value = rs.getTimestamp(index);
            if (value != null && !rs.wasNull()) value = new java.util.Date(((Timestamp) value).getTime());
        }
        else if (java.sql.Date.class.equals(requiredType)) {
            value = rs.getTimestamp(index);
            if (value != null && !rs.wasNull()) value = new java.sql.Date(((Timestamp) value).getTime());
        }
        else if (java.sql.Time.class.equals(requiredType)) value = rs.getTime(index);
        else if (java.sql.Timestamp.class.equals(requiredType) || java.util.Date.class.equals(requiredType)) value = rs
                .getTimestamp(index);
        else if (BigDecimal.class.equals(requiredType)) value = rs.getBigDecimal(index);
        else if (Blob.class.equals(requiredType)) value = rs.getBlob(index);
        else if (Clob.class.equals(requiredType)) value = rs.getClob(index);
        else // Some unknown type desired -> rely on getObject.
        value = getResultSetValue(rs, index);

        // Perform was-null check if demanded (for results that the
        // JDBC driver returns as primitives).
        if (wasNullCheck && value != null && rs.wasNull()) value = null;
        return value;
    }

}
