package com.mgm_solutions.dao;

import com.mgm_solutions.config.ConnectionDB;
import com.mgm_solutions.models.Ciudad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CiudadDAO {
    
    public List<Ciudad> listar() {
        List<Ciudad> ciudades = new ArrayList<>();
        String sql = "SELECT ID_Ciudad, Nombre FROM TBL_CIUDADES";

        // Usamos try-with-resources para cerrar la conexión automáticamente
        try (Connection con = ConnectionDB.gConnectionDB();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ciudades.add(new Ciudad(
                    rs.getInt("ID_Ciudad"),
                    rs.getString("Nombre")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar ciudades: " + e.getMessage());
        }
        return ciudades;
    }
}