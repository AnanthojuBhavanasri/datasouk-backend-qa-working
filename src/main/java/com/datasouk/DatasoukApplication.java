package com.datasouk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;


@ComponentScan({"com.datasouk.*"})
@SpringBootApplication
@Configuration
//@EnableResourceServer
public class DatasoukApplication {
	public static void main(String[] args) {

	SpringApplication application =	new SpringApplication(DatasoukApplication.class);
	application.setAdditionalProfiles("dev");
	application.run(args);
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfig() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
