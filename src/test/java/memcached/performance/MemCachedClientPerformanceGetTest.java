package memcached.performance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

public class MemCachedClientPerformanceGetTest {
    @Test
    public void testSingleThreadGet() {
        MemGetThread memThread = new MemGetThread(0, 1);
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
        for(int j = 0, jj = threadsNum; j < jj; ++j) {
            threadPool.submit(new MemGetThread(j, threadsNum));
        }

        threadPool.shutdown();
        threadPool.awaitTermination(10, TimeUnit.MINUTES);
    }

    @BeforeClass
    public static void setup() {
        String[] servers = { "192.168.1.108:11211" };
        SockIOPool pool = SockIOPool.getInstance();
        pool.setServers(servers);
        pool.setFailover(true);
        pool.setInitConn(10);
        pool.setMinConn(1);
        pool.setMaxConn(20);
        // pool.setMaintSleep( 30 );
        pool.setNagle(false);
        pool.setSocketTO(3000);
        pool.setAliveCheck(true);
        pool.initialize();
    }

    public static class MemGetThread implements Runnable{
        private int index;
        private int threadsNum;

        public MemGetThread(int index, int threadsNum) {
            this.index = index;
            this.threadsNum = threadsNum;
        }

        @Override
        public void run() {
            for(int i = 0, ii = 60000 / threadsNum; i < ii; ++i) {
                MemCachedClient mcc = new MemCachedClient();
                String str = (String) mcc.get(index + ":" + i);

                Assert.assertTrue(str == null || 100 == str.length());
            }
        }
    }


}
