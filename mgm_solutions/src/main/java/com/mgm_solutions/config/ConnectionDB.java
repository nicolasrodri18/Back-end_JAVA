package com.mgm_solutions.config;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * Clase encargada de la configuración y gestión de la conexión a la base de datos MySQL.
 * Utiliza el driver JDBC para establecer el canal de comunicación con el esquema 'mgm_solucions'.
 */
public class ConnectionDB {
    // Configuración de la base de datos
    private static String URL = "jdbc:mysql://localhost:3306/mgm_solucions";
    private static String USER = "mgm_solucions";
    private static String PASSWD = "mgm_solucions";

    /**
     * Establece y retorna una conexión activa a la base de datos.
     * 
     * @return Connection objeto de conexión si es exitosa, null en caso de error.
     */
    public static Connection gConnectionDB() {
        Connection connection = null;
        try {
            // Carga dinámica del driver de MySQL especificado en las dependencias (pom.xml)
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            
            connection = DriverManager.getConnection(URL, USER, PASSWD);
            System.out.println("¡La conexión con MGM Solutions fue realizada con éxito!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error crítico: No se encontró el Driver de MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error de red/autenticación: No se pudo conectar a la base de datos.");
            e.printStackTrace();
        }
        return connection;
    }
}
