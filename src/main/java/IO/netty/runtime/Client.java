package IO.netty.runtime;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Try on 2017/6/20.
 */
public class Client {
    private static class SingletonHolder {
        static final Client instance = new Client();
    }

    private static Client getInstance() {
        return SingletonHolder.instance;
    }

    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private ChannelFuture cf;

    private Client() {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                        socketChannel.pipeline().addLast(new ReadTimeoutHandler(5));
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });
    }

    public void connect() {
        try {
            this.cf = bootstrap.connect("127.0.0.1", 8765).sync();
            System.out.println("远程服务器已经连接, 可以进行数据交换..");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChannelFuture getChannelFuture() {
        if (this.cf == null) {
            this.connect();
        }
        if (!this.cf.channel().isActive()) {
            this.connect();
        }
        return this.cf;
    }

    public static void main(String[] args) throws InterruptedException {
        final Client c = Client.getInstance();

        ChannelFuture channelFuture = c.getChannelFuture();
        for (int i = 0; i < 3; i++) {
            Request request = new Request();
            request.setId("" + i);
            request.setName("pro" + i);
            request.setRequestMessage("数据信息" + i);
            channelFuture.channel().writeAndFlush(request);
            TimeUnit.SECONDS.sleep(4);
        }

        channelFuture.channel().closeFuture().sync();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("进入子线程...");
                    ChannelFuture cf = c.getChannelFuture();
                    System.out.println(cf.channel().isActive());
                    System.out.println(cf.channel().isOpen());

                    //再次发送数据
                    Request request = new Request();
                    request.setId("" + 4);
                    request.setName("pro" + 4);
                    request.setRequestMessage("数据信息" + 4);
                    cf.channel().writeAndFlush(request);
                    cf.channel().closeFuture().sync();
                    System.out.println("子线程结束.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.out.println("断开连接,主线程结束..");
    }
}
