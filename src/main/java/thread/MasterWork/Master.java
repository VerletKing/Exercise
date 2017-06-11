package thread.MasterWork;

import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Try on 2017/6/5.
 */
public class Master {
    //装载需要处理工作的容器  worker处理时需要得到
    private ConcurrentLinkedQueue<Task> workQueue = new ConcurrentLinkedQueue<>();
    //装载所有worker的容器
    private HashMap<String, Thread> works = new HashMap<>();
    //装载需要处理工作返回的容器  worker返回时添加数据
    private ConcurrentHashMap<String, Object> resultMap = new ConcurrentHashMap<>();

    public Master(Worker worker, int workerCount){
        worker.setWorkQueue(workQueue);
        worker.setResultMap(resultMap);
        for (int i = 0; i < workerCount; i++) {
            works.put("worker"+(i+1), new Thread(worker));
        }
    }

    public void submit(Task task){
        workQueue.add(task);
    }

    public void execute(){
        for(Map.Entry<String, Thread> map : works.entrySet()){
            map.getValue().start();
        }
    }

    public boolean isCompile(){
        for(Map.Entry<String, Thread> map : works.entrySet()){
            if(map.getValue().getState() != Thread.State.TERMINATED){
                return false;
            }
        }
        return true;
    }

    public String getResult(){
        StringBuffer sb = new StringBuffer();
        for(Map.Entry<String, Object> map : resultMap.entrySet()){
            sb.append(map.getValue()+" ");
        }
        return sb.toString();
    }
}
