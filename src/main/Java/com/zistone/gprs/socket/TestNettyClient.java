package com.zistone.gprs.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.time.LocalTime;

public class TestNettyClient {

    private static final Logger LOGGER = Logger.getLogger(TestNettyClient.class);

    private final String HOST;
    private final int PORT;

    class EchoClientHandler extends SimpleChannelInboundHandler {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info("客户端与服务端的通道开启");
            String sendInfo = "武汉123火车ABC站~！@#￥%……&**（）-+";
            ctx.writeAndFlush(Unpooled.copiedBuffer(sendInfo, CharsetUtil.UTF_8));
            LOGGER.info("已发送数据至服务端");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info("客户端与服务端的通道关闭");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            //异常关闭
            ctx.close();
            LOGGER.error("异常退出：\n" + cause.getMessage());
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
            LOGGER.info(channelHandlerContext.channel().remoteAddress());
            String str = (String) o;
            LOGGER.info("收到：" + str);
        }
    }

    public TestNettyClient(String host, int port) {
        HOST = host;
        PORT = port;
    }

    public void Start() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        //绑定线程池
        bootstrap.group(eventLoopGroup);
        //通道标识，表示服务端
        bootstrap.channel(NioSocketChannel.class);
        //绑定端口
        bootstrap.remoteAddress(new InetSocketAddress(HOST, PORT));
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                socketChannel.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")));
                socketChannel.pipeline().addLast(new EchoClientHandler());
            }
        });
        //异步连接服务端
        ChannelFuture channelFuture = bootstrap.connect().sync();
        //异步等待关闭通道
        channelFuture.channel().closeFuture().sync();
        LOGGER.info("通道已关闭");
        eventLoopGroup.shutdownGracefully().sync();
    }

    public static void main(String[] args) throws InterruptedException {
        new TestNettyClient("localhost", 8065).Start();
    }

}
