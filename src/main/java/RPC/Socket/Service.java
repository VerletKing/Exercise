package RPC.Socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Try on 2017/6/3.
 */
public class Service {

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(5200);
        while (true) {
            Socket socket = server.accept();

            //读取服务信息
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            String interfacename = input.readUTF(); //接口名称
            String methodName = input.readUTF(); //方法名称
            Class<?>[] parameterTypes = (Class<?>[]) input.readObject(); //参数类型
            Object[] arguments = (Object[]) input.readObject(); //参数对象

            //执行调用
            Class serviceinterfaceclass = Class.forName(interfacename);
            Object service = getObject(interfacename);
            Method method = serviceinterfaceclass.getMethod(methodName, parameterTypes);
            Object result = method.invoke(service, arguments);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(result);
        }
    }

    /**
     * 给定接口名返回接口类实现的对象
     * @param interfacename 接口名
     * @return
     */
    public static Object getObject(String interfacename) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String name = interfacename+"Impl";
        final Class<?> obj = Class.forName(name);
        return obj.newInstance();
    }
}
