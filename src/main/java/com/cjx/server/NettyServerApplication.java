package com.cjx.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;

import com.cjx.server.server.HelloWorldServer;

@ComponentScan("com.cjx")
@SpringBootApplication
@EnableAsync
public class NettyServerApplication {
    
	public static void main(String[] args) {
		SpringApplication.run(NettyServerApplication.class, args);
	}

    @EventListener({ContextRefreshedEvent.class})
    public void start() throws Exception {
        int port = 8888;
        new HelloWorldServer(port).start();  
    }
    
}
