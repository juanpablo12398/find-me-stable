package edu.utn.proyecto.infrastructure.adapters.in.rest.controller;

import edu.utn.proyecto.applicacion.dtos.DesaparecidoResponseDTO;
import edu.utn.proyecto.applicacion.usecase.desaparecido.CreateDesaparecidoUseCase;
import edu.utn.proyecto.applicacion.usecase.desaparecido.ReadDesaparecidoUseCase;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoFrontDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.DesaparecidoRequestDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.PagedResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Desaparecidos", description = "Registro y consulta de personas desaparecidas.")
@RestController
@RequestMapping({"/api/v1/desaparecidos", "/api/desaparecidos"})
public class DesaparecidoController {

    private final CreateDesaparecidoUseCase crearDesaparecidoUseCase;
    private final ReadDesaparecidoUseCase readDesaparecidoUseCase;

    public DesaparecidoController(CreateDesaparecidoUseCase crearDesaparecidoUseCase,
            ReadDesaparecidoUseCase readDesaparecidoUseCase) {
        this.crearDesaparecidoUseCase = crearDesaparecidoUseCase;
        this.readDesaparecidoUseCase = readDesaparecidoUseCase;
    }

    @Operation(summary = "Registrar desaparecido 🔒", description = "Registra una persona desaparecida. Requiere sesión activa (USUARIO o ADMINISTRADOR). Protegido por SecurityConfig: POST /api/desaparecidos requiere authentication().")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Desaparecido registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DesaparecidoResponseDTO> crear(@RequestBody DesaparecidoRequestDTO requestDto) {
        DesaparecidoResponseDTO dto = crearDesaparecidoUseCase.execute(requestDto);
        URI location = URI.create("/api/v1/desaparecidos/" + dto.getId());
        return ResponseEntity.created(location).body(dto);
    }

    @Operation(summary = "Listar desaparecidos", description = "Devuelve lista paginada con filtros opcionales de búsqueda, estado y orden.")
    @ApiResponse(responseCode = "200", description = "Lista paginada")
    @GetMapping
    public ResponseEntity<PagedResponseDTO<DesaparecidoFrontDTO>> getAll(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "estado", required = false) String estado,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        
        PagedResponseDTO<DesaparecidoFrontDTO> paged = readDesaparecidoUseCase.execute(search, estado, sort, page, size);
        return ResponseEntity.ok(paged);
    }

    @Operation(summary = "Estadísticas generales", description = "Total de desaparecidos por estado. Acceso público.")
    @ApiResponse(responseCode = "200", description = "Estadísticas")
    @GetMapping("/estadisticas")
    public ResponseEntity<edu.utn.proyecto.applicacion.dtos.DesaparecidoEstadisticasDTO> getEstadisticas() {
        return ResponseEntity.ok(readDesaparecidoUseCase.getEstadisticas());
    }
}
