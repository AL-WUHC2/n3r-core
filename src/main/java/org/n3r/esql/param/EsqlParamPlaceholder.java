package org.n3r.esql.param;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;

public class EsqlParamPlaceholder {
    public static enum InOut {
        IN, OUT, INOUT
    }

    private String placeholder;
    private InOut inOut = InOut.IN;
    private EsqlPlaceholderType placeholderType;
    private int seq;
    private String option;

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public EsqlPlaceholderType getPlaceholderType() {
        return placeholderType;
    }

    public void setPlaceholderType(EsqlPlaceholderType placeholderType) {
        this.placeholderType = placeholderType;
    }

    public InOut getInOut() {
        return inOut;
    }

    public void setInOut(InOut inOut) {
        this.inOut = inOut;
    }

    public void setOption(String placeHolderOption) {
        this.option = placeHolderOption;
        Iterable<String> optionParts = Splitter.on(',').omitEmptyStrings().trimResults().split(this.option);
        for (String optionPart : optionParts)
            if (StringUtils.equalsIgnoreCase("OUT", optionPart)) setInOut(InOut.OUT);
            else if (StringUtils.equalsIgnoreCase("INOUT", optionPart)) setInOut(InOut.INOUT);
    }

}
