package com.example.whrwhr446.asynclearning;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by whrwhr446 on 18/05/2017.
 */

public class ThreadPool {
    public static  int CORE_SIZE = 10;
    private static final int CORE_MAX_SIZE = 30;
    private static final int KEEP_LIVE = 1;
    private static final BlockingDeque<Runnable> linkedBlockingDeque = new LinkedBlockingDeque<Runnable>(128);
    private static final SerialExecutor serialExecutor = new SerialExecutor();


    public final static ThreadPoolExecutor threadPoolExe = new ThreadPoolExecutor(
            CORE_SIZE, CORE_MAX_SIZE, KEEP_LIVE, TimeUnit.SECONDS, linkedBlockingDeque, new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r);
        }
    });
    public static void run(final String tag){
        threadPoolExe.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(tag);
            }
        });
    }
    public static void serialRun(final String tag){
        serialExecutor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(tag);
            }
        });
    }
    static class SerialExecutor implements Executor{
        ArrayDeque<Runnable> mDeque = new ArrayDeque<Runnable>();
        Runnable mActive;
        @Override
        public void execute(@NonNull final Runnable r) {
            mDeque.offer(new Runnable() {
                @Override
                public void run() {
                    //即使 报错了 也要继续往下走。。
                    try {
                        r.run();
                    }finally {
                        sheduleNext();
                    }
                }
            });
            if(mActive == null){
                sheduleNext();
            }

        }
        private void sheduleNext(){
            if((mActive = mDeque.pop())!= null){
                threadPoolExe.execute(mActive);
            }
        }
    }
}
