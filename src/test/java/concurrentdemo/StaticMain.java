package concurrentdemo;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StaticMain {
    public static class HtmlCreator implements Runnable {
        private BlockingQueue<String> staticHtmlQueue;
        private CountDownLatch countDownLatch;
        private int index;
        private AtomicLong totalProduced;

        public HtmlCreator(int index, BlockingQueue<String> staticHtmlQueue, CountDownLatch countDownLatch,
                AtomicLong totalProduced) {
            this.index = index;
            this.staticHtmlQueue = staticHtmlQueue;
            this.countDownLatch = countDownLatch;
            this.totalProduced = totalProduced;
        }

        @Override
        public void run() {
            Random random = new Random();
            for (int i = 0, ii = random.nextInt(30); i < ii; ++i) {
                totalProduced.incrementAndGet();
                staticHtmlQueue.add(new String("index " + index + " value " + i));
            }

            countDownLatch.countDown();
            System.err.println("index " + index + " exiting!");
        }
    }

    public static class HtmlUploader implements Runnable {
        private BlockingQueue<String> blockingQueue;
        private AtomicLong totalConsumed;
        private AtomicLong total;
        private CountDownLatch htmlCreaterCountDown;

        //        private AtomicInteger blocking;

        public HtmlUploader(AtomicInteger blocking, BlockingQueue<String> blockingQueue, AtomicLong total,
                AtomicLong totalConsumed,
                CountDownLatch htmlCreaterCountDown) {
            //            this.blocking = blocking;
            this.blockingQueue = blockingQueue;
            this.total = total;
            this.totalConsumed = totalConsumed;
            this.htmlCreaterCountDown = htmlCreaterCountDown;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String string = blockingQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (string != null) {
                        System.out.println("comsumed " + string);
                        total.decrementAndGet();
                        totalConsumed.incrementAndGet();
                        sleep(new Random().nextInt(100));
                    }
                    else if (htmlCreaterCountDown.getCount() == 0) break;
                }
            } catch (InterruptedException ex) {
                System.err.println("InterruptedException");
            }

            System.err.println("MyRun exiting!");
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws InterruptedException {
        ExecutorService staticHtmlCreatorService = Executors.newCachedThreadPool();
        BlockingQueue<String> staticHtmlQueue = new LinkedBlockingQueue<String>();
        AtomicLong totalProduced = new AtomicLong();

        int staticServiceNum = 31;
        CountDownLatch htmlCreaterCountDown = new CountDownLatch(staticServiceNum);
        for (int i = 0; i < staticServiceNum; ++i)
            staticHtmlCreatorService.submit(new HtmlCreator(i, staticHtmlQueue, htmlCreaterCountDown,
                    totalProduced));

        int uploadServiceNum = 17;
        ExecutorService staticHtmlUploaderService = Executors.newCachedThreadPool();
        BlockingQueue<String>[] uploadHtmlQueue = new BlockingQueue[uploadServiceNum];
        AtomicLong[] total = new AtomicLong[uploadServiceNum];
        AtomicLong totalConsumed = new AtomicLong();
        AtomicInteger blocking = new AtomicInteger(0); // 0 -waiting 1 - running 2 - quitable
        for (int i = 0; i < uploadServiceNum; ++i) {
            total[i] = new AtomicLong();
            uploadHtmlQueue[i] = new LinkedBlockingQueue<String>();
            staticHtmlUploaderService
                    .submit(new HtmlUploader(blocking, uploadHtmlQueue[i], total[i], totalConsumed,
                            htmlCreaterCountDown));
        }

        while (true) {
            String string = staticHtmlQueue.poll(100, TimeUnit.MILLISECONDS);
            if (string != null) for (int i = 0; i < uploadServiceNum; ++i) {
                total[i].incrementAndGet();
                uploadHtmlQueue[i].put(string);
            }
            else if (htmlCreaterCountDown.getCount() == 0) break;
        }

        staticHtmlCreatorService.shutdown();
        while (!staticHtmlCreatorService.isTerminated())
            sleep(100);

        GOTO: while (true) {
            for (int i = 0; i < uploadServiceNum; ++i)
                if (total[i].get() > 0) {
                    sleep(100);
                    continue GOTO;
                }

            break;
        }

        staticHtmlUploaderService.shutdown();
        while (!staticHtmlUploaderService.isTerminated())
            sleep(100);

        System.err.println("main exiting, total produced " + totalProduced + ", total consumed " + totalConsumed);
    }

    public static void sleep(long mili) {
        try {
            Thread.sleep(mili);
        } catch (InterruptedException e) {}
    }
}
