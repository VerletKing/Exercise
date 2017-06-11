package thread.Disruptor.HelloWorld;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * Created by Try on 2017/6/10.
 */
public class LongEventProducer {
    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * onData用来发布事件，每调用一次就发布一次事件
     * 它的参数会用过事件传递给消费者
     */
    public void onData(ByteBuffer bb) {
        long sequence = ringBuffer.next();
        try{
            LongEvent event = ringBuffer.get(sequence);
            event.setValue(bb.getLong(0));
        }finally {
            ringBuffer.publish(sequence);
        }
    }
}
