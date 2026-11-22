package com.biblioteca.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Se ejecuta una vez al arrancar: si encuentra contraseñas guardadas sin cifrar
 * (de antes de agregar BCrypt), las cifra. Los hash de BCrypt empiezan por "$2".
 */
@Component
public class CifrarClavesViejas implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private BCryptPasswordEncoder cifrador;

    @Override
    public void run(String... args) throws Exception {
        try (Connection con = dataSource.getConnection();
             PreparedStatement buscar = con.prepareStatement("SELECT id, clave FROM usuarios WHERE clave NOT LIKE '$2%'");
             ResultSet rs = buscar.executeQuery();
             PreparedStatement actualizar = con.prepareStatement("UPDATE usuarios SET clave = ? WHERE id = ?")) {
            while (rs.next()) {
                actualizar.setString(1, cifrador.encode(rs.getString("clave")));
                actualizar.setLong(2, rs.getLong("id"));
                actualizar.executeUpdate();
            }
        }
    }
}
