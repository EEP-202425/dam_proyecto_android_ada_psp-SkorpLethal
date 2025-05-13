package com.transportes.coches.models;

import com.fasterxml.jackson.annotation.JsonBackReference;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "modelos")
public class Modelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(length = 1000, nullable = false)
    private String descripcion;

    private Double precioBase;

    @ManyToOne
    @JoinColumn(name = "id_marca", nullable = false)
    @JsonBackReference
    private Marca marca;
    
    @ManyToOne
    @JoinColumn(name = "id_color", nullable = false)    
    private Color color;
    
    public Modelo() {
    	
    }

    public Modelo(Long id, String nombre, String descripcion, Double precioBase, Marca marca, Color color) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.marca = marca;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(Double precioBase) {
        this.precioBase = precioBase;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }
    

	public Color getColor() {
	    return color;
	}
	
	public void setColor(Color color) {
	    this.color = color;
	}
    
    public Long getMarcaId() {
        return marca != null ? marca.getId() : null;
    }
    
    public Long getColorId() {
        return color != null ? color.getId() : null;
    }
    
    public double getPrecioTotal() {
        return precioBase + (color != null ?  color.getPrecio(): 0);
    }
}