package br.com.planner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "*")
@EnableAsync
@EnableCaching
public class PlannerRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlannerRestApplication.class, args);
	}

}
