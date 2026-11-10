    # Documentación Técnica Completa — Find Me
**Sistema de Reporte y Seguimiento de Personas Desaparecidas**

**Autor:** Juan  
**Proyecto:** Trabajo final académico — UTN  
**Versión del documento:** 1.1.0  
**Ultima actualizacion:** marzo 2026

---

## Tabla de contenidos

1. [Resumen ejecutivo](#1-resumen-ejecutivo)
2. [Contexto y alcance del proyecto](#2-contexto-y-alcance-del-proyecto)
3. [Requisitos del sistema](#3-requisitos-del-sistema)
4. [Metodología de desarrollo](#4-metodología-de-desarrollo)
5. [Arquitectura del sistema](#5-arquitectura-del-sistema)
6. [Stack tecnológico](#6-stack-tecnológico)
7. [Modelo de dominio](#7-modelo-de-dominio)
8. [Diseño de capas](#8-diseño-de-capas)
9. [Sistema de validación](#9-sistema-de-validación)
10. [Seguridad](#10-seguridad)
11. [Base de datos y geolocalización](#11-base-de-datos-y-geolocalización)
12. [API REST](#12-api-rest)
13. [Frontend](#13-frontend)
14. [Estrategia de testing](#14-estrategia-de-testing)
15. [Despliegue](#15-despliegue)
16. [Conclusiones](#16-conclusiones)
17. [Trabajo futuro](#17-trabajo-futuro)
18. [Anexo A — Decisiones técnicas y limitaciones](#18-anexo-a--decisiones-técnicas-y-limitaciones)
19. [Referencias bibliográficas](#19-referencias-bibliográficas)

---

## 1. Resumen ejecutivo

### 1.1 Descripción del proyecto

**Find Me** es una plataforma web para el registro, búsqueda y seguimiento de personas desaparecidas en Argentina.  
El sistema permite a usuarios autenticados reportar desapariciones y avistamientos georreferenciados, facilitando la coordinación de esfuerzos durante la ventana temporal crítica (24—48 horas posteriores a la desaparición).

El proyecto se concibe como un prototipo funcional con énfasis en:

- Arquitectura limpia y mantenible.
- Integración geoespacial avanzada (PostGIS).
- Validación de identidad contra un padrón nacional simulado.
- Estrategia de validación de negocio extensible.
- Alto nivel de cobertura de pruebas en componentes críticos.

### 1.2 Objetivo general

Desarrollar un sistema web que permita centralizar información de personas desaparecidas, registrar avistamientos georreferenciados y ofrecer herramientas de búsqueda espacial, bajo una arquitectura escalable y alineada con buenas prácticas de ingeniería de software.

### 1.3 Objetivos específicos

Los objetivos específicos del sistema son:

1. Centralizar la información sobre personas desaparecidas en una única plataforma.
2. Facilitar el reporte ciudadano de avistamientos georreferenciados.
3. Proporcionar búsquedas espaciales avanzadas (radio, área, polígono, vecinos más cercanos).
4. Integrar un mecanismo de validación de identidades contra un padrón nacional simulado (RENAPER).
5. Garantizar la seguridad y confidencialidad de los datos tratados.
6. Aplicar principios SOLID y patrones de arquitectura moderna (Clean Architecture / ports & adapters).
7. Implementar una estrategia de pruebas automatizadas con alta cobertura en los componentes críticos del dominio.

### 1.4 Características destacadas

| Característica                | Implementación                                                                 |
|------------------------------|-------------------------------------------------------------------------------|
| Geolocalización avanzada     | PostgreSQL + PostGIS, consultas espaciales optimizadas con índices GIST      |
| Validación de identidad      | Integración con padrón RENAPER simulado                                      |
| Autenticación                | JWT almacenado en cookies HttpOnly, con expiración de 7 días                 |
| Arquitectura                 | Clean Architecture, orientación a puertos y adaptadores, principios SOLID    |
| Internacionalización         | Soporte de idiomas ES, EN y PT                                               |
| Estrategia de validación     | Patrón Chain of Responsibility con Rules y Policies                          |
| Errores estandarizados       | Respuestas de error bajo formato RFC 7807 (Problem Details)                  |
| Testing                      | Cobertura elevada en componentes críticos del dominio                        |

### 1.5 Métricas del proyecto

- Líneas de código backend: ~8.500
- Líneas de código frontend: ~3.200
- Clases testeadas: más de 25
- Cobertura en componentes críticos del dominio (mappers, use cases, policies, normalizadores): ~100 %
- Cobertura global aproximada del backend: ~85 %
- Entidades de dominio principales: 3 (Desaparecido, Avistador, Avistamiento)
- Endpoints de la API: más de 15

---

## 2. Contexto y alcance del proyecto

### 2.1 Contexto

La problemática de las personas desaparecidas requiere herramientas tecnológicas que permitan:

- Centralizar información dispersa.
- Registrar reportes ciudadanos de manera estructurada.
- Visualizar avistamientos en el espacio geográfico.
- Facilitar la colaboración entre distintos actores (familiares, ciudadanía, instituciones).

En este contexto, **Find Me** surge como prototipo académico orientado a demostrar la viabilidad técnica de un sistema de este tipo, utilizando tecnologías modernas de backend, frontend y geolocalización.

### 2.2 Alcance funcional

El alcance funcional del proyecto incluye:

- Registro de personas desaparecidas.
- Registro de usuarios avistadores (ciudadanos).
- Registro de avistamientos georreferenciados.
- Visualización de avistamientos sobre un mapa interactivo.
- Búsquedas espaciales por radio, área rectangular, polígono y vecinos más cercanos.
- Validación de identidades contra un padrón nacional simulado.
- Autenticación basada en JWT (sin uso de contraseñas).

No se incluyen en esta versión:

- Integración con bases oficiales reales (por ejemplo SIFEBU).
- Módulos de moderación automática mediante ML (Machine Learning para análisis predictivo o detección de contenido anómalo).
- Notificaciones en tiempo real o aplicaciones móviles nativas.

---

## 3. Requisitos del sistema

### 3.1 Requisitos funcionales (RF)

**RF1. Registro de personas desaparecidas**  
El sistema debe permitir registrar personas desaparecidas con nombre, apellido, DNI, fecha de desaparición, descripción y foto opcional.

**RF2. Registro de avistadores**  
El sistema debe permitir registrar usuarios avistadores validando su identidad contra el padrón RENAPER simulado.

**RF3. Registro de avistamientos**  
El sistema debe permitir registrar avistamientos georreferenciados vinculados a una persona desaparecida y a un avistador.

**RF4. Búsqueda y listado de personas desaparecidas**  
El sistema debe permitir listar y consultar el detalle de personas desaparecidas.

**RF5. Visualización de avistamientos en mapa**  
El sistema debe permitir visualizar avistamientos públicos sobre un mapa interactivo.

**RF6. Búsqueda espacial por radio**  
El sistema debe permitir recuperar avistamientos dentro de un radio determinado a partir de una coordenada.

**RF7. Búsqueda espacial por área y polígono**  
El sistema debe permitir recuperar avistamientos dentro de un rectángulo bounding box y dentro de un polígono arbitrario (WKT).

**RF8. Búsqueda por persona**  
El sistema debe permitir recuperar avistamientos asociados a una persona desaparecida concreta.

**RF9. Autenticación y sesión**  
El sistema debe permitir a un usuario avistador iniciar sesión y mantener un estado autenticado mediante token JWT en cookie HttpOnly.

### 3.2 Requisitos no funcionales (RNF)

**RNF1. Arquitectura**  
El sistema debe seguir una arquitectura limpia (Clean Architecture), aislando el dominio de los frameworks y de los detalles de infraestructura.

**RNF2. Mantenibilidad**  
El código debe mantener una complejidad ciclomática promedio por método menor a 8 y asegurar un porcentaje de test unitarios superior al 80% en los módulos de dominio y casos de uso.

**RNF3. Performance geoespacial**  
Las consultas que involucren procesamiento espacial deben ejecutarse utilizando PostGIS e índices GIST, respondiendo en menos de 500 ms para búsquedas en un radio típico (50 km).

**RNF4. Seguridad**  
La autenticación debe basarse en tokens JWT, almacenados en cookies HttpOnly, y el sistema debe incorporar validaciones en múltiples capas para evitar entradas inválidas o inconsistentes.

**RNF5. Testabilidad**  
Los componentes críticos del dominio (mappers, casos de uso, políticas y normalizadores) deben contar con pruebas automatizadas unitarias con alta cobertura.

**RNF6. Portabilidad**  
La solución debe poder ejecutarse en entorno local y en contenedores Docker sin cambios en el código fuente.

**RNF7. Usabilidad**  
La interfaz web debe ser sencilla, con un mapa interactivo y formularios claros para el registro de desapariciones y avistamientos.

---

## 4. Metodología de desarrollo

El desarrollo de **Find Me** se abordó de forma **iterativa e incremental**, priorizando primero:

1. Definición del modelo de dominio y reglas de negocio.
2. Diseño de la arquitectura (capas, puertos y adaptadores).
3. Implementación de la persistencia y la capa geoespacial.
4. Exposición de la API REST.
5. Implementación del frontend con Leaflet.
6. Agregado de validaciones y pruebas automatizadas.

Se utilizaron las siguientes prácticas:

- **Control de versiones** con Git.
- **Gestión de dependencias** con Maven (backend) y npm (frontend/testing).
- **Desarrollo guiado por pruebas** (de forma parcial) en mapeadores, casos de uso y políticas.
- **Documentación viva** del diseño, sincronizada con el repositorio de código.

---

## 5. Arquitectura del sistema

### 5.1 Patrón arquitectónico

El sistema adopta un enfoque inspirado en **Clean Architecture**, organizado en capas concéntricas, y utiliza el patrón de **puertos y adaptadores** en la periferia (hexagonal). Esto permite:

- Separación clara de responsabilidades entre dominio, aplicación, infraestructura y presentación.
- Independencia del dominio respecto de frameworks (Spring Boot, JPA, etc.).
- Alta testabilidad mediante mocks de puertos (interfaces).
- Facilidad para reemplazar detalles técnicos (base de datos, framework web, etc.).

### 5.2 Diagrama de capas
```text
┌─────────────────────────────────────────────────────────────
│                  PRESENTACIÓN (Controllers)                 │
│ REST Controllers │ Request/Response DTOs │ ExceptionHandler│
└────────────────────────┬────────────────────────────────────┘
                         │ Puertos de entrada (Adapters IN)
┌────────────────────────┴────────────────────────────────────
│               CAPA DE APLICACIÓN (Use Cases)                │
│  ┌────────────  ┌──────────  ┌──────────────            │
│  │ Use Cases  │  │ Mappers  │  │  Validators  │            │
│  │  (CQRS)    │  │  (DTOs)  │  │  (Policies)  │            │
│  └────────────┘  └──────────┘  └──────────────┘            │
└────────────────────────┬────────────────────────────────────┘
                         │ Lógica de negocio orquestada
┌────────────────────────┴────────────────────────────────────
│               CAPA DE DOMINIO (Core Business)               │
│  ┌────────────  ┌──────────  ┌──────────────            │
│  │  Entities  │  │ Services │  │    Rules     │            │
│  │  (Modelos) │  │          │  │ (Validación) │            │
│  └────────────┘  └──────────┘  └──────────────┘            │
└────────────────────────┬────────────────────────────────────┘
                         │ Puertos de salida (Adapters OUT)
┌────────────────────────┴────────────────────────────────────
│            CAPA DE INFRAESTRUCTURA (Detalles técnicos)      │
│  ┌────────────────  ┌─────────────  ┌─────────────      │
│  │  Repositories  │  │   Config    │  │   Security  │      │
│  │  (JPA/PostGIS) │  │  (Spring)   │  │    (JWT)    │      │
│  └────────────────┘  └─────────────┘  └─────────────┘      │
└─────────────────────────────────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────────
│                      BASE DE DATOS                          │
│             PostgreSQL + Extensión PostGIS                  │
└─────────────────────────────────────────────────────────────┘
```

### 5.3 Principios SOLID aplicados

#### Single Responsibility Principle (SRP)
Cada clase tiene una única razón de cambio:

- `DesaparecidoMapper`: transformación entre DTOs y modelos.
- `DesaparecidoService`: reglas de negocio específicas de desaparecidos.
- `DesaparecidoController`: gestión de HTTP y exposición de endpoints.

#### Open/Closed Principle (OCP)
El sistema se puede extender sin modificar código existente:

- Nuevas validaciones se agregan como nuevas `Rule` sin modificar la `Policy`.
- Nuevos casos de uso se agregan sin modificar el dominio.

#### Liskov Substitution Principle (LSP)
Las dependencias se formulan en términos de interfaces:

- `IRepoDeDesaparecidos` puede tener múltiples implementaciones.
- Los tests utilizan mocks que sustituyen a las implementaciones reales.

#### Interface Segregation Principle (ISP)
Interfaces específicas y coherentes:

- `IAvistadorService`, `IAuthService`, etc.
- Se evita un único `IService` monolítico.

#### Dependency Inversion Principle (DIP)

- Los casos de uso dependen de abstracciones (`IRepoDeDesaparecidos`, etc.), no de implementaciones concretas.
- La infraestructura implementa esas interfaces mediante adaptadores (repositorios JPA, etc.).

### 5.4 Flujo típico de una request

**Ejemplo:** `POST /api/desaparecidos`

1. El usuario envía una solicitud `POST /api/desaparecidos` con un `DesaparecidoRequestDTO`.
2. `DesaparecidoController` recibe el DTO.
3. `CreateDesaparecidoUseCase` invoca a `DesaparecidoCreatePolicy` para validar.
4. La `Policy` ejecuta, en orden, las reglas de negocio:
   - `DesaparecidoRenaperExistsRule` (DNI existe).
   - `DesaparecidoLengthRule` (descripción mínima).
   - `DesaparecidoDniDuplicadoRule` (DNI único).
5. `DesaparecidoMapper` normaliza datos (mayúsculas, sin acentos, DNI solo dígitos).
6. `DesaparecidoService` aplica la lógica de negocio necesaria.
7. `RepositorioDeDesaparecidos` persiste el resultado en PostgreSQL mediante JPA.
8. El mapper transforma a `DesaparecidoResponseDTO`.
9. El controlador retorna `201 Created` con encabezado `Location`.

**Ventajas:**

- Validación centralizada antes de tocar la capa de dominio.
- Normalización de datos consistente.
- Tests sencillos (cada paso es aislable).
- Errores de negocio unificados mediante `DomainException`.

---

## 6. Stack tecnológico

### 6.1 Backend

| Tecnología | Versión aproximada | Propósito |
|------------|-------------------|-----------|
| Java | 17 LTS | Lenguaje principal |
| Spring Boot | 3.3.4 | Framework de aplicación |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 3.3.x | ORM y persistencia |
| PostgreSQL | 15+ | Base de datos relacional |
| PostGIS | 3.3+ | Extensión geoespacial |
| Hibernate | 6.4+ | Implementación de JPA |
| JJWT | 0.11.5 | Manejo de tokens JWT |
| JTS Topology Suite | 1.19+ | Manejo de geometrías espaciales en Java |
| Jackson | 2.16+ | Serialización/deserialización JSON |
| Lombok | 1.18+ | Reducción de código boilerplate |

**Motivaciones principales:**

- Ecosistema maduro y ampliamente utilizado (Spring Boot + JPA).
- Soporte robusto para geolocalización (PostGIS + JTS).
- Facilidad para pruebas y configuración por convención.

### 6.2 Frontend

| Tecnologia | Version | Proposito |
|------------|---------|-----------|
| React | 18.x | Framework principal de UI |
| Vite | 5.x | Dev server y build del frontend |
| React Router DOM | 6.x | Navegacion cliente |
| Leaflet + React Leaflet | 1.9.4 / 4.x | Mapa interactivo y capas geograficas |
| Tailwind CSS | 3.x | Sistema de estilos utilitario |
| shadcn/ui + Radix UI | - | Componentes reutilizables y accesibles |
| TanStack React Query | 5.x | Cache y sincronizacion de estado remoto |
| Framer Motion | 10.x | Animaciones de interfaz |

**Motivaciones principales:**

- Arquitectura por componentes para reducir acoplamiento en UI.
- Integracion natural entre React y el mapa (React Leaflet).
- Flujo de desarrollo rapido y predecible con Vite.
- Mejor mantenibilidad al separar `pages/`, `components/`, `services/`, `hooks/`.

### 6.3 Testing

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| JUnit 5 | 5.10+ | Testing unitario backend |
| Mockito | 5.8+ | Mocking y stubs |
| AssertJ | 3.25+ | Assertions fluent para Java |
| Vitest | 4.0.x | Testing JavaScript |
| Testing Library (React/Jest-DOM) | 16.x / 6.x | Testing de componentes React |
| Playwright | 1.58.x | Tests E2E frontend |
| JaCoCo | 0.8.11 | Medición de cobertura en Java |

### 6.4 Infraestructura y herramientas

- **Maven:** gestión de dependencias, build y ejecución de tests.
- **Git:** control de versiones.
- **Docker:** contenedorización y despliegue reproducible.
- **PostgreSQL local/Docker:** base de datos de desarrollo.
- **Almacenamiento de imágenes:** filesystem local (`findme.storage.root`) + metadatos en BD + URLs firmadas con expiración.

---

## 7. Modelo de dominio

### 7.1 Entidades principales

El dominio se centra en tres entidades fundamentales:

- **Desaparecido**
- **Avistador**
- **Avistamiento**

#### 7.1.1 Desaparecido

**Propósito:** representa a una persona reportada como desaparecida.

**Atributos principales:**

- `id`: UUID
- `nombre`: String (normalizado a mayúsculas, sin acentos)
- `apellido`: String (normalizado)
- `dni`: String (solo dígitos, único en el sistema)
- `fechaDesaparicion`: LocalDateTime (autogenerada al crear)
- `descripcion`: String (longitud mínima de 20 caracteres)
- `foto`: String (URL opcional)

**Reglas de negocio:**

- El DNI debe existir en el padrón RENAPER.
- El DNI no puede duplicarse en el sistema.
- La descripción debe tener longitud mínima.
- La fecha de desaparición se establece automáticamente.
- Nombres y apellidos se normalizan.

**Ejemplo de normalización:**
```text
Entrada:
  nombre: "josé maría"
  apellido: "péRez"
  dni: "12.345.678"

Después de normalización:
  nombre: "JOSE MARIA"
  apellido: "PEREZ"
  dni: "12345678"
```

#### 7.1.2 Avistador

**Propósito:** usuario registrado que puede reportar avistamientos y desapariciones.

**Atributos principales:**

- `id`: UUID
- `dni`: String (único, normalizado)
- `nombre`: String
- `apellido`: String
- `edad`: Integer (• 18)
- `direccion`: String (opcional)
- `email`: String (requerido para login)
- `telefono`: String (opcional)
- `creadoEn`: LocalDateTime

**Reglas de negocio:**

- Debe ser mayor de 18 años.
- El DNI debe existir en RENAPER.
- Los datos personales deben coincidir con RENAPER.
- El DNI no puede duplicarse.
- Tras registro exitoso, se genera un JWT y el usuario queda autenticado.
- La autenticación no utiliza contraseñas: se basa en validación contra el padrón simulado.

#### 7.1.3 Avistamiento

**Propósito:** reporte ciudadano georreferenciado de una posible visualización de una persona desaparecida.

**Atributos principales:**

- `id`: UUID
- `avistadorId`: UUID (referencia a Avistador)
- `desaparecidoId`: UUID (referencia a Desaparecido)

**Datos geográficos:**

- `latitud`: Double (^'90 a 90)
- `longitud`: Double (^'180 a 180)
- `ubicacion`: Point (tipo PostGIS con SRID 4326)

**Datos del avistamiento:**

- `fechaHora`: LocalDateTime
- `descripcion`: String (mínimo 20 caracteres)
- `fotoUrl`: String (opcional)
- `verificado`: Boolean (por defecto false)
- `publico`: Boolean (por defecto true)
- `creadoEn`: LocalDateTime

**Reglas de negocio:**

- Coordenadas válidas en rango WGS84.
- Descripción con longitud mínima.
- Avistador y Desaparecido deben existir.
- Las coordenadas se sincronizan con el campo `ubicacion` (Point) a través de un trigger en la BD.

### 7.2 Relaciones entre entidades
```text
RenaperPersona (Padrón Nacional)
    │
    │ valida
    │
    ├──────────────┬──────────────
    │              │              │
    ▼              ▼              ▼
Avistador    Desaparecido    (validación)
    │              │
    │              │
    └──────┬───────┘
           │
           ▼
     Avistamiento
```

**Cardinalidades:**

- Un Avistador puede crear N Avistamientos (1:N).
- Un Desaparecido puede tener N Avistamientos (1:N).
- Un Avistamiento pertenece a un único Avistador y un único Desaparecido.
- RenaperPersona se utiliza para validar tanto avistadores como desaparecidos.

### 7.3 Value Objects — Coordenadas geográficas

**Representación en Java (JTS):**
```java
Point ubicacion = geometryFactory.createPoint(
    new Coordinate(longitud, latitud)
);
ubicacion.setSRID(4326); // WGS84
```

**Representación en PostgreSQL:**
```sql
GEOMETRY(Point, 4326)

-- Ejemplo de valor:
ST_SetSRID(ST_MakePoint(-58.3816, -34.6037), 4326);
```

> **Nota:** PostGIS utiliza el orden (longitud, latitud) mientras que Leaflet utiliza [latitud, longitud]. Esta diferencia se tiene en cuenta en los mapeos entre backend y frontend.

---

## 8. Diseño de capas

### 8.1 Capa de dominio (Core Business)

**Responsabilidad:** contener entidades y lógica de negocio pura, independiente de frameworks.

**Componentes:**

- Entidades de dominio (Desaparecido, Avistador, Avistamiento).
- Servicios de dominio (DesaparecidoService, AvistadorService, AvistamientoService, AuthService).
- Reglas de negocio encapsuladas en Rules y servicios auxiliares.

**Ejemplo conceptual de un servicio de dominio:**
```java
public List<AvistamientoFrontDTO> obtenerEnRadio(
    Double lat, Double lng, Double radioKm
) {
    // 1. Obtener avistamientos espaciales
    List<Avistamiento> avistamientos =
        repository.findWithinRadius(lat, lng, radioKm);

    // 2. Enriquecer con datos de Desaparecido y Avistador
    return enrichAvistamientos(avistamientos);
}

private List<AvistamientoFrontDTO> enrichAvistamientos(
    List<Avistamiento> avistamientos
) {
    Set<UUID> desaparecidoIds = extractDesaparecidoIds(avistamientos);
    Set<UUID> avistadorIds = extractAvistadorIds(avistamientos);

    Map<UUID, Desaparecido> desaparecidosMap =
        desaparecidoRepo.findAllById(desaparecidoIds);
    Map<UUID, Avistador> avistadoresMap =
        avistadorRepo.findAllByIds(avistadorIds);

    return avistamientos.stream()
        .map(a -> mapToFrontDTO(a, desaparecidosMap, avistadoresMap))
        .collect(Collectors.toList());
}
```

> Este enfoque evita el problema de N+1 consultas, utilizando pocas consultas batched.

### 8.2 Capa de aplicación (Use cases)

**Responsabilidad:** orquestar el flujo de operaciones, sin contener lógica de negocio compleja.

Se sigue un enfoque **CQRS simple:**

**Commands (escritura)**
- CreateDesaparecidoUseCase
- CreateAvistadorUseCase
- CreateAvistamientoUseCase

**Queries (lectura)**
- ReadDesaparecidoUseCase
- ReadAvistamientoUseCase
- ReadAvistamientoGeoUseCase (consultas espaciales)

**Flujo típico:**
```java
@Service
public class CreateDesaparecidoUseCase {
    private final IDesaparecidoService service;
    private final DesaparecidoCreatePolicy policy;

    public DesaparecidoResponseDTO execute(DesaparecidoRequestDTO dto) {
        // 1. Validar
        policy.validate(dto);

        // 2. Delegar en servicio de dominio
        return service.crearDesaparecido(dto);
    }
}
```

### 8.3 Mappers

**Responsabilidad:** transformación de datos entre capas sin lógica de negocio.

**Flujo general:**
```text
Request DTO → Domain Model → Persistence Entity → DB
DB → Persistence Entity → Domain Model → Response DTO → Front DTO
```

**Ejemplo de normalización en mapper:**

- Nombres y apellidos: `upperNoAccents()`
- DNI: `dniNormalizer()`
- Descripciones: `trim()` + validación de longitud (en validadores).
- URLs: `normalizeOptional()` (trim o null).

### 8.4 Capa de infraestructura

**Responsabilidad:** detalles técnicos (HTTP, persistencia, seguridad, configuración).

#### 8.4.1 Controllers (puertos de entrada)

**Ejemplo:**
```java
@RestController
@RequestMapping("/api/desaparecidos")
public class DesaparecidoController {
    private final CreateDesaparecidoUseCase createUseCase;
    private final ReadDesaparecidoUseCase readUseCase;

    @PostMapping
    public ResponseEntity<DesaparecidoResponseDTO> crear(
        @RequestBody DesaparecidoRequestDTO dto
    ) {
        DesaparecidoResponseDTO response = createUseCase.execute(dto);
        URI location = URI.create("/api/desaparecidos/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DesaparecidoFrontDTO>> getAll() {
        List<DesaparecidoFrontDTO> lista = readUseCase.execute();
        return ResponseEntity.ok(lista);
    }
}
```

Los controladores:

- Parsean requests a DTOs.
- Invocan casos de uso.
- Construyen respuestas HTTP (códigos, cabeceras, cuerpo).
- No contienen lógica de negocio ni lógica de persistencia.

#### 8.4.2 Repositorios (puertos de salida)

**Interfaz en dominio:**
```java
public interface IRepoDeDesaparecidos {
    Desaparecido save(Desaparecido desaparecido);
    List<Desaparecido> getDesaparecidos();
    Optional<Desaparecido> findById(UUID id);
    boolean existsByDni(String dni);
}
```

**Implementación en infraestructura:**
```java
@Repository
public class RepositorioDeDesaparecidos implements IRepoDeDesaparecidos {
    private final DesaparecidoJpaRepository jpa;
    private final DesaparecidoPersistenceMapper mapper;

    @Override
    public Desaparecido save(Desaparecido d) {
        DesaparecidoEntity entity = mapper.domainToEntity(d);
        DesaparecidoEntity saved = jpa.save(entity);
        return mapper.entityToDomain(saved);
    }
}
```

**Repositorio JPA:**
```java
public interface DesaparecidoJpaRepository
    extends JpaRepository<DesaparecidoEntity, UUID> {

    boolean existsByDni(String dni);

    @Query("SELECT d FROM DesaparecidoEntity d WHERE d.dni = :dni")
    Optional<DesaparecidoEntity> findByDni(@Param("dni") String dni);
}
```

#### 8.4.3 Manejo centralizado de errores

Se utiliza `DomainException` + `ApiExceptionHandler` + `ProblemDetail` (RFC 7807).

**Ejemplo de respuesta:**
```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "Ya existe un desaparecido con ese DNI",
  "key": "desaparecido.dni.duplicado"
}
```

---

## 9. Sistema de validación

### 9.1 Arquitectura de validación multi-capa

Se implementan tres niveles de validación:

1. **Validación de bean (Jakarta Validation)**  
   Uso de anotaciones como `@NotNull`, `@Size`, etc.

2. **Rules de negocio**  
   Validaciones específicas (DNI existe en RENAPER, edad • 18, coordenadas válidas, etc.).

3. **Policies (orquestación)**  
   Combinan varias Rule para validar un DTO en un contexto específico (por ejemplo, creación de desaparecido).

### 9.2 Patrón Chain of Responsibility

**Interfaces:**
```java
public interface Rule<T> {
    void check(T dto);
}

public interface Validator<T> {
    void validate(T dto);
}
```

**Ejemplo de Policy:**
```java
@Component
public class DesaparecidoCreatePolicy implements Validator<DesaparecidoRequestDTO> {
    private final List<Rule<DesaparecidoRequestDTO>> rules;

    public DesaparecidoCreatePolicy(List<Rule<DesaparecidoRequestDTO>> rules) {
        this.rules = rules;
    }

    @Override
    public void validate(DesaparecidoRequestDTO dto) {
        for (var rule : rules) {
            rule.check(dto);  // se detiene ante el primer error
        }
    }
}
```

**Rules específicas:**
```java
@Component
@Order(1)
public class DesaparecidoRenaperExistsRule implements Rule<DesaparecidoRequestDTO> {
    private final IRepoDeRenaper renaper;

    @Override
    public void check(DesaparecidoRequestDTO dto) {
        if (!renaper.findByDni(dto.getDni()).isPresent()) {
            throw DomainException.of(
                DesaparecidoError.RENAPER_NOT_FOUND.key,
                DesaparecidoError.RENAPER_NOT_FOUND.status
            );
        }
    }
}
```

### 9.3 Rules implementadas

**Avistamiento (5 rules):**

- `AvistamientoCoordsRule` — valida lat/lng en rango WGS84.
- `AvistamientoDescripcionRule` — longitud mínima de la descripción.
- `AvistadorExistsRule` — el avistador existe en la BD.
- `DesaparecidoExistsRule` — la persona desaparecida existe en la BD.
- `AvistamientoDateRule` — la fecha no es futura.

**Desaparecido (3 rules):**

- `DesaparecidoRenaperExistsRule` — DNI existe en RENAPER.
- `DesaparecidoLengthRule` — descripción mínima.
- `DesaparecidoDniDuplicadoRule` — DNI único.

**Avistador (4 rules):**

- `AvistadorAgeRule` — edad • 18.
- `AvistadorDniDuplicadoRule` — DNI único.
- `AvistadorRenaperExistsRule` — DNI existe en RENAPER.
- `AvistadorRenaperMatchRule` — datos coinciden con RENAPER.

**Token/Auth (10 rules):**

- Validación exhaustiva de la cookie, token JWT, claims, subject, etc.

### 9.4 Ventajas del enfoque

- **Open/Closed Principle:** agregar nuevas reglas sin modificar las policies.
- **Single Responsibility:** cada Rule valida un único aspecto.
- **Testeable:** reglas y policies son fácilmente testeables de forma aislada.
- **Orden controlado:** `@Order` permite priorizar reglas baratas sobre reglas costosas.

---

## 10. Seguridad

### 10.1 Autenticación basada en JWT

**Flujo simplificado:**

1. El usuario envía `POST /api/auth/login` con DNI y email.
2. El backend valida contra el padrón RENAPER y la BD.
3. Se genera un JWT con validez de 7 días.
4. El token se envía en una cookie `FM_TOKEN` HttpOnly y con `SameSite=Lax`.
5. En cada request posterior, un filtro (`JwtCookieFilter`) valida el token y establece el contexto de seguridad.

**Servicio de token:**
```java
@Service
public class TokenService {
    public static final String COOKIE_NAME = "FM_TOKEN";

    @Value("${jwt.secret}")
    private String SECRET;

    public String generate(
        String avistadorId,
        String dni,
        String email,
        String nombre
    ) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(7 * 24 * 3600);

        return Jwts.builder()
            .setSubject(dni)
            .claim("id", avistadorId)
            .claim("email", email)
            .claim("name", nombre)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(exp))
            .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
            .compact();
    }

    public void writeCookie(HttpServletResponse resp, String jwt) {
        Cookie c = new Cookie(COOKIE_NAME, jwt);
        c.setHttpOnly(true);
        c.setPath("/");
        c.setMaxAge(7 * 24 * 3600);
        // En producción debe ser true (HTTPS)
        c.setSecure(true);
        c.setAttribute("SameSite", "Lax");
        resp.addCookie(c);
    }
}
```

### 10.2 Configuración de Spring Security

Se utiliza un `SecurityFilterChain` con:

- Desactivación de CSRF para la API REST (token en cookie + SameSite).
- CORS configurado por variables de entorno (`cors.allowed-origins`).
- Filtro `JwtCookieFilter` antes de `AnonymousAuthenticationFilter`.
- Rutas públicas y protegidas diferenciadas por método HTTP y path.

### 10.3 Protecciones implementadas

| Riesgo | Contramedida | Implementación |
|--------|--------------|----------------|
| XSS | Tokens en cookies HttpOnly | `cookie.setHttpOnly(true)` |
| CSRF | Cookies SameSite=Lax y ausencia de formularios 3rd | `cookie.setAttribute("SameSite", "Lax")` |
| SQL Injection | ORMs y consultas parametrizadas | Spring Data JPA |
| Mass Assignment | DTOs diferenciados para request/response | Separación de DTOs |
| Exposición de datos sensibles | No se almacenan contraseñas | Autenticación basada en padrón externo |
| Broken Authentication | JWT con expiración y validación por request | Claims y expiración |

### 10.4 Limitaciones de seguridad reconocidas

- Rate limiting implementado en memoria por IP para `/api/auth/*` y `/api/files/*`; falta estrategia distribuida para multi-instancia.
- Sin auditoria detallada de accesos (audit trail completo).
- Secrets parametrizados por variables de entorno, con valores por defecto solo para desarrollo.
- Sin autenticacion de multiples factores.
- Sin mecanismo de revocacion temprana de tokens ni rotacion anticipada.

### 10.5 Tratamiento de Datos Personales (Cifrado y Almacenamiento)

**¿Cómo se almacenan los datos de las personas?**
Los datos de las personas desaparecidas y avistadores se almacenan en tablas relacionales dentro de PostgreSQL (`public.desaparecidos`, `public.avistadores`). Para resguardar la privacidad, datos altamente sensibles se manejan de la siguiente forma:

**Algoritmo de cifrado implementado:**
Para proteger los DNI en la base de datos se utiliza una encriptación a nivel de campo (Encryption in Rest) mediante JPA `AttributeConverter`.
- **Cifrado simétrico:** Se emplea **AES/GCM/NoPadding** (Advanced Encryption Standard con Galois/Counter Mode) con llaves de 256 bits y un vector de inicialización (IV) dinámico para que registros idénticos generen cifrados distintos.
- **Búsqueda exactas:** Dado que el IV aleatorio impide realizar un `SELECT * WHERE dni = ?`, se implementó un `BlindIndexer` que genera un hash seguro (HMAC-SHA256) del DNI original y lo almacena en una columna separada (`dni_hash`). Las búsquedas se realizan comparando el hash, asegurando que el dato original en tránsito y en reposo no quede expuesto.

---

## 11. Base de datos y geolocalización

### 11.1 Esquema relacional

- **Esquema public:** entidades del sistema.
- **Esquema renaper:** padrón nacional simulado.
- **Extensión postgis** habilitada.

**Tablas principales:**

- `renaper.personas`
- `public.desaparecidos`
- `public.avistadores`
- `public.avistamientos`

Incluyen claves primarias, claves foráneas, restricciones de integridad y diversos índices (incluido índice espacial GIST sobre `ubicacion` en `avistamientos`).

### 11.2 PostGIS y consultas espaciales

Se utilizan funciones principales como:

- `ST_DWithin` — búsqueda por radio.
- `ST_Within` — búsqueda por área o polígono.
- `ST_Distance` — cálculo de distancias en metros.
- Operador `<->` — KNN (k vecinos más cercanos).

**Ejemplo: búsqueda por radio:**
```sql
SELECT a.*,
       ST_Distance(
           a.ubicacion::geography,
           ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
       ) AS distancia_metros
FROM avistamientos a
WHERE ST_DWithin(
    a.ubicacion::geography,
    ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
    :radioMetros
)
ORDER BY distancia_metros;
```

### 11.3 ndices espaciales GIST

Se define:
```sql
CREATE INDEX idx_avistamientos_ubicacion
    ON public.avistamientos USING GIST(ubicacion);
```

Esto permite que búsquedas espaciales se resuelvan con complejidad aproximadamente logarítmica, mejorando significativamente la performance frente a un recorrido completo de la tabla.

### 11.4 Configuración JPA/Hibernate

**Configuración relevante:**
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.spatial.dialect.postgis.PostgisDialect
spring.jpa.show-sql=false
```

**Entidad con campo Point:**
```java
@Entity
@Table(name = "avistamientos", schema = "public")
public class AvistamientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point ubicacion;

    // ...
}
```

---

## 12. API REST

### 12.1 Principios de diseño

- Estilo RESTful.
- Recursos como sustantivos; verbos representados por métodos HTTP.
- Stateless: el servidor no mantiene sesión; la autenticación se realiza con JWT en cookie.
- Formato JSON.
- Uso coherente de códigos HTTP (200, 201, 400, 401, 404, 409, 422, 500).
- Formato de error unificado mediante Problem Details (RFC 7807).

### 12.2 Endpoints principales

#### Autenticacion

| Metodo | Endpoint | Requiere autenticacion | Descripcion |
|--------|----------|------------------------|-------------|
| POST | /api/auth/login | No | Iniciar sesion |
| GET | /api/auth/me | Si | Obtener usuario actual |
| POST | /api/auth/logout | No | Cerrar sesion |

#### Avistadores

| Metodo | Endpoint | Requiere autenticacion | Descripcion |
|--------|----------|------------------------|-------------|
| POST | /api/avistadores | No | Registrar nuevo avistador |

#### Desaparecidos

| Metodo | Endpoint | Requiere autenticacion | Descripcion |
|--------|----------|------------------------|-------------|
| POST | /api/desaparecidos | Si | Registrar persona desaparecida |
| GET | /api/desaparecidos | No | Listar personas desaparecidas (con `search`, `estado`, `sort`) |

#### Avistamientos (consultas espaciales)

| Metodo | Endpoint | Requiere autenticacion | Descripcion |
|--------|----------|------------------------|-------------|
| POST | /api/avistamientos | Si | Crear avistamiento |
| GET | /api/avistamientos | Si | Listado completo |
| GET | /api/avistamientos/mapa | No | Listar avistamientos para mapa |
| GET | /api/avistamientos/radio | No | Busqueda por radio |
| GET | /api/avistamientos/mapa/area | No | Busqueda por area rectangular |
| POST | /api/avistamientos/poligono | No | Busqueda por poligono (WKT) |
| GET | /api/avistamientos/cercanos | No | N avistamientos mas cercanos |
| GET | /api/avistamientos/desaparecido/{desaparecidoId} | No | Avistamientos por desaparecido |
| GET | /api/avistamientos/desaparecido/{desaparecidoId}/radio | No | Avistamientos por desaparecido en radio |
| GET | /api/avistamientos/desaparecido/{desaparecidoId}/ultimo | No | Obtencion del ultimo avistamiento reportado optimizado |
| GET | /api/avistamientos/area/count | No | Conteo por area |

#### Archivos

| Metodo | Endpoint | Requiere autenticacion | Descripcion |
|--------|----------|------------------------|-------------|
| POST | /api/files/upload | Si | Subida de archivo |
| GET | /api/files/{fileId} | No* | Descarga de archivo |
| DELETE | /api/files/{fileId} | Si (rol admin) | Eliminacion de archivo |

`*` Sin firma requiere sesion; con `expires` + `sig` valida URL firmada con expiracion.

### 12.3 Códigos de estado HTTP

- **200 OK:** operaciones de lectura exitosas.
- **201 Created:** creación de recursos (POST).
- **204 No Content:** operaciones sin cuerpo (logout).
- **400 Bad Request:** errores de request mal formado.
- **401 Unauthorized:** autenticación ausente o inválida.
- **404 Not Found:** recurso inexistente.
- **409 Conflict:** conflictos (por ejemplo, DNI duplicado).
- **422 Unprocessable Entity:** errores de validación de negocio.
- **500 Internal Server Error:** errores no controlados.

### 12.4 Formato de errores (RFC 7807)

**Ejemplo:**
```json
{
  "type": "about:blank",
  "title": "Unprocessable Entity",
  "status": 422,
  "detail": "La descripción debe tener al menos 20 caracteres",
  "instance": "/api/desaparecidos",
  "key": "desaparecido.descripcion.corta"
}
```

El campo `key` permite al frontend mapear mensajes a textos personalizados e internacionalizados.

---

## 13. Frontend

### 13.1 Arquitectura actual

El frontend se implementa como una SPA en React, con Vite como toolchain de desarrollo/build.

- `main.jsx` inicializa la aplicacion.
- `App.jsx` define las rutas principales.
- `pages/` concentra pantallas de alto nivel.
- `components/ui/` contiene componentes reutilizables de interfaz.
- `components/map/` encapsula comportamiento geoespacial (filtros, handlers, popups, modal de reporte).
- `services/` centraliza llamadas HTTP a la API.
- `hooks/` abstrae logica transversal (por ejemplo, autenticacion).

**Estructura principal:**
```text
frontend/
 src/
    main.jsx
    App.jsx
    Layout.jsx
    pages/
    components/
       ui/
       map/
    services/
    hooks/
    config/
    utils/
    test/
 e2e/
 playwright.config.js
```

### 13.2 Estado y flujo de datos

El frontend combina:

- Estado local con hooks de React (`useState`, `useEffect`, `useCallback`).
- Estado remoto/cache con TanStack React Query (`useQuery`, invalidacion y refetch).
- Estado de sesion encapsulado en `useAuth`.

Este enfoque permite separar estado de UI, estado de red y autenticacion.

### 13.3 Sistema de filtros de mapa

La pantalla de mapa soporta filtros por:

- Todos los avistamientos.
- Radio.
- Area rectangular.
- Poligono.
- Persona desaparecida.
- Ruta cronologica de avistamientos (`Polyline`) con sus marcadores numerados.
- Última Ubicación: filtro optimizado server-side que devuelve un único punto.

Los filtros disparan consultas a endpoints geoespaciales del backend y actualizan los marcadores en el mapa.

### 13.4 Servicios de API

Los servicios del frontend encapsulan llamadas a `/api/*` y normalizan errores para mostrarlos en UI.

- `AuthService` maneja login/sesion.
- `DesaparecidoService` y `AvistamientoService` concentran operaciones de dominio.
- El frontend utiliza `credentials: include` cuando corresponde para interoperar con cookies HttpOnly.

### 13.5 Mapa base y visualizacion

Se utiliza OpenStreetMap como base y React Leaflet para:

- Renderizado de marcadores.
- Dibujo de circulos, rectangulos y poligonos.
- Popups con informacion del avistamiento.
- Trazado de rutas cronologicas.

**Estado actual relevante:** el repositorio no evidencia una capa de clustering activa en el mapa.

### 13.6 Validacion en frontend

La validacion se distribuye en:

- Validaciones de formularios y saneamiento de campos en UI.
- Manejo centralizado de errores de API.
- Validacion final de reglas de negocio en backend.

---

## 14. Estrategia de testing

### 14.1 Pirámide de pruebas

Se sigue el enfoque:
```text
        /\ 
       /  \
      /E2E \          (implementacion parcial: specs Playwright en frontend/e2e)
     /──────\
    /Integrac\        (parcial)
   /──────────\
  /   Unitarias\      (principal foco)
 /──────────────\
```

### 14.2 Cobertura

**Backend**

- Mappers: ~100 %
- Use Cases: ~100 %
- Policies: ~100 %
- Rules principales: ~85 %
- Normalizadores: ~100 %
- Cobertura global aproximada del backend: ~85 %

**Componentes aún no cubiertos con tests automatizados:**

- Servicios de dominio (en su mayoría).
- Controladores.
- Repositorios (tests de acceso a BD).

**Frontend**

- Servicios y utilidades: ~70 % en Vitest.
- Tests unitarios sobre logica de negocio en JS y specs E2E base con Playwright (ejecucion/cobertura E2E pendiente de consolidacion).

### 14.3 Ejemplos de tests

Se implementan pruebas unitarias para:

- Verificar el orden de normalización en mappers.
- Asegurar que las policies ejecutan reglas en el orden correcto y se detienen ante el primer error.
- Confirmar que los casos de uso invocan primero validación y luego servicios de dominio.

---

## 15. Despliegue

### 15.1 Entorno de desarrollo

- **Backend:** Spring Boot (puerto 8080).
- **Frontend:** Vite dev server (puerto 5173), con proxy `/api` hacia `http://localhost:8080`.
- **Base de datos:** PostgreSQL + PostGIS local o en contenedor.

### 15.2 Instalacion local (resumen operativo)

1. Instalar PostgreSQL + PostGIS y crear la base.
2. Configurar credenciales/variables en `application.properties` y entorno.
3. Levantar backend (`mvn spring-boot:run` o wrapper equivalente).
4. Levantar frontend:
   - `cd frontend`
   - `npm install`
   - `npm run dev`

Accesos tipicos:

- Frontend: `http://localhost:5173`
- API: `http://localhost:8080/api/*`
- Swagger: `http://localhost:8080/swagger-ui.html`

### 15.3 Build estatico opcional en backend

El repositorio incluye scripts (`build-frontend.sh` / `build-frontend.bat`) que:

1. Ejecutan build del frontend.
2. Copian `frontend/dist` a `src/main/resources/static`.

Con ese flujo, Spring Boot puede servir frontend + API desde el mismo puerto 8080.

### 15.4 Docker

El repositorio versiona dos Dockerfiles:

- `Dockerfile.backend`
- `Dockerfile.frontend`

No hay `docker-compose.yml` versionado en la raiz; si se desea orquestacion local, debe agregarse uno propio.

**Build manual de imagenes (referencia):**

```bash
docker build -f Dockerfile.backend -t findme-backend .
docker build -f Dockerfile.frontend -t findme-frontend .
```

La imagen frontend usa Nginx y su `nginx.conf` define el proxy de `/api` al backend configurado.

---

## 16. Conclusiones

El proyecto **Find Me** implementa un sistema web para el registro y seguimiento de personas desaparecidas, combinando una arquitectura limpia y modular con capacidades geoespaciales avanzadas.

Desde el punto de vista arquitectónico, se logró:

- Mantener la capa de dominio independiente de frameworks y detalles de infraestructura.
- Organizar la lógica de aplicación en casos de uso claramente definidos.
- Encapsular el acceso a la base de datos mediante repositorios que implementan puertos de salida.
- Estandarizar el manejo de errores y validaciones.

En cuanto a geolocalización, el uso de PostgreSQL + PostGIS permite:

- Ejecutar consultas por radio, área, polígono y vecinos más cercanos.
- Optimizar el rendimiento mediante índices GIST.
- Integrar los resultados con un frontend basado en Leaflet de manera natural.

En términos de calidad, se alcanzó una cobertura elevada en los componentes críticos del dominio, lo que contribuye a la mantenibilidad y reduce la probabilidad de regresiones. El sistema de validación basado en rules y policies facilita la extensibilidad ante nuevas reglas de negocio.

Si bien el proyecto se encuentra en estado de prototipo académico y no ha sido desplegado en un entorno productivo, constituye una base sólida sobre la cual podrían construirse funcionalidades adicionales, integraciones con sistemas reales y mejoras operacionales (seguridad, monitoreo, escalabilidad).

---

## 17. Trabajo futuro

Entre las posibles líneas de trabajo futuro se destacan:

### Paginación y optimización de consultas

- Incorporar paginación en listados de personas y avistamientos.
- Agregar caché (por ejemplo, Redis o Caffeine) para consultas frecuentes.

### Mejoras de seguridad

- Endurecer rate limiting (algoritmo/bucket distribuido y storage compartido).
- Registrar auditorias de accesos e intentos fallidos.
- Migrar secrets a un gestor seguro de variables de entorno.
- Evaluar la incorporacion de autenticacion multifactor (2FA).

### Tests de integración y E2E

- Agregar pruebas de integración para controladores y repositorios (por ejemplo, con MockMvc y Testcontainers).
- Implementar pruebas de extremo a extremo sobre flujos principales del sistema.

### Despliegue en producción

- Configurar un entorno cloud (Railway, Render u otro).
- Habilitar HTTPS y cookies Secure.
- Implementar monitoreo (Spring Actuator + Prometheus/Grafana) y logging estructurado.

### Funcionalidades avanzadas (Implementadas en v2)

- **Notificaciones en tiempo real:** uso de WebSockets (STOMP/SockJS) en Spring Boot para escuchar inmediatamente la creación de nuevos avistamientos e impactar el estado del mapa sin recargar, suscribiéndose a tópicos globales (`/topic/avistamientos`) y específicos por persona (`/topic/avistamientos/{id}`).
- **Paginación Database-to-Front:** refactorización para utilizar las clases `Pageable` de Spring Data JPA con `PagedResponseDTO` logrando que los grandes volúmenes de avistamientos y reportes no colapsen la RAM del backend.

### Tareas a Futuro

- Integración con fuentes de datos oficiales.
- Incorporación de modelos de ML para análisis de patrones de avistamientos.
- Agregar estrategias de Clustering sobre el frontend (MarkerClusterGroup) para Leaflet.
- Soporte para Redis distribuido (Rate Limiting y Caché).

### Frontend y Estado Global

- **Manejo de estados robusto:** profundizar politicas de cache, invalidacion y prefetch en React Query; evaluar Zustand solo si aumenta significativamente la complejidad de estados locales compartidos.

---

## 18. Anexo A — Decisiones técnicas y limitaciones

### A.1 Decisiones de diseño relevantes

| Decisión | Justificación | Trade-off |
|----------|---------------|-----------|
| Uso de Clean Architecture | Mantenibilidad y escalabilidad | Mayor complejidad inicial |
| Uso de PostGIS | Requerimientos geoespaciales avanzados | Dependencia de PostgreSQL |
| Frontend con React + React Leaflet | Componentizacion, mejor mantenibilidad y ecosistema de UI moderno | Mayor complejidad inicial y dependencia de toolchain Node |
| JWT en cookies HttpOnly | Balance entre seguridad y usabilidad | Manejo cuidadoso de CORS y SameSite |
| Sin contraseñas (validación RENAPER) | Simplificación del flujo de autenticación | Dependencia de padrones externos |
| Uso de servicios en capas gratuitas (cloud) | Viabilidad académica y costo cero | Limitaciones de recursos y escalabilidad |
| Arquitectura monolítica | Simplicidad operativa para un MVP académico | Escalamiento horizontal menos directo |

### A.2 Limitaciones técnicas reconocidas

**Funcionales**

- No se implementa análisis de patrones o ML.
- No hay integración con bases oficiales reales.
- Moderación de contenido no automatizada.
- Sin notificaciones en tiempo real.
- Sin gestión avanzada de casos (workflow).

**Técnicas**

- **Sin paginación en listados:** Debido al volumen acotado de datos inicial en este alcance, se omitió por simplicidad operativa provisoria. Para producción será reemplazado por paginación o keyset loops.
- **Sin versionado explícito de la API (/v1, /v2):** Al ser una arquitectura fundacional sin clientes legacy que la consuman de momento, no requiere retro-compatibilidad. Se añadirá en la primera iteración a producción real.
- Sin sistema de caché.
- Logging relativamente básico.

**Operacionales**

- No desplegado aún en entorno productivo.
- Sin CI/CD automatizado.
- Sin monitoreo centralizado.
- **Backups de base de datos manuales:** Se programa como trabajo futuro utilizar `pg_cron` o Jobs en la base de datos permitiendo acoplar scripts externos que atomicen tareas de volcado.
- Sin estrategia de disaster recovery formal.

**De seguridad**

- Rate limiting basico en memoria (no distribuido).
- Sin auditoria de accesos detallada.
- Secrets por variables de entorno con defaults de desarrollo.
- Sin 2FA.
- Sin revocacion temprana de tokens.

---

## 19. Referencias bibliográficas

- Evans, E. (2003). *Domain-Driven Design: Tackling Complexity in the Heart of Software*. Addison-Wesley.
- Martin, R. C. (2017). *Clean Architecture: A Craftsman's Guide to Software Structure and Design*. Prentice Hall.
- Fowler, M. (2010). *Patterns of Enterprise Application Architecture*. Addison-Wesley.
- OWASP Foundation. *OWASP Top 10 — Web Application Security Risks*.
- Documentación oficial de Spring Boot: https://spring.io/projects/spring-boot
- Documentación oficial de Spring Security: https://spring.io/projects/spring-security
- Documentación oficial de PostGIS: https://postgis.net/documentation/
- Documentación oficial de Leaflet: https://leafletjs.com/reference.html

---

**Fin de la documentación técnica — Find Me**







    

---

## 20. Actualizacion Tecnica (Marzo 2026)

### 20.1 CI/CD Basico

Se agrego workflow de GitHub Actions en `.github/workflows/ci.yml` con dos jobs:

- `backend-tests`: ejecuta `./mvnw clean test jacoco:report` con servicio PostGIS.
- `frontend-tests`: ejecuta `npm ci`, `npm run test` y `npm run test:js:cov` en `frontend/`.

### 20.2 Seguridad Endurecida

Mejoras implementadas:

- Sanitizacion server-side de texto libre (`HtmlSanitizer`) antes de persistencia.
- Validacion de WKT para busqueda por poligono:
  - requerido,
  - formato `POLYGON((...))`,
  - limite de longitud.
- Headers adicionales en Spring Security:
  - `Content-Security-Policy`,
  - `X-XSS-Protection`,
  - `X-Content-Type-Options`,
  - `Frame-Options`.

### 20.3 Audit Logging

Se incorporo auditoria basica con AOP:

- anotacion `@Auditable`,
- aspecto `AuditLogAspect`,
- aplicado a casos de uso criticos de creacion.

### 20.4 API Versionada y Compatibilidad

Se adopto prefijo `/api/v1/*` en frontend y backend, manteniendo compatibilidad temporal con rutas legacy `/api/*`.

### 20.5 Paginacion y Cache

- Paginacion server-side para listado de desaparecidos (parametros `page` y `size`).
- Cache en memoria para consultas frecuentes de desaparecidos.
- Invalida cache en operaciones de alta.
