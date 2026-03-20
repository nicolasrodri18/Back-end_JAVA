package com.mgm_solutions.config;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Clase de utilidad para gestionar la seguridad de las contraseñas.
 * Utiliza la biblioteca BCrypt para el hashing y verificación de claves.
 */
public class SecurityUtils {

    /**
     * Genera un hash seguro para una contraseña en texto plano.
     * 
     * @param password La contraseña a encriptar.
     * @return El hash generado incluyendo la sal (salt).
     */
    public static String hashPassword(String password) {
        // gensalt() genera una sal aleatoria para fortalecer el hash
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash almacenado.
     * 
     * @param passwordInput La contraseña ingresada por el usuario.
     * @param hashedStored El hash recuperado de la base de datos.
     * @return true si la contraseña es válida, false en caso contrario.
     */
    public static boolean checkPassword(String passwordInput, String hashedStored) {
        try {
            // BCrypt extrae la sal del hash automáticamente para realizar la comparación
            return BCrypt.checkpw(passwordInput, hashedStored);
        } catch (Exception e) {
            // Retorna false si el hash está mal formado o hay errores de comparación
            return false;
        }
    }
}
