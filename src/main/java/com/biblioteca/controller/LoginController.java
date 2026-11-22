package com.biblioteca.controller;

import com.biblioteca.dao.UsuarioDAO;
import com.biblioteca.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UsuarioDAO usuarioDAO;          // acceso a la tabla usuarios con JDBC

    @Autowired
    private BCryptPasswordEncoder cifrador; // cifra y compara contraseñas

    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // LOGIN: busca el usuario con JDBC y compara la clave con BCrypt
    @PostMapping("/login")
    public String entrar(@RequestParam String usuario, @RequestParam String clave,
                         HttpServletRequest request, Model model) {
        Usuario u = usuarioDAO.buscarPorUsuario(usuario.trim());

        if (u != null && u.getIntentosFallidos() >= UsuarioDAO.MAX_INTENTOS) {
            model.addAttribute("error", "Usuario bloqueado por 3 intentos fallidos. Contacte al administrador.");
            return "login";
        }
        if (u == null || !cifrador.matches(clave, u.getClave())) {
            if (u != null) usuarioDAO.sumarIntentoFallido(u.getId());
            // mismo mensaje en los dos casos para no revelar qué usuarios existen
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        }

        usuarioDAO.reiniciarIntentos(u.getId());
        request.getSession().invalidate();           // sesión nueva al entrar (evita robo de sesión)
        HttpSession session = request.getSession(true);
        u.setClave(null);                             // no se guarda la clave en la sesión
        session.setAttribute("usuario", u);
        return "redirect:/libros";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    // REGISTRO: valida los datos, cifra la clave y guarda con JDBC
    @PostMapping("/registro")
    public String registrar(@RequestParam String nombre, @RequestParam String usuario,
                            @RequestParam String clave, Model model) {
        nombre = nombre.trim();
        usuario = usuario.trim();
        String error = null;
        if (nombre.isEmpty() || nombre.length() > 80) {
            error = "Escriba su nombre (máximo 80 caracteres)";
        } else if (!usuario.matches("[a-zA-Z0-9_]{4,20}")) {
            error = "El usuario debe tener de 4 a 20 letras o números, sin espacios";
        } else if (clave.length() < 6 || !clave.matches(".*[A-Za-z].*") || !clave.matches(".*\\d.*")) {
            error = "La contraseña debe tener mínimo 6 caracteres, con letras y números";
        } else if (usuarioDAO.existe(usuario)) {
            error = "Ese usuario ya existe";
        }
        if (error != null) {
            model.addAttribute("error", error);
            return "registro";
        }
        usuarioDAO.guardar(nombre, usuario, cifrador.encode(clave));
        model.addAttribute("ok", "Usuario creado, ya puede ingresar");
        return "login";
    }

    @GetMapping("/salir")
    public String salir(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
