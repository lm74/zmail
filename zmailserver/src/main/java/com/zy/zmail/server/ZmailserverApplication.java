package com.zy.zmail.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ZmailserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZmailserverApplication.class, args);
		System.out.println("服务端启动完成！");
	}
}
