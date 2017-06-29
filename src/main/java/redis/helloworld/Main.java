package redis.helloworld;

import redis.clients.jedis.Jedis;

/**
 * Created by Try on 2017/6/25.
 */
public class Main {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.153.132", 6379);
        System.out.println(jedis.get("name"));
    }
}
