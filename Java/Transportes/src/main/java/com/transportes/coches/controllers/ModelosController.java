package com.transportes.coches.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transportes.coches.models.Modelo;
import com.transportes.coches.repositories.ModelosRepository;

@RestController
@RequestMapping("/api/modelos")
public class ModelosController {

	@Autowired
	private ModelosRepository modeloRepository;
	
	@PostMapping
	public ResponseEntity<Modelo> crear(@RequestBody Modelo modelo){
		return ResponseEntity.ok(modeloRepository.save(modelo));
	}
	
	@GetMapping
	public Iterable<Modelo> listarTodos(){
		return modeloRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Modelo> obtenerPorId(@PathVariable Long id){
		Optional<Modelo> modelo = modeloRepository.findById(id);
		return modelo.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Modelo> actualizar(@PathVariable Long id, @RequestBody Modelo actualizado){
		return modeloRepository.findById(id).map(existente -> {
			existente.setNombre(actualizado.getNombre());
			existente.setDescripcion(actualizado.getDescripcion());
			existente.setPrecioBase(actualizado.getPrecioBase());
			return ResponseEntity.ok(modeloRepository.save(existente));
		}).orElse(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id){
		modeloRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}

