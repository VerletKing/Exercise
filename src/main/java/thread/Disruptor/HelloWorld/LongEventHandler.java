package thread.Disruptor.HelloWorld;

import com.lmax.disruptor.EventHandler;

/**
 * 事件处理器，也就是事件消费者
 * Created by Try on 2017/6/10.
 */
public class LongEventHandler implements EventHandler<LongEvent>{

    @Override
    public void onEvent(LongEvent longEvent, long l, boolean b) throws Exception {
        System.out.println(longEvent.getValue());
    }
}
