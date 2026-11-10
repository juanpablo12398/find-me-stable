package edu.utn.proyecto.infrastructure.adapters.in.rest.controller;

import edu.utn.proyecto.applicacion.dtos.AvistamientoResponseDTO;
import edu.utn.proyecto.applicacion.usecase.avistamiento.CreateAvistamientoUseCase;
import edu.utn.proyecto.applicacion.usecase.avistamiento.ReadAvistamientoGeoUseCase;
import edu.utn.proyecto.applicacion.usecase.avistamiento.ReadAvistamientoUseCase;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoFrontDTO;
import edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.AvistamientoRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Avistamientos", description = "Reportes geolocalizados de personas desaparecidas. Incluye búsqueda por radio, área, polígono WKT y vecinos más cercanos.")
@RestController
@RequestMapping({"/api/v1/avistamientos", "/api/avistamientos"})
public class AvistamientoController {

    private final CreateAvistamientoUseCase createUseCase;
    private final ReadAvistamientoUseCase readUseCase;
    private final ReadAvistamientoGeoUseCase readGeoUseCase;

    public AvistamientoController(
            CreateAvistamientoUseCase createUseCase,
            ReadAvistamientoUseCase readUseCase,
            ReadAvistamientoGeoUseCase readGeoUseCase) {
        this.createUseCase = createUseCase;
        this.readUseCase = readUseCase;
        this.readGeoUseCase = readGeoUseCase;
    }

    @Operation(summary = "Reportar avistamiento 🔒", description = "Registra un avistamiento con coordenadas lat/lng. Requiere sesión activa. Protegido por SecurityConfig: POST /api/avistamientos requiere authentication().")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Avistamiento registrado"),
            @ApiResponse(responseCode = "400", description = "Coordenadas inválidas o desaparecido inexistente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Rol insuficiente")
    })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AvistamientoResponseDTO> crear(
            @Valid @RequestBody AvistamientoRequestDTO request) {

        AvistamientoResponseDTO dto = createUseCase.execute(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();

        return ResponseEntity.created(location).body(dto);
    }

    @Operation(summary = "Todos para el mapa", description = "Devuelve avistamientos visibles según el rol: ADMIN ve todos, USUARIO ve los suyos + públicos, anónimo solo públicos.")
    @ApiResponse(responseCode = "200", description = "Lista de avistamientos enriquecidos")
    @GetMapping("/mapa")
    public ResponseEntity<List<AvistamientoFrontDTO>> obtenerParaMapa() {
        return ResponseEntity.ok(readGeoUseCase.paraMapa());
    }

    @Operation(summary = "Búsqueda por bounding box", description = "Avistamientos dentro de un rectángulo definido por latMin, latMax, lngMin, lngMax. Usa ST_Intersects.")
    @ApiResponse(responseCode = "200", description = "Lista de avistamientos en el área")
    @GetMapping("/mapa/area")
    public ResponseEntity<List<AvistamientoFrontDTO>> obtenerEnArea(
            @RequestParam Double latMin,
            @RequestParam Double latMax,
            @RequestParam Double lngMin,
            @RequestParam Double lngMax) {
        return ResponseEntity.ok(readGeoUseCase.enArea(latMin, latMax, lngMin, lngMax));
    }

    @GetMapping("/desaparecido/{desaparecidoId}")
    public ResponseEntity<List<AvistamientoFrontDTO>> obtenerPorDesaparecido(
            @PathVariable UUID desaparecidoId) {
        return ResponseEntity.ok(readGeoUseCase.porDesaparecido(desaparecidoId));
    }

    @GetMapping("/desaparecido/{desaparecidoId}/ultimo")
    public ResponseEntity<List<AvistamientoFrontDTO>> obtenerUltimaUbicacion(
            @PathVariable UUID desaparecidoId) {
        return ResponseEntity.ok(readGeoUseCase.ultimaUbicacion(desaparecidoId));
    }

    @GetMapping
    public ResponseEntity<edu.utn.proyecto.infrastructure.adapters.in.rest.dtos.PagedResponseDTO<AvistamientoResponseDTO>> obtenerTodos(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "50") Integer size) {
        return ResponseEntity.ok(readUseCase.obtenerTodosPaginado(page, size));
    }

    @Operation(summary = "Búsqueda por radio", description = "Avistamientos dentro de un radio en km desde un punto (lat, lng). Usa ST_DWithin con índice GIST.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de avistamientos en el radio"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @GetMapping("/radio")
    public ResponseEntity<List<AvistamientoFrontDTO>> obtenerEnRadio(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam Double radioKm) {
        return ResponseEntity.ok(readGeoUseCase.enRadio(lat, lng, radioKm));
    }

    @Operation(summary = "Vecinos más cercanos", description = "Devuelve los N avistamientos más cercanos a un punto (lat, lng). Usa operador <-> de PostGIS.")
    @ApiResponse(responseCode = "200", description = "Lista de avistamientos ordenados por distancia")
    @GetMapping("/cercanos")
    public ResponseEntity<List<AvistamientoFrontDTO>> obtenerMasCercanos(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(readGeoUseCase.masCercanos(lat, lng, limite));
    }

    @Operation(
            summary = "Búsqueda por polígono WKT",
            description = """
                    Avistamientos dentro de un polígono arbitrario expresado en WKT (Well-Known Text).
                    Usa ST_Contains con índice GIST.

                    **Ejemplo de parámetro wkt:**
                    ```
                    POLYGON((-58.5 -34.5, -58.3 -34.5, -58.3 -34.7, -58.5 -34.7, -58.5 -34.5))
                    ```
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de avistamientos en el polígono"),
            @ApiResponse(responseCode = "400", description = "WKT inválido o malformado")
    })
    @PostMapping("/poligono")
    public ResponseEntity<List<AvistamientoFrontDTO>> obtenerEnPoligono(
            @Parameter(description = "Polígono en formato WKT. Ej: POLYGON((-58.5 -34.5, ...))")
            @RequestParam String wkt) {
        return ResponseEntity.ok(readGeoUseCase.enPoligono(wkt));
    }

    @GetMapping("/desaparecido/{desaparecidoId}/radio")
    public ResponseEntity<List<AvistamientoFrontDTO>> obtenerPorDesaparecidoEnRadio(
            @PathVariable UUID desaparecidoId,
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam Double radioKm) {
        return ResponseEntity.ok(readGeoUseCase.porDesaparecidoEnRadio(desaparecidoId, lat, lng, radioKm));
    }

    @GetMapping("/area/count")
    public ResponseEntity<Long> contarEnArea(
            @RequestParam Double latMin,
            @RequestParam Double latMax,
            @RequestParam Double lngMin,
            @RequestParam Double lngMax) {
        return ResponseEntity.ok(readGeoUseCase.contarEnArea(latMin, latMax, lngMin, lngMax));
    }
}