package com.biblioteca.controller;

import com.biblioteca.model.Libro;
import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.LibroRepository;
import com.biblioteca.repository.PrestamoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de PRÉSTAMOS (responsable: Daniel Sandoval).
 */
@ExtendWith(MockitoExtension.class)
class PrestamoControllerTest {

    @Mock
    private PrestamoRepository prestamoRepo;

    @Mock
    private LibroRepository libroRepo;

    @InjectMocks
    private PrestamoController controller;

    private Libro libroConEjemplares(int cantidad) {
        Libro l = new Libro();
        l.setId(1L);
        l.setTitulo("Rayuela");
        l.setAutor("Julio Cortázar");
        l.setCantidad(cantidad);
        return l;
    }

    private MockHttpSession sesionIniciada() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", new Usuario());
        return session;
    }

    @Test
    void prestar_descuentaUnEjemplarYGuardaElPrestamo() {
        Libro libro = libroConEjemplares(2);
        when(libroRepo.findById(1L)).thenReturn(Optional.of(libro));

        String vista = controller.prestar(1L, "Natalia Castro", sesionIniciada(), new ExtendedModelMap());

        assertEquals("redirect:/prestamos", vista);
        assertEquals(1, libro.getCantidad());       // quedaba 2, ahora 1
        verify(prestamoRepo).save(any(Prestamo.class));
    }

    @Test
    void prestar_sinEjemplaresDisponibles_noPresta() {
        when(libroRepo.findById(1L)).thenReturn(Optional.of(libroConEjemplares(0)));
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.prestar(1L, "Natalia Castro", sesionIniciada(), model);

        assertEquals("prestamos", vista);
        assertNotNull(model.get("error"));
        verify(prestamoRepo, never()).save(any());
    }

    @Test
    void devolver_sumaElEjemplarYMarcaDevuelto() {
        Libro libro = libroConEjemplares(1);
        Prestamo p = new Prestamo();
        p.setId(5L);
        p.setLibro(libro);
        when(prestamoRepo.findById(5L)).thenReturn(Optional.of(p));

        controller.devolver(5L, sesionIniciada());

        assertTrue(p.isDevuelto());
        assertNotNull(p.getFechaDevolucion());
        assertEquals(2, libro.getCantidad());       // vuelve a sumar 1
    }
}
