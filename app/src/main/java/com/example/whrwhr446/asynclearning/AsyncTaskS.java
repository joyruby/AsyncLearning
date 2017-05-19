package com.example.whrwhr446.asynclearning;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by whrwhr446 on 18/05/2017.
 */

public abstract class AsyncTaskS<Params,Progress,Result> {
    //use ThreadPoolExecutor replace Thread
    private static final int CORE_SIZE = 5;
    private static final int CORE_MAX_SIZE = 128;
    private static final int KEEP_LIVE =1;

    private static final BlockingDeque<Runnable> mblockingDeque = new LinkedBlockingDeque<Runnable>();

    private static final ThreadPoolExecutor mDefaultThread =
            new ThreadPoolExecutor(CORE_SIZE,CORE_MAX_SIZE,KEEP_LIVE, TimeUnit.SECONDS,mblockingDeque,new ThreadFactory(){

                @Override
                public Thread newThread(@NonNull Runnable r) {
                    return new Thread(r);
                }
            });

    private static final SerialExecute mSerialExecute = new SerialExecute();

    private static class SerialExecute implements Executor{
        ArrayDeque<Runnable> arrayDeque = new ArrayDeque<Runnable>();
        Runnable mActive;
        @Override
        public synchronized void execute(@NonNull final Runnable command) {
            arrayDeque.offer(new Runnable() {
                @Override
                public void run() {
                    try {
                        command.run();
                    }finally {
                        sheduleNext();
                    }
                }
            });
            if(mActive == null){
                sheduleNext();
            }
        }

        public synchronized void sheduleNext(){
            if((mActive = arrayDeque.poll()) != null){
                mDefaultThread.execute(mActive);
            }
        }
    }

    private final    WorkRunnable mWork;
    private  final FutureTask mFuture;
    public AsyncTaskS(){
        mWork = new WorkRunnable<Params,Result>() {
            @Override
            public Result call() throws Exception {
                return postResult(doInBackground(mParams));
            }
        };
        mFuture = new FutureTask(mWork){
            @Override
            protected void done() {
                super.done();
            }
        };
    }

    public AsyncTaskS<Params,Progress,Result> execute(Params... var){
        return onSerialExecute(mSerialExecute,var);

    }


    public AsyncTaskS<Params,Progress,Result> onSerialExecute(Executor executor,Params... var){

        onPreExecute();
        mWork.mParams =var;
        executor.execute(mFuture);

        return  this;
    }

    private static final int MESSAGE_PROGRESS = 1;
    private static final int MESSAGE_RESULT = 0;

    private static final InternalHandler mHandler = new InternalHandler();


    private static class InternalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult asyncTask = (AsyncTaskResult) msg.obj;
            switch (msg.what){
                case MESSAGE_RESULT:
                    asyncTask.mTask.onPostExecute(asyncTask.mData[0]);
                    break;
                case MESSAGE_PROGRESS:
                    asyncTask.mTask.onProgressUpdate(asyncTask.mData);
                    break;
            }
        }
    }


    private Result postResult(Result result){
        mHandler.obtainMessage(MESSAGE_RESULT, new AsyncTaskResult<Result>(this,result)).sendToTarget();
        return result;
    }

    protected abstract Result doInBackground(Params... var1);

    protected void onPreExecute() {
    }

    protected void onPostExecute(Result result) {
    }

    protected void onProgressUpdate(Progress... values) {

    }
    private static abstract class  WorkRunnable<Params,Result> implements Callable<Result>{
        Params[] mParams;
    }
    private static class AsyncTaskResult<Data>{
        Data[] mData;
        AsyncTaskS mTask;
        public AsyncTaskResult(AsyncTaskS task,Data... data){
            mTask = task;
            mData = data;
        }
    }

    protected   void publishProgress(Progress... progress){
        mHandler.obtainMessage(MESSAGE_PROGRESS, new AsyncTaskResult<Progress>(this,progress)).sendToTarget();
    }
}
