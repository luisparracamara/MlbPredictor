package com.example.mlbpredictor;

import com.example.mlbpredictor.properties.MlbProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({MlbProperties.class})
public class MlbPredictorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MlbPredictorApplication.class, args);
	}

}
