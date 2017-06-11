package thread.Disruptor.generate1;

import com.lmax.disruptor.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Try on 2017/6/11.
 */
public class Main02 {
    public static void main(String[] args) throws InterruptedException {
        final int BUFFER_SIZE = 1024;
        final int THREAD_NUMBERS = 4;

        EventFactory<Trade> eventFactory = new EventFactory<Trade>() {
            @Override
            public Trade newInstance() {
                return new Trade();
            }
        };

        RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(eventFactory, BUFFER_SIZE);
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUMBERS);

        WorkHandler<Trade> handler = new TradeHandler();
        WorkerPool<Trade> workerPool = new WorkerPool<Trade>(
                ringBuffer, sequenceBarrier, new IgnoreExceptionHandler(), handler);

        workerPool.start(executorService);

        for (int i = 0; i < 8; i++) {
            long seq = ringBuffer.next();
            ringBuffer.get(seq).setPrice(Math.random() * 9999);
            ringBuffer.publish(seq);
        }

        Thread.sleep(1000);
        workerPool.halt();
        executorService.shutdown();
    }
}
