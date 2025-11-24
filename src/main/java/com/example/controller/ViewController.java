@Controller
public class ViewController {

    @GetMapping("/")
    public String root() {
        return "login"; // cuando entran al root, redirige al login
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // acceso público
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // acceso público
    }

    @GetMapping("/index")
    public String index() {
        return "index"; // requiere autenticación
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // requiere autenticación
    }
}
