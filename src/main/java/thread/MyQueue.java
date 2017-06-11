package thread;


import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Try on 2017/6/4.
 */
public class MyQueue {
    private AtomicInteger count = new AtomicInteger(0);//具有原子性操作
    private final Object lock = new Object();

    private LinkedList<String> list = new LinkedList<String>();
    private int minSize = 0;
    private int maxSize;

    public MyQueue() {
    }
    public MyQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isNotNull(){
        if(count.get() == minSize){
            return false;
        }else{
            return true;
        }
    }

    public void put(String str) throws InterruptedException {
        synchronized (lock) {
            if (count.get() == maxSize) {
                lock.wait();
            }
            list.add(str);
            count.getAndIncrement();
            System.out.println(Thread.currentThread()+" 添加"+str+"成功");
            lock.notify();
        }
    }

    public String tack() throws InterruptedException {
        String str = null;
        synchronized (lock){
            if(count.get() == minSize){
                lock.wait();
            }
            str = list.removeFirst();
            count.getAndDecrement();
            System.out.println(Thread.currentThread()+" 删除"+str+"成功");
            lock.notify();
        }
        return str;
    }

    public static void main(String[] args) throws InterruptedException {
        MyQueue myQueue = new MyQueue(5);
        myQueue.put("a");
        myQueue.put("b");
        myQueue.put("c");
        myQueue.put("d");
        myQueue.put("e");

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    myQueue.put("f");
                    myQueue.put("g");
                    myQueue.put("h");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"T1");

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (myQueue.isNotNull()){
                    try {
                        Thread.sleep(1000);
                        myQueue.tack();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        },"T2");

        t1.start();
        Thread.sleep(1000);
        t2.start();

    }
}
