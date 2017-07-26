package zookeeper.base.cluster;

public class Client2 {

	public static void main(String[] args) throws Exception{
		
		ZKWatcher myWatcher = new ZKWatcher();
		System.out.println("c2启动");
		Thread.sleep(100000000);
	}
}
