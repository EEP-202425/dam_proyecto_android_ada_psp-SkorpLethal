package com.transportes.coches.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transportes.coches.models.Color;
import com.transportes.coches.repositories.ColoresRepository;


@RestController
@RequestMapping("/api/colores")
public class ColoresController {
	
	@Autowired
	private ColoresRepository coloresRepository;
	
	 @GetMapping
	 public Iterable<Color> listarColores() {
		 return coloresRepository.findAll();
	 }
}
