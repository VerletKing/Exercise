package thread;

/**
 * Created by Try on 2017/6/4.
 */
public class Volatile implements Runnable{
    private volatile boolean flage = true;

    public void setFlage(boolean flage) {
        this.flage = flage;
    }

    @Override
    public void run() {
        while (flage){

        }
        System.out.println(Thread.currentThread()+"线程停止");
    }

    public static void main(String[] args) throws InterruptedException {
        Volatile d = new Volatile();
        Thread t1 = new Thread(d,"t1");
        t1.start();
        Thread.sleep(2000);
        d.setFlage(false);
        System.out.println("flage设置为false");
    }
}
