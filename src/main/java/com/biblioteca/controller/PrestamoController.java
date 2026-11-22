package com.biblioteca.controller;

import com.biblioteca.model.Libro;
import com.biblioteca.model.Prestamo;
import com.biblioteca.repository.LibroRepository;
import com.biblioteca.repository.PrestamoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoRepository prestamoRepo;

    @Autowired
    private LibroRepository libroRepo;

    // LISTAR: muestra el formulario para prestar y la lista de préstamos
    @GetMapping
    public String listar(@RequestParam(required = false) Long libroId, HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        model.addAttribute("libroElegido", libroId); // si viene del botón Prestar de la lista de libros
        model.addAttribute("prestamos", prestamoRepo.findAllByOrderByDevueltoAscFechaPrestamoDesc());
        model.addAttribute("libros", libroRepo.findAll());
        model.addAttribute("pendientes", prestamoRepo.countByDevueltoFalse());
        return "prestamos";
    }

    // PRESTAR: guarda el préstamo y descuenta 1 de la cantidad del libro
    @PostMapping("/guardar")
    public String prestar(@RequestParam Long libroId, @RequestParam String persona,
                          HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Libro libro = libroRepo.findById(libroId).orElse(null);
        if (libro == null || libro.getCantidad() <= 0) {
            model.addAttribute("error", "Ese libro no tiene ejemplares disponibles");
            return listar(libroId, session, model);
        }
        libro.setCantidad(libro.getCantidad() - 1);
        libroRepo.save(libro);

        Prestamo p = new Prestamo();
        p.setLibro(libro);
        p.setPersona(persona);
        p.setFechaPrestamo(LocalDate.now());
        prestamoRepo.save(p);
        return "redirect:/prestamos";
    }

    // DEVOLVER: marca el préstamo como devuelto y suma 1 a la cantidad del libro
    @GetMapping("/devolver/{id}")
    public String devolver(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        Prestamo p = prestamoRepo.findById(id).orElse(null);
        if (p != null && !p.isDevuelto()) {
            p.setDevuelto(true);
            p.setFechaDevolucion(LocalDate.now());
            prestamoRepo.save(p);
            Libro libro = p.getLibro();
            libro.setCantidad(libro.getCantidad() + 1);
            libroRepo.save(libro);
        }
        return "redirect:/prestamos";
    }
}
