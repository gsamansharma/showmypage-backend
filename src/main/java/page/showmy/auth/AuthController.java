package page.showmy.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import page.showmy.auth.dto.*;
import page.showmy.model.AuthProvider;
import page.showmy.model.User;
import page.showmy.repository.UserRepository;
import page.showmy.security.JwtUtil;
import page.showmy.security.UserDetailsServiceImpl;
import page.showmy.service.EmailService;

import java.security.Principal;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final EmailService emailService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setAuthProvider(AuthProvider.local);
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        String verificationToken = jwtUtil.generateResetToken(user.getUsername());
        Date expirationDate = jwtUtil.extractExpiration(verificationToken);
        user.setResetToken(verificationToken);
        user.setResetTokenExpiryDate(expirationDate);
        user.setIsEmailVerified(false);
        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        return ResponseEntity.ok(Map.of("message","User registered successfully! Check your email for a verification link!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticateToken(@RequestBody LoginRequest loginRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        }
        catch (Exception e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("token", jwt));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailRequest verifyEmailRequest) {
        String username;
        Date tokenExpiryDate;

        try{
            username = jwtUtil.extractUsername(verifyEmailRequest.getToken());
            tokenExpiryDate = jwtUtil.extractExpiration(verifyEmailRequest.getToken());
        } catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid verification token"));
        }
        if(tokenExpiryDate.before(new Date())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Token expired."));
        }

        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found!"));
        }
        User user = userOptional.get();

        if(Boolean.TRUE.equals(user.getIsEmailVerified())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email is already verified!"));
        }

        if(user.getResetToken() == null || !user.getResetToken().equals(verifyEmailRequest.getToken())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid or previously used verification token"));
        }
        user.setIsEmailVerified(true);
        user.setResetToken(null);
        user.setResetTokenExpiryDate(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Email successfully verified."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        Optional<User> userOptional = userRepository.findByEmail(forgotPasswordRequest.getEmail());

        if(userOptional.isEmpty())
            return ResponseEntity.ok(Map.of("message", "If an account with that email exists, a password reset link has been sent."));

        User user = userOptional.get();
        String resetToken = jwtUtil.generateResetToken(user.getUsername());
        Date expiryDate = jwtUtil.extractExpiration(resetToken);

        user.setResetToken(resetToken);
        user.setResetTokenExpiryDate(expiryDate);
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

        return ResponseEntity.ok(Map.of("message", "If an account with that email exists, a password reset link has been sent." ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        String username;
        Date tokenExpiry;

        try{
            username = jwtUtil.extractUsername(resetPasswordRequest.getToken());
            tokenExpiry = jwtUtil.extractExpiration(resetPasswordRequest.getToken());
        } catch (Exception e){
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or malformed reset token. "));

        }
        if(tokenExpiry.before(new Date())){
            return ResponseEntity.badRequest().body(Map.of("error", "Reset token has expired."));
        }

        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        User user = userOptional.get();

        if(user.getResetToken() == null || !user.getResetToken().equals(resetPasswordRequest.getToken())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid or previously used reset Token."));
        }

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiryDate(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password has been successfully reset."));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateUser(Principal principal) {
        if(principal == null) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "valid",false,
                            "message","No active user session"));
        }
        return ResponseEntity.ok(
                Map.of(
                        "valid",true,
                        "username",principal.getName(),
                        "message","Token is valid"));
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        if(userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.ok(Map.of("available",false));
        }
        return ResponseEntity.ok(Map.of("available",true));
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        if(userRepository.findByEmail(email).isPresent()){
            return ResponseEntity.ok(Map.of("available",false));
        }
        return ResponseEntity.ok(Map.of("available",true));
    }

    @PostMapping("/oauth/initiate")
    public ResponseEntity<?> initiateOAuthRegistration(@RequestParam String username, HttpServletRequest request) {
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is already taken!"));
        }
        request.getSession().setAttribute("OAUTH2_USERNAME", username);
        return ResponseEntity.ok(Map.of("message", "Username saved. Proceed to OAuth2 login."));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2LoginSuccess(OAuth2AuthenticationToken authentication) {
        OAuth2User oAuth2User = authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
