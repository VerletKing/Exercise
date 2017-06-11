package thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Try on 2017/6/4.
 */
public class Demo {

    public static void main(String[] args) {
        List list = new ArrayList();
        final Object lock = new Object();
        //CountDownLatch不需要加锁
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
//                synchronized (lock) {
                    for (int i = 0; i < 10; i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        list.add(i);
                        System.out.println(Thread.currentThread()+" 线程 =》" + list.size());
                        if (list.size() == 5) {
//                            lock.notify();
                            countDownLatch.countDown();
                            System.out.println("发送消息");
                        }
                    }
                }
//            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
//                synchronized (lock){
                    if(list.size()!=5){
                        try {
//                            lock.wait();
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(Thread.currentThread()+"收到通知");
                    throw new RuntimeException("收到通知");
                }
//            }
        });

        t2.start();
        t1.start();
    }
}
