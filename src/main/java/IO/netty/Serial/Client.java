package IO.netty.Serial;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Try on 2017/6/18.
 */
public class Client {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                        socketChannel.pipeline().addLast(new ClientHandler());

                    }
                });
        ChannelFuture future = bootstrap.connect("127.0.0.1", 8765).sync();
        for (int i = 0; i < 3; i++) {
            Req req = new Req();
            req.setId(String.valueOf(i));
            req.setName("pro " + i);
            req.setRequestMessage("数据信息 " + i);
//            String path = System.getProperty("user.dir") + File.separator + "sources" + File.separatorChar + "001.jpg";
//            File file = new File(path);
//            FileInputStream fileInputStream = new FileInputStream(file);
//            byte[] data = new byte[fileInputStream.available()];
//            fileInputStream.read(data);
//            fileInputStream.close();
//            req.setAttachment(GzipUtils.gzip(data));
            future.channel().writeAndFlush(req);
        }

        future.channel().closeFuture().sync();
        workerGroup.shutdownGracefully();
    }
}
