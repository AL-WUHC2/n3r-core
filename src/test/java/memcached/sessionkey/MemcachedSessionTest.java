package memcached.sessionkey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.BeforeClass;
import org.junit.Test;
import org.n3r.core.lang.RBaseBean;
import org.n3r.core.text.RRand;
import org.n3r.eson.Eson;
import org.n3r.esql.Esql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

public class MemcachedSessionTest {
    @BeforeClass
    public static void setup() {
        String[] servers = { "192.168.32.129:11211" };
        SockIOPool pool = SockIOPool.getInstance();
        pool.setServers(servers);
        pool.setFailover(true);
        pool.setInitConn(10);
        pool.setMinConn(5);
        pool.setMaxConn(250);
        // pool.setMaintSleep( 30 );
        pool.setNagle(false);
        pool.setSocketTO(3000);
        pool.setAliveCheck(true);
        pool.initialize();
    }

    @Test
    public void testBatchSet() {
        for(int i = 0, ii = 10000; i < ii; ++i) {
            MemCachedClient mcc = new MemCachedClient();
            mcc.set("" + i, RRand.randLetters(100));
        }

        for(int i = 0, ii = 10000; i < ii; ++i) {
            MemCachedClient mcc = new MemCachedClient();
            System.out.println(i + ":" + mcc.get("" + i));
        }
    }

    @Test
    public void testOracleUse() throws InterruptedException {
        final AtomicLong oracleUsed = new AtomicLong(0);
        final AtomicLong memcachedUsed = new AtomicLong(0);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int j = 0, jj = 20; j < jj; ++j)
            threadPool.submit(new Runnable() {

                @Override
                public void run() {
                    Eson eson = new Eson();
                    for (int i = 0, ii = 100; i < ii; ++i) {
                        PhwSecurityStoreable impl = new OracleStoreableImpl();
                        PhwSecurityBean phwSecurityBean = new PhwSecurityBean(true);

                        String key = UUID.randomUUID().toString();

                        long start = System.currentTimeMillis();
                        impl.writeSecurity(key, phwSecurityBean);
                        PhwSecurityBean security = impl.readSecurity(key);
                        oracleUsed.addAndGet(System.currentTimeMillis() - start);
                        assertEquals(phwSecurityBean, security);

                        start = System.currentTimeMillis();
                        MemCachedClient mcc = new MemCachedClient();
                        mcc.set(key, eson.toString(phwSecurityBean),
                                new Date(phwSecurityBean.getExpiredTime()/*注意这里是过期毫秒数,不是过期时间点*/));
                        security = eson.parse((String) mcc.get(key), PhwSecurityBean.class);
                        mcc.delete(key);
                        memcachedUsed.addAndGet(System.currentTimeMillis() - start);
                        assertEquals(phwSecurityBean, security);
                    }

                }
            });

        threadPool.shutdown();
        while (!threadPool.isTerminated())
            Thread.sleep(1000);

        System.out.println("oracleUsed: " + oracleUsed);
        System.out.println("memcachedUsed: " + memcachedUsed);
    }

    @Test
    public void testKey() {
        MemCachedClient mcc = new MemCachedClient();
        Object object = mcc.get("3da11d41-88e7-4bc7-bb61-590a20d78e26");
        System.out.println(object);
    }

    @Test
    public void testMemcachedUse() throws InterruptedException {
        MemCachedClient mcc = new MemCachedClient();
        assertTrue(mcc.set("mykey", "myvalue", new Date(3000)));

        assertEquals("myvalue", mcc.get("mykey"));
        TimeUnit.SECONDS.sleep(2);
        assertEquals("myvalue", mcc.get("mykey"));
        TimeUnit.SECONDS.sleep(2);
        assertNull(mcc.get("mykey"));

        //        mcc.delete("mykey");
        mcc.set("name", "bingoo");
        mcc.set("age", "32");
        /*
        Map<String, Map<String, String>> stats = mcc.stats();
        System.out.println(stats);

        Map<String, Map<String, String>> statsItems = mcc.statsItems();
        System.out.println(statsItems);

        Map<String, Map<String, String>> statsCacheDump = mcc.statsCacheDump(0, 1000);
        System.out.println(statsCacheDump);

        List<String> allKeys = getAllKeys(mcc);
        System.out.println(allKeys);
        */

        dump(mcc);

    }

    /**

     * 获取某台memcached的所有key
     * @param memcachedNo
     * @return
     */
    public static List<String> getAllKeys(MemCachedClient mcc) {
        List<String> list = new ArrayList<String>();

        Map<String, Map<String, String>> items = mcc.statsItems();
        for (String itemKey : items.keySet()) {

            System.out.println("----------------------------");
            System.out.println("items:" + itemKey);//items:127.0.0.1:11211
            System.out.println("============================");

            Map<String, String> maps = items.get(itemKey);
            for (String mapsKey : maps.keySet()) {
                String mapsValue = maps.get(mapsKey);

                System.out.println("----------------------------");
                System.out.println("maps:" + mapsKey);//items:15:number
                System.out.println("maps:" + mapsValue);//50
                System.out.println("============================");

                if (mapsKey.endsWith("number")) {//memcached key 类型  item_str:integer:number_str
                    String[] arr = mapsKey.split(":");
                    int slabNumber = Integer.valueOf(arr[1].trim());
                    int limit = Integer.valueOf(mapsValue.trim());

                    Map<String, Map<String, String>> dumpMaps = mcc.statsCacheDump(slabNumber, limit);
                    for (String dumpKey : dumpMaps.keySet()) {

                        System.out.println("----------------------------");
                        System.out.println("dumpMaps:" + dumpKey);//127.0.0.1:11211
                        System.out.println("============================");

                        Map<String, String> allMap = dumpMaps.get(dumpKey);
                        for (String allKey : allMap.keySet())
                            list.add(allKey.trim());
                    }
                }
            }
        }
        return list;
    }

    public static void dump(MemCachedClient mcc) {

        Map<String, Map<String, String>> items = mcc.statsItems();
        for (String itemKey : items.keySet()) {

            System.out.println("----------------------------");
            System.out.println("items:" + itemKey);//items:127.0.0.1:11211
            System.out
                    .println("============http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=321859================");

            Map<String, String> maps = items.get(itemKey);
            for (String mapsKey : maps.keySet()) {
                String mapsValue = maps.get(mapsKey);

                System.out.println("----------------------------");
                System.out.println("maps:" + mapsKey);//items:15:number
                System.out.println("maps:" + mapsValue);//50
                System.out.println("============================");

                if (mapsKey.endsWith("number")) {//memcached key 类型  item_str:integer:number_str
                    String[] arr = mapsKey.split(":");
                    int slabNumber = Integer.valueOf(arr[1].trim());
                    int limit = Integer.valueOf(mapsValue.trim());

                    Map<String, Map<String, String>> dumpMaps = mcc.statsCacheDump(slabNumber, limit);
                    for (String dumpKey : dumpMaps.keySet()) {

                        System.out.println("----------------------------");
                        System.out.println("dumpMaps:" + dumpKey);//127.0.0.1:11211
                        System.out.println("============================");

                        Map<String, String> allMap = dumpMaps.get(dumpKey);
                        for (String allKey : allMap.keySet())
                            System.out.println(allKey);
                    }
                }
            }
        }
    }

    /**
     * 敏感信息存储oracle实现类。
     */
    public class OracleStoreableImpl implements PhwSecurityStoreable {
        private Logger logger = LoggerFactory.getLogger(OracleStoreableImpl.class);

        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public boolean writeSecurity(String key, PhwSecurityBean phwSecurityBean) {
            Map inMap = new HashMap();
            inMap.put("SESSION_KEY", key);

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(phwSecurityBean.getExpiredTime()));

            inMap.put("EXPIRED_TIME", phwSecurityBean.getExpiredTime());
            inMap.put("BUSI_BIND", phwSecurityBean.getBinding());
            String string = new Eson().toString(phwSecurityBean);
            inMap.put("SESSION_CONTENT", string);

            int ret = new Esql().params(inMap).insert("phwSecurity.insertPhwSecurity").execute();
            logger.info("当前存储的敏感信息为:{}", inMap);

            return ret == 1;
        }

        @Override
        public PhwSecurityBean readSecurity(String key) {
            Esql esql = new Esql().params(key);
            String sessionContent = esql.select("phwSecurity.queryPhwSecurity").limit(1).execute();

            if (sessionContent == null) return null;

            try {
                PhwSecurityBean outMap = new Eson().parse(sessionContent, PhwSecurityBean.class);
                esql.delete("phwSecurity.deletePhwSecurity").execute();
                return outMap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    /**
     * 敏感信息存储接口：实现读写敏感信息。
     */
    public static interface PhwSecurityStoreable {

        /**
         * 写入敏感信息。
         * @param key 敏感信息key
         * @param phwSecurityBean 敏感信息javabean
         */
        boolean writeSecurity(String key, PhwSecurityBean phwSecurityBean);

        /**
         * 读取敏感信息。
         * @param key 敏感信息key
         * @return 敏感信息
         */
        PhwSecurityBean readSecurity(String key);
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
}
