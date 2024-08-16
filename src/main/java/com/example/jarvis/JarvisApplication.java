package com.example.jarvis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class JarvisApplication {
	public static void main(String[] args) {
		SpringApplication.run(JarvisApplication.class, args);
	}
}