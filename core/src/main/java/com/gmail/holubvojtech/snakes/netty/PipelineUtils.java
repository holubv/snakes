package com.gmail.holubvojtech.snakes.netty;

import com.gmail.holubvojtech.snakes.protocol.FrameDecoder;
import com.gmail.holubvojtech.snakes.protocol.LengthPrepender;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.PlatformDependent;

import java.util.concurrent.ThreadFactory;

public class PipelineUtils {

    public static final PipelineUtils.Base BASE = new Base();
    private static final boolean E_POLL;

    static {
        E_POLL = !PlatformDependent.isWindows() && Epoll.isAvailable();
    }

    private PipelineUtils() {
    }

    public static EventLoopGroup newEventLoopGroup() {
        return E_POLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    public static EventLoopGroup newEventLoopGroup(int threads) {
        return E_POLL ? new EpollEventLoopGroup(threads) : new NioEventLoopGroup(threads);
    }

    public static EventLoopGroup newEventLoopGroup(int threads, ThreadFactory factory) {
        return E_POLL ? new EpollEventLoopGroup(threads, factory) : new NioEventLoopGroup(threads, factory);
    }

    public static Class<? extends Channel> getChannel() {
        return E_POLL ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends ServerChannel> getServerChannel() {
        return E_POLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static boolean isEPoll() {
        return E_POLL;
    }

    public static final class Base extends ChannelInitializer<Channel> {

        @Override
        public void initChannel(Channel ch) throws Exception {

            ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);

            ch.pipeline().addLast("frame-decoder", new FrameDecoder());
            ch.pipeline().addLast("frame-prepender", new LengthPrepender());
            ch.pipeline().addLast("inbound-boss", new HandlerBoss());
        }
    }
}
