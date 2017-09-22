package cn.com.sit.servicegateway;

import filter.AccessFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableZuulProxy
public class SitBaseServiceGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SitBaseServiceGatewayApplication.class, args);
	}

    @Bean
    public AccessFilter accessFilter() {
        return new AccessFilter();
    }
}
