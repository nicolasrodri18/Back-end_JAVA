package com.mgm_solutions.config;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class ConnectionDB {
    private static String URL = "jdbc:mysql://localhost:3306/MGM_Solucions";
    private static String USER = "root";
    private static String PASSWD = "#Aprendiz2024";

    public static Connection gConnectionDB(){
        try{
            Connection connection = DriverManager.getConnection(URL, USER, PASSWD);
            System.out.print("¡La conexión fue realizada con exito!");
            return connection;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}