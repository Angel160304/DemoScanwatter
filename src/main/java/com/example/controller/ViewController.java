@Controller
public class ViewController {

    @GetMapping("/login")
    public String login() {
        return "login"; // público (login.html sigue en static/)
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // público (registro.html sigue en static/)
    }

    @GetMapping("/index")
    public String index() {
        return "index"; // ahora busca templates/index.html
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // ahora busca templates/dashboard.html
    }
}
