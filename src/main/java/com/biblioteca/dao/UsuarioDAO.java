package com.biblioteca.dao;

import com.biblioteca.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Acceso a la tabla "usuarios" usando JDBC directamente.
 * - La conexión sale del DataSource que Spring arma con los datos de application.properties.
 * - Se usa PreparedStatement con "?" para que nadie pueda hacer inyección SQL.
 * - try-with-resources cierra la conexión, la sentencia y el ResultSet automáticamente.
 */
@Repository
public class UsuarioDAO {

    public static final int MAX_INTENTOS = 3;

    @Autowired
    private DataSource dataSource;

    // Busca un usuario por su nombre de usuario (null si no existe)
    public Usuario buscarPorUsuario(String usuario) {
        String sql = "SELECT id, nombre, usuario, clave, intentos_fallidos FROM usuarios WHERE usuario = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getLong("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setClave(rs.getString("clave"));
                    u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
                    return u;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando el usuario", e);
        }
    }

    public boolean existe(String usuario) {
        return buscarPorUsuario(usuario) != null;
    }

    // Guarda un usuario nuevo (la clave ya debe venir cifrada)
    public void guardar(String nombre, String usuario, String claveCifrada) {
        String sql = "INSERT INTO usuarios (nombre, usuario, clave, intentos_fallidos) VALUES (?, ?, ?, 0)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, usuario);
            ps.setString(3, claveCifrada);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error guardando el usuario", e);
        }
    }

    // Suma 1 a los intentos fallidos
    public void sumarIntentoFallido(Long id) {
        ejecutar("UPDATE usuarios SET intentos_fallidos = intentos_fallidos + 1 WHERE id = ?", id);
    }

    // Vuelve a 0 los intentos cuando entra bien
    public void reiniciarIntentos(Long id) {
        ejecutar("UPDATE usuarios SET intentos_fallidos = 0 WHERE id = ?", id);
    }

    private void ejecutar(String sql, Long id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando el usuario", e);
        }
    }
}
