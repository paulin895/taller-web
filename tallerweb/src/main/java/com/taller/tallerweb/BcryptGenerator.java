package com.taller.tallerweb;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Generar un nuevo hash para una contraseña
        String passwordEnTextoPlano = "123456";
        String nuevoHashedPassword = encoder.encode(passwordEnTextoPlano);
        System.out.println("Nuevo hash de '123456': " + nuevoHashedPassword);

        // Verificar si una contraseña coincide con un hash existente
        String rawPassword = "123456";
        String hashExistente = "$2a$10$zHW2nDrEnPgP8l9EMTsyUuLOLawEcnR.9/1jbulEjEW4fuJ2UV.4a";

        boolean match = encoder.matches(rawPassword, hashExistente);
        System.out.println("¿Coincide con el hash existente? " + match);
    }


}