package zookeeper.base.cluster;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKWatcher implements Watcher{
    private ZooKeeper zk = null;
    static final String PARENT_PATH = "/super";

    private CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private List<String> cowaList = new ArrayList<>();

    public static final String CONNECT_ADDR = "192.168.153.133:2181,192.168.153.131:2181,192.168.153.128:2181";
    public static final int SESSION_TIMEOUT = 30000;

    public ZKWatcher() throws IOException, InterruptedException {
        zk = new ZooKeeper(CONNECT_ADDR, SESSION_TIMEOUT, (Watcher) this);
        System.out.println("开始连接zk服务器");
        connectedSemaphore.await();
    }

    @Override
    public void process(WatchedEvent event) {
        Event.KeeperState keeperState = event.getState();
        Event.EventType eventType = event.getType();
        String eventPath = event.getPath();
        System.out.println("收影响的path："+eventPath);


        if (keeperState.SyncConnected == keeperState) {
            // 成功连接上ZK服务器
            if (Event.EventType.None == eventType) {
                System.out.println("成功连接上ZK服务器");
                connectedSemaphore.countDown();
                try {
                    if(this.zk.exists(PARENT_PATH, false) == null){
                        this.zk.create(PARENT_PATH, "root".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                    List<String> paths = this.zk.getChildren(PARENT_PATH, true);
                    for (String p : paths) {
                        System.out.println(p);
                        this.zk.exists(PARENT_PATH + "/" + p, true);
                    }
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //创建节点
            else if (Event.EventType.NodeCreated == eventType) {
                System.out.println("节点创建");
                try {
                    this.zk.exists(eventPath, true);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //更新节点
            else if (Event.EventType.NodeDataChanged == eventType) {
                System.out.println("节点数据更新");
                try {
                    //update nodes  call function
                    this.zk.exists(eventPath, true);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //更新子节点
            else if (Event.EventType.NodeChildrenChanged == eventType) {
                System.out.println("子节点 ... 变更");
                try {
                    List<String> paths = this.zk.getChildren(eventPath, true);
                    if(paths.size() >= cowaList.size()){
                        paths.removeAll(cowaList);
                        for(String p : paths){
                            this.zk.exists(eventPath + "/" + p, true);
                            //this.zk.getChildren(path + "/" + p, true);
                            System.out.println("这个是新增的子节点 : " + eventPath + "/" + p);
                            //add new nodes  call function
                        }
                        cowaList.addAll(paths);
                    } else {
                        cowaList = paths;
                    }
                    System.out.println("cowaList: " + cowaList.toString());
                    System.out.println("paths: " + paths.toString());

                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //删除节点
            else if (Event.EventType.NodeDeleted == eventType) {
                System.out.println("节点 " + eventPath + " 被删除");
                try {
                    //delete nodes  call function
                    this.zk.exists(eventPath, true);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else ;
        }
        else if (Event.KeeperState.Disconnected == keeperState) {
            System.out.println("与ZK服务器断开连接");
        }
        else if (Event.KeeperState.AuthFailed == keeperState) {
            System.out.println("权限检查失败");
        }
        else if (Event.KeeperState.Expired == keeperState) {
            System.out.println("会话失效");
        }
        else ;

        System.out.println("--------------------------------------------");
    }
}
