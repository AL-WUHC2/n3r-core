package memcached.performance;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.junit.BeforeClass;
import org.junit.Test;

public class XClientPerformanceGetTest {
    private static MemcachedClient mcc;

    @Test
    public void testSingleThreadGet() {
        MemXGetThread memThread = new MemXGetThread(0, 1);
        memThread.run();
    }

    @Test
    public void test10ThreadsGet() throws InterruptedException {
        startThreads(10);
    }

    @Test
    public void test20ThreadsGet() throws InterruptedException {
        startThreads(20);
    }

    @Test
    public void test30ThreadsGet() throws InterruptedException {
        startThreads(30);
    }

    @Test
    public void test40ThreadsGet() throws InterruptedException {
        startThreads(40);
    }

    @Test
    public void test50ThreadsGet() throws InterruptedException {
        startThreads(50);
    }

    @Test
    public void test100ThreadsGet() throws InterruptedException {
        startThreads(100);
    }

    @Test
    public void test200ThreadsGet() throws InterruptedException {
        startThreads(200);
    }

    @Test
    public void test500ThreadsGet() throws InterruptedException {
        startThreads(500);
    }

    @Test
    public void test1000ThreadsGet() throws InterruptedException {
        startThreads(1000);
    }

    private void startThreads(int threadsNum) throws InterruptedException {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int j = 0, jj = threadsNum; j < jj; ++j) {
            threadPool.submit(new MemXGetThread(j, threadsNum));
        }

        threadPool.shutdown();
        threadPool.awaitTermination(10, TimeUnit.MINUTES);
    }

    public static class MemXGetThread implements Runnable {
        private int index;
        private int threadsNum;

        public MemXGetThread(int index, int threadsNum) {
            this.index = index;
            this.threadsNum = threadsNum;
        }

        @Override
        public void run() {
            for (int i = 0, ii = 60000 / threadsNum; i < ii; ++i) {
                String str = (String) getCache(index + ":" + i);
                Assert.assertTrue(str == null || 100 == str.length());
            }
        }
    }

    @BeforeClass
    public static void getSpyClient() {
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("192.168.1.108:11211"));
        try {
            mcc = builder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getCache(String key) {
        try {
            return mcc.get(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
