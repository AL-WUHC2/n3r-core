package org.n3r.eson;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;
import org.n3r.core.lang.RBaseBean;
import org.n3r.eson.mapper.serializer.PrimitivesSerializer;

import static org.junit.Assert.*;

public class PrimitiveTest {
    @Test
    public void testPrimitive() {
        new PrimitivesSerializer();

        PrimitiveBean primitiveBean = new PrimitiveBean();
        String json = new Eson().toString(primitiveBean);
        assertEquals(primitiveBean, new Eson().parse(json, PrimitiveBean.class));

        primitiveBean.setS((short) 100);
        primitiveBean.setI(200);
        primitiveBean.setL(300L);
        primitiveBean.setBo(true);
        primitiveBean.setC('a');
        primitiveBean.setBy((byte) 400);
        primitiveBean.setF(500.5005F);
        primitiveBean.setD(600.60006D);
        primitiveBean.setBd(new BigDecimal("70007000.70007009"));
        primitiveBean.setBi(new BigInteger("80008000"));

        json = new Eson().toString(primitiveBean);
        assertEquals("{bd:70007000.70007009,bi:80008000,bo:true," +
                "by:-112,c:a,d:600.60006,f:500.5005,i:200,l:300,s:100}", json);
        assertEquals(primitiveBean, new Eson().parse(json, PrimitiveBean.class));

        primitiveBean.setD(Double.NaN);
        primitiveBean.setF(Float.NaN);
        json = new Eson().toString(primitiveBean);
        assertEquals("{bd:70007000.70007009,bi:80008000,bo:true," +
                "by:-112,c:a,d:null,f:null,i:200,l:300,s:100}", json);

        primitiveBean.setD(Double.NEGATIVE_INFINITY);
        primitiveBean.setF(Float.POSITIVE_INFINITY);
        json = new Eson().toString(primitiveBean);
        assertEquals("{bd:70007000.70007009,bi:80008000,bo:true," +
                "by:-112,c:a,d:null,f:null,i:200,l:300,s:100}", json);

    }

    @Test
    public void testWrapper() {
        WrapPrimitiveBean bean = new WrapPrimitiveBean();
        String string = new Eson().toString(bean);
        assertEquals("{bo:null,by:null,c:null,d:null,f:null,i:null,l:null,s:null}", string);
        assertEquals(bean, new Eson().parse(string, WrapPrimitiveBean.class));

        bean.setI(200);
        bean.setL(300L);
        bean.setBo(true);
        bean.setC('a');
        bean.setBy((byte) 400);
        bean.setF(500.5005F);
        bean.setD(600.60006D);

        string = new Eson().toString(bean);
        assertEquals("{bo:true,by:-112,c:a,d:600.60006,f:500.5005,i:200,l:300,s:null}", string);

        assertEquals(bean, new Eson().parse(string, WrapPrimitiveBean.class));
    }

    public static class PrimitiveBean extends RBaseBean {
        private short s;
        private int i;
        private long l;
        private boolean bo;
        private char c;
        private byte by;
        private float f;
        private double d;
        private BigDecimal bd;
        private BigInteger bi;

        public short getS() {
            return s;
        }

        public void setS(short s) {
            this.s = s;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public long getL() {
            return l;
        }

        public void setL(long l) {
            this.l = l;
        }

        public boolean isBo() {
            return bo;
        }

        public void setBo(boolean bo) {
            this.bo = bo;
        }

        public char getC() {
            return c;
        }

        public void setC(char c) {
            this.c = c;
        }

        public byte getBy() {
            return by;
        }

        public void setBy(byte by) {
            this.by = by;
        }

        public float getF() {
            return f;
        }

        public void setF(float f) {
            this.f = f;
        }

        public double getD() {
            return d;
        }

        public void setD(double d) {
            this.d = d;
        }

        public BigDecimal getBd() {
            return bd;
        }

        public void setBd(BigDecimal bd) {
            this.bd = bd;
        }

        public BigInteger getBi() {
            return bi;
        }

        public void setBi(BigInteger bi) {
            this.bi = bi;
        }

    }

    public static class WrapPrimitiveBean extends RBaseBean {
        private Short s;
        private Integer i;
        private Long l;
        private Boolean bo;
        private Character c;
        private Byte by;
        private Float f;
        private Double d;

        public Short getS() {
            return s;
        }

        public void setS(Short s) {
            this.s = s;
        }

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        public Long getL() {
            return l;
        }

        public void setL(Long l) {
            this.l = l;
        }

        public Boolean getBo() {
            return bo;
        }

        public void setBo(Boolean bo) {
            this.bo = bo;
        }

        public Character getC() {
            return c;
        }

        public void setC(Character c) {
            this.c = c;
        }

        public Byte getBy() {
            return by;
        }

        public void setBy(Byte by) {
            this.by = by;
        }

        public Float getF() {
            return f;
        }

        public void setF(Float f) {
            this.f = f;
        }

        public Double getD() {
            return d;
        }

        public void setD(Double d) {
            this.d = d;
        }

    }
}
