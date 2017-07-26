package zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

/**
 * 分布式计数
 */
public class Atomicinteger {

    static final String CONNECT_ADDR = "192.168.153.133:2181,192.168.153.131:2181,192.168.153.128:2181";
    static final int SESSION_OUTTIME = 5000;

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .sessionTimeoutMs(SESSION_OUTTIME)
                .retryPolicy(retryPolicy)
                .build();
        cf.start();

//        cf.create().forPath("/stomic");

        DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(cf, "/stomic", new RetryNTimes(3, 1000));
//        atomicInteger.forceSet(0);
        AtomicValue<Integer> value = atomicInteger.add(1);
        System.out.println(value.succeeded());
        System.out.println(value.postValue());    //最新值
        System.out.println(value.preValue());    //原始值

//        cf.delete().forPath("/stomic");
        cf.close();
    }
}
