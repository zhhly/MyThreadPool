package com.zh;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;

/**
 * @author zhy
 * @version 1.0
 * @description: 手动实现线程池: 使用 阻塞列表 存放任务.
 */
public class MyThreadPool {

    // （核心线程数 core pool size）
    public int corePoolSize = 10;
    // （最大线程数，maxPoolSize）
    public int maxPoolSize = 16;
    // 等待时间
    public int timeout = 60;
    // 时间单位
    public TimeUnit timeUnit = TimeUnit.SECONDS;
    // 使用阻塞队列存放任务, 没有任务的时候进行阻塞, 不会占用CPU资源.
    BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(maxPoolSize);
    // 拒绝策略
    RejectHandler rejectHandler = new ThrowRejectHandler();
    // 现在：假设我们有多个线程. 但是需要多少个线程呢？ 让用户自己传。
    List<Thread> coreThreadList = new ArrayList();
    // 辅助线程
    List<Thread> supporThreadList = new ArrayList();

    public MyThreadPool(int corePoolSize, int maxPoolSize, int timeout, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue, RejectHandler rejectHandler) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.blockingQueue = blockingQueue;
        this.rejectHandler = rejectHandler;
    }

    // 线程什么时候创建
    // 线程的runnable 是什么？
    void execute(Runnable command) {
        // commaneList.add(command); // 使用add的原因是，当队列满了之后，add会报错。

        // 而offer也是给队列添加任务，但是队列满了则会返回false，而不是报错。
        boolean offer = blockingQueue.offer(command);

        // 当任务小于核心线程数的时候。那么问题来了，当阻塞队列满了还有新的任务进来，忙不过来了怎么办？（给他几个辅助线程，即最大线程数）
        if (coreThreadList.size() < corePoolSize) {
            Thread thread = new CoreThread();
            coreThreadList.add(thread);
            thread.start();
        }

        if (offer) {
            return;
        }

        // 辅助线程
        if ((coreThreadList.size() + supporThreadList.size()) < maxPoolSize) {
            Thread thread = new SupporThread();
            supporThreadList.add(thread);
            thread.start();
        }

        // 如果辅助线程与核心线程都忙不过来了怎么办？使用拒绝策略
        if (!offer) {
            rejectHandler.reject(command, this);
        }
    }

    // 拒绝策略 - 自动丢弃任务
    static class DiscardReject implements RejectHandler {
        @Override
        public void reject(Runnable command, MyThreadPool myThreadPool) {
            // 该策略是 任务满了，就丢弃被拒绝的任务
            myThreadPool.blockingQueue.poll();
            command.run();
        }
    }

    // 拒绝策略 - 抛异常
    static class ThrowRejectHandler implements RejectHandler {
        @Override
        public void reject(Runnable command, MyThreadPool myThreadPool) {
            throw new RuntimeException("任务满了！");
        }
    }

    // 拒绝策略 - 重试
    static class RetryRejectHandler implements RejectHandler {
        @Override
        public void reject(Runnable command, MyThreadPool myThreadPool) {
            command.run();
        }
    }

    // 核心线程
    class CoreThread extends Thread {
        @Override
        public void run() {
            while (true) {
                // 任务不为空,则执行. 为空则阻塞在这里.
                try {
                    Runnable command = blockingQueue.take();
                    command.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // 辅助线程
    class SupporThread extends Thread {
        @Override
        public void run() {
            while (true) {
                // 任务不为空,则执行. 为空则阻塞在这里.
                try {
                    Runnable command = blockingQueue.poll(timeout, timeUnit);
                    if (command == null) {
                        break;
                    }
                    command.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("辅助线程任务完成");
            }
        }
    }
}
