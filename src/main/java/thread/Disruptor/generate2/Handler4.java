package thread.Disruptor.generate2;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import thread.Disruptor.generate1.Trade;

/**
 * Created by Try on 2017/6/11.
 */
public class Handler4 implements EventHandler<Trade>, WorkHandler<Trade>{

    @Override
    public void onEvent(Trade trade, long l, boolean b) throws Exception {
        this.onEvent(trade);
    }

    @Override
    public void onEvent(Trade trade) throws Exception {
        System.out.println("handler4: get name:"+trade.getName());
        trade.setName(trade.getName()+"h4");
    }
}
