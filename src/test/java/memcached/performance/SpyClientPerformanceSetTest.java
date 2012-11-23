package memcached.performance;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

import org.junit.BeforeClass;
import org.junit.Test;
import org.n3r.core.text.RRand;

public class SpyClientPerformanceSetTest {
    private static MemcachedClient mcc;

    @Test
    public void testSingleThreadSet() {
        MemSpySetThread memThread = new MemSpySetThread(0, 1);
        memThread.run();
    }

    @Test
    public void test10ThreadsSet() throws InterruptedException {
        startThreads(10);
    }

    @Test
    public void test20ThreadsSet() throws InterruptedException {
        startThreads(20);
    }

    @Test
    public void test30ThreadsSet() throws InterruptedException {
        startThreads(30);
    }

    @Test
    public void test40ThreadsSet() throws InterruptedException {
        startThreads(40);
    }

    @Test
    public void test50ThreadsSet() throws InterruptedException {
        startThreads(50);
    }

    @Test
    public void test100ThreadsSet() throws InterruptedException {
        startThreads(100);
    }

    @Test
    public void test200ThreadsSet() throws InterruptedException {
        startThreads(200);
    }

    @Test
    public void test500ThreadsSet() throws InterruptedException {
        startThreads(500);
    }

    @Test
    public void test1000ThreadsSet() throws InterruptedException {
        startThreads(1000);
    }

    private void startThreads(int threadsNum) throws InterruptedException {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for(int j = 0, jj = threadsNum; j < jj; ++j) {
            threadPool.submit(new MemSpySetThread(j, threadsNum));
        }

        threadPool.shutdown();
        threadPool.awaitTermination(10, TimeUnit.MINUTES);
    }


    public static class MemSpySetThread implements Runnable{
        private int index;
        private int threadsNum;

        public MemSpySetThread(int index, int threadsNum) {
            this.index = index;
            this.threadsNum = threadsNum;
        }

        @Override
        public void run() {
            for(int i = 0, ii = 60000 / threadsNum; i < ii; ++i) {
//                MemcachedClient mcc = getSpyClient();
                mcc.set(index + ":" + i, 300, RRand.randLetters(100));
            }
        }
    }

    @BeforeClass
    public static void getSpyClient() {
        // MemcachedClient c = new MemcachedClient(AddrUtil.getAddresses("server1:11211 server2:11211"));
        try {
            mcc = new MemcachedClient(AddrUtil.getAddresses("192.168.1.108:11211"));
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }
}
