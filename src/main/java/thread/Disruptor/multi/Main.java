package thread.Disruptor.multi;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Try on 2017/6/11.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        RingBuffer<Order> ringBuffer = RingBuffer.create(
                ProducerType.MULTI, new EventFactory<Order>() {

                    @Override
                    public Order newInstance() {
                        return new Order();
                    }
                }, 1024 * 1024, new YieldingWaitStrategy());

        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        Consumer[] consumers = new Consumer[3];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer("C" + i);
        }

        WorkerPool<Order> orderWorkerPool = new WorkerPool<Order>(
                ringBuffer, sequenceBarrier, new IntEventExceptionHandler(), consumers);

        //把消费者的位置信息引用注入到生产者
        ringBuffer.addGatingSequences(orderWorkerPool.getWorkerSequences());
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        orderWorkerPool.start(executorService);

        final CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 100; i++) {
            final Producer p = new Producer(ringBuffer);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int j = 0; j < 100; j++) {
                        p.onData(UUID.randomUUID().toString());
                    }
                }
            }).start();
        }
        Thread.sleep(2000);
        System.out.println("---------------开始生产-----------------");
        latch.countDown();
        Thread.sleep(5000);
        System.out.println("总数:" + consumers[0].getCount());
        orderWorkerPool.halt();
        executorService.shutdown();
    }

    static class IntEventExceptionHandler implements ExceptionHandler {
        public void handleEventException(Throwable ex, long sequence, Object event) {
        }

        public void handleOnStartException(Throwable ex) {
        }

        public void handleOnShutdownException(Throwable ex) {
        }
    }

}
