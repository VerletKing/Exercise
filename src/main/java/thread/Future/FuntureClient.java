package thread.Future;

/**
 * Created by Try on 2017/6/5.
 */
public class FuntureClient {
    public Data request(String str){
        FutureDataImpl fd = new FutureDataImpl();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ReslutDataImpl rd  = new ReslutDataImpl();
                fd.setResltData(rd,str);
            }
        }).start();
        return fd;
    }
}