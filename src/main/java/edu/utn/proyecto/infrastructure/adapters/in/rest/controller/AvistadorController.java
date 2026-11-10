package edu.utn.proyecto.infrastructure.adapters.in.rest.controller;

import edu.utn.proyecto.applicacion.dtos.AvistadorResponseDTO;
import edu.utn.proyecto.applicacion.usecase.avistador.CreateAvistadorUseCase;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistadorRequestDTO;
import edu.utn.proyecto.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Avistadores", description = "Registro de usuarios. Máximo 3 registros por IP por hora.")
@RestController
@RequestMapping({"/api/v1/avistadores", "/api/avistadores"})
public class AvistadorController {

    private final CreateAvistadorUseCase createAvistadorUseCase;
    private final TokenService tokenService;

    public AvistadorController(CreateAvistadorUseCase createAvistadorUseCase,
                               TokenService tokenService) {
        this.createAvistadorUseCase = createAvistadorUseCase;
        this.tokenService = tokenService;
    }

    @Operation(summary = "Registrar avistador", description = "Crea una cuenta nueva. Valida DNI contra RENAPER simulado. DNI cifrado con AES-256-GCM + blind index HMAC.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Avistador creado y sesión iniciada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o DNI ya registrado"),
            @ApiResponse(responseCode = "429", description = "Demasiados registros (3 por hora)")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AvistadorResponseDTO> registrar(@Valid @RequestBody AvistadorRequestDTO request,
                                                          HttpServletResponse response) {
        AvistadorResponseDTO dto = createAvistadorUseCase.execute(request);

        String jwt = tokenService.generate(
                dto.getId().toString(),
                dto.getDni(),
                dto.getEmail() != null ? dto.getEmail() : "",
                dto.getNombre(),
                dto.getRol()
        );
        tokenService.writeCookie(response, jwt);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(location).body(dto);
    }
}