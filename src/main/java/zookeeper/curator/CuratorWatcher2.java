package zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorWatcher2 {
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

        //创建节点本身不触发监听,必需在创建PathChildrenCache缓存前创建
        //不创建会抛KeeperErrorCode = Unimplemented for /super异常，不影响整体程序
        cf.create().creatingParentsIfNeeded().forPath("/super", "init".getBytes());

        //创建PathChildrenCache缓存，第三个参数为是否接受节点数据内容，false则不接受
        PathChildrenCache pathChildrenCache = new PathChildrenCache(cf, "/super", true);
        //初始化进行缓存监听
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {

            //异步监听子节点的变更（CUD）,节点本身不触发
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                switch (pathChildrenCacheEvent.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED 路径: " + pathChildrenCacheEvent.getData().getPath());
                        System.out.println("CHILD_ADDED 数据: " + new String(pathChildrenCacheEvent.getData().getData()));
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED 路径: " + pathChildrenCacheEvent.getData().getPath());
                        System.out.println("CHILD_UPDATED 数据: " + new String(pathChildrenCacheEvent.getData().getData()));
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED 路径: " + pathChildrenCacheEvent.getData().getPath());
                        System.out.println("CHILD_REMOVED 数据: " + new String(pathChildrenCacheEvent.getData().getData()));
                        break;
                }
                System.out.println("=========================================================================");
            }
        });

        //创建子节点
        Thread.sleep(1000);
        cf.create().creatingParentsIfNeeded().forPath("/super/c1", "c1内容".getBytes());
        Thread.sleep(1000);
        cf.create().creatingParentsIfNeeded().forPath("/super/c2", "c2内容".getBytes());

        //修改子节点
        Thread.sleep(1000);
        cf.setData().forPath("/super/c1", "c1更新的内容".getBytes());

        //删除子节点
        Thread.sleep(1000);
        cf.delete().forPath("/super/c2");

        //删除节点本身
        Thread.sleep(1000);
        cf.delete().deletingChildrenIfNeeded().forPath("/super");

        Thread.sleep(Integer.MAX_VALUE);
    }
}