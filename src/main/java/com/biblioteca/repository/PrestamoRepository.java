package com.biblioteca.repository;

import com.biblioteca.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    // Primero los que no se han devuelto, luego los más recientes
    List<Prestamo> findAllByOrderByDevueltoAscFechaPrestamoDesc();
    long countByDevueltoFalse();
}
