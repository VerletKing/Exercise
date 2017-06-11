package thread.MasterWork;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Try on 2017/6/5.
 */
public class Worker implements Runnable {
    private ConcurrentLinkedQueue<Task> workQueue;
    private ConcurrentHashMap<String, Object> resultMap;

    public void setWorkQueue(ConcurrentLinkedQueue<Task> workQueue) {
        this.workQueue = workQueue;
    }

    public void setResultMap(ConcurrentHashMap<String, Object> resultMap) {
        this.resultMap = resultMap;
    }

    @Override
    public void run() {
        while(true){
            Task task = this.workQueue.poll();
            if(task == null){
                break;
            }
            Object output = process(task);
            resultMap.put(Integer.toString(task.getId()), output);
        }
    }

    private Object process(Task task){
        Object output = task.getName();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (String)Thread.currentThread().getName()+output;
    }


}
