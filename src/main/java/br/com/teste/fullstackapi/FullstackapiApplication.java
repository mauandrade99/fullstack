package br.com.teste.fullstackapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
public class FullstackapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FullstackapiApplication.class, args);
	}

}   
