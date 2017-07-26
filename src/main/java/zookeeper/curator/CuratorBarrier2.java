package zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 *程序等待，一个程序确认，确认完成后等待程序继续执行
 */
public class CuratorBarrier2 {
    static final String CONNECT_ADDR = "192.168.153.133:2181,192.168.153.131:2181,192.168.153.128:2181";
    static final int SESSION_OUTTIME = 5000;

    static DistributedBarrier barrier = null;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 5; i++)
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

                        barrier = new DistributedBarrier(cf, "/Barrier2");
                        System.out.println(Thread.currentThread().getName() + "设置barrier!");
                        barrier.setBarrier();    //设置
                        barrier.waitOnBarrier();    //等待
                        System.out.println("---------开始执行程序----------");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();

        Thread.sleep(20*1000);
        barrier.removeBarrier();  //释放
        System.out.println("可以开始了");
    }
}
