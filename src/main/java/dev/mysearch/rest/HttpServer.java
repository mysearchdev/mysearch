package dev.mysearch.rest;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HttpServer implements InitializingBean, DisposableBean {

	private int port = 8080;

	@Autowired
	private SearchHttpServerHandler handler;

	private NioEventLoopGroup bossGroup;
	private NioEventLoopGroup workerGroup;

	@Override
	public void afterPropertiesSet() throws Exception {
		new Thread(() -> {
			Thread.currentThread().setName("HTTPServer");
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			try {
				run();

			} catch (Exception e) {
				log.error("Error: ", e);
			}
		}).start();
	}

	// TODO port to configs
	// TODO bind ip to configs
	// TODO add app version/other info to HTTP response headers
	// TODO add netty headers to HTTP response
	public void run() throws Exception {

		var bossGroup = new NioEventLoopGroup(1);
		var workerGroup = new NioEventLoopGroup();
		try {
			var b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							var p = ch.pipeline();
							p.addLast(new HttpRequestDecoder());
							p.addLast(new HttpResponseEncoder());
							p.addLast(new HttpObjectAggregator(1048576));

							p.addLast(handler);
						}
					});

			var f = b.bind(port).sync();
			f.channel().closeFuture().sync();

		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	@Override
	public void destroy() throws Exception {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

}