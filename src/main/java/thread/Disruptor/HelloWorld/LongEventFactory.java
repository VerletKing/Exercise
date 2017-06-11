package thread.Disruptor.HelloWorld;

import com.lmax.disruptor.EventFactory;

/**
 * Created by Try on 2017/6/10.
 */
public class LongEventFactory implements EventFactory<LongEvent>{

    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
