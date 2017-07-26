package zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CuratorBase {
    //zookeeper地址
    static final String CONNECT_ADDR = "192.168.153.133:2181,192.168.153.131:2181,192.168.153.128:2181";
    //session超时时间
    static final int SESSION_OUTTIME = 5000; //ms

    public static void main(String[] args) throws Exception {
        //重试策略：初试时间为1s 重试十次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        //通过工厂创建连接
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .sessionTimeoutMs(SESSION_OUTTIME)
                .retryPolicy(retryPolicy)
                //.namespace("super")
                .build();
        //开启连接
        cf.start();

//        System.out.println(ZooKeeper.States.CONNECTED);
//        System.out.println(cf.getState());

        if(cf.checkExists().forPath("/super")!=null){
            //删除节点
            cf.delete().guaranteed().deletingChildrenIfNeeded().forPath("/super");
        }

        //添加节点  指定节点类型（不加withMode方法默认为持久类型节点）
        cf.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/super/c1", "c1内容".getBytes());
        //读取节点
        System.out.println(new String(cf.getData().forPath("/super/c1")));
        //修改节点
        cf.setData().forPath("/super/c1", "修改后c2内容".getBytes());
        System.out.println(new String(cf.getData().forPath("/super/c1")));


        //绑定回调函数，异步调用
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cf.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        System.out.println("code:" + curatorEvent.getResultCode());
                        System.out.println("type:" + curatorEvent.getType());
                        System.out.println("线程为:" + Thread.currentThread().getName());
                    }
                }, cachedThreadPool)
                .forPath("/super/c2", "c2内容".getBytes());
        System.out.println("=========================");
        Thread.sleep(10 * 1000);
        System.out.println("=========================");

        //读取子节点
        List<String> list = cf.getChildren().forPath("/super");
        for (String s : list) {
            System.out.println(s);
        }
        //判断节点是否存在
        Stat stat = cf.checkExists().forPath("/super/c2");
        System.out.println(stat);

        Thread.sleep(10 * 1000);

        //删除节点
        cf.delete().guaranteed().deletingChildrenIfNeeded().forPath("/super");

        cf.close();
    }
}
