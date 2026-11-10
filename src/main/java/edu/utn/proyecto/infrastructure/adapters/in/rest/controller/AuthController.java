package edu.utn.proyecto.infrastructure.adapters.in.rest.controller;

import edu.utn.proyecto.applicacion.usecase.auth.GetCurrentUserUseCase;
import edu.utn.proyecto.applicacion.usecase.auth.LoginUseCase;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.LoginRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.SessionUserDTO;
import edu.utn.proyecto.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Login, logout y sesión activa. Usa cookie HttpOnly FM_TOKEN.")
@RestController
@RequestMapping({"/api/v1/auth", "/api/auth"})
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final TokenService tokenService;

    public AuthController(LoginUseCase loginUseCase,
                          GetCurrentUserUseCase getCurrentUserUseCase,
                          TokenService tokenService) {
        this.loginUseCase = loginUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.tokenService = tokenService;
    }

    @Operation(summary = "Iniciar sesión", description = "Valida DNI + email contra RENAPER y emite cookie JWT FM_TOKEN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "429", description = "Demasiados intentos (5 por minuto)")
    })
    @PostMapping("/login")
    public ResponseEntity<SessionUserDTO> doLogin(@RequestBody LoginRequestDTO req,
                                                  HttpServletResponse response) {
        SessionUserDTO user = loginUseCase.execute(req);
        String jwt = tokenService.generate(
                user.getId().toString(),
                user.getDni(),
                user.getEmail(),
                user.getNombre(),
                user.getRol()
        );
        tokenService.writeCookie(response, jwt);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Sesión activa 🔒", description = "Devuelve los datos del usuario autenticado a partir de la cookie JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario autenticado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<SessionUserDTO> me(HttpServletRequest request) {
        SessionUserDTO user = getCurrentUserUseCase.execute(request);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Cerrar sesión", description = "Invalida la cookie JWT FM_TOKEN.")
    @ApiResponse(responseCode = "204", description = "Sesión cerrada")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        tokenService.clearCookie(response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}