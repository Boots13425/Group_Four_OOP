package com.group4.demo.service;

import com.group4.demo.model.*;
import com.group4.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * User Registration Service - Handles user signup and account verification
 * This implements the user registration flow with matricule/email validation
 */
@Service
@Transactional
public class UserRegistrationService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Autowired
    private PendingUserRepository pendingUserRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private NotificationService notificationService;
    
    // Email pattern for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Student matricule pattern (example: STU2024001, STU2024002, etc.)
    private static final Pattern STUDENT_MATRICULE_PATTERN = Pattern.compile(
        "^STU\\d{7}$"
    );
    
    // Professor employee ID pattern (example: PROF2024001, PROF2024002, etc.)
    private static final Pattern PROFESSOR_ID_PATTERN = Pattern.compile(
        "^PROF\\d{7}$"
    );
    
    /**
     * USER REGISTRATION SCENARIO
     * This implements the complete user signup flow
     */
    public SignupResponse registerUser(com.group4.demo.controller.AuthController.SignupRequest request) {
        try {
            // Step 1: Validate input data
            String identifier = request.getMatriculeOrEmail().trim();
            
            // Step 2: Determine user type based on identifier
            UserType userType = determineUserType(identifier, request.getUserType());
            
            // Step 3: Validate identifier format
            if (!validateIdentifierFormat(identifier, userType)) {
                return new SignupResponse(false, "Invalid identifier format for " + userType.name().toLowerCase(), null);
            }
            
            // Step 4: Check if identifier already exists
            if (!checkIdentifierAvailability(identifier)) {
                return new SignupResponse(false, "This identifier is already registered", null);
            }
            
            // Step 5: Validate password strength
            if (!validatePasswordStrength(request.getPassword())) {
                return new SignupResponse(false, 
                    "Password must be at least 8 characters long and contain at least one letter, one number, and one special character", null);
            }
            
            // Step 6: Generate username from identifier
            String username = generateUsername(identifier, userType);
            
            // Step 7: Generate verification code
            String verificationCode = generateVerificationCode();
            
            // Step 8: Create pending user record
            PendingUser pendingUser = new PendingUser(
                username,
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                isEmail(identifier) ? identifier : generateEmailFromIdentifier(identifier),
                identifier,
                userType,
                verificationCode
            );
            
            if (request.getPhoneNumber() != null) {
                pendingUser.setPhoneNumber(request.getPhoneNumber());
            }
            
            pendingUserRepository.save(pendingUser);
            
            // Step 9: Send verification email
            sendVerificationEmail(pendingUser);
            
            return new SignupResponse(true, 
                "Registration successful! Please check your email for verification code.", 
                pendingUser.getId());
            
        } catch (Exception e) {
            return new SignupResponse(false, "Registration failed: " + e.getMessage(), null);
        }
    }
    
    /**
     * ACCOUNT VERIFICATION SCENARIO
     * This verifies the user account and creates the actual user record
     */
    public VerificationResponse verifyAccount(String identifier, String verificationCode) {
        try {
            // Step 1: Find pending user
            Optional<PendingUser> pendingUserOpt = pendingUserRepository
                .findByIdentifierAndVerificationCode(identifier, verificationCode);
            
            if (pendingUserOpt.isEmpty()) {
                return new VerificationResponse(false, "Invalid verification code or identifier");
            }
            
            PendingUser pendingUser = pendingUserOpt.get();
            
            // Step 2: Check if verification code has expired (24 hours)
            if (pendingUser.getCreatedAt().isBefore(LocalDateTime.now().minusHours(24))) {
                pendingUserRepository.delete(pendingUser);
                return new VerificationResponse(false, "Verification code has expired. Please register again.");
            }
            
            // Step 3: Create actual user account based on user type
            User newUser = createUserAccount(pendingUser);
            
            // Step 4: Save the user
            userRepository.save(newUser);
            
            // Step 5: Delete pending user record
            pendingUserRepository.delete(pendingUser);
            
            // Step 6: Send welcome email
            sendWelcomeEmail(newUser);
            
            return new VerificationResponse(true, "Account verified successfully! You can now login.");
            
        } catch (Exception e) {
            return new VerificationResponse(false, "Verification failed: " + e.getMessage());
        }
    }
    
    /**
     * RESEND VERIFICATION CODE SCENARIO
     */
    public ResendResponse resendVerificationCode(String identifier) {
        try {
            Optional<PendingUser> pendingUserOpt = pendingUserRepository.findByIdentifier(identifier);
            
            if (pendingUserOpt.isEmpty()) {
                return new ResendResponse(false, "No pending registration found for this identifier");
            }
            
            PendingUser pendingUser = pendingUserOpt.get();
            
            // Generate new verification code
            String newVerificationCode = generateVerificationCode();
            pendingUser.setVerificationCode(newVerificationCode);
            pendingUser.setCreatedAt(LocalDateTime.now()); // Reset expiration time
            
            pendingUserRepository.save(pendingUser);
            
            // Send new verification email
            sendVerificationEmail(pendingUser);
            
            return new ResendResponse(true, "New verification code sent to your email");
            
        } catch (Exception e) {
            return new ResendResponse(false, "Failed to resend verification code: " + e.getMessage());
        }
    }
    
    /**
     * CHECK IDENTIFIER AVAILABILITY
     */
    public boolean checkIdentifierAvailability(String identifier) {
        // Check in existing users
        if (isEmail(identifier)) {
            if (userRepository.existsByEmail(identifier)) {
                return false;
            }
        } else {
            // Check student matricule
            if (STUDENT_MATRICULE_PATTERN.matcher(identifier).matches()) {
                if (studentRepository.existsByStudentId(identifier)) {
                    return false;
                }
            }
            // Check professor employee ID
            if (PROFESSOR_ID_PATTERN.matcher(identifier).matches()) {
                if (professorRepository.existsByEmployeeId(identifier)) {
                    return false;
                }
            }
        }
        
        // Check in pending users
        return !pendingUserRepository.existsByIdentifier(identifier);
    }
    
    /**
     * Helper methods
     */
    private UserType determineUserType(String identifier, String requestedType) {
        // If user explicitly requested a type, use it (with validation)
        if (requestedType != null) {
            try {
                return UserType.valueOf(requestedType.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Fall through to automatic detection
            }
        }
        
        // Auto-detect based on identifier pattern
        if (STUDENT_MATRICULE_PATTERN.matcher(identifier).matches()) {
            return UserType.STUDENT;
        } else if (PROFESSOR_ID_PATTERN.matcher(identifier).matches()) {
            return UserType.PROFESSOR;
        } else if (isEmail(identifier)) {
            // Default to student for email registrations
            return UserType.STUDENT;
        }
        
        // Default to student
        return UserType.STUDENT;
    }
    
    private boolean validateIdentifierFormat(String identifier, UserType userType) {
        if (isEmail(identifier)) {
            return EMAIL_PATTERN.matcher(identifier).matches();
        }
        
        switch (userType) {
            case STUDENT:
                return STUDENT_MATRICULE_PATTERN.matcher(identifier).matches();
            case PROFESSOR:
                return PROFESSOR_ID_PATTERN.matcher(identifier).matches();
            default:
                return true; // Allow other formats for flexibility
        }
    }
    
    private boolean validatePasswordStrength(String password) {
        if (password.length() < 8) {
            return false;
        }
        
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        
        return hasLetter && hasNumber && hasSpecial;
    }
    
    private String generateUsername(String identifier, UserType userType) {
        if (isEmail(identifier)) {
            // Extract username part from email
            return identifier.substring(0, identifier.indexOf('@'));
        } else {
            // Use the identifier as username
            return identifier.toLowerCase();
        }
    }
    
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    
    private boolean isEmail(String identifier) {
        return identifier.contains("@");
    }
    
    private String generateEmailFromIdentifier(String identifier) {
        // Generate a default email for non-email identifiers
        return identifier.toLowerCase() + "@university.edu";
    }
    
    private User createUserAccount(PendingUser pendingUser) {
        switch (pendingUser.getUserType()) {
            case STUDENT:
                Student student = new Student(
                    pendingUser.getUsername(),
                    pendingUser.getPassword(), // Already encoded
                    pendingUser.getName(),
                    pendingUser.getEmail(),
                    pendingUser.getIdentifier()

                );
                return student;
                
            case PROFESSOR:
                Professor professor = new Professor(
                    pendingUser.getUsername(),
                    pendingUser.getPassword(), // Already encoded
                    pendingUser.getName(),
                    pendingUser.getEmail(),
                    pendingUser.getIdentifier(),
                    "General" // Default department, can be updated later
                );
                return professor;
                
            default:
                throw new RuntimeException("Unsupported user type: " + pendingUser.getUserType());
        }
    }
    
    private void sendVerificationEmail(PendingUser pendingUser) {
        try {
            String subject = "Verify Your University Account";
            String message = String.format(
                "Dear %s,\n\n" +
                "Thank you for registering with the University Grade Management System.\n\n" +
                "Your verification code is: %s\n\n" +
                "Please use this code to verify your account within 24 hours.\n" +
                "If you didn't register for this account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "University Grade Management System",
                pendingUser.getName(),
                pendingUser.getVerificationCode()
            );
            
            // Use the notification service to send email
            notificationService.sendVerificationEmail(pendingUser.getEmail(), subject, message);
            
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }



    private void sendWelcomeEmail(User user) {
        try {
            String subject = "Welcome to University Grade Management System";
            String message = String.format(
                "Dear %s,\n\n" +
                "Welcome to the University Grade Management System!\n\n" +
                "Your account has been successfully created with the following details:\n" +
                "Username: %s\n" +
                "Role: %s\n\n" +
                "You can now login to access the system.\n\n" +
                "Best regards,\n" +
                "University Grade Management System",
                user.getUsername(),
                user.getUsername(),
                user.getRole()
            );
            
            notificationService.sendWelcomeEmail(user.getEmail(), subject, message);
            
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }
    
    /**
     * Response classes
     */
    public static class SignupResponse {
        private boolean success;
        private String message;
        private Long pendingUserId;
        
        public SignupResponse(boolean success, String message, Long pendingUserId) {
            this.success = success;
            this.message = message;
            this.pendingUserId = pendingUserId;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Long getPendingUserId() { return pendingUserId; }
    }
    
    public static class VerificationResponse {
        private boolean success;
        private String message;
        
        public VerificationResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    public static class ResendResponse {
        private boolean success;
        private String message;
        
        public ResendResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    /**
     * Enum for user types during registration
     */
    public enum UserType {
        STUDENT,
        PROFESSOR
    }
}
