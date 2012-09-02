package org.n3r.esql.parser;

public class EsqlFileParserContext {
    private String filePath;
    private String sqlId;
    private int startLineNo;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public int getStartLineNo() {
        return startLineNo;
    }

    public void setStartLineNo(int startLineNo) {
        this.startLineNo = startLineNo;
    }

    public String getErr(int lineOffset, String err) {
        return "sql source: " + getFilePath() + ", sql id: " + getSqlId()
                + " line: " + (getStartLineNo() + lineOffset) + ", error: " + err;
    }
}
