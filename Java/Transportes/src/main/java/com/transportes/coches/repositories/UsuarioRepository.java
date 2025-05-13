package com.transportes.coches.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transportes.coches.models.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByNombre(String nombre);
}
