package RPC.Socket;

/**
 * Created by Try on 2017/6/3.
 */
public class SayHelloServiceImpl implements SayHelloService {
    @Override
    public String sayHello(String helloArg) {
        if ("hello".equals(helloArg)) {
            return "hello";
        } else {
            return "bye bye";
        }
    }
}
