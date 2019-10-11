package com.mybaitsplus.devtools.core.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 线程处理类
 *
 */
public class ExecutorProcessPool {

    private ExecutorService executor;
    private static ExecutorProcessPool pool = new ExecutorProcessPool();
    private static ExecutorProcessPool cachedThreadPool=new ExecutorProcessPool("CachedThreadPool");
    private final int threadMax = 15;

    private ExecutorProcessPool() {
        System.out.println("threadMax>>>>>>>" + threadMax);
        executor = ExecutorServiceFactory.getInstance().createFixedThreadPool(threadMax);
    }
    
    private ExecutorProcessPool(String ThreadPool){
    	System.out.println("ThreadPoolName>>>>>>>" + ThreadPool);
        executor = ExecutorServiceFactory.getInstance().createCachedThreadPool();
    }
    
    public static ExecutorProcessPool getInstance() {
        return pool;
    }
    
    public static ExecutorProcessPool getInstancecached(){
    	return cachedThreadPool;
    }

    /**
     * 关闭线程池，这里要说明的是：调用关闭线程池方法后，线程池会执行完队列中的所有任务才退出
     * 
     */
    public void shutdown(){
        executor.shutdown();
    }

    /**
     * 提交任务到线程池，可以接收线程返回值
     * 
     */
    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    /**
     * 提交任务到线程池，可以接收线程返回值
     */
    public Future<?> submit(Callable<?> task) {
        return executor.submit(task);
    }

    /**
     * 直接提交任务到线程池，无返回值
     */
    public void execute(Runnable task){
        executor.execute(task);
    }

}
