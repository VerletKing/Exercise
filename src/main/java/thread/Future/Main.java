package thread.Future;

/**
 * Created by Try on 2017/6/5.
 */
public class Main {

    public static void main(String[] args){
        FuntureClient fc = new FuntureClient();
        Data fd = fc.request("输入参数");
        System.out.println("发送产生成功");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("我在干嘛呢");
        System.out.println(fd.getRequest());
    }
}
