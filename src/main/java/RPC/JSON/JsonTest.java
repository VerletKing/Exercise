package RPC.JSON;


import Serialization.Person;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by Try on 2017/6/3.
 */
public class JsonTest {
    public static void main(String[] args) throws IOException {
        Person person = new Person();
        person.setName("哈哈");
        person.setAge(18);

        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(stringWriter);
        objectMapper.writeValue(generator, person);
        generator.close();
        String personJson = stringWriter.toString();
        System.out.println(personJson);
        stringWriter.close();

        Person person1 = objectMapper.readValue(personJson, Person.class);
        System.out.println(person1.getName()+" "+person1.getAge());

    }

}
