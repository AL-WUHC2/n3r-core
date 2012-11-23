package org.n3r.core.xml.unmarshal;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.n3r.core.collection.RMap;
import org.n3r.core.xml.RXml;
import org.n3r.core.xml.bean.AnnoBean;
import org.n3r.core.xml.bean.GenericBean;
import org.n3r.core.xml.bean.GenericSub;
import org.n3r.core.xml.bean.NormalBean;
import org.n3r.core.xml.bean.NormalSub;
import org.n3r.core.xml.bean.Person;
import org.n3r.core.xml.bean.PersonWithId;
import org.n3r.core.xml.bean.UnnecessaryBean;

import static org.junit.Assert.*;

public class RUnmarshallerTest {

    @Test
    public void test1() {
        Person person = new Person();
        person.setName("aaa");
        person.setAge(12);

        Person person2 = RXml.xmlToBean("<Person><Name>aaa</Name><Age>12</Age></Person>", Person.class);
        assertEquals(person, person2);

        String separtor = System.getProperty("line.separator");
        person2 = RXml.xmlToBean("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + separtor
                + "<Person>" + separtor
                + "    <Name>aaa</Name>" + separtor
                + "    <Age>12</Age>" + separtor
                + "</Person>" + separtor, Person.class);
        assertEquals(person, person2);
    }

    @Test
    public void test2() {
        String str = RXml.xmlToBean("<String>Hello</String>", String.class);
        assertEquals("Hello", str);
    }

    @Test
    public void test3() {
        PersonWithId person = new PersonWithId();
        person.setName("AAA");
        person.setAge(12);
        person.setIdCode("1234567890");

        PersonWithId person2 = RXml.xmlToBean(
                "<PersonWithId><Age>12</Age><IdCode>1234567890</IdCode><Name>AAA</Name></PersonWithId>",
                PersonWithId.class);
        assertEquals(person, person2);
    }

    @Test
    public void test4() {
        AnnoBean anno = new AnnoBean();
        anno.setParam1("HELLO");
        AnnoBean anno2 = RXml.xmlToBean("<root><branch>HELLO</branch></root>", AnnoBean.class);
        assertEquals(anno, anno2);
    }

    @Test
    public void testUnnecessary() {
        UnnecessaryBean bean = new UnnecessaryBean();
        bean.setName("aaa");
        UnnecessaryBean bean2 = RXml.xmlToBean("<Root><Name>aaa</Name></Root>", UnnecessaryBean.class);
        assertEquals(bean, bean2);
        bean.setNickName("bbb");
        bean2 = RXml.xmlToBean("<Root><Name>aaa</Name><NickName>bbb</NickName></Root>", UnnecessaryBean.class);
        assertEquals(bean, bean2);

        try {
            bean2 = RXml.xmlToBean("<Root><NickName>bbb</NickName></Root>", UnnecessaryBean.class);
            fail();
        }
        catch (Exception e) {
            assertEquals("Node Name isn't found in Root", e.getMessage());
        }
    }

    @Test
    public void testGeneric() {
        GenericBean<NormalBean> gen = new GenericBean<NormalBean>();

        NormalSub nor = new NormalSub();
        nor.setNormal("normal");
        gen.setGenericField(nor);

        NormalBean nor2 = new NormalBean();
        nor2.setNormal("normal2");
        gen.setNormal(nor2);

        NormalSub nor3 = new NormalSub();
        nor3.setNormal("normal3");
        GenericSub<NormalBean> sub = new GenericSub<NormalBean>();
        sub.setSub(nor3);
        gen.setSubField(sub);

        NormalBean nor4 = new NormalBean();
        nor4.setNormal("normal4");
        NormalBean nor5 = new NormalBean();
        nor5.setNormal("normal5");
        gen.setBeans(Arrays.asList(nor4, nor5));

        NormalBean nor7 = new NormalBean();
        nor7.setNormal("normal7");
        NormalBean nor8 = new NormalBean();
        nor8.setNormal("normal8");
        gen.setNormal2s(Arrays.asList(nor7, nor8));

        NormalSub nor6 = new NormalSub();
        nor6.setNormal("normal6");
        gen.setCdataField(nor6);

        NormalSub nor9 = new NormalSub();
        nor9.setNormal("normal9");
        GenericSub<NormalBean> sub2 = new GenericSub<NormalBean>();
        sub2.setSub(nor9);
        gen.setGenericCData(sub2);

        String xml = "<GenericBean>"
                + "<Bean _type_=\"org.n3r.core.xml.bean.NormalBean\"><Normal>normal4</Normal></Bean>"
                + "<Bean _type_=\"org.n3r.core.xml.bean.NormalBean\"><Normal>normal5</Normal></Bean>"
                + "<CdataField _type_=\"org.n3r.core.xml.bean.NormalSub\"><![CDATA[<NormalSub><Normal>normal6</Normal></NormalSub>]]></CdataField>"
                + "<GenericCData><![CDATA[<GenericSub><Sub _type_=\"org.n3r.core.xml.bean.NormalSub\"><Normal>normal9</Normal></Sub></GenericSub>]]></GenericCData>"
                + "<GenericField _type_=\"org.n3r.core.xml.bean.NormalSub\"><Normal>normal</Normal></GenericField>"
                + "<Normal><Normal>normal2</Normal></Normal>"
                + "<Normal2><Normal>normal7</Normal></Normal2>"
                + "<Normal2><Normal>normal8</Normal></Normal2>"
                + "<SubField><Sub _type_=\"org.n3r.core.xml.bean.NormalSub\"><Normal>normal3</Normal></Sub></SubField>"
                + "</GenericBean>";
        GenericBean xmlToBean = RXml.xmlToBean(xml, GenericBean.class);

        assertEquals(gen, xmlToBean);
    }

    @Test
    public void testGeneric2() {
        GenericBean<String> gen = new GenericBean<String>();

        gen.setGenericField("normal");

        NormalBean nor2 = new NormalBean();
        nor2.setNormal("normal2");
        gen.setNormal(nor2);

        GenericSub<String> sub = new GenericSub<String>();
        sub.setSub("normal3");
        gen.setSubField(sub);

        gen.setBeans(Arrays.asList("normal4", "normal5"));

        NormalBean nor7 = new NormalBean();
        nor7.setNormal("normal7");
        NormalBean nor8 = new NormalBean();
        nor8.setNormal("normal8");
        gen.setNormal2s(Arrays.asList(nor7, nor8));

        gen.setCdataField("normal6");

        GenericSub<String> sub2 = new GenericSub<String>();
        sub2.setSub("normal9");
        gen.setGenericCData(sub2);

        String xml = "<GenericBean>"
                + "<Bean _type_=\"java.lang.String\">normal4</Bean>"
                + "<Bean _type_=\"java.lang.String\">normal5</Bean>"
                + "<CdataField _type_=\"java.lang.String\"><![CDATA[<String>normal6</String>]]></CdataField>"
                + "<GenericCData><![CDATA[<GenericSub><Sub _type_=\"java.lang.String\">normal9</Sub></GenericSub>]]></GenericCData>"
                + "<GenericField _type_=\"java.lang.String\">normal</GenericField>"
                + "<Normal><Normal>normal2</Normal></Normal>"
                + "<Normal2><Normal>normal7</Normal></Normal2>"
                + "<Normal2><Normal>normal8</Normal></Normal2>"
                + "<SubField><Sub _type_=\"java.lang.String\">normal3</Sub></SubField>"
                + "</GenericBean>";
        GenericBean xmlToBean = RXml.xmlToBean(xml, GenericBean.class);

        assertEquals(gen, xmlToBean);
    }

    @Test
    public void testGeneric3() {
        GenericBean<NormalBean> gen = new GenericBean<NormalBean>();

        NormalSub nor = new NormalSub();
        nor.setNormal("normal");
        gen.setGenericField(nor);

        NormalBean nor2 = new NormalBean();
        nor2.setNormal("normal2");
        gen.setNormal(nor2);

        NormalSub nor3 = new NormalSub();
        nor3.setNormal("normal3");
        GenericSub<NormalBean> sub = new GenericSub<NormalBean>();
        sub.setSub(nor3);
        gen.setSubField(sub);

        NormalBean nor4 = new NormalBean();
        nor4.setNormal("normal4");
        NormalBean nor5 = new NormalBean();
        nor5.setNormal("normal5");
        gen.setBeans(Arrays.asList(nor4, nor5));

        NormalBean nor7 = new NormalBean();
        nor7.setNormal("normal7");
        NormalBean nor8 = new NormalBean();
        nor8.setNormal("normal8");
        gen.setNormal2s(Arrays.asList(nor7, nor8));

        NormalSub nor6 = new NormalSub();
        nor6.setNormal("normal6");
        gen.setCdataField(nor6);

        NormalSub nor9 = new NormalSub();
        nor9.setNormal("normal9");
        GenericSub<NormalBean> sub2 = new GenericSub<NormalBean>();
        sub2.setSub(nor9);
        gen.setGenericCData(sub2);

        String xml = "<GenericBean>"
                + "<Bean><Normal>normal4</Normal></Bean>"
                + "<Bean><Normal>normal5</Normal></Bean>"
                + "<CdataField><![CDATA[<NormalSub><Normal>normal6</Normal></NormalSub>]]></CdataField>"
                + "<GenericCData><![CDATA[<GenericSub><Sub><Normal>normal9</Normal></Sub></GenericSub>]]></GenericCData>"
                + "<GenericField><Normal>normal</Normal></GenericField>"
                + "<Normal><Normal>normal2</Normal></Normal>"
                + "<Normal2><Normal>normal7</Normal></Normal2>"
                + "<Normal2><Normal>normal8</Normal></Normal2>"
                + "<SubField><Sub><Normal>normal3</Normal></Sub></SubField>"
                + "</GenericBean>";
        Map<String, Object> types = RMap.of("beans", NormalBean.class,
                        "cdataField", NormalSub.class,
                        "genericCData", RMap.of("sub", NormalSub.class),
                        "genericField", NormalSub.class,
                        "subField", RMap.of("sub", NormalSub.class));
        GenericBean xmlToBean = RXml.xmlToBean(xml, GenericBean.class, types);

        assertEquals(gen, xmlToBean);
    }

    @Test
    public void testGeneric4() {
        GenericBean<String> gen = new GenericBean<String>();

        gen.setGenericField("normal");

        NormalBean nor2 = new NormalBean();
        nor2.setNormal("normal2");
        gen.setNormal(nor2);

        GenericSub<String> sub = new GenericSub<String>();
        sub.setSub("normal3");
        gen.setSubField(sub);

        gen.setBeans(Arrays.asList("normal4", "normal5"));

        NormalBean nor7 = new NormalBean();
        nor7.setNormal("normal7");
        NormalBean nor8 = new NormalBean();
        nor8.setNormal("normal8");
        gen.setNormal2s(Arrays.asList(nor7, nor8));

        gen.setCdataField("normal6");

        GenericSub<String> sub2 = new GenericSub<String>();
        sub2.setSub("normal9");
        gen.setGenericCData(sub2);

        String xml = "<GenericBean>"
                + "<Bean>normal4</Bean>"
                + "<Bean>normal5</Bean>"
                + "<CdataField><![CDATA[<String>normal6</String>]]></CdataField>"
                + "<GenericCData><![CDATA[<GenericSub><Sub>normal9</Sub></GenericSub>]]></GenericCData>"
                + "<GenericField>normal</GenericField>"
                + "<Normal><Normal>normal2</Normal></Normal>"
                + "<Normal2><Normal>normal7</Normal></Normal2>"
                + "<Normal2><Normal>normal8</Normal></Normal2>"
                + "<SubField><Sub>normal3</Sub></SubField>"
                + "</GenericBean>";
        Map<String, Object> types = RMap.of("beans", String.class,
                "cdataField", String.class,
                "genericCData", RMap.of("sub", String.class),
                "genericField", String.class,
                "subField", RMap.of("sub", String.class));
        GenericBean xmlToBean = RXml.xmlToBean(xml, GenericBean.class, types);

        assertEquals(gen, xmlToBean);
    }

}
