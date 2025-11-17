package org.example.usuarios.service.implementacion;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.usuarios.DTOS.NotificationException;
import org.example.usuarios.DTOS.UserDTOPost;
import org.example.usuarios.DTOS.UsuarioYaExistenteException;
import org.example.usuarios.client.NotificationClient;
import org.example.usuarios.config.JwtUtil;
import org.example.usuarios.domain.User;
import org.example.usuarios.entity.UserEntity;
import org.example.usuarios.repository.UserRepository;
import org.example.usuarios.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private EmailServiceImpl emailService;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${app.verification.base-url}")
    private String baseUrl;

    @Override
    public String login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!user.isVerified()) {
            throw new RuntimeException("La cuenta no ha sido verificada. Por favor, revisa tu email.");
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(email);
        }

        throw new RuntimeException("Credenciales invalidas");
    }

    @Transactional
    @Override
    public UserEntity registerUser(UserDTOPost user) {

        boolean isCaptchaValid = verifyRecaptcha(user.getCaptchaResponse());

        if (!isCaptchaValid) {
            throw new IllegalArgumentException("Captcha inválido");
        }

        Optional<UserEntity> userEntity = userRepository.findByEmail(user.getEmail());

        if (userEntity.isPresent()) {
            if (userEntity.get().isActivo()) {
                throw new UsuarioYaExistenteException("El usuario con email " + user.getEmail() + " ya existe", "USER_EXISTS");
            } else {
                throw new UsuarioYaExistenteException("El usuario está dado de baja.", "USER_INACTIVE");
            }
        }

        UserEntity entity = new UserEntity();
        entity.setName(user.getName());
        entity.setRol(user.getRol());
        entity.setLastname(user.getLastname());
        entity.setUsername("");
        entity.setEmail(user.getEmail());
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        entity.setNumberphone("");
        entity.setActivo(true);
        entity.setFechaBaja(null);
        entity.setAcceptTerms(true);

        String token = UUID.randomUUID().toString();
        entity.setVerificationToken(token);
        entity.setVerified(false);

        // guardar usuario
        UserEntity savedUser = userRepository.save(entity);

        String verificationLink = "http://localhost:8081/auth/verify?token=" + token;

        // enviar email
        try {
            notificationClient.notifyVerificationEmail(savedUser.getEmail(), savedUser.getId(), verificationLink);
        } catch (Exception e) {
            throw new NotificationException("Error al enviar el correo de verificación", "NOTIF_FAILED");
        }

        return savedUser;
    }

    @Override
    public UserEntity activarCuenta(String email) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);

        if (userEntity.isEmpty()) {
            throw new RuntimeException("No existe el usuario");
        }

        if (userEntity.get().isActivo()) {
            throw new RuntimeException("El usuario ya esta activo");
        }

        String token = UUID.randomUUID().toString();
        userEntity.get().setVerificationToken(token);
        userRepository.save(userEntity.get());

        String verificationLink = baseUrl + "/auth/verify?token=" + token;
        notificationClient.notifyVerificationEmail(userEntity.get().getEmail(), userEntity.get().getId(), verificationLink);

        return userEntity.get();
    }

    protected boolean verifyRecaptcha(String recaptchaResponse) {
        String secretKey = recaptchaSecret;
        String url = "https://www.google.com/recaptcha/api/siteverify";
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", recaptchaResponse);

        ResponseEntity<String> response = restTemplate.postForEntity(url, params, String.class);

        if (response.getBody().contains("\"success\": true")) {
            return true;
        }
        return false;
    }

    @Override
    public void requestPasswordReset(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        userRepository.save(user);

        String resetLink = "http://localhost:4200/restablecer-password?token=" + token;

        try {
            notificationClient.notifyResetPasswordEmail(user.getEmail(), user.getId(), resetLink);
        } catch (Exception e) {
            throw new NotificationException("Error al enviar el correo de recuperación", "RESET_EMAIL_FAILED");
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        UserEntity user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerificationToken(null);

        userRepository.save(user);
    }
}
