package com.example.Notes_Managment_System.Controller;

import com.example.Notes_Managment_System.Dto.*;
import com.example.Notes_Managment_System.Model.Purchase;
import com.example.Notes_Managment_System.Services.User_Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final User_Service userService;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ReigsterResponseDto>> register(
             @RequestBody RegisterRequestDto request) {

        ReigsterResponseDto dto = userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        201, true,
                        "User registered successfully",
                        dto, null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginRSponse>> login(
            @RequestBody LoginRequestDto request,
            HttpServletResponse response
    ) {

        LoginRSponse loginResponse = userService.login(request);
        String token = loginResponse.getToken();

        int maxAge = request.isRememberMe()
                ? 30 * 24 * 60 * 60
                : -1;

        Cookie cookie = new Cookie("JWT_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // prod: true
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);

        String msg = request.isRememberMe()
                ? "Login successful (Remember Me enabled)"
                : "Login successful (Session only)";

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        msg,
                        loginResponse, // ✅ data = LoginResponse
                        null
                )
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestBody ForgotPasswordRequestDto request
    ) {

        userService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        "OTP sent to registered email",
                        null,
                        null
                )
        );
    }
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody VerifyOtpRequestDto request
    ) {

        userService.resetPassword(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        "Password changed successfully",
                        null,
                        null
                )
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            HttpServletResponse response
    ) {

        // 🔥 JWT cookie delete
        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);   // prod me true
        cookie.setPath("/");
        cookie.setMaxAge(0);       // 💥 DELETE COOKIE

        response.addCookie(cookie);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        true,
                        "Logout successful",
                        "JWT cookie deleted",
                        null
                )
        );
    }


}
