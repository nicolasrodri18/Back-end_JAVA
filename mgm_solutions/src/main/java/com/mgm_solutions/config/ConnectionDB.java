package com.mgm_solutions.config;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class ConnectionDB {
    private static String URL = "jdbc:mysql://localhost:3306/MGM_Solucions";
    private static String USER = "root";
    private static String PASSWD = "#Aprendiz2024";

    public static Connection gConnectionDB() {
        Connection connection = null;
        try {
            // Esto busca el driver de MySQL que pusiste en el pom.xml
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            
            connection = DriverManager.getConnection(URL, USER, PASSWD);
            System.out.println("¡La conexión fue realizada con éxito!");
            
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el Driver de MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: No se pudo conectar a la base de datos.");
            e.printStackTrace();
        }
        return connection;
    }
}
