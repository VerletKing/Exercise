package zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 等待同时开始  等待同时结束
 */
public class CuratorBarrier1 {
    static final String CONNECT_ADDR = "192.168.153.133:2181,192.168.153.131:2181,192.168.153.128:2181";
    static final int SESSION_OUTTIME = 5000;

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
                        CuratorFramework cf = CuratorFrameworkFactory.builder()
                                .connectString(CONNECT_ADDR)
                                .sessionTimeoutMs(SESSION_OUTTIME)
                                .retryPolicy(retryPolicy)
                                .build();
                        cf.start();

                        try {
                            lock.lock();
                            if (cf.checkExists().forPath("/barrier") == null) {
                                cf.create().forPath("/barrier");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            lock.unlock();
                        }

                        DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(cf, "/barrier", 5);
                        Thread.sleep(1000 * (new Random()).nextInt(3));
                        System.out.println(Thread.currentThread().getName() + "已经准备");

                        barrier.enter();
                        System.out.println("同时开始运行...");
                        Thread.sleep(1000 * (new Random()).nextInt(3));
                        System.out.println(Thread.currentThread().getName() + "运行完毕");

                        barrier.leave();
                        System.out.println("同时退出运行...");
                        cf.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
