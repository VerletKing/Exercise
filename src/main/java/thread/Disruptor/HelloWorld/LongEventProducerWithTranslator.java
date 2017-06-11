package thread.Disruptor.HelloWorld;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * Created by Try on 2017/6/10.
 */
public class LongEventProducerWithTranslator {
    //一个translator可以看做一个事件初始化器，publicEvent方法会调用它
    private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR = new EventTranslatorOneArg<LongEvent, ByteBuffer>() {

        @Override
        public void translateTo(LongEvent longEvent, long sequeue, ByteBuffer byteBuffer) {
            longEvent.setValue(byteBuffer.getLong(0));
        }
    };

    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer buffer) {
        ringBuffer.publishEvent(TRANSLATOR, buffer);
    }

}
