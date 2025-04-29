package com.transportes.coches.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.transportes.coches.models.Marca;

public interface MarcasRepository extends JpaRepository<Marca, Long>,
	PagingAndSortingRepository<Marca, Long>{

}
