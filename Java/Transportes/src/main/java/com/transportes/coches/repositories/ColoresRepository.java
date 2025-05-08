package com.transportes.coches.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transportes.coches.models.Color;

public interface ColoresRepository extends JpaRepository<Color, Long> {
	
}
