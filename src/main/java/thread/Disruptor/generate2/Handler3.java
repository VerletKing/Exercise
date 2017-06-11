package thread.Disruptor.generate2;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import thread.Disruptor.generate1.Trade;

/**
 * Created by Try on 2017/6/11.
 */
public class Handler3 implements EventHandler<Trade>{

    @Override
    public void onEvent(Trade trade, long l, boolean b) throws Exception {
        System.out.println("handler3: name:"+trade.getName()+"\t price"+trade.getPrice()+"\t instace:"+trade.toString());
    }
}
