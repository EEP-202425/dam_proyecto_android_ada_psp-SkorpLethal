package com.transportes.coches;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.transportes.coches.models.Usuario;
import com.transportes.coches.repositories.UsuarioRepository;

@SpringBootApplication
public class TransportesApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransportesApplication.class, args);
	}
	
	@Bean
    CommandLineRunner init(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByNombre("admin").isEmpty()) {
                Usuario u = new Usuario();
                u.setNombre("admin");
                u.setContrasenia(encoder.encode("admin123"));
                repo.save(u);
            }
        };
    }
}
