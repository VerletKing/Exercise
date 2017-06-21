package IO.netty.Stickybag;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by Try on 2017/6/16.
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        /*ByteBuf byteBuf = Unpooled.copiedBuffer("$_".getBytes());
                        socketChannel.pipeline().addLast(
                                new DelimiterBasedFrameDecoder(1024, byteBuf));*/
                        socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(32));
                        socketChannel.pipeline().addLast(new StringDecoder());
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });
        ChannelFuture cf = bootstrap.connect("127.0.0.1", 12306).sync();
        cf.channel().writeAndFlush(Unpooled.wrappedBuffer("bbbb$_gggg$_".getBytes()));
        cf.channel().writeAndFlush(Unpooled.wrappedBuffer("cccc$_".getBytes()));

        //等待客户端端口关闭
        cf.channel().closeFuture().sync();
        workerGroup.shutdownGracefully();
    }
}
