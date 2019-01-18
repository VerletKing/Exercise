package thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 通过一个缓存示例说明读写锁的使用
 *
 * @author wky
 * @date 2019/1/9
 */
public class Cache {
    static Map<String, Object> map = new HashMap<>();
    static ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    static Lock readLock = reentrantReadWriteLock.readLock();
    static Lock writeLock = reentrantReadWriteLock.writeLock();

    /**
     * 获取一个key对应的value
     */
    public static Object get(String key) {
        readLock.lock();
        try {
            return map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 设置key对应的value，并返回旧的value
     */
    public static Object put(String key,Object value){
        writeLock.lock();
        try {
            return map.put(key,value);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 清空所有的内容
     */
    public static void clear(){
        writeLock.lock();
        try {
            map.clear();
        } finally {
            writeLock.unlock();
        }
    }
}
