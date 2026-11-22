package com.biblioteca.repository;

import com.biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

// Ya no se usa para el login ni el registro: eso se hace con JDBC en dao/UsuarioDAO.java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
