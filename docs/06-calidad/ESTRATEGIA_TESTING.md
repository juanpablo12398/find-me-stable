# Estrategia de Testing - Find Me

Plan completo de pruebas, tipos de tests y cobertura objetivo.

---

## Tabla de Contenidos

1. [Objetivos de Testing](#objetivos-de-testing)
2. [Pirámide de Testing](#pirámide-de-testing)
3. [Tipos de Tests](#tipos-de-tests)
4. [Cobertura de Código](#cobertura-de-código)
5. [Tests Unitarios](#tests-unitarios)
6. [Tests de Integración](#tests-de-integración)
7. [Tests de Frontend](#tests-de-frontend)
8. [Tests E2E](#tests-e2e)
9. [Tests de Seguridad](#tests-de-seguridad)
10. [Tests de Performance](#tests-de-performance)
11. [Herramientas y Frameworks](#herramientas-y-frameworks)
12. [Proceso de Ejecución](#proceso-de-ejecución)
13. [Métricas y Reportes](#métricas-y-reportes)
14. [Criterios de Aceptación](#criterios-de-aceptación)
15. [Buenas Prácticas](#buenas-prácticas)

---

## Objetivos de Testing

### Objetivos Generales

1. **Garantizar calidad del código:** Verificar que el sistema cumpla con los requisitos funcionales y no funcionales.
2. **Prevenir regresiones:** Detectar errores tempranamente antes de que lleguen a producción (o a la demo).
3. **Facilitar refactorización:** Permitir modificar el código con confianza, apoyándose en una suite de tests estable.
4. **Documentar comportamiento:** Usar los tests como documentación viva del sistema.
5. **Cumplir estándares académicos:** Demostrar aplicación de buenas prácticas de ingeniería de software.

### Objetivos Específicos por Entrega

#### Entrega 1 (E1 - 20/12/2025)

- **Cobertura mínima global backend:** ≥ 70%
- **Componentes críticos:** 100% de cobertura (mappers, use cases, policies de negocio)
- **Foco principal:** Tests unitarios sobre la capa de dominio y aplicación
- **Tests de integración básicos:** Repositorios + controladores principales (al menos los flujos más importantes)

#### Entrega 2 (E2 - 20/02/2026)

- **Cobertura mínima global backend:** ≥ 80%
- **Tests de integración completos:** Flujos críticos de punta a punta en backend
- **Tests E2E:** Flujos de usuario clave sobre entorno desplegado
- **Tests de seguridad:** Validación de JWT, autorización por rol, sanitización de inputs
- **Tests de performance básicos:** Tiempos de respuesta de endpoints críticos bajo carga moderada

---

## Pirámide de Testing
```
           /\
          /  \        Tests E2E (≈ 5%)
         /    \       - Flujos completos de usuario
        /------\      - Pocos pero muy críticos
       /        \     
      /          \    Tests de Integración (≈ 25%)
     /            \   - Controladores + repositorios
    /              \  - Base de datos real (H2 o PostgreSQL de test)
   /----------------\ 
  /                  \ Tests Unitarios (≈ 70%)
 /                    \ - Lógica de negocio pura
/______________________\ - Casos de uso, mappers, validaciones
```

### Distribución Objetivo (Orientativa)

| Tipo de Test | Porcentaje | Cantidad Estimada | Tiempo de Ejecución Aproximado |
|--------------|------------|-------------------|-------------------------------|
| **Unitarios** | ~ 70% | ~ 150 tests | < 10 segundos |
| **Integración** | ~ 25% | ~ 50 tests | < 30 segundos |
| **E2E** | ~ 5% | ~ 10 tests | < 2 minutos |

**Nota:** Las cantidades son orientativas, lo importante es respetar la forma de la pirámide: muchos tests unitarios, algunos de integración y pocos E2E muy bien elegidos.

---

## Tipos de Tests

### 1. Tests Unitarios

**Objetivo:** Verificar el comportamiento de unidades individuales de código en aislamiento.

**Alcance típico:**
- Casos de uso (use cases)
- Mapeadores (mappers)
- Validaciones (rules, policies)
- Utilidades (helpers) con lógica
- DTOs que contengan lógica (por ejemplo, formateos o cálculos)

**Características esperadas:**
- Muy rápidos (< 100 ms por test)
- Aislados: sin acceso real a base de datos, red, filesystem, etc.
- Uso intensivo de mocks para dependencias externas
- Cobertura alta en clases de dominio (objetivo: ≥ 90% en componentes críticos)

---

### 2. Tests de Integración

**Objetivo:** Verificar la correcta integración entre componentes del sistema (no solo la lógica aislada).

**Alcance típico:**
- Controladores REST + servicios + repositorios
- Consultas de base de datos (incluyendo queries geoespaciales con PostGIS)
- Serialización/deserialización JSON
- Configuración de seguridad (filtros de seguridad, JWT, etc.)

**Características esperadas:**
- Más lentos que los unitarios (~ 200—1000 ms por test)
- Uso de base de datos de prueba (H2 o PostgreSQL de test con PostGIS según necesidad)
- Tests transaccionales con rollback automático para no dejar estado sucio
- Uso de `@SpringBootTest`, `@AutoConfigureMockMvc` y perfiles de test

---

### 3. Tests de Frontend

**Objetivo:** Verificar la funcionalidad crítica de JavaScript del lado del cliente.

**Alcance típico:**
- Funciones de manipulación del mapa (Leaflet)
- Lógica de filtrado y ordenamiento
- Validación de formularios en el navegador
- Interacción con el DOM (mostrar/ocultar mensajes, estados de error, etc.)

**Características esperadas:**
- Framework: Vitest + @testing-library/dom
- Entorno: Node.js + JSDOM (no navegador real)
- Simulación de eventos (click, input, submit) sobre elementos DOM

---

### 4. Tests End-to-End (E2E)

**Objetivo:** Verificar flujos completos de usuario desde la UI hasta la base de datos, pasando por el backend real.

**Alcance típico (E2):**
- Registro de avistador
- Creación de persona desaparecida
- Reporte de avistamiento
- Búsqueda de personas
- Visualización de avistamientos en el mapa

**Características esperadas:**
- Herramienta sugerida: Playwright (prioridad) o Selenium como alternativa
- Ambiente completo desplegado (backend + base de datos + frontend)
- Pocos tests, pero muy representativos de los flujos de negocio críticos
- No se ejecutan en cada commit, sino en pipeline CI o antes de releases/demo

---

## Cobertura de Código

### Métricas Objetivo

| Métrica | E1 | E2 | Componentes Críticos |
|---------|----|----|---------------------|
| **Line Coverage** | • 70% | • 80% | 100% |
| **Branch Coverage** | • 65% | • 75% | • 90% |
| **Method Coverage** | • 70% | • 80% | 100% |

### Componentes Críticos (Cobertura Obligatoria 100%)

**1. Mappers:**
- `DesaparecidoMapper`
- `AvistadorMapper`
- `AvistamientoMapper`

**2. Casos de Uso (Use Cases):**
- `CrearDesaparecidoUseCase`
- `CrearAvistamientoUseCase`
- `BuscarDesaparecidoUseCase`
- `ObtenerAvistamientosCercanosUseCase`

**3. Validaciones y Políticas:**
- `DniValidationRule`
- `EdadValidationRule`
- `CoordenadasValidationRule`
- Todas las business policies asociadas a la creación de desaparecidos y avistamientos

**Nota:** Los nombres de clases son representativos. Deben ajustarse a los nombres reales del proyecto si difieren.

### Exclusiones de Cobertura (Justificadas)

- Clases de configuración (`@Configuration`)
- Clases DTO sin lógica (solo getters/setters)
- Clase principal de arranque (`Application`)
- Código generado automáticamente por Lombok (getters/setters, equals, hashCode, etc.)

---

## Tests Unitarios

### Patrón Recomendado de Escritura (AAA)
```java
@ExtendWith(MockitoExtension.class)
class CrearDesaparecidoUseCaseTest {

    @Mock
    private DesaparecidoRepository repository;

    @Mock
    private RenaperService renaperService;

    @InjectMocks
    private CrearDesaparecidoUseCase useCase;

    @Test
    @DisplayName("Debe crear un desaparecido cuando los datos son válidos")
    void debeCrearDesaparecidoCuandoDatosSonValidos() {
        // Arrange (Given)
        var request = new CrearDesaparecidoRequest(
            "12345678",
            "Juan",
            "Pérez",
            30
        );

        when(renaperService.validarPersona(any()))
            .thenReturn(true);
        when(repository.save(any()))
            .thenReturn(desaparecidoEsperado());

        // Act (When)
        var resultado = useCase.ejecutar(request);

        // Assert (Then)
        assertThat(resultado)
            .isNotNull()
            .hasFieldOrPropertyWithValue("dni", "12345678");

        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el DNI no existe en RENAPER")
    void debeLanzarExcepcionCuandoDniNoExisteEnRenaper() {
        // Arrange
        var request = new CrearDesaparecidoRequest(
            "99999999",
            "Juan",
            "Pérez",
            30
        );

        when(renaperService.validarPersona(any()))
            .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(request))
            .isInstanceOf(DniNoValidoException.class)
            .hasMessageContaining("DNI no encontrado en RENAPER");

        verify(repository, never()).save(any());
    }
}
```

### Ejemplos de Componentes a Testear Unitariamente

#### 1. Mappers
```java
@Test
void debeMapearDesaparecidoEntityADTO() {
    // Arrange
    var entity = DesaparecidoEntity.builder()
        .id(1L)
        .dni("12345678")
        .nombre("Juan")
        .apellido("Pérez")
        .edad(30)
        .build();

    // Act
    var dto = DesaparecidoMapper.toDTO(entity);

    // Assert
    assertThat(dto)
        .hasFieldOrPropertyWithValue("id", 1L)
        .hasFieldOrPropertyWithValue("dni", "12345678")
        .hasFieldOrPropertyWithValue("nombreCompleto", "Juan Pérez");
}
```

#### 2. Validaciones (Rules)
```java
@Test
void debeValidarDniArgentino() {
    // Arrange
    var rule = new DniValidationRule();
    var dniValido = "12345678";

    // Act
    var resultado = rule.validate(dniValido);

    // Assert
    assertThat(resultado.isValid()).isTrue();
}

@Test
void debeRechazarDniConLetras() {
    // Arrange
    var rule = new DniValidationRule();
    var dniInvalido = "1234ABC8";

    // Act
    var resultado = rule.validate(dniInvalido);

    // Assert
    assertThat(resultado.isValid()).isFalse();
    assertThat(resultado.getErrorMessage())
        .contains("debe contener solo números");
}
```

#### 3. Políticas de Negocio (Policies)
```java
@Test
void debePermitirCrearAvistamientoDentroDelRadioPermitido() {
    // Arrange
    var policy = new AvistamientoRadioPolicy();
    var ubicacionDesaparecido = new Point(-34.6037, -58.3816); // Buenos Aires
    var ubicacionAvistamiento = new Point(-34.6050, -58.3820); // ~150 m

    // Act
    var resultado = policy.evaluate(ubicacionDesaparecido, ubicacionAvistamiento);

    // Assert
    assertThat(resultado.isAllowed()).isTrue();
}
```

---

## Tests de Integración

### Configuración Base
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DesaparecidoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DesaparecidoRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }
}
```

### Ejemplo: Controlador + Repositorio
```java
@Test
void debeCrearDesaparecidoYRetornar201() throws Exception {
    // Arrange
    var request = new CrearDesaparecidoRequest(
        "12345678",
        "Juan",
        "Pérez",
        30
    );

    // Act & Assert
    mockMvc.perform(post("/api/desaparecidos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.dni").value("12345678"))
        .andExpect(jsonPath("$.nombre").value("Juan"))
        .andExpect(header().exists("Location"));

    // Verificar persistencia
    var desaparecidos = repository.findAll();
    assertThat(desaparecidos).hasSize(1);
}
```

### Ejemplo: Query Geoespacial (PostGIS)
```java
@Test
void debeEncontrarAvistamientosCercanosDentroDeRadio() {
    // Arrange: crear desaparecido
    var desaparecido = crearDesaparecido("12345678", "Juan", "Pérez");

    // Avistamientos en diferentes ubicaciones
    var avistamiento1 = crearAvistamiento(
        desaparecido,
        -34.6037, -58.3816  // Ubicación base
    );

    var avistamiento2 = crearAvistamiento(
        desaparecido,
        -34.6050, -58.3820  // ~150 m
    );

    var avistamiento3 = crearAvistamiento(
        desaparecido,
        -34.7000, -58.5000  // ~15 km
    );

    // Act: buscar avistamientos dentro de 1 km
    var resultado = avistamientoRepository
        .findByDesaparecidoIdAndWithinRadius(
            desaparecido.getId(),
            -34.6037,
            -58.3816,
            1000.0  // metros
        );

    // Assert: solo deben aparecer los 2 primeros
    assertThat(resultado)
        .hasSize(2)
        .extracting("id")
        .containsExactlyInAnyOrder(
            avistamiento1.getId(),
            avistamiento2.getId()
        );
}
```

---

## Tests de Frontend

### Configuración de Vitest
```javascript
// vitest.config.mjs
import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/test/setup.js'],
  },
});
```

### Ejemplo: Función de Filtrado
```javascript
import { describe, it, expect } from 'vitest';
import { filtrarDesaparecidosPorNombre } from '../utils/filtros';

describe('Filtros de búsqueda', () => {
  it('debe filtrar personas por nombre exacto', () => {
    // Arrange
    const personas = [
      { id: 1, nombre: 'Juan', apellido: 'Pérez' },
      { id: 2, nombre: 'María', apellido: 'González' },
      { id: 3, nombre: 'Juan', apellido: 'López' },
    ];

    // Act
    const resultado = filtrarDesaparecidosPorNombre(personas, 'Juan');

    // Assert
    expect(resultado).toHaveLength(2);
    expect(resultado[0].nombre).toBe('Juan');
  });

  it('debe ser case-insensitive', () => {
    const personas = [{ id: 1, nombre: 'Juan', apellido: 'Pérez' }];

    const resultado = filtrarDesaparecidosPorNombre(personas, 'juan');

    expect(resultado).toHaveLength(1);
  });
});
```

### Ejemplo: Interacción con el Mapa
```javascript
import { describe, it, expect, beforeEach } from 'vitest';
import { crearMarcadorAvistamiento } from '../mapa/marcadores';

describe('Marcadores del mapa', () => {
  let mapa;

  beforeEach(() => {
    document.body.innerHTML = '<div id="map"></div>';
    mapa = L.map('map').setView([-34.6037, -58.3816], 13);
  });

  it('debe crear marcador en la ubicación correcta', () => {
    const avistamiento = {
      id: 1,
      desaparecido: { id: 5, nombre: 'Juan' },
      latitud: -34.6037,
      longitud: -58.3816,
    };

    const marcador = crearMarcadorAvistamiento(avistamiento, mapa);

    expect(marcador).toBeDefined();
    expect(marcador.getLatLng()).toEqual({
      lat: -34.6037,
      lng: -58.3816,
    });
  });
});
```

---

## Tests E2E

### Configuración (Futuro - E2)
```javascript
// playwright.config.js
import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  use: {
    baseURL: 'http://localhost:8080',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
});
```

### Ejemplo: Flujo Completo de Reporte de Avistamiento
```javascript
import { test, expect } from '@playwright/test';

test('flujo completo de reporte de avistamiento', async ({ page }) => {
  await page.goto('/');

  await page.fill('#buscar-dni', '12345678');
  await page.click('#btn-buscar');

  await expect(page.locator('.persona-card')).toBeVisible();
  await expect(page.locator('.nombre')).toHaveText('Juan Pérez');

  await page.click('#btn-reportar-avistamiento');

  await page.fill('#descripcion', 'Vi a esta persona en Plaza de Mayo');
  await page.click('#mapa', { position: { x: 200, y: 200 } });

  await page.click('#btn-enviar-avistamiento');

  await expect(page.locator('.mensaje-exito')).toBeVisible();
  await expect(page.locator('.mensaje-exito')).toHaveText(
    'Avistamiento reportado correctamente'
  );

  await expect(page.locator('.leaflet-marker')).toBeVisible();
});
```

---

## Tests de Seguridad

### 1. Validación de JWT
```java
@Test
void debeRechazarRequestSinToken() throws Exception {
    mockMvc.perform(post("/api/desaparecidos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isUnauthorized());
}

@Test
void debeRechazarTokenInvalido() throws Exception {
    mockMvc.perform(post("/api/desaparecidos")
            .header("Authorization", "Bearer token_invalido")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isUnauthorized());
}
```

### 2. Autorización por Rol
```java
@Test
@WithMockUser(roles = "AVISTADOR")
void debePermitirCrearAvistamientoConRolAvistador() throws Exception {
    // Test de autorización exitoso...
}

@Test
@WithMockUser(roles = "USER")
void debeRechazarCrearAvistamientoSinRolAvistador() throws Exception {
    mockMvc.perform(post("/api/avistamientos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isForbidden());
}
```

### 3. Sanitización de Inputs (XSS / Payloads Maliciosos)
```java
@Test
void debeRechazarScriptInjectionEnDescripcion() throws Exception {
    var request = new CrearAvistamientoRequest(
        1L,
        "<script>alert('XSS')</script>",
        -34.6037,
        -58.3816
    );

    mockMvc.perform(post("/api/avistamientos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value(containsString("caracteres no permitidos")));
}
```

---

## Tests de Performance

### Objetivos de Performance (E2, Orientativos)

| Endpoint | Tiempo Máximo (p95) | Carga Objetivo |
|----------|---------------------|----------------|
| `GET /api/desaparecidos` | • 200 ms | 100 usuarios concurrentes |
| `POST /api/avistamientos` | • 300 ms | 50 usuarios concurrentes |
| Query geoespacial (radio 1 km) | • 100 ms | 20 usuarios concurrentes |

### Herramientas Sugeridas

- **JMeter:** Tests de carga simples y reproducibles
- **Spring Boot Actuator:** Métricas en tiempo real de latencia
- **Micrometer (@Timed):** Medición de tiempos de métodos clave
```java
@Service
public class BuscarDesaparecidoService {

    @Timed(
        value = "buscar.desaparecido.tiempo",
        description = "Tiempo de búsqueda de desaparecido"
    )
    public List<Desaparecido> buscar(String criterio) {
        // Implementación...
    }
}
```

---

## Herramientas y Frameworks

### Backend

| Herramienta | Propósito | Versión Ref. |
|-------------|-----------|-------------|
| **JUnit 5** | Framework de testing | 5.10+ |
| **Mockito** | Mocking de dependencias | 5.8+ |
| **AssertJ** | Assertions fluidas | 3.25+ |
| **Spring Boot Test** | Contexto de Spring en tests | 3.x |
| **MockMvc** | Testing de controladores REST | 3.x |
| **JaCoCo** | Medición de cobertura | 0.8.11+ |
| **Testcontainers** (opcional) | BD real en contenedores para test | 1.19+ |

### Frontend

| Herramienta | Propósito | Versión Ref. |
|-------------|-----------|-------------|
| **Vitest** | Framework de testing JS | 3.2.x |
| **@testing-library/dom** | Testing de interacción DOM | 10.4.x |
| **JSDOM** | Simulación de navegador | Latest |

### E2E (Futuro - E2)

| Herramienta | Propósito |
|-------------|-----------|
| **Playwright** | Automatización de navegador |
| **Selenium** | Alternativa a Playwright |

---

## Proceso de Ejecución

### Ejecución Local

El proyecto utiliza scripts npm unificados para ejecutar tests tanto de backend (Java/Maven) como de frontend (JavaScript/Vitest).

#### Tests de Backend (Java)
```bash
# Ejecutar todos los tests de backend
npm run test:java

# Ejecutar tests con reporte de cobertura
npm run test:java:cov

# Abrir reporte de cobertura de Java en el navegador
npm run open:cov:java
```

**Equivalente directo con Maven:**
```bash
# Linux/macOS
./mvnw test

# Windows
mvnw.cmd test

# Con cobertura
./mvnw clean test jacoco:report
```

---

#### Tests de Frontend (JavaScript)
```bash
# Ejecutar tests de frontend
npm run test:js

# Ejecutar tests de frontend con cobertura
npm run test:js:cov

# Abrir reporte de cobertura de JavaScript en el navegador
npm run open:cov:js
```

**Nota:** Los tests de frontend utilizan Vitest + JSDOM, sin necesidad de navegador real.

---

#### Ejecutar Todos los Tests (Backend + Frontend)
```bash
# Ejecutar todos los tests (Java + JavaScript)
npm run test:all

# Ejecutar todos los tests con cobertura + abrir reportes automáticamente
npm run test:all:cov
```

**Resultado esperado del comando `test:all:cov`:**

1. Ejecuta tests de backend con cobertura
2. Ejecuta tests de frontend con cobertura
3. Abre automáticamente ambos reportes en el navegador:
    - **Backend:** `target/site/jacoco/index.html`
    - **Frontend:** `coverage/index.html`

---

### Ubicación de Reportes

| Tipo | Ubicación del Reporte | Comando para Abrir |
|------|----------------------|-------------------|
| **Backend (JaCoCo)** | `target/site/jacoco/index.html` | `npm run open:cov:java` |
| **Frontend (Vitest)** | `coverage/index.html` | `npm run open:cov:js` |

---

### Comandos Detallados por Herramienta

#### Maven (Backend)
```bash
# Compilar sin ejecutar tests
./mvnw clean install -DskipTests

# Ejecutar solo tests unitarios (convención: *Test.java)
./mvnw test -Dtest='*Test'

# Ejecutar solo tests de integración (convención: *IntegrationTest.java)
./mvnw test -Dtest='*IntegrationTest'

# Generar reporte de cobertura sin ejecutar tests
./mvnw jacoco:report

# Ver árbol de dependencias
./mvnw dependency:tree

# Limpiar build anterior
./mvnw clean
```

---

#### Vitest (Frontend)
```bash
# Ejecutar tests en modo watch (re-ejecuta al guardar)
npx vitest

# Ejecutar tests una sola vez
npx vitest run

# Ejecutar tests con cobertura
npx vitest run --coverage

# Ejecutar tests de un archivo específico
npx vitest run src/test/ejemplo.test.js

# Modo UI (interfaz gráfica de tests)
npx vitest --ui
```

---

### Pipeline CI/CD (GitHub Actions - Futuro)
```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgis/postgis:15-3.3
        env:
          POSTGRES_DB: findme_test
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        ports:
          - 5432:5432

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'

      - name: Set up Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'

      - name: Install dependencies
        run: npm install

      - name: Run all tests with coverage
        run: npm run test:all:cov

      - name: Upload backend coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./target/site/jacoco/jacoco.xml
          flags: backend

      - name: Upload frontend coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./coverage/coverage-final.json
          flags: frontend
```

---

### Verificación de Configuración

#### Verificar que Vitest está correctamente configurado
```bash
# Ver versión de Vitest
npx vitest --version

# Ejecutar tests en modo debug
npx vitest run --reporter=verbose
```

#### Verificar que Maven está correctamente configurado
```bash
# Ver versión de Maven
./mvnw --version

# Verificar plugins
./mvnw help:effective-pom | grep -A5 "jacoco"
```

---

### Ejecución en Entornos Específicos

#### Desarrollo Local
```bash
# Ejecutar solo tests rápidos (unitarios)
npm run test:java -- -Dtest='*Test'
npm run test:js
```

#### Pre-Commit (Antes de hacer commit)
```bash
# Ejecutar todos los tests sin abrir reportes
npm run test:all
```

#### Pre-Release (Antes de una demo o entrega)
```bash
# Ejecutar todos los tests con cobertura completa
npm run test:all:cov

# Verificar manualmente los reportes que se abren automáticamente
```

---

### Troubleshooting de Ejecución

#### Error: "Cannot find module 'vitest'"

**Causa:** Dependencias npm no instaladas.

**Solución:**
```bash
npm install
```

---

#### Error: "mvnw: Permission denied" (Linux/macOS)

**Causa:** El wrapper de Maven no tiene permisos de ejecución.

**Solución:**
```bash
chmod +x mvnw
./mvnw test
```

---

#### Error: "Tests fallando en CI pero pasan localmente"

**Posibles causas:**

1. **Base de datos no disponible en CI:** Verificar que el servicio PostgreSQL esté configurado
2. **Variables de entorno faltantes:** Agregar secrets en GitHub Actions
3. **Timezone diferente:** Usar UTC en tests que dependen de fechas

**Solución:**
```yaml
# En GitHub Actions, agregar:
env:
  TZ: UTC
  SPRING_PROFILES_ACTIVE: test
```

---

#### Comando `open:cov:java` no funciona en Linux/macOS

**Causa:** El comando `powershell` es específico de Windows.

**Solución:** Usar comandos específicos de cada SO:

**Linux:**
```bash
xdg-open target/site/jacoco/index.html
```

**macOS:**
```bash
open target/site/jacoco/index.html
```

**Alternativa multiplataforma (actualizar `package.json`):**
```json
"scripts": {
  "open:cov:java": "node -e \"require('child_process').exec(process.platform === 'win32' ? 'start target/site/jacoco/index.html' : process.platform === 'darwin' ? 'open target/site/jacoco/index.html' : 'xdg-open target/site/jacoco/index.html')\"",
  "open:cov:js": "node -e \"require('child_process').exec(process.platform === 'win32' ? 'start coverage/index.html' : process.platform === 'darwin' ? 'open coverage/index.html' : 'xdg-open coverage/index.html')\""
}
```

---

### Resumen de Comandos Clave

| Acción | Comando |
|--------|---------|
| **Tests backend** | `npm run test:java` |
| **Tests backend + cobertura** | `npm run test:java:cov` |
| **Tests frontend** | `npm run test:js` |
| **Tests frontend + cobertura** | `npm run test:js:cov` |
| **Todos los tests** | `npm run test:all` |
| **Todos los tests + reportes** | `npm run test:all:cov` |
| **Abrir reporte backend** | `npm run open:cov:java` |
| **Abrir reporte frontend** | `npm run open:cov:js` |

---

## Métricas y Reportes

### Reporte de Cobertura (JaCoCo)

El reporte HTML de JaCoCo se genera en:
```
target/site/jacoco/index.html
```

**Contenido principal:**
- **Resumen general:** Cobertura total del proyecto
- **Por paquete:** Desglose por paquete
- **Por clase:** Cobertura línea a línea
- **Por método:** Cobertura a nivel de método y complejidad ciclomática

### Métricas a Monitorear

1. **Line Coverage:** Porcentaje de líneas ejecutadas
2. **Branch Coverage:** Porcentaje de ramas de decisión cubiertas
3. **Cyclomatic Complexity:** Complejidad de métodos; ayuda a detectar código que requiere más tests o refactor
4. **Missed Lines:** Líneas sin cobertura, idealmente minimizadas en componentes críticos

### Reportes de Ejecución de Tests

Maven Surefire genera reportes en:
```
target/surefire-reports/
```

**Incluyen:**
- `TEST-*.xml` (formato JUnit XML)
- `TEST-*.txt` (salida legible en texto)

---

## Criterios de Aceptación

### Para Entrega 1 (E1)

- [ ] Cobertura de líneas global backend ≥ 70%
- [ ] Cobertura de mappers = 100%
- [ ] Cobertura de use cases críticos = 100%
- [ ] 100% de los tests unitarios definidos pasando exitosamente
- [ ] Al menos una batería de tests de integración básica ejecutándose sin errores
- [ ] Reporte de JaCoCo generado y adjuntado en la entrega

### Para Entrega 2 (E2)

- [ ] Cobertura de líneas global backend ≥ 80%
- [ ] Cobertura de branches global ≥ 75%
- [ ] Tests E2E implementados para flujos críticos
- [ ] Tests de seguridad (JWT, roles, sanitización) ejecutados
- [ ] Tests de performance básicos documentados
- [ ] (Opcional pero recomendado) Pipeline CI/CD ejecutando tests automáticamente
- [ ] Reporte de cobertura actualizado y accesible

---

## Buenas Prácticas

### Nomenclatura de Tests
```java
// ✓ BUENO: descriptivo y claro
@Test
void debeCrearDesaparecidoCuandoDatosValidos() { }

@Test
void debeLanzarExcepcionCuandoDniInvalido() { }

// X MALO: poco descriptivo
@Test
void test1() { }

@Test
void testCrear() { }
```

### Patrón AAA (Arrange—Act—Assert)
```java
@Test
void ejemplo() {
    // Arrange: preparar datos y mocks
    var datos = prepararDatos();
    when(mock.metodo()).thenReturn(valor);

    // Act: ejecutar la acción
    var resultado = servicio.ejecutar(datos);

    // Assert: verificar el resultado
    assertThat(resultado).isNotNull();
    verify(mock).metodo();
}
```

### Independencia de Tests

- Cada test debe poder ejecutarse de forma aislada
- No depender del orden de ejecución
- Limpiar estado entre tests (`@BeforeEach`, `@AfterEach`)
- Evitar usar variables estáticas compartidas para estado mutable

### Legibilidad

- Usar `@DisplayName` con descripciones claras en castellano
- Evitar lógica compleja en los propios tests
- Idealmente, un caso de negocio por test (con excepciones justificadas)
- Comentar solo cuando el caso no sea evidente

---

**Última actualización:** Diciembre 2025  
**Versión:** 1.1
