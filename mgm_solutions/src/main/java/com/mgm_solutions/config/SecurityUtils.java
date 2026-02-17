package com.mgm_solutions.config;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtils {

    // Método para encriptar (Hashing)
    public static String hashPassword(String password) {
        // gensalt() genera una sal aleatoria y el hash resultante la incluye
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Método para verificar si la clave coincide
    public static boolean checkPassword(String passwordInput, String hashedStored) {
        try {
            // BCrypt extrae la sal del hash almacenado automáticamente
            return BCrypt.checkpw(passwordInput, hashedStored);
        } catch (Exception e) {
            return false;
        }
    }
}
