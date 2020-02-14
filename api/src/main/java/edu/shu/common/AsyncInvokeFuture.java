package edu.shu.common;

import java.util.concurrent.*;

/**
 * @author liang
 * @create 2020/2/14 10:10 上午
 */
public class AsyncInvokeFuture implements Future {

    public RpcResponse response;

    public CountDownLatch countDownLatch = new CountDownLatch(1);

    private AsyncCallBack callBack;

    public AsyncCallBack getCallBack() {
        return callBack;
    }

    public void addCallBack(AsyncCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return countDownLatch.getCount()==0;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException{
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response.getResult();
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException{
        try {
            countDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response.getResult();
    }
}
