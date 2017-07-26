package zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorWatcher1 {
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

        //建立一个NodeCache缓存
        final NodeCache nodeCache = new NodeCache(cf, "/super", false);
        //设置成true,那么nodeCache在第一次启动的时候就会到zookeeper上去获取节点的数据内容，并保存在cache中
        nodeCache.start(true);
        nodeCache.getListenable().addListener(new NodeCacheListener() {

            //触发事件为节点本身创建和更新节点，删除节点不触发此操作
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("路径为：" + nodeCache.getCurrentData().getPath());
                System.out.println("数据为：" + new String(nodeCache.getCurrentData().getData()));
                System.out.println("状态为：" + nodeCache.getCurrentData().getStat());
                System.out.println("========================================================");
            }
        });

        Thread.sleep(1000);
        cf.create().creatingParentsIfNeeded().forPath("/super", "123".getBytes());

        Thread.sleep(1000);
        cf.setData().forPath("/super", "456".getBytes());

        Thread.sleep(1000);
        //删除cache缓存监听的节点时需要关闭cache缓存,否则NullPointerException
        nodeCache.close();
        cf.delete().forPath("/super");

        Thread.sleep(Integer.MAX_VALUE);
    }
}
