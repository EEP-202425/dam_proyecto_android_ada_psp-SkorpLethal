package com.transportes.coches;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.transportes.coches.models.Color;
import com.transportes.coches.models.Usuario;
import com.transportes.coches.repositories.ColoresRepository;
import com.transportes.coches.repositories.UsuarioRepository;

@SpringBootApplication
public class TransportesApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransportesApplication.class, args);
	}
	
	
	// Carga de Datos inicial en BBDD
	@Bean
	CommandLineRunner init(UsuarioRepository usuarioRepo, 
	                       PasswordEncoder encoder,
	                       ColoresRepository colorRepo) {
	    return _ -> {
	        // Crear usuario admin si no existe
	        if (usuarioRepo.findByNombre("admin").isEmpty()) {
	            Usuario u = new Usuario();
	            u.setNombre("admin");
	            u.setContrasenia(encoder.encode("admin123"));
	            usuarioRepo.save(u);
	        }

	        // Crear colores si la tabla está vacía
	        if (colorRepo.count() == 0) {
	            colorRepo.saveAll(List.of(
	                new Color(null, "Rojo", 150.0),
	                new Color(null, "Azul", 100.0),
	                new Color(null, "Negro", 200.0),
	                new Color(null, "Blanco", 80.0)
	            ));
	        }
	    };
	}
}
