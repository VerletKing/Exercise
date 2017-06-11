package thread.Future;

/**
 * Created by Try on 2017/6/5.
 */
public class ReslutDataImpl implements Data {

    private String result;

    public void setString(String str){
        System.out.println("输入数据"+str);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       result = str;
    }

    @Override
    public String getRequest() {
        return result+"  SUCCESS";
    }
}
