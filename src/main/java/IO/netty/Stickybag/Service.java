package IO.netty.Stickybag;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by Try on 2017/6/16.
 */
public class Service {

    private int port;

    public Service(int port) {
        this.port = port;
    }

    private void run() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            /*//设置特殊分隔符
                            ByteBuf byteBuf = Unpooled.copiedBuffer("$_".getBytes());
                            socketChannel.pipeline().addLast(
                                    new DelimiterBasedFrameDecoder(1024, byteBuf));*/
                            //设置定长字符串接收
                            socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(6));
                            //设置字符串形式的解码
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new ServiceHandler());

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024);

            ChannelFuture cf = serverBootstrap.bind(port).sync();
            cf.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Service(12306).run();
    }
}
