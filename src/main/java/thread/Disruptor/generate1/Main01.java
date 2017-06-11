package thread.Disruptor.generate1;

import com.lmax.disruptor.*;

import java.util.concurrent.*;

/**
 * Created by Try on 2017/6/11.
 */
public class Main01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int BUDDER_SIZE = 1024;
        final int THREAD_NUMNERS = 4;

        /*
         * createSingleProducer创建一个单生产者的RingBuffer，
         * 第一个参数叫EventFactory，从名字上理解就是"事件工厂"，其实它的职责就是产生数据填充RingBuffer的区块。
         * 第二个参数是RingBuffer的大小，它必须是2的指数倍 目的是为了将求模运算转为&运算提高效率
         * 第三个参数是RingBuffer的生产都在没有可用区块的时候(可能是消费者（或者说是事件处理器） 太慢了)的等待策略
         */
        final RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(new EventFactory<Trade>() {
            @Override
            public Trade newInstance() {
                return new Trade();
            }
        }, BUDDER_SIZE, new YieldingWaitStrategy());

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUMNERS);
        //创建SequenceBarrier
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        //创建消息处理器
        BatchEventProcessor<Trade> tradeBatchEventProcessor = new BatchEventProcessor<Trade>(
                ringBuffer, sequenceBarrier, new TradeHandler<Trade>());

        //把消费者的位置信息引用注入到生产者    如果只有一个消费者的情况可以省略
        ringBuffer.addGatingSequences(tradeBatchEventProcessor.getSequence());

        //把消息处理器提交到线程池
        executorService.submit(tradeBatchEventProcessor);

        Future<Void> future = executorService.submit(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                long seq;
                for (int i = 0; i < 10; i++) {
                    seq = ringBuffer.next();
                    ringBuffer.get(seq).setPrice(Math.random() * 9999);
                    ringBuffer.publish(seq);
                }
                return null;
            }
        });

        future.get();//等待生产者结束
        Thread.sleep(1000);
        tradeBatchEventProcessor.halt();//通知事件(或者说消息)处理器 可以结束了（并不是马上结束!!!）
        executorService.shutdown();//终止线程
    }

}