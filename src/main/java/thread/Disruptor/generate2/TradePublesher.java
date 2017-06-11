package thread.Disruptor.generate2;


import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import thread.Disruptor.generate1.Trade;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Try on 2017/6/11.
 */
public class TradePublesher implements Runnable {
    Disruptor<Trade> disruptor;
    private CountDownLatch latch;

    private static int LOOP = 10;

    public TradePublesher(CountDownLatch latch, Disruptor<Trade> disruptor) {
        this.disruptor = disruptor;
        this.latch = latch;
    }

    @Override
    public void run() {
        TradeEventTranslator tradeEventTranslator = new TradeEventTranslator();
        for (int i = 0; i < LOOP; i++) {
            disruptor.publishEvent(tradeEventTranslator);
        }
        latch.countDown();
    }

    class TradeEventTranslator implements EventTranslator<Trade> {

        private Random random = new Random();

        @Override
        public void translateTo(Trade event, long sequence) {
            this.generateTrade(event);
        }

        private Trade generateTrade(Trade trade) {
            trade.setPrice(random.nextDouble() * 9999);
            return trade;
        }
    }
}
