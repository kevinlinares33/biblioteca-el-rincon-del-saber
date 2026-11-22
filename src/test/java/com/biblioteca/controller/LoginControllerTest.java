package com.biblioteca.controller;

import com.biblioteca.dao.UsuarioDAO;
import com.biblioteca.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.ExtendedModelMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de USUARIOS Y SEGURIDAD (responsable: Kevin Andrés Castañeda Linares).
 * El DAO (JDBC) se simula con Mockito; BCrypt es el real.
 */
@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @Spy
    private BCryptPasswordEncoder cifrador = new BCryptPasswordEncoder();

    @InjectMocks
    private LoginController controller;

    private Usuario usuario(String clavePlana, int intentos) {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNombre("Administrador");
        u.setUsuario("admin");
        u.setClave(cifrador.encode(clavePlana));   // en la BD la clave está cifrada
        u.setIntentosFallidos(intentos);
        return u;
    }

    @Test
    void loginCorrecto_entraALibrosYReiniciaIntentos() {
        Usuario admin = usuario("Admin2026", 0);
        when(usuarioDAO.buscarPorUsuario("admin")).thenReturn(admin);
        MockHttpServletRequest request = new MockHttpServletRequest();

        String vista = controller.entrar("admin", "Admin2026", request, new ExtendedModelMap());

        assertEquals("redirect:/libros", vista);
        verify(usuarioDAO).reiniciarIntentos(1L);
        Usuario enSesion = (Usuario) request.getSession().getAttribute("usuario");
        assertNotNull(enSesion);
        assertNull(enSesion.getClave());           // la clave no queda guardada en la sesión
    }

    @Test
    void claveIncorrecta_noEntraYSumaUnIntento() {
        Usuario admin = usuario("Admin2026", 0);
        when(usuarioDAO.buscarPorUsuario("admin")).thenReturn(admin);
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.entrar("admin", "otraClave1", new MockHttpServletRequest(), model);

        assertEquals("login", vista);
        assertEquals("Usuario o contraseña incorrectos", model.get("error"));
        verify(usuarioDAO).sumarIntentoFallido(1L);
    }

    @Test
    void conTresIntentosFallidos_elUsuarioQuedaBloqueado() {
        Usuario admin = usuario("Admin2026", 3);
        when(usuarioDAO.buscarPorUsuario("admin")).thenReturn(admin);
        ExtendedModelMap model = new ExtendedModelMap();

        // aunque escriba la clave correcta, no lo deja entrar
        String vista = controller.entrar("admin", "Admin2026", new MockHttpServletRequest(), model);

        assertEquals("login", vista);
        assertTrue(model.get("error").toString().contains("bloqueado"));
    }

    @Test
    void registro_rechazaContrasenaDebil() {
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.registrar("Prueba", "prueba1", "123", model);

        assertEquals("registro", vista);
        verify(usuarioDAO, never()).guardar(anyString(), anyString(), anyString());
    }

    @Test
    void registro_guardaLaContrasenaCifradaConBCrypt() {
        when(usuarioDAO.existe("prueba1")).thenReturn(false);

        String vista = controller.registrar("Prueba", "prueba1", "clave123", new ExtendedModelMap());

        assertEquals("login", vista);
        ArgumentCaptor<String> claveGuardada = ArgumentCaptor.forClass(String.class);
        verify(usuarioDAO).guardar(eq("Prueba"), eq("prueba1"), claveGuardada.capture());
        assertNotEquals("clave123", claveGuardada.getValue());               // no se guarda en texto plano
        assertTrue(cifrador.matches("clave123", claveGuardada.getValue()));  // pero sí se puede validar
    }
}
