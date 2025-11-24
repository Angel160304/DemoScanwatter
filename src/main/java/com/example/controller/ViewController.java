@Controller
public class ViewController {

    @GetMapping("/index")
    public String index() { return "index"; }

    @GetMapping("/dashboard")
    public String dashboard() { return "dashboard"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/registro")
    public String registro() { return "registro"; }

    // Cualquier acceso directo a *.html redirige a login
    @GetMapping("/*.html")
    public String redirectHtml() {
        return "redirect:/login";
    }
}
