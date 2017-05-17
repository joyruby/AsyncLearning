package com.example.whrwhr446.asynclearning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        AsyncTask<Integer,Integer,Integer> asyncTask = new AsyncTask<Integer, Integer, Integer>() {
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
                progressBar.setProgress(values[0]);
            }
        };
        asyncTask.execute(0);
    }
}
