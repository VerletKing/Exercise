package zookeeper.base.cluster;

public class Client1 {

	public static void main(String[] args) throws Exception{
		
		ZKWatcher myWatcher = new ZKWatcher();
        System.out.println("c1启动");
        Thread.sleep(100000000);
	}
}
