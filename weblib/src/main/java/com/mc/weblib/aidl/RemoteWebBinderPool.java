package com.mc.weblib.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import com.mc.weblib.IBinderPool;
import com.mc.weblib.aidl.mainpro.MainProAidlInterface;
import com.mc.weblib.aidl.mainpro.MainProHandleRemoteService;

import java.util.concurrent.CountDownLatch;

/**
 * 为了维护独立进程和主进程之间的连接，避免每次aidl调用时都去重新进行binder连接和获取，需要专门提供一个类去维护连接，并根据条件返回Binder
 */

public class RemoteWebBinderPool {

    // 定义BinderCode
    public static final int BINDER_WEB_AIDL = 1;
    private Context mContext;
    // volatile 用来修饰被不同线程访问和修改的变量
    private static volatile RemoteWebBinderPool sInstance;
    /**
     * CountDownLatch类是一个同步计数器,构造时传入int参数,该参数就是计数器的初始值，每调用一次countDown()方法，计数器减1,计数器大于0 时，await()方法会阻塞程序继续执行
     * CountDownLatch如其所写，是一个倒计数的锁存器，当计数减至0时触发特定的事件。利用这种特性，可以让主线程等待子线程的结束。
     */
    private CountDownLatch mConnectBinderPoolCountDownLatch;
    private IBinderPool mBinderPool;


    private RemoteWebBinderPool(Context context) {
        mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    public static RemoteWebBinderPool getInstance(Context context){
        if(sInstance == null){
           synchronized (RemoteWebBinderPool.class){
               if(sInstance == null){
                   sInstance = new RemoteWebBinderPool(context);
               }
           }
        }
        return sInstance;
    }

    private synchronized void connectBinderPoolService(){
        // 只有一个线程有效
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent service = new Intent(mContext, MainProHandleRemoteService.class);
        mContext.bindService(service,mBinderPoolConnection,Context.BIND_AUTO_CREATE);
        try {
            // 等待，直到CountDownLatch中的线程数为0
            mConnectBinderPoolCountDownLatch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        try {
            if (mBinderPool != null) {
                binder = mBinderPool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service); //得到该对象之后，我们就可以用来进行进程间的方法调用和传输啦。
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient,0);
            }catch (RemoteException e){
                e.printStackTrace();
            }
            // 执行一次countDown，其计数减一
            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            //解除死亡绑定
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient,0);
            mBinderPool = null;
            // 重连
            connectBinderPoolService();
        }
    };

    //Stub内部继承Binder，具有跨进程传输能力
    public static class BinderPoolImpl extends IBinderPool.Stub{

        private Context context;

        public BinderPoolImpl(Context context) {
            this.context = context;
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_WEB_AIDL: {
                    binder = new MainProAidlInterface(context);
                    break;
                }
                default:
                    break;
            }
            return binder;
        }
    }


}
