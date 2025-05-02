package com.transportes.coches.controllers;

import java.util.Collections;
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
import com.transportes.coches.utils.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody DTOUsuario dto) {
		Optional<Usuario> usuarioOpcional = usuarioRepository.findByNombre(dto.getNombre());
		if (usuarioOpcional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado.");
		}
		Usuario usuario = usuarioOpcional.get();
		if (!encoder.matches(dto.getContrasenia(), usuario.getContrasenia())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("La contraseña no es correcta.");
		}
		String token = jwtUtil.generarToken(dto.getNombre());
		return ResponseEntity.ok(Collections.singletonMap("token", token));
	}

	@PostMapping("/registro")
	public ResponseEntity<String> registro(@RequestBody DTOUsuario dto) {
		Optional<Usuario> usuarioOpcional = usuarioRepository.findByNombre(dto.getNombre());
		if (!usuarioOpcional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya existe.");
		}
		Usuario nuevoUsuario = new Usuario();
		nuevoUsuario.setNombre(dto.getNombre());
		nuevoUsuario.setContrasenia(encoder.encode(dto.getContrasenia()));
		usuarioRepository.save(nuevoUsuario);
		return ResponseEntity.ok("Registro correcto.");
	}
}
