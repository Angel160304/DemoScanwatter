// com.example.controller.ViewController.java

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    // NUEVO MÉTODO: Manejar la ruta raíz "/"
    // @GetMapping("/")
    // public String rootRedirect() {
    //     // Redirigir la ruta raíz a /index. Spring Security (WebSecurityConfig)
    //     // se encargará de interceptar esta solicitud y, si el usuario no está logueado,
    //     // lo redirigirá a /login.
    //     return "redirect:/index"; 
    // }

   @GetMapping("/login")
public String login() {
    return "login.html"; // Sirve directamente login.html desde templates
}



    @GetMapping("/registro")
    public String registro() {
        return "registro"; // Muestra registro.html
    }

    // Mapea la URL /index a la vista "index" (index.html)
    @GetMapping("/index")
    public String index() {
        return "index"; 
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // Muestra dashboard.html
    }
}