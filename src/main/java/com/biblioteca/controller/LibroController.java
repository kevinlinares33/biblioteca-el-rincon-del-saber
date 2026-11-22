package com.biblioteca.controller;

import com.biblioteca.model.Libro;
import com.biblioteca.repository.LibroRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroRepository libroRepo;

    // LISTAR y BUSCAR: si escriben algo en el buscador, filtra por título o autor
    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        List<Libro> libros;
        if (buscar != null && !buscar.isBlank()) {
            libros = libroRepo.findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(buscar.trim(), buscar.trim());
        } else {
            libros = libroRepo.findAll();
        }
        int ejemplares = 0;
        for (Libro l : libros) ejemplares += l.getCantidad();

        model.addAttribute("libros", libros);
        model.addAttribute("buscar", buscar);
        model.addAttribute("ejemplares", ejemplares);
        model.addAttribute("libro", new Libro());
        return "libros";
    }

    // EDITAR: carga el libro elegido en el formulario
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        List<Libro> libros = libroRepo.findAll();
        int ejemplares = 0;
        for (Libro l : libros) ejemplares += l.getCantidad();
        model.addAttribute("libros", libros);
        model.addAttribute("ejemplares", ejemplares);
        model.addAttribute("libro", libroRepo.findById(id).orElse(new Libro()));
        return "libros";
    }

    // GUARDAR: si el libro no tiene id lo crea, si tiene id lo actualiza
    @PostMapping("/guardar")
    public String guardar(Libro libro, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        libroRepo.save(libro);
        return "redirect:/libros";
    }

    // ELIMINAR (si el libro tiene préstamos no se puede borrar)
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("usuario") == null) return "redirect:/login";
        try {
            libroRepo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            return "redirect:/libros?noEliminar";
        }
        return "redirect:/libros";
    }
}
