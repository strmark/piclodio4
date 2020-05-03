package nl.oradev.piclodio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PiclodioApplication {

	public static void main(String[] args) {
		SpringApplication.run(PiclodioApplication.class, args);
	}

}
