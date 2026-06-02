package com.Bumeran.Prestamos.Controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Bumeran.Prestamos.Entidades.Articulo;
import com.Bumeran.Prestamos.Entidades.Prestamo;
import com.Bumeran.Prestamos.Entidades.Usuario;
import com.Bumeran.Prestamos.Repositories.ArticuloRepository;
import com.Bumeran.Prestamos.Repositories.PrestamoRepository;
import com.Bumeran.Prestamos.Repositories.UsuarioRepository;
import com.Bumeran.Prestamos.Servicios.ArticuloService;
import com.Bumeran.Prestamos.Servicios.PrestamoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final UsuarioRepository usuarioRepository;
    private final ArticuloRepository articuloRepository;
    private final ArticuloService articuloService;
    private final PrestamoService prestamoService;
    private final PrestamoRepository prestamoRepository;

    private Usuario getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email);
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirect) {
        if (!password.equals(confirmPassword)) {
            redirect.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/register";
        }
        try {
            if (usuarioRepository.findByEmail(email) != null) {
                redirect.addFlashAttribute("error", "Ya existe un usuario con ese email");
                return "redirect:/register";
            }
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(password));
            usuarioRepository.save(usuario);
            return "redirect:/login?registered";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        Usuario user = getCurrentUser();

        List<Articulo> todosArticulos = articuloRepository.findByPropietarioId(user.getId());
        List<Prestamo> prestamosActivos = prestamoRepository.findByUsuarioPropietarioId(user.getId())
                .stream().filter(p -> p.getFechaDevuelto() == null).collect(Collectors.toList());

        List<Long> articulosPrestadosIds = prestamosActivos.stream()
                .map(p -> p.getArticulo().getId())
                .collect(Collectors.toList());

        List<Prestamo> prestamosRecibidos = prestamoRepository.findByUsuarioReceptorId(user.getId());

        model.addAttribute("todosArticulos", todosArticulos);
        model.addAttribute("articulosPrestadosIds", articulosPrestadosIds);
        model.addAttribute("prestamosRecibidos", prestamosRecibidos);
        return "dashboard";
    }

    @GetMapping("/prestamos")
    public String prestamos(Model model) {
        Usuario user = getCurrentUser();
        List<Prestamo> prestamos = prestamoRepository.findByUsuarioPropietarioId(user.getId());
        List<Prestamo> prestamosRecibidos = prestamoRepository.findByUsuarioReceptorId(user.getId());
        model.addAttribute("prestamos", prestamos);
        model.addAttribute("prestamosRecibidos", prestamosRecibidos);
        return "prestamos";
    }

    @GetMapping("/articulos/nuevo")
    public String nuevoArticulo() {
        return "articulo-nuevo";
    }

    @PostMapping("/articulos/nuevo")
    public String crearArticulo(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            RedirectAttributes redirect) {
        try {
            Usuario user = getCurrentUser();
            Articulo articulo = new Articulo();
            articulo.setNombre(nombre);
            articulo.setDescripcion(descripcion);
            articulo.setEstado("DISPONIBLE");
            articulo.setPropietario(user);
            articuloRepository.save(articulo);
            redirect.addFlashAttribute("success", "Artículo creado correctamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al crear artículo: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/articulos/{id}/eliminar")
    public String eliminarArticulo(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            articuloService.eliminarArticulo(id);
            redirect.addFlashAttribute("success", "Artículo eliminado con éxito");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "No se pudo eliminar el artículo");
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/prestamos/nuevo")
    public String nuevoPrestamo(Model model) {
        Usuario user = getCurrentUser();
        List<Articulo> todosArticulos = articuloRepository.findByPropietarioId(user.getId());
        List<Prestamo> misPrestamos = prestamoRepository.findByUsuarioPropietarioId(user.getId());

        List<Long> articulosPrestadosIds = misPrestamos.stream()
                .filter(p -> p.getFechaDevuelto() == null)
                .map(p -> p.getArticulo().getId())
                .collect(Collectors.toList());

        List<Articulo> articulosDisponibles = todosArticulos.stream()
                .filter(a -> !articulosPrestadosIds.contains(a.getId()) && "DISPONIBLE".equals(a.getEstado()))
                .collect(Collectors.toList());

        List<Usuario> usuarios = usuarioRepository.findAll();

        model.addAttribute("articulosDisponibles", articulosDisponibles);
        model.addAttribute("usuarios", usuarios);
        return "prestamo-nuevo";
    }

    @PostMapping("/prestamos/nuevo")
    public String crearPrestamo(
            @RequestParam Long articuloId,
            @RequestParam(required = false) Long usuarioReceptorId,
            @RequestParam(required = false) String nombreReceptor,
            @RequestParam(defaultValue = "usuario") String tipoPrestatario,
            RedirectAttributes redirect) {
        try {
            com.Bumeran.Prestamos.dto.CrearPrestamoRequest request =
                new com.Bumeran.Prestamos.dto.CrearPrestamoRequest();
            request.setArticuloId(articuloId);
            if ("usuario".equals(tipoPrestatario)) {
                request.setUsuarioReceptorId(usuarioReceptorId);
            } else {
                request.setNombreReceptor(nombreReceptor);
            }
            prestamoService.prestarObjeto(request);
            redirect.addFlashAttribute("success", "Préstamo creado correctamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al crear préstamo: " + e.getMessage());
        }
        return "redirect:/prestamos";
    }

    @PostMapping("/prestamos/{id}/devolver")
    public String devolverPrestamo(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            prestamoService.devolverObjeto(id);
            redirect.addFlashAttribute("success", "Artículo devuelto correctamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al devolver: " + e.getMessage());
        }
        return "redirect:/prestamos";
    }
}
