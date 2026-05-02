package com.zh;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {

        // MyThreadPool_List myTheadPool = new MyThreadPool_List();
        // myTheadPool.execute(()->{
        //     try {
        //         Thread.sleep(1000);
        //     } catch (InterruptedException e) {
        //         throw new RuntimeException(e);
        //     }
        //     System.out.println(Thread.currentThread().getName());
        // });

        MyThreadPool myTheadPool = new MyThreadPool(2, 4, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<>(4), new MyThreadPool.DiscardReject());
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            myTheadPool.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + " " + finalI);
            });
        }
        System.out.println("其他任务！");

    }
}
