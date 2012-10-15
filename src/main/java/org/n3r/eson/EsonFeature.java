package org.n3r.eson;

public class EsonFeature {
    public static String DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final int SortKey = 0x01;
    public static final int PrettyFormat = 0x02;
    public static final int SkipTransientField = 0x04;

    private int features = 0x01 | 0x08; // default sort key and skip transient
    private char keyQuote = 0;
    private char valQuote = 0;

    //    private DateFormat dateFormat;

    public int config(int feature, boolean state) {
        if (state) features |= feature;
        else features &= ~feature;

        return features;
    }

    public boolean isEnabled(int feature) {
        return (features & feature) > 0;
    }

    public char getKeyQuote() {

        return keyQuote;
    }

    public void setKeyQuote(char keyQuote) {
        this.keyQuote = keyQuote;
    }

    public char getValQuote() {
        return valQuote;
    }

    public void setValQuote(char valQuote) {
        this.valQuote = valQuote;
    }

    //    public DateFormat getDateFormat() {
    //        if (dateFormat == null) dateFormat = new SimpleDateFormat(DEFFAULT_DATE_FORMAT);
    //        return dateFormat;
    //    }
    //
    //    public void setDateFormat(String format) {
    //        dateFormat = new SimpleDateFormat(format);
    //    }

}
