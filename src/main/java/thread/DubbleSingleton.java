package thread;

/**
 * Created by Try on 2017/6/4.
 */
public class DubbleSingleton {
    private static DubbleSingleton ds;

    private DubbleSingleton() {
    }

    public static DubbleSingleton getSingleton(){
        if(ds == null){
            synchronized (DubbleSingleton.class){
                if(ds == null){
                    ds = new DubbleSingleton();
                }
            }
        }
        return ds;
    }
}
