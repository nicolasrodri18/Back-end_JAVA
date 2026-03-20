package com.mgm_solutions.models;

/**
 * Clase que representa el modelo de una Ciudad en el sistema.
 * Mapea directamente con los registros de la tabla 'TBL_CIUDADES'.
 */
public class Ciudad {
    private int id;
    private String nombre;

    public Ciudad(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
}