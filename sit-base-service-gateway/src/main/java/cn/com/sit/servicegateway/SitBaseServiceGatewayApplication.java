package cn.com.sit.servicegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class SitBaseServiceGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SitBaseServiceGatewayApplication.class, args);
	}
}
