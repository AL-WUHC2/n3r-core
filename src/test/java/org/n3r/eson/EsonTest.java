package org.n3r.eson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.n3r.core.lang.RBaseBean;
import org.n3r.core.text.RRand;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import static org.junit.Assert.*;

public class EsonTest {
    @Test
    public void testMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "bingoo");
        map.put("age", 32);
        map.put("man", true);

        Eson eson = new Eson().on(map);
        assertEquals("{age:32,man:true,name:bingoo}", eson.toString());
        assertEquals("{age:32,man:true,name:'bingoo'}", eson.valueQuote('\'').toString());

        eson = new Eson().on(map).sortKey(false);
        assertEquals("{age:32,name:bingoo,man:true}", eson.toString());
        assertEquals("{age:32,name:'bingoo',man:true}", eson.valueQuote('\'').toString());

        assertEquals(map, eson.parse("{age:32,man:true,name:bingoo}"));
        assertEquals(map, eson.parse("{age:32,man:true,name:'bingoo'}"));
        assertEquals(map, eson.parse("{age:32,man:true,name:\"bingoo\"}"));
    }

    @Test
    public void testBean() {
        Person person = new Person("bingoo", 32, true);
        Eson eson = new Eson().on(person);
        assertEquals("{age:32,man:true,name:bingoo}", eson.toString());
        assertEquals("{age:32,man:true,name:'bingoo'}", eson.valueQuote('\'').toString());
        assertEquals("{age:32,man:true,name:\"bingoo\"}", eson.valueQuote('\"').toString());
        assertEquals("{\"age\":32,\"man\":true,\"name\":\"bingoo\"}", eson.std().toString());

        // JavaBean 没有默认构造函数，可以使用ObjectInstantiator来实例化
        ObjectInstantiator personInstantiator = new ObjenesisStd().getInstantiatorOf(Person.class);
        Person newInstance = (Person) personInstantiator.newInstance();
        assertNotNull(newInstance);

        eson = new Eson().targetClass(Person.class);
        assertEquals(person, eson.parse("{age:32,man:true,name:bingoo}"));
        assertEquals(person, eson.parse("{age:32,man:true,name:'bingoo'}"));
        assertEquals(person, eson.parse("{age:32,man:true,name:\"bingoo\"}"));

        String text = "[{KH:\"(2010-2011-2)-13105-13039-1\", JC:\"1\"}] ";
        List<Map<String, String>> l = new Eson().parse(text);
        assertEquals("1", l.get(0).get("JC"));
        assertEquals("(2010-2011-2)-13105-13039-1", l.get(0).get("KH"));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testPhwSecurityBean() {
        String str = "{binding:18602506990,domain:10010.com,expiredTime:{},fromAction:null,ip:192.168.1.108,minituesToLive:{},safeKeyName:c30b87b5-80ae-4e63-a27d-7c9bbb48a623,securityContent:{HLoSRPXKfg:DdjIe5yYuYAQjcpupd2QWsyaFXB9Xe,PWePBugXwj:EqTCXsMIJ3kP3U7P38tN1edGO9FuaR,PvmZOLwkWk:sojjuvv4YItlZMHUs1feq4P76qaCqP,QljtUpvKDR:z793oA05G9lTbl8Qz4uDOrDP1tueuf,TjkoAaRPzZ:NbRdVh8gJvdl0BSz2kHQr6F1mjridF,ZwWryjhLGN:nhCwAJXaDiiQ0hqrDaL3bZPEt67Do7,dSrowjSqTr:TAfFbM6wUQnAEnX0pYVnhAE5t4ZvYB,fVLxhJiYkP:y246He6jm7S6MN76giaNiZxZIzSeGu,iJmELhTYHg:HBvmSiHBHsPco2x3IPYwDBqPeBT2MI,kyDuqHoHZX:bsvMCohphETRPQTO7n5Vkxfki9uAL7,mGmGpoEghU:x53Uzzny8P0y06jvEEnhIBZaghyhVn,qbtWHCwPBK:NZwXdnHMifrtpFujaQBWjm5aQeyCEm,tsWWGyCmiz:BqXJnUKOEbyVp9N52reVDcmOMt2HCW,wZtgWAuXMp:vPKn9A0DxDudgO9980HaPJyeCBsk6n,zqQqEtiePX:zxgnjid0jxn4pYN2Ijo7CjdLYVQScX},securitySafekey:c06a4f76-049d-455c-94ad-d5cd03c562e2,securityToken:71e0f055-2621-41e7-900f-7cd3327ac872,stepAction:98bde530-5194-496e-b1d4-b091067f8142}";
        Map map = new Eson().parse(str);
        String str2 = new Eson().toString(map);
        Map map2 = new Eson().parse(str2);
        assertEquals(map, map2);

        str = "{binding:18602506990,domain:10010.com,expiredTime:1348906564252,fromAction:null,ip:192.168.1.108,minituesToLive:30,safeKeyName:1a892151-d406-44f2-be59-4309d588ba6f,securityContent:{HIatpJNuql:2FZoeQiXEvhopwgpKr9eowSvuXDbuA,JpbcizApwc:bPlcR3pBnGBb2wc8XgWujyojCds7va,LQOnqYWmbi:9hrqjN4bl19q3dIGHDETN6O5LYFFSd,QrDzRZDVlK:Gysz8UIgxekIwOpRdRvGql4siTnpQ3,ZOlirbeDqe:N8ktDJbqcxBOD2HBxwfW45CgfAUcjW,annKikZkIz:Q7iAnrW1vMUHSFF3BBLCykUNu74eJx,cUjxpwQSDi:PWeXpXmW7KchYA90qMe4bwZA9YQ8ED,cpuovmdnlN:beWCFaZCIZL9cLpxte8wKaudhZTwEO,hYDdlsnwOW:xg7Oiiu0kUg2TvsOipAy4IGJ0zvXo0,iLXGOGRMQq:8D4ntOHqQaT1TBOdjTNHks7Ht4lfB3,ixuYMLagYW:swCmWfTu5zGyDfEp9sqDBOFXu1DnX9,izLUydkGrz:cBmdhQaObk6w1gT6Z7kvuOz4fBMgtw,pOoPSZSGud:41ijJMSAM86zO4ND6SV8hDQNZ202Gd,sAhWGTrgiW:X6VwZFIS2VFXctzDFx7V4rsdAcZdSY,sacTvKxLOv:gIW9PgotJcoCV1WzxFGhoApPaf4qQD,vqOrBLYFHt:Y4TYTSa7HYRfH1JISA2MtAxNKAgBQF,wHFQqopfPc:IeT0LSoWF2uhQbSoqJZf7q2zacexEz},securitySafekey:1f65d0dd-0a45-4b4f-8503-31f9b30d012e,securityToken:2cd6c4e1-a56f-4559-ad36-2b6d5289e5fa,stepAction:1cb5b808-5f66-4ea8-a308-3af4fb80263d}";
        PhwSecurityBean bean = new Eson().parse(str, PhwSecurityBean.class);
        str2 = new Eson().toString(bean);
        PhwSecurityBean bean2 = new Eson().parse(str2, PhwSecurityBean.class);
        assertEquals(bean, bean2);
    }

    @Test
    public void testValueNullTrueFalse() {
        assertEquals("null", new Eson().toString(null));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", null);
        map.put("age", "null");
        map.put("bool1", true);
        map.put("bool2", "true");
        map.put("bool3", false);
        map.put("bool4", "false");
        String expected = "{age:\"null\",bool1:true,bool2:\"true\",bool3:false,bool4:\"false\",name:null}";
        assertEquals(expected, new Eson().toString(map));
        assertEquals(map, new Eson().parse(expected));
    }

    @Test
    public void testKeyNullTrueFalse() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", null);
        map.put(null, null);
        String expected = "{null:null,name:null}";
        assertEquals(expected, new Eson().toString(map));
        assertEquals(map, new Eson().parse(expected));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testSpecical() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "bingoo\r\nhuang");
        String expected = "{name:bingoo\\r\\nhuang}";
        assertEquals(expected, new Eson().toString(map));
        assertEquals(map, new Eson().parse(expected));

        map.put("name", "bingoo\"huang");
        expected = "{name:bingoo\\\"huang}";
        assertEquals(expected, new Eson().toString(map));
        assertEquals(map, new Eson().parse(expected));

        map.put("name", "bingoo\'huang");
        expected = "{name:bingoo\\\'huang}";
        assertEquals(expected, new Eson().toString(map));
        assertEquals(map, new Eson().parse(expected));

        String text = "{\"errorMessage\":\"resource '/rpc/hello/none.json' is not found !\"}";
        Map json = new Eson().parse(text);
        assertEquals("{errorMessage:resource \\'/rpc/hello/none.json\\' is not found !}", json.toString());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testJdbcUrl() {
        String text = "{'jdbcUrl':\"jdbc:wrap-jdbc:filters=default:name=com.alibaba.dragoon.monitor:jdbc:mysql://10.20.129.167/dragoon_v25monitordb?useUnicode=true&characterEncoding=UTF-8\"}";
        Map m1 = new Eson().parse(text);

        text = "{'jdbcUrl':'jdbc:wrap-jdbc:filters=default:name=com.alibaba.dragoon.monitor:jdbc:mysql://10.20.129.167/dragoon_v25monitordb?useUnicode=true&characterEncoding=UTF-8'}";
        Map m2 = new Eson().parse(text);
        assertEquals(m1, m2);
    }

    @Test
    public void testList() {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("a");
        arrayList.add("b");
        arrayList.add("c");
        String string = new Eson().toString(arrayList);
        assertEquals("[a,b,c]", string);
        JSONArray array = new Eson().parse("[a,b,c]");
        assertEquals("a", array.getString(0));
        assertEquals("b", array.getString(1));
        assertEquals("c", array.getString(2));

        List<String> list = new Eson().parse("[a,b,c]", String.class);
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    public void testPrettyFormatMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "bingoo");
        map.put("age", 32);
        map.put("man", true);

        Eson eson = new Eson().on(map).prettyFormat(true);
        String expected = "{\n\tage:32,\n\tman:true,\n\tname:bingoo\n}";
        assertEquals(expected, eson.toString());
        assertEquals(map, eson.parse(expected));

        String str = "{binding:18602506990,domain:10010.com,expiredTime:1348906564252,fromAction:null,ip:192.168.1.108,minituesToLive:30,safeKeyName:1a892151-d406-44f2-be59-4309d588ba6f,securityContent:{HIatpJNuql:2FZoeQiXEvhopwgpKr9eowSvuXDbuA,JpbcizApwc:bPlcR3pBnGBb2wc8XgWujyojCds7va,LQOnqYWmbi:9hrqjN4bl19q3dIGHDETN6O5LYFFSd,QrDzRZDVlK:Gysz8UIgxekIwOpRdRvGql4siTnpQ3,ZOlirbeDqe:N8ktDJbqcxBOD2HBxwfW45CgfAUcjW,annKikZkIz:Q7iAnrW1vMUHSFF3BBLCykUNu74eJx,cUjxpwQSDi:PWeXpXmW7KchYA90qMe4bwZA9YQ8ED,cpuovmdnlN:beWCFaZCIZL9cLpxte8wKaudhZTwEO,hYDdlsnwOW:xg7Oiiu0kUg2TvsOipAy4IGJ0zvXo0,iLXGOGRMQq:8D4ntOHqQaT1TBOdjTNHks7Ht4lfB3,ixuYMLagYW:swCmWfTu5zGyDfEp9sqDBOFXu1DnX9,izLUydkGrz:cBmdhQaObk6w1gT6Z7kvuOz4fBMgtw,pOoPSZSGud:41ijJMSAM86zO4ND6SV8hDQNZ202Gd,sAhWGTrgiW:X6VwZFIS2VFXctzDFx7V4rsdAcZdSY,sacTvKxLOv:gIW9PgotJcoCV1WzxFGhoApPaf4qQD,vqOrBLYFHt:Y4TYTSa7HYRfH1JISA2MtAxNKAgBQF,wHFQqopfPc:IeT0LSoWF2uhQbSoqJZf7q2zacexEz},securitySafekey:1f65d0dd-0a45-4b4f-8503-31f9b30d012e,securityToken:2cd6c4e1-a56f-4559-ad36-2b6d5289e5fa,stepAction:1cb5b808-5f66-4ea8-a308-3af4fb80263d}";
        String str1 = "{\n\tbinding:18602506990,\n\tdomain:10010.com,\n\texpiredTime:1348906564252,\n\tfromAction:null,\n\tip:192.168.1.108,\n\tminituesToLive:30,\n\tsafeKeyName:1a892151-d406-44f2-be59-4309d588ba6f,\n\tsecurityContent:\n\t{\n\t\tHIatpJNuql:2FZoeQiXEvhopwgpKr9eowSvuXDbuA,\n\t\tJpbcizApwc:bPlcR3pBnGBb2wc8XgWujyojCds7va,\n\t\tLQOnqYWmbi:9hrqjN4bl19q3dIGHDETN6O5LYFFSd,\n\t\tQrDzRZDVlK:Gysz8UIgxekIwOpRdRvGql4siTnpQ3,\n\t\tZOlirbeDqe:N8ktDJbqcxBOD2HBxwfW45CgfAUcjW,\n\t\tannKikZkIz:Q7iAnrW1vMUHSFF3BBLCykUNu74eJx,\n\t\tcUjxpwQSDi:PWeXpXmW7KchYA90qMe4bwZA9YQ8ED,\n\t\tcpuovmdnlN:beWCFaZCIZL9cLpxte8wKaudhZTwEO,\n\t\thYDdlsnwOW:xg7Oiiu0kUg2TvsOipAy4IGJ0zvXo0,\n\t\tiLXGOGRMQq:8D4ntOHqQaT1TBOdjTNHks7Ht4lfB3,\n\t\tixuYMLagYW:swCmWfTu5zGyDfEp9sqDBOFXu1DnX9,\n\t\tizLUydkGrz:cBmdhQaObk6w1gT6Z7kvuOz4fBMgtw,\n\t\tpOoPSZSGud:41ijJMSAM86zO4ND6SV8hDQNZ202Gd,\n\t\tsAhWGTrgiW:X6VwZFIS2VFXctzDFx7V4rsdAcZdSY,\n\t\tsacTvKxLOv:gIW9PgotJcoCV1WzxFGhoApPaf4qQD,\n\t\tvqOrBLYFHt:Y4TYTSa7HYRfH1JISA2MtAxNKAgBQF,\n\t\twHFQqopfPc:IeT0LSoWF2uhQbSoqJZf7q2zacexEz\n\t},\n\tsecuritySafekey:1f65d0dd-0a45-4b4f-8503-31f9b30d012e,\n\tsecurityToken:2cd6c4e1-a56f-4559-ad36-2b6d5289e5fa,\n\tstepAction:1cb5b808-5f66-4ea8-a308-3af4fb80263d\n}";
        PhwSecurityBean bean = new Eson().parse(str, PhwSecurityBean.class);
        String str2 = new Eson().prettyFormat(true).toString(bean);
        assertEquals(str1, str2);
        PhwSecurityBean bean2 = new Eson().parse(str2, PhwSecurityBean.class);
        assertEquals(bean, bean2);
    }

    @Test
    public void testPrettyFormatBean() {
        House house = new House();
        house.setName("bingoo huang");
        Room diningRoom = new Room();
        diningRoom.setArea(15);
        diningRoom.setDirection("北");
        house.setDiningRoom(diningRoom);
        Room sittingRoom = new Room();
        sittingRoom.setArea(30);
        sittingRoom.setDirection("南");

        house.setSittingRoom(sittingRoom);

        Eson eson = new Eson().on(house).prettyFormat(true);
        String expected = "{\n\tdiningRoom:\n\t{\n\t\tarea:15,\n\t\tdirection:北\n\t}," +
                "\n\tname:bingoo huang,\n\tsittingRoom:\n\t" +
                "{\n\t\tarea:30,\n\t\tdirection:南\n\t}\n}";
        assertEquals(expected, eson.toString());
        House real = new Eson().parse(expected, House.class);
        assertEquals(house, real);
    }

    @Test
    public void testBlank() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "  bingoo  ");
        map.put("age", 32);
        map.put("man", true);

        Eson eson = new Eson().on(map);
        String expected = "{age:32,man:true,name:\"  bingoo  \"}";
        String str = "{  age:  32 , man : true, name :  \"  bingoo  \"  }";
        assertEquals(expected, eson.toString());
        assertEquals(map, eson.parse(str));
    }

    public static class House extends RBaseBean {
        private String name;
        private Room diningRoom;
        private Room sittingRoom;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Room getDiningRoom() {
            return diningRoom;
        }

        public void setDiningRoom(Room diningRoom) {
            this.diningRoom = diningRoom;
        }

        public Room getSittingRoom() {
            return sittingRoom;
        }

        public void setSittingRoom(Room sittingRoom) {
            this.sittingRoom = sittingRoom;
        }
    }

    public static class Room extends RBaseBean {
        private int area;
        private String direction;

        public int getArea() {
            return area;
        }

        public void setArea(int area) {
            this.area = area;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }

    @SuppressWarnings("rawtypes")
    public static class PhwSecurityBean extends RBaseBean {
        private String ip;
        private String domain;
        private String binding;

        private String stepAction;
        private String securityToken;
        private String safeKeyName;
        private String securitySafekey;
        private long expiredTime;
        private long minituesToLive;

        @SuppressWarnings({ "serial", "unchecked" })
        private Map securityContent;

        public PhwSecurityBean() {

        }

        @SuppressWarnings({ "serial", "unchecked" })
        public PhwSecurityBean(boolean createRandomValues) {
            ip = "192.168.1.108";
            domain = "10010.com";
            binding = "18602506990";

            stepAction = UUID.randomUUID().toString();
            securityToken = UUID.randomUUID().toString();
            safeKeyName = UUID.randomUUID().toString();
            securitySafekey = UUID.randomUUID().toString();
            expiredTime = System.currentTimeMillis() + 10000;
            minituesToLive = 30;

            securityContent = new HashMap() {
                {
                    for (int i = 0, ii = 10 + RRand.randInt(10); i < ii; ++i)
                        put(RRand.randLetters(10), RRand.randAlphanumeric(30));
                }
            };
        }

        private String fromAction;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getBinding() {
            return binding;
        }

        public void setBinding(String binding) {
            this.binding = binding;
        }

        public String getStepAction() {
            return stepAction;
        }

        public void setStepAction(String stepAction) {
            this.stepAction = stepAction;
        }

        public String getSecurityToken() {
            return securityToken;
        }

        public void setSecurityToken(String securityToken) {
            this.securityToken = securityToken;
        }

        public String getSafeKeyName() {
            return safeKeyName;
        }

        public void setSafeKeyName(String safeKeyName) {
            this.safeKeyName = safeKeyName;
        }

        public String getSecuritySafekey() {
            return securitySafekey;
        }

        public void setSecuritySafekey(String securitySafekey) {
            this.securitySafekey = securitySafekey;
        }

        public long getExpiredTime() {
            return expiredTime;
        }

        public void setExpiredTime(long expiredTime) {
            this.expiredTime = expiredTime;
        }

        public long getMinituesToLive() {
            return minituesToLive;
        }

        public void setMinituesToLive(long minituesToLive) {
            this.minituesToLive = minituesToLive;
        }

        public Map getSecurityContent() {
            return securityContent;
        }

        public void setSecurityContent(Map securityContent) {
            this.securityContent = securityContent;
        }

        public String getFromAction() {
            return fromAction;
        }

        public void setFromAction(String fromAction) {
            this.fromAction = fromAction;
        }

    }

    public static class Person extends RBaseBean {
        private String name;
        private int age;
        private boolean man;

        public Person(String name, int age, boolean man) {
            this.name = name;
            this.age = age;
            this.man = man;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public boolean isMan() {
            return man;
        }

        public void setMan(boolean man) {
            this.man = man;
        }
    }

}
