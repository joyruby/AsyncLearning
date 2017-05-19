package com.example.whrwhr446.asynclearning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

     ProgressBar progressBar1;
     ProgressBar progressBar2;
     ProgressBar progressBar3;
     ProgressBar progressBar4;
     ProgressBar progressBar5;
     ProgressBar progressBar6;
     ProgressBar progressBar7;
     ProgressBar progressBar8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initMultiThread();
    }

    private void initMultiThread() {
          //方法-；
//        Object A = new Object();
//        Object B = new Object();
//        Object C = new Object();
//        MultiThread threadA = new MultiThread("A",A,C);
//        MultiThread threadB = new MultiThread("B",B,A);
//        MultiThread threadC = new MultiThread("C",C,B);
//        new Thread(threadA).start();
//        new Thread(threadB).start();
//
//        new Thread(threadC).start();
        //方案二
//        ThreadPool.CORE_SIZE = 1;
//        for(int i =0; i< 10;i++){
//            ThreadPool.run("A");
//            ThreadPool.run("B");
//            ThreadPool.run("C");
//        }
        //方案三
//        for(int i =0; i< 10;i++){
//            ThreadPool.run("A");
//            ThreadPool.run("B");
//            ThreadPool.run("C");
//        }
    }

    private void initView(){
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);
        progressBar4 = (ProgressBar) findViewById(R.id.progressBar4);
        progressBar1.setProgress(0);
        progressBar2.setProgress(0);
        progressBar3.setProgress(0);
        progressBar4.setProgress(0);
        AsyncTaskVOne asyncTask1 = new AsyncTaskVOne(progressBar1);
        AsyncTaskVOne asyncTask2 = new AsyncTaskVOne(progressBar2);
        AsyncTaskVOne asyncTask3 = new AsyncTaskVOne(progressBar3);
        AsyncTaskVOne asyncTask4 = new AsyncTaskVOne(progressBar4);
        asyncTask1.execute(0);
        asyncTask2.execute(0);
        asyncTask3.execute(0);
        asyncTask4.execute(0);

        progressBar5 = (ProgressBar) findViewById(R.id.progressBar5);
        progressBar6 = (ProgressBar) findViewById(R.id.progressBar6);
        progressBar7 = (ProgressBar) findViewById(R.id.progressBar7);
        progressBar8 = (ProgressBar) findViewById(R.id.progressBar8);
        progressBar5.setProgress(0);
        progressBar6.setProgress(0);
        progressBar7.setProgress(0);
        progressBar8.setProgress(0);
        AsyncTaskVTwo asyncTask5 = new AsyncTaskVTwo(progressBar5);
        AsyncTaskVTwo asyncTask6 = new AsyncTaskVTwo(progressBar6);
        AsyncTaskVTwo asyncTask7 = new AsyncTaskVTwo(progressBar7);
        AsyncTaskVTwo asyncTask8 = new AsyncTaskVTwo(progressBar8);
        asyncTask5.execute(0);
        asyncTask6.execute(0);
        asyncTask7.execute(0);
        asyncTask8.execute(0);
    }
    private class AsyncTaskVOne extends AsyncTask<Integer,Integer,Integer>{
        private ProgressBar viewItem;
        public AsyncTaskVOne(ProgressBar view) {
            viewItem = view;
        }

        @Override
        protected Integer doInBackground(Integer... var1) {
            int time = var1[0];
            while (time<100){
                try {
                    Thread.sleep(1000);
                    time=time+5;
                    publishProgress(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return time;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Toast.makeText(MainActivity.this,"下载成功！",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            viewItem.setProgress(values[0]);
        }
    }

    private class AsyncTaskVTwo extends AsyncTaskS<Integer,Integer,Integer>{
        private ProgressBar viewItem;
        public AsyncTaskVTwo(ProgressBar view) {
            viewItem = view;
        }

        @Override
        protected Integer doInBackground(Integer... var1) {
            int time = var1[0];
            while (time<50){
                try {
                    Thread.sleep(1000);
                    time=time+5;
                    publishProgress(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return time;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Toast.makeText(MainActivity.this,"下载成功！",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            viewItem.setProgress(values[0]);
        }
    }
}
