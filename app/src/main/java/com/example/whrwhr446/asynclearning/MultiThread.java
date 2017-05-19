package com.example.whrwhr446.asynclearning;

/**
 * Created by whrwhr446 on 18/05/2017.
 * use multiThread to print ABC in order by 10times;
 */

public class MultiThread implements Runnable {
    private Object mlock;
    private Object mpre;
    private String mTag;

    public MultiThread(String Tag,Object lock,Object pre) {
        mlock = lock;
        mpre = pre;
        mTag = Tag;
    }
    @Override
    public void run(){
        int i =0;
        while (i <= 10){
            synchronized (mpre){

                synchronized (mlock){
                    mlock.notify();
                }
                try {
                    mpre.wait();
                    i++;
                    if(i > 10) break;
                    System.out.println(mTag);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        }
    }

}
