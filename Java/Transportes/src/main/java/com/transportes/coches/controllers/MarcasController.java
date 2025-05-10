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

import com.transportes.coches.models.Marca;
import com.transportes.coches.repositories.MarcasRepository;

@RestController
@RequestMapping("/api/marcas")
public class MarcasController {
	@Autowired
	private MarcasRepository marcaRepository;
	
	@PostMapping
    public ResponseEntity<Marca> crear(@RequestBody Marca marca) {
        return ResponseEntity.ok(marcaRepository.save(marca));
    }

    @GetMapping
    public Iterable<Marca> listarTodas() {
        return marcaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marca> obtenerPorId(@PathVariable Long id) {
        Optional<Marca> marca = marcaRepository.findById(id);
        return marca.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Marca> actualizar(@PathVariable Long id, @RequestBody Marca actualizado) {
        if (!marcaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        actualizado.setId(id); // Aseguramos que el ID no se pierde
        Marca resultado = marcaRepository.save(actualizado);
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        marcaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
