# AsnycTaskLearning
AsyncTask有四个重要的回调onPreExecute、doInBackground, onProgressUpdate 和 onPostExecute。另外还有一个执行函数execute.


他们的调用执行顺序：


---

onPreExecute（ui线程）-->
doInBackground(新线程) -->onPostExecute

onProgressUpdate会在doInBackground过程中调用，但是它执行在ui线程中

---
如果把上面的看做需求，改如何实现？

version1 对应代码AsyncTask；

version2 对应代码AsyncTaskS;
## version 1
1. 四个函数的调用其实是ui线程和另外一个线程来回切换过程，必然想到用Handler实现。
2. 新的线程可以用 new Thread（）来实现
3. 内部定义一个publishProgress函数，
在doInBackground里调用，用来执行onProgressUpdate
4. 伪代码：

```
class InternalHandler extends Handler{
    @Override
    public void handleMessage(Message msg) {
    
        switch (msg.what)
            case MESSAGE_POST_RESULT:
                onPostExecute(参数1);
                break;
            case MESSAGE_POST_PROGRESS:
                onProgressUpdate(参数3);
                break;
        }
    }
}

onPreExecute();
new Thread(new Runnable(){
参数1 = doInBackground(参数2);
    mHandler.obtainMessage(MESSAGE_POST_RESULT,参数1).sendToTarget();
}).start()

publishProgress(参数3) {
        mHandler.obtainMessage(MESSAGE_POST_PROGRESS,参数3).sendToTarget();
    }
```
### 参数定义
先根据需求讨论一下不确定类型的参数：

1. execute函数允许传入一个参数（Params）。

2. doInBackground 执行完返回值的参数(Result)。

3. onProgressUpdate 也需要传入一个中间过程的值的参数(Progress)。

因为都是未知，提供开发者来定义的，使用泛型：

```
public AsyncTask<Param,Progress,Result>{
    
}
```
根据上面的分析，可以确定伪代码中的中文参数了：

参数 | 类型
---|---
参数1 | Result
参数2 | Params
参数3 | Progress

---

现在还有一处是不确定的：

Handler里对应的handleMessage函数的
参数1，参数5.

为了不用根据消息类型判断参数的类型，这里选择封装消息参数：

```
class AsyncResult<Data>{
        private Data[] mdata;
        private AsyncTask mtask;
        public AsyncResult(AsyncTask task,Data... data) {
            mtask = task;
            mdata = data;
        }
    }
```
在这里 Data类型对应Result，Progress两种类型。

关于这个版本，相信经常写java的人，只要了解需求，很快就能实现。

---
## version2
1. 在版本1的基础上，加入线程管理
2. 使用ThreadPoolExecutor替换Thread,
3. 封装ThreadPoolExecutor，实现线程的串行

---
### ThreadPoolExecutor的定义：

```
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
```

### SerialExecute（串行）

```
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
```


---











