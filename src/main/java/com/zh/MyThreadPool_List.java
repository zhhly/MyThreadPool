package com.zh;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhy
 * @version 1.0
 * @description: 手动实现线程池: 使用list列表存放任务.
 */
public class MyThreadPool_List {

    // 使用数组存放任务. 可是没有任务的时候, 会占用CPU资源
    List<Runnable> commaneList=new ArrayList<Runnable>();

    Thread thread = new Thread(()->{
        while(true){
            // 任务不为空,则执行.为空则等待.
            if(!commaneList.isEmpty()){
                Runnable remove = commaneList.remove(0);
                remove.run();
            }
        }
    });

    {
        thread.start();
    }

    // 线程什么时候创建
    // 线程的runnable 是什么？
    void execute(Runnable command) {
        commaneList.add(command);
    }

}
