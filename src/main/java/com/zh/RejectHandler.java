package com.zh;

/**
 * @author zhy
 * @version 1.0
 * @description: TODO
 * @date 2026/5/2 19:40
 */
public interface RejectHandler {
    void  reject(Runnable command, MyThreadPool myThreadPool);
}
