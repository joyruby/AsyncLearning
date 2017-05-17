package com.example.whrwhr446.asynclearning;

import android.os.Handler;
import android.os.Message;

/**
 * Created by whrwhr446 on 16/05/2017.
 * 1.先执行onPreExecute方法
 * 2.在另外一个线程执行doInBackground
 *   执行过程中可以通过publishProgress 在onProgressUpdate中更新ui
 * 3.2执行完，执行onPostExecute
 * ********************************************
 * 思路：
 * 1.封装Handler，负责更新ui，
 * 提供两种状态：执行中，执行完
 * 2.用thread启动一个新的线程，在新线程中执行doInBackground
 *
 */

public abstract class AsyncTask<Params,Progress,Result> {

    private final static int MESSAGE_POST_RESULT =0x1;
    private final static int MESSAGE_POST_PROGRESS =0x2;

    public static   class InternalHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            AsyncResult result = (AsyncResult) msg.obj;
            switch (msg.what){
                case MESSAGE_POST_RESULT:
                    result.mtask.onPostExecute(result.mdata[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mtask.onProgressUpdate(result.mdata);
                    break;
            }
        }
    }
    private InternalHandler mHandler =new InternalHandler();
    public AsyncTask() {
    }

    public AsyncTask<Params,Progress,Result> execute( final Params... pr){
        final AsyncTask self = this;
        onPreExecute();

        new Thread(new Runnable() {
            @Override
            public void run() {

                mHandler.obtainMessage(MESSAGE_POST_RESULT,new AsyncResult<Result>(self,doInBackground(pr))).sendToTarget();
            }
        }).start();
        return this;
    }

    protected abstract Result doInBackground(Params... var1);

    protected void onPreExecute() {
    }

    protected void onPostExecute(Result result) {
    }

    protected void onProgressUpdate(Progress... values) {

    }


    protected final void publishProgress(Progress... values) {
        mHandler.obtainMessage(MESSAGE_POST_PROGRESS,new AsyncResult<Progress>(this,values)).sendToTarget();
    }
    static class AsyncResult<Data>{
        private Data[] mdata;
        private AsyncTask mtask;
        public AsyncResult(AsyncTask task,Data... data) {
            mtask = task;
            mdata = data;
        }
    }


}
