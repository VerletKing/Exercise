package IO.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by Try on 2017/6/14.
 */
public class Service {
    private int[] port;

    public Service(int... port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        //1 第一个线程组 是用于接收Client端连接的
        EventLoopGroup boossGroup = new NioEventLoopGroup();
        //2 第二个线程组 是用于实际的业务处理操作的
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //3 创建一个辅助类Bootstrap，就是对我们的Server进行一系列的配置
            ServerBootstrap bootstrap = new ServerBootstrap();
            //把俩个工作线程组加入进来
            bootstrap.group(boossGroup, workerGroup)
                    //我要指定使用NioServerSocketChannel这种类型的通道
                    .channel(NioServerSocketChannel.class)
                    //一定要使用 childHandler 去绑定具体的 事件处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ServiceHandler());
                        }
                    })
                    //设置tcp缓存区
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置发送缓存大小
                    .option(ChannelOption.SO_SNDBUF,32*1024)
                    //设置接收缓存大小
                    .option(ChannelOption.SO_RCVBUF,32*1024)
                    //保持连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture[] cfs = new ChannelFuture[port.length];
            //绑定指定的端口 进行监听
            ChannelFuture f = bootstrap.bind(port[0]).sync();
            f.channel().closeFuture().sync();
            /*for (int i = 0; i < port.length; i++) {
                cfs[i] = bootstrap.bind(port[i]).sync();
            }*/
            /*for (int i = 0; i < cfs.length; i++) {
                cfs[i].channel().closeFuture().sync();
            }*/
        } finally {
            workerGroup.shutdownGracefully();
            boossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Service(5200).run();
    }
}
