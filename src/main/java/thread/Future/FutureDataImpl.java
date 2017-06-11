package thread.Future;

/**
 * Created by Try on 2017/6/5.
 */
public class FutureDataImpl implements Data {

    private ReslutDataImpl rd;
    private boolean flage = false;

    public synchronized void setResltData(ReslutDataImpl rd,String str){
        if(flage){
            return ;
        }
        this.rd = rd;
        rd.setString(str);
        flage = true;
        notify();
    }

    @Override
    public synchronized String getRequest() {
        if(!flage){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.rd.getRequest();
    }
}
