package IO.netty.Stickybag;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;

/**
 * Created by Try on 2017/6/16.
 */
public class ServiceHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server channel active...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String data = (String) msg;
        System.out.println("客户端数据：" + data);

        String response = "这是服务器端返回数据" + "$_";
        ChannelFuture channelFuture = ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));
        channelFuture.addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
