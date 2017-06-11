package Serialization;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Try on 2017/6/3.
 */
public class Default {

    private Long timeLong;

    {
        timeLong = null;
    }

    private Person getPersion(){
        Person person = new Person();
        person.setName("李四");
        person.setAge(13);
        return person;
    }

    @Before
    public void testTimeBefore() {
        timeLong = System.currentTimeMillis();
    }

    @After
    public void testTimeAfter() {
        Long time = System.currentTimeMillis();
        System.out.println("需要时间是="+ (time - timeLong));
    }

    @Test
    public void defaulteSerialization() throws Exception {
        //定义一个字节数组输出流
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(os);
        out.writeObject(getPersion());
        final byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(is);
        Person person = (Person) ois.readObject();
//        System.out.println(person.getName()+" "+person.getAge());
    }

    @Test
    public void hessianSerialization() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        ho.writeObject(getPersion());
        final byte[] bytes = os.toByteArray();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        HessianInput hi = new HessianInput(is);
        Person person = (Person) hi.readObject();
//        System.out.println(person.getName()+" "+person.getAge());
    }
}
