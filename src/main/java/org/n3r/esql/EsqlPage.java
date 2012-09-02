package org.n3r.esql;

import org.n3r.core.lang.RBaseBean;

public class EsqlPage extends RBaseBean {
    private int startIndex;
    private int pageRows;
    private int totalRows;

    public EsqlPage() {

    }

    public EsqlPage(int startIndex, int pageRows) {
        this.setStartIndex(startIndex);
        this.setPageRows(pageRows);
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getPageRows() {
        return pageRows;
    }

    public void setPageRows(int pageRows) {
        this.pageRows = pageRows;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

}
