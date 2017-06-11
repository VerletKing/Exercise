package RPC.Socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Created by Try on 2017/6/3.
 */
public class Client {

    public static void main(String[] args) throws NoSuchMethodException, IOException, ClassNotFoundException {
        final String interfacename = SayHelloService.class.getName();
        Method method = SayHelloService.class.getMethod("sayHello", String.class);
        Object[] arguments = {"Hello"};
        
        Socket socket = new Socket("127.0.0.1",5200);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeUTF(interfacename);
        output.writeUTF(method.getName());
        output.writeObject(method.getParameterTypes());
        output.writeObject(arguments);

        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        Object result = input.readObject();
        System.out.println(result);
        input.close();
        output.close();
        socket.close();
    }
}
