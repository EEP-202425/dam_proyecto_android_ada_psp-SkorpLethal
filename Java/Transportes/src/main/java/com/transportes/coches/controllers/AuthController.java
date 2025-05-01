package com.transportes.coches.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transportes.coches.dto.DTOUsuario;
import com.transportes.coches.models.Usuario;
import com.transportes.coches.repositories.UsuarioRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PasswordEncoder encoder;
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody DTOUsuario usuario) {
	Optional<Usuario> user = usuarioRepository.findByNombre(usuario.getNombre());
	if(user.isEmpty()) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado.");
	}
	Usuario _user = user.get();
	if(!encoder.matches(usuario.getContrasenia(), _user.getContrasenia())) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("La contraseña no es correcta.");
	}
	return ResponseEntity.ok("Login correcto.");
	    }
		
	@PostMapping("/registro")
	public ResponseEntity<String> registro(@RequestBody DTOUsuario usuario){
	return ResponseEntity.ok("Registro correcto.");
		}
}
