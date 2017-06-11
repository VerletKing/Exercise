package thread;

/**
 * Created by Try on 2017/6/4.
 */
public class InnerSingleton {

    private static class Singletion {
        private static Singletion singletion = new Singletion();
    }

    public static Singletion getInstance() {
        return Singletion.singletion;
    }
}
