package com.zistone.gprs.socket;

import com.zistone.gprs.util.MyPropertiesUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class TestNettyServer {

    private static final Logger LOGGER = Logger.getLogger(TestNettyServer.class);
    private static final int PORT;

    static {
        PORT = Integer.parseInt(MyPropertiesUtil.GetValueProperties().getProperty("PORT_SOCKET1"));
    }

    class TestNettyServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info("通道" + ctx.channel().localAddress() + "激活");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info("通道" + ctx.channel().localAddress() + "不活跃");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            String str = GetMessage(byteBuf);
            LOGGER.info("收到：" + str);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info("数据接收完毕");
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            //发生异常则关闭
            ctx.close();
            LOGGER.error("异常信息：\n" + cause.getMessage());
        }
    }

    public String GetMessage(ByteBuf byteBuf) throws UnsupportedEncodingException {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        return new String(bytes, "UTF-8");
    }

    public void Start() throws Exception {
        EventLoopGroup eventLoopGroup1 = new NioEventLoopGroup();
        EventLoopGroup eventLoopGroup2 = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        //绑定线程池
        serverBootstrap.group(eventLoopGroup2, eventLoopGroup1);
        //通道标识，表示服务端
        serverBootstrap.channel(NioServerSocketChannel.class);
        //绑定端口
        serverBootstrap.localAddress(PORT);
        //客户端连接时触发
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                InetSocketAddress address = socketChannel.localAddress();
                int port = address.getPort();
                LOGGER.info("客户端" + address + " " + port + "连接");
                socketChannel.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")));
                socketChannel.pipeline().addLast(new TestNettyServerHandler());
                socketChannel.pipeline().addLast(new ByteArrayEncoder());
            }
        });
        //异步绑定端口
        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        LOGGER.info("正在监听" + channelFuture.channel().localAddress());
    }

}
