package com.biblioteca.controller;

import com.biblioteca.model.Libro;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.LibroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de LIBROS (responsable: Daniel Sandoval).
 * Se usa Mockito para simular la base de datos: no hace falta tener MySQL encendido.
 */
@ExtendWith(MockitoExtension.class)
class LibroControllerTest {

    @Mock
    private LibroRepository libroRepo;      // base de datos simulada

    @InjectMocks
    private LibroController controller;     // clase que se prueba

    private Libro libro(String titulo, String autor, int cantidad) {
        Libro l = new Libro();
        l.setTitulo(titulo);
        l.setAutor(autor);
        l.setCantidad(cantidad);
        return l;
    }

    private MockHttpSession sesionIniciada() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuario", new Usuario());
        return session;
    }

    @Test
    void buscar_muestraSoloLosLibrosQueCoincidenYCuentaEjemplares() {
        when(libroRepo.findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase("garcía", "garcía"))
                .thenReturn(List.of(libro("Cien años de soledad", "Gabriel García Márquez", 2),
                                    libro("María", "Jorge Isaacs", 3)));
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.listar("garcía", sesionIniciada(), model);

        assertEquals("libros", vista);
        assertEquals(2, ((List<?>) model.get("libros")).size());
        assertEquals(5, model.get("ejemplares"));   // 2 + 3 ejemplares
    }

    @Test
    void sinIniciarSesion_mandaAlLogin() {
        String vista = controller.listar(null, new MockHttpSession(), new ExtendedModelMap());

        assertEquals("redirect:/login", vista);
        verifyNoInteractions(libroRepo);            // no consulta la base de datos
    }

    @Test
    void eliminarUnLibroConPrestamos_noSeBorraYMuestraAviso() {
        doThrow(new DataIntegrityViolationException("tiene préstamos")).when(libroRepo).deleteById(1L);

        String vista = controller.eliminar(1L, sesionIniciada());

        assertEquals("redirect:/libros?noEliminar", vista);
    }
}
