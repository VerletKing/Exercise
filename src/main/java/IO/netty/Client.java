package IO.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by Try on 2017/6/14.
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup workgroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workgroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });

        ChannelFuture cf = bootstrap.connect("127.0.0.1", 5200).sync();
        ChannelFuture cf02 = bootstrap.connect("127.0.0.1", 5201).sync();
        ChannelFuture cf03 = bootstrap.connect("127.0.0.1", 5202).sync();
        cf.channel().writeAndFlush(Unpooled.copiedBuffer("7778".getBytes()));
        cf02.channel().writeAndFlush(Unpooled.copiedBuffer("8888".getBytes()));
        cf03.channel().writeAndFlush(Unpooled.copiedBuffer("9999".getBytes()));

        cf.channel().closeFuture().sync();
        cf02.channel().closeFuture().sync();
        cf03.channel().closeFuture().sync();
        workgroup.shutdownGracefully();
    }
}
