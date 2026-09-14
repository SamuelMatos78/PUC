package br.com.pucminas.secure_login.controller;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.regex.Pattern;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.pucminas.secure_login.model.User;
import br.com.pucminas.secure_login.repository.UserRepository;



@Controller
public class AuthController {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JavaMailSender mailSender;

    public AuthController(UserRepository userRepository, PasswordEncoder encoder,JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.mailSender = mailSender;

    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/recoverpassword")
    public String recoverPage() {
        return "recoverpassword";
    }

    @GetMapping({"/", "/home"})
    public String home(Principal principal, Model model) {
        model.addAttribute("nome", principal.getName());
        return "home";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "") String email,
            @RequestParam(defaultValue = "") String password,
            @RequestParam(defaultValue = "") String confirmPassword,
            Model model) {
        username = username.trim();
        email = email.trim();
        model.addAttribute("username", username);
        model.addAttribute("email", email);

        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            model.addAttribute("error", "Preencha todos os campos.");
        } else if (username.length() > 255 || email.length() > 255 || !EMAIL.matcher(email).matches()) {
            model.addAttribute("error", "Informe um email válido e utilize até 255 caracteres no usuário e no email.");
        } else if (password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            model.addAttribute("error", "A senha deve ter ao menos 8 caracteres e no máximo 72 bytes (acentos podem ocupar mais de um byte).");
        } else if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "As senhas não conferem.");
        } else if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "Nome de usuário já cadastrado.");
        } else if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email já cadastrado.");
        } else {
            try {
                userRepository.saveAndFlush(new User(username, email, encoder.encode(password)));
                return "redirect:/login?registered";
            } catch (DataIntegrityViolationException exception) {
                // A restrição no banco também protege contra cadastros simultâneos.
                model.addAttribute("error", "Não foi possível cadastrar: usuário ou email já cadastrado.");
            }
        }
        return "register";
    }

    @PostMapping("/recoverpassword")
    public String recoverPassword(
        @RequestParam(defaultValue = "") String email,
        Model model) {

        email = email.trim();
        model.addAttribute("email", email);

        if (email.length() > 255 || !EMAIL.matcher(email).matches()) {
            model.addAttribute("error", "Informe um email válido.");
            return "recoverpassword";
        }

        if (userRepository.existsByEmail(email)) {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(email);
            message.setSubject("Recuperação de senha - PUC Minas");
            message.setText(
                    "Recebemos uma solicitação de recuperação de senha.\n\n" +
                    "Entre em contato com o suporte acadêmico para realizar a alteração da senha."
            );

        
            try {
                mailSender.send(message);

                model.addAttribute(
                    "message",
                    "As instruções de recuperação serão enviadas."
                );
            }catch (Exception exception) { //pra dar erro só nos logs em caso de má configuração e não quebrar a pagina nos testes
                System.err.println("Erro ao enviar email: " + exception.getMessage());
                model.addAttribute(
                    "error",
                    "Não foi possível enviar o email de recuperação no momento."
                );
                return "recoverpassword";
            }
        }
        model.addAttribute(
            "message",
            "Se o email estiver cadastrado, as instruções de recuperação serão enviadas."
        );


        return "recoverpassword";
    }
}
