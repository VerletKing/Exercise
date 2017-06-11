package thread.MasterWork;

import java.util.Random;

/**
 * Created by Try on 2017/6/5.
 */
public class Main {
    public static void main(String[] args) {
        Master master = new Master(new Worker(), Runtime.getRuntime().availableProcessors());

        Random random = new Random();
        for (int i = 0; i < 40; i++) {
            Task task = new Task();
            task.setId(random.nextInt(100));
            task.setName("名字"+task.getId());
            master.submit(task);
        }

        master.execute();

        long start = System.currentTimeMillis();

        while(true){
            if(master.isCompile()){
                long end = System.currentTimeMillis() - start;
                String ret = master.getResult();
                System.out.println(end);
                System.out.println(ret);
                break;
            }
        }
    }
}

