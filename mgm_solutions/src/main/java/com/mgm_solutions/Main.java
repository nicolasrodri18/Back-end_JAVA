package com.mgm_solutions;

import java.sql.Connection;

import com.mgm_solutions.config.ConnectionDB;


public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando prueba de conexión...");
        Connection testConn = ConnectionDB.gConnectionDB();
        
        if (testConn != null) {
            System.out.println("¡Perfecto! El Back-end ya reconoce la base de datos.");
        } else {
            System.out.println("Algo falló. Revisa que MySQL esté encendido.");
        }
    }
}