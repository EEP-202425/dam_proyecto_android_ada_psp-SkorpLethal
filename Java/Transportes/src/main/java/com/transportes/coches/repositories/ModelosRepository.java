package com.transportes.coches.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.transportes.coches.models.Modelo;

public interface ModelosRepository extends CrudRepository<Modelo, Long>,	
	PagingAndSortingRepository<Modelo, Long>{
	
}