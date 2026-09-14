package br.com.pucminas.secure_login;

import br.com.pucminas.secure_login.model.User;
import br.com.pucminas.secure_login.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
@AutoConfigureMockMvc
class SecureLoginApplicationTests {
    @Autowired MockMvc mvc;
    @Autowired UserRepository repository;
    @Autowired PasswordEncoder encoder;

    @BeforeEach
    void resetDatabase() {
        repository.deleteAll();
    }

    @Test
    void registrationHashesPasswordAndRejectsDuplicates() throws Exception {
        register("aluno", "aluno@example.com", "Senha123!", "Senha123!")
                .andExpect(redirectedUrl("/login?registered"));
        User user = repository.findByUsername("aluno").orElseThrow();
        assertThat(user.getPasswordHash()).startsWith("$2").isNotEqualTo("Senha123!");
        assertThat(encoder.matches("Senha123!", user.getPasswordHash())).isTrue();
        register("aluno", "outro@example.com", "Senha123!", "Senha123!")
                .andExpect(model().attribute("error", "Nome de usuário já cadastrado."));
        register("outro", "aluno@example.com", "Senha123!", "Senha123!")
                .andExpect(model().attribute("error", "Email já cadastrado."));
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void invalidRegistrationDoesNotPersistAnything() throws Exception {
        mvc.perform(post("/register").with(csrf()))
                .andExpect(view().name("register")).andExpect(model().attributeExists("error"));
        register("   ", "aluno@example.com", "Senha123!", "Senha123!")
                .andExpect(model().attributeExists("error"));
        register("aluno", "invalido", "Senha123!", "Senha123!")
                .andExpect(model().attributeExists("error"));
        register("aluno", "aluno@example.com", "Senha123!", "Outra123!")
                .andExpect(model().attributeExists("error"));
        register("aluno", "aluno@example.com", "curta", "curta")
                .andExpect(model().attributeExists("error"));
        register("aluno", "aluno@example.com", "á".repeat(37), "á".repeat(37))
                .andExpect(model().attributeExists("error"));
        assertThat(repository.count()).isZero();
    }

    @Test
    void loginProtectsHomeCreatesSessionAndLogoutInvalidatesIt() throws Exception {
        register("aluno", "aluno@example.com", "Senha123!", "Senha123!");
        mvc.perform(get("/home")).andExpect(redirectedUrl("/login"));
        mvc.perform(get("/")).andExpect(redirectedUrl("/login"));
        mvc.perform(post("/login").with(csrf()).param("username", "aluno").param("password", "errada"))
                .andExpect(redirectedUrl("/login?error")).andExpect(unauthenticated());
        mvc.perform(post("/login").with(csrf()).param("username", "' OR '1'='1").param("password", "Senha123!"))
                .andExpect(redirectedUrl("/login?error")).andExpect(unauthenticated());
        MockHttpSession initialSession = new MockHttpSession();
        String oldId = initialSession.getId();
        var result = mvc.perform(post("/login").session(initialSession).with(csrf())
                        .param("username", "aluno").param("password", "Senha123!"))
                .andExpect(redirectedUrl("/home")).andExpect(authenticated().withUsername("aluno"))
                .andReturn();
        var session = (MockHttpSession) result.getRequest().getSession(false);
        assertThat(session).isNotNull();
        assertThat(session.getId()).isNotEqualTo(oldId);
        mvc.perform(get("/home").session(session)).andExpect(status().isOk())
                .andExpect(content().string(containsString("aluno")))
                .andExpect(content().string(containsString("/images/puc.jpeg")));
        mvc.perform(post("/logout").session(session)).andExpect(status().isForbidden());
        mvc.perform(post("/logout").session(session).with(csrf()))
                .andExpect(redirectedUrl("/login?logout")).andExpect(unauthenticated());
        assertThat(session.isInvalid()).isTrue();
        mvc.perform(get("/home")).andExpect(redirectedUrl("/login"));
    }

    @Test
    void csrfIsRequiredAndPublicPagesRenderImageAndForms() throws Exception {
        for (String route : new String[]{"/login", "/register", "/recoverpassword"}) {
            mvc.perform(get(route)).andExpect(status().isOk())
                    .andExpect(content().string(containsString("name=\"_csrf\"")))
                    .andExpect(content().string(containsString("src=\"/images/puc.jpeg\"")));
            mvc.perform(post(route)).andExpect(status().isForbidden());
        }
        mvc.perform(get("/images/puc.jpeg")).andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"));
        mvc.perform(get("/css/style.css")).andExpect(status().isOk());
    }

    @Test
    void recoveryUsesSameResponseForKnownAndUnknownEmailAndNeverChangesPassword() throws Exception {
        register("aluno", "aluno@example.com", "Senha123!", "Senha123!");
        String hash = repository.findByUsername("aluno").orElseThrow().getPasswordHash();
        var known = mvc.perform(post("/recoverpassword").with(csrf()).param("email", "aluno@example.com"))
                .andExpect(status().isOk()).andExpect(model().attributeExists("message")).andReturn();
        var unknown = mvc.perform(post("/recoverpassword").with(csrf()).param("email", "ausente@example.com"))
                .andExpect(status().isOk()).andExpect(model().attributeExists("message")).andReturn();
        assertThat(known.getModelAndView().getModel().get("message"))
                .isEqualTo(unknown.getModelAndView().getModel().get("message"));
        mvc.perform(post("/recoverpassword").with(csrf()).param("email", "invalido"))
                .andExpect(model().attributeExists("error"));
        mvc.perform(post("/recoverpassword").with(csrf()))
                .andExpect(model().attributeExists("error"));
        assertThat(repository.findByUsername("aluno").orElseThrow().getPasswordHash()).isEqualTo(hash);
    }

    @Test
    void databaseEnforcesBothUniqueConstraints() {
        String hash = encoder.encode("Senha123!");
        repository.saveAndFlush(new User("aluno", "aluno@example.com", hash));
        assertThatThrownBy(() -> repository.saveAndFlush(new User("aluno", "outro@example.com", hash)))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> repository.saveAndFlush(new User("outro", "aluno@example.com", hash)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private org.springframework.test.web.servlet.ResultActions register(
            String username, String email, String password, String confirmation) throws Exception {
        return mvc.perform(post("/register").with(csrf()).param("username", username)
                .param("email", email).param("password", password).param("confirmPassword", confirmation));
    }
}
