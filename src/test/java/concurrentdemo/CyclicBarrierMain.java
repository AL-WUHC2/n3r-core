package concurrentdemo;

import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CyclicBarrierMain {
    public static class Consumer implements Runnable {
        private CyclicBarrier cyclicBarrier;
        private int index;

        public Consumer(int index, CyclicBarrier cyclicBarrier) {
            this.index = index;
            this.cyclicBarrier = cyclicBarrier;

        }

        @Override
        public void run() {
            try {
                while (true) {
                    cyclicBarrier.await();

                    System.err.println("index " + index + " Running !");
                }
            } catch (InterruptedException e) {
                System.err.println("index " + index + " InterruptedException!");
            } catch (BrokenBarrierException e) {
                System.err.println("index " + index + " BrokenBarrierException!");
            }
        }

    }

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        ExecutorService staticHtmlUploaderService = Executors.newCachedThreadPool();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(17 + 1);
        for (int i = 0; i < 17; ++i)
            staticHtmlUploaderService.submit(new Consumer(i, cyclicBarrier));

        Scanner scanner = new Scanner(System.in);
        while (true) {
            while (cyclicBarrier.getNumberWaiting() < 17) {
                Thread.sleep(100);
            }

            String line = scanner.nextLine();
            if ("quit".equals(line)) break;

            cyclicBarrier.await();
        }

        staticHtmlUploaderService.shutdownNow();
    }
}
