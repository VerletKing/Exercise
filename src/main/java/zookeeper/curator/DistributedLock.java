package zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 借助curatorFramework使用分布式锁
 */
public class DistributedLock {
    static final String CONNECT_ADDR = "192.168.153.133:2181,192.168.153.131:2181,192.168.153.128:2181";
    static final int SESSION_OUTTIME = 5000;

    static int count = 10;

    public static void genarNo() {
        count--;
        System.out.println("count = " + count);
    }

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .sessionTimeoutMs(SESSION_OUTTIME)
                .retryPolicy(retryPolicy)
                .build();
        cf.start();

        cf.create().forPath("/lock");

        final CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //分布式锁
                    final InterProcessMutex lock = new InterProcessMutex(cf, "/lock");
                    try {
                        cyclicBarrier.await();
                        //加锁
                        lock.acquire();
                        genarNo();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss|SSS");
                        System.out.println(simpleDateFormat.format(new Date()));
                        System.out.println("=================================");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            //释放
                            lock.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        Thread.sleep(50*1000);
        cf.delete().forPath("/lock");

        cf.close();
    }
}