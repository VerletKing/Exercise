package thread.Disruptor.generate1;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.WorkHandler;

import java.util.UUID;

/**
 * Created by Try on 2017/6/11.
 */
public class TradeHandler<T> implements EventHandler<Trade>, WorkHandler<Trade> {
    @Override
    public void onEvent(Trade trade, long l, boolean b) throws Exception {
        this.onEvent(trade);
    }

    @Override
    public void onEvent(Trade trade) throws Exception {
        trade.setId(UUID.randomUUID().toString());
        System.out.println(trade.getId());
    }
}
