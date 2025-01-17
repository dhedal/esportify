package com.esportify.service;

import com.esportify.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class AuthenticationServiceIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationServiceIntegrationTest.class);

    private UserService userService;
    private AuthenticationService authenticationService;
    private final BCryptPasswordEncoder passwordEncoder;
    private RegisterRequest request;

    @Autowired
    public AuthenticationServiceIntegrationTest(
            UserService userService,
            AuthenticationService authenticationService,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    public void setup() {
        this.request = new RegisterRequest();
        this.request.setName("John Doe");
        this.request.setEmail("john.doe@example.com");
        this.request.setPassword("StrongPass1!");


    }


    /**                              TEST REGISTER                          **/
    @Test
    public void test_register_NullRequest_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.authenticationService.register(null, new RegisterResponse());
        });
        assertEquals("RegisterRequest ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_register_NullResponse_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.authenticationService.register(this.request, null);
        });
        assertEquals("RegisterResponse ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_register_InvalidRequest() {
        List<String> tests = new ArrayList<>();

        this.request.setName("");
        tests.add("Le nom d'utilisateur est obligatoire");
        tests.add("le nom doit avoir entre 3 et 50 charactères");
        this.checkResponse(this.authenticationService.register( this.request, new RegisterResponse()), tests);
        tests.clear();

        this.setup();
        request.setEmail("");
        tests.add("L'email' est obligatoire");
        tests.add("le format d'email est invalide. ex : xxxx@xxx.xxx");
        this.checkResponse(this.authenticationService.register( this.request, new RegisterResponse()), tests);
        tests.clear();

        this.setup();
        request.setEmail("dqsfqsf.fr");
        tests.add("le format d'email est invalide. ex : xxxx@xxx.xxx");
        this.checkResponse(this.authenticationService.register( this.request, new RegisterResponse()), tests);
        tests.clear();

        this.setup();
        request.setEmail("dqsfqsf@.fr");
        tests.add("le format d'email est invalide. ex : xxxx@xxx.xxx");
        this.checkResponse(this.authenticationService.register( this.request, new RegisterResponse()), tests);
        tests.clear();

        this.setup();
        request.setEmail("dqsfqsf@qsff");
        tests.add("le format d'email est invalide. ex : xxxx@xxx.xxx");
        this.checkResponse(this.authenticationService.register( this.request, new RegisterResponse()), tests);
        tests.clear();

        this.setup();
        request.setPassword("");
        tests.add("Le mot de passe est obligatoire");
        tests.add("Le mot de passe doit avoir au moins 8 caractères.");
        tests.add("Le mot de passe doit contenir à la fois des majuscules et des minuscules.");
        tests.add("Le mot de passe doit contenir au moins un chiffre");
        tests.add("Le mot de passe doit contenir au moins un caractère spécial");
        this.checkResponse(this.authenticationService.register( this.request, new RegisterResponse()), tests);
        tests.clear();

        this.setup();
        request.setPassword("StrongPassword8");
        tests.add("Le mot de passe doit contenir au moins un caractère spécial");
        this.checkResponse(this.authenticationService.register( this.request, new RegisterResponse()), tests);
        tests.clear();

        this.setup();
        request.setPassword("Str!8");
        tests.add("Le mot de passe doit avoir au moins 8 caractères.");
        this.checkResponse(this.authenticationService.register( this.request, new RegisterResponse()), tests);
        tests.clear();

        this.setup();
        request.setPassword("strongpassword8!");
        tests.add("Le mot de passe doit contenir à la fois des majuscules et des minuscules.");
        this.checkResponse(this.authenticationService.register( this.request, new RegisterResponse()), tests);
        tests.clear();

        this.setup();
        request.setPassword("STRONGPASSWORD8!");
        tests.add("Le mot de passe doit contenir à la fois des majuscules et des minuscules.");
        this.checkResponse(this.authenticationService.register( this.request, new RegisterResponse()), tests);
        tests.clear();

        this.setup();
        request.setPassword("strongPassword!");
        tests.add("Le mot de passe doit contenir au moins un chiffre");
        this.checkResponse(this.authenticationService.register( this.request, new RegisterResponse()), tests);
        tests.clear();
    }

    @Test
    public void test_register_emailNotUnique() {
        List<String> tests = new ArrayList<>();
        this.request.setEmail("hedgardavid@studi.com");
        RegisterResponse response = this.checkResponse(
                this.authenticationService.register(request, new RegisterResponse()), tests);
        assertTrue(response.isOk());
        tests.add("L'email existe déjà.");
        this.checkResponse(this.authenticationService.register(request, new RegisterResponse()), tests);
        tests.clear();
    }

    @Test
    public void test_register_success() {
        List<String> tests = new ArrayList<>();
        RegisterResponse response = this.checkResponse(
                this.authenticationService.register(request, new RegisterResponse()), tests);
        assertTrue(response.isOk());

        UserDTO userDTO = response.getUserDTO();
        assertNotNull(userDTO);
        assertNotNull(userDTO.getUuid());
        assertTrue(userDTO.getUuid().length() == 36);
        assertEquals(this.request.getName(), userDTO.getName());
        assertEquals(this.request.getEmail(), userDTO.getEmail());
        assertFalse(userDTO.isAdim());

    }

    /**                              TEST LOGIN                          **/

    @Test
    public void test_login_NullRequest_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.authenticationService.login(null, new LoginResponse());
        });

        assertEquals("LoginRequest ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_login_NullResponse_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.authenticationService.login(new LoginRequest(), null);
        });
        assertEquals("LoginResponse ne doit pas être null", exception.getMessage());
    }

    @Test
    public void test_login_InvalidRequest() {
        List<String> tests = new ArrayList<>();

        this.setup();
        request.setEmail("");
        tests.add("L'email' est obligatoire");
        tests.add("le format d'email est invalide. ex : xxxx@xxx.xxx");
        this.checkResponse(this.authenticationService.login( this.request, new LoginResponse()), tests);
        tests.clear();

        this.setup();
        request.setEmail("dqsfqsf.fr");
        tests.add("le format d'email est invalide. ex : xxxx@xxx.xxx");
        this.checkResponse(this.authenticationService.login( this.request, new LoginResponse()), tests);
        tests.clear();

        this.setup();
        request.setEmail("dqsfqsf@.fr");
        tests.add("le format d'email est invalide. ex : xxxx@xxx.xxx");
        this.checkResponse(this.authenticationService.login( this.request, new LoginResponse()), tests);
        tests.clear();

        this.setup();
        request.setEmail("dqsfqsf@qsff");
        tests.add("le format d'email est invalide. ex : xxxx@xxx.xxx");
        this.checkResponse(this.authenticationService.login( this.request, new LoginResponse()), tests);
        tests.clear();

        this.setup();
        request.setPassword("");
        tests.add("Le mot de passe est obligatoire");
        tests.add("Le mot de passe doit avoir au moins 8 caractères.");
        tests.add("Le mot de passe doit contenir à la fois des majuscules et des minuscules.");
        tests.add("Le mot de passe doit contenir au moins un chiffre");
        tests.add("Le mot de passe doit contenir au moins un caractère spécial");
        this.checkResponse(this.authenticationService.login( this.request, new LoginResponse()), tests);
        tests.clear();

        this.setup();
        request.setPassword("StrongPassword8");
        tests.add("Le mot de passe doit contenir au moins un caractère spécial");
        this.checkResponse(this.authenticationService.login( this.request, new LoginResponse()), tests);
        tests.clear();

        this.setup();
        request.setPassword("Str!8");
        tests.add("Le mot de passe doit avoir au moins 8 caractères.");
        this.checkResponse(this.authenticationService.login( this.request, new LoginResponse()), tests);
        tests.clear();

        this.setup();
        request.setPassword("strongpassword8!");
        tests.add("Le mot de passe doit contenir à la fois des majuscules et des minuscules.");
        this.checkResponse(this.authenticationService.login( this.request, new LoginResponse()), tests);
        tests.clear();

        this.setup();
        request.setPassword("STRONGPASSWORD8!");
        tests.add("Le mot de passe doit contenir à la fois des majuscules et des minuscules.");
        this.checkResponse(this.authenticationService.login( this.request, new LoginResponse()), tests);
        tests.clear();

        this.setup();
        request.setPassword("strongPassword!");
        tests.add("Le mot de passe doit contenir au moins un chiffre");
        this.checkResponse(this.authenticationService.login( this.request, new LoginResponse()), tests);
        tests.clear();
    }

    @Test
    public void test_login_emailNotExist() {
        List<String> tests = new ArrayList<>();
        this.request.setName("dhedgar");
        this.request.setEmail("dhedgar@test.fr");
        this.request.setPassword("StrongPassword8!");
        tests.add("L'email n'existe pas.");
        LoginResponse response = this.checkResponse(
                this.authenticationService.login(request, new LoginResponse()), tests);
        tests.clear();
    }

    @Test
    public void test_login_passwordNotExist() {
        List<String> tests = new ArrayList<>();
        this.request.setName("dhedgar");
        this.request.setEmail("password@test.fr");
        this.request.setPassword("StrongPassword8!");
        RegisterResponse registerResponse = this.checkResponse(
                this.authenticationService.register(this.request, new RegisterResponse()), tests);
        tests.clear();

        assertTrue(registerResponse.isOk());
        UserDTO userDTO = registerResponse.getUserDTO();
        assertNotNull(userDTO);

        this.request.setPassword("StrongPassword9!");
        tests.add("Le mot de passe ne correspond pas.");
        LoginResponse loginResponse = this.checkResponse(
                this.authenticationService.login(this.request, new LoginResponse()), tests);
        tests.clear();

    }

    @Test
    public void test_login_success() {
        this.request.setName("dhedal");
        this.request.setEmail("login@success.fr");
        this.request.setPassword("StrongPassword8!");
        List<String> tests = new ArrayList<>();
        RegisterResponse registerResponse = this.checkResponse(
                this.authenticationService.register(request, new RegisterResponse()), tests);
        assertTrue(registerResponse.isOk());

        assertTrue(registerResponse.isOk());
        UserDTO newUserDTO = registerResponse.getUserDTO();
        assertNotNull(newUserDTO);
        assertNotNull(newUserDTO.getUuid());
        assertTrue(newUserDTO.getUuid().length() == 36);
        assertEquals(this.request.getName(), newUserDTO.getName());
        assertEquals(this.request.getEmail(), newUserDTO.getEmail());
        assertFalse(newUserDTO.isAdim());

        LoginResponse loginResponse = this.checkResponse(
                this.authenticationService.login(this.request, new LoginResponse()), tests);

        assertTrue(registerResponse.isOk());
        UserDTO loginUserDTO = registerResponse.getUserDTO();
        assertNotNull(loginUserDTO);
        assertEquals(newUserDTO.getUuid(), loginUserDTO.getUuid());
        assertEquals(newUserDTO.getName(), loginUserDTO.getName());
        assertEquals(newUserDTO.getEmail(), loginUserDTO.getEmail());
        assertEquals(newUserDTO.isAdim(), loginUserDTO.isAdim());

    }

    /**                              METHOD                         **/

    private <T extends Response> T checkResponse(T response, List<String> tests) {
        assertNotNull(response);
        List<String> messages = response.getMessages();
        System.out.println();
        messages.forEach(System.out::println);
        assertTrue(messages.isEmpty() == tests.isEmpty());

        for(String test: tests) {
            assertTrue(messages.contains(test));
        }
        return response;
    }
}
