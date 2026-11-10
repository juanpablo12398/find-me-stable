# Find Me - Sistema de Avistamientos

> **Snapshot público del prototipo académico:** corresponde a la rama privada
> `demo-estable` del 4 de septiembre de 2026. Se publicó el código de esa etapa
> sin el historial privado, artefactos compilados, configuración local de agentes
> ni documentación de gestión interna. Los datos de ejemplo y la integración
> RENAPER son simulados. No es un servicio oficial ni está listo para recibir
> datos reales de personas.

**Find Me** es una plataforma web academica para registrar, buscar y visualizar avistamientos georreferenciados de personas desaparecidas.

---

## LIMITACION DE PRODUCCION: Validacion de identidad simulada

> **La validacion de identidad usa una base de datos RENAPER simulada con fines academicos.**
>
> El sistema verifica DNI y datos personales contra una tabla `renaper_personas` cargada localmente
> que **no esta conectada al servicio oficial del RENAPER argentino**.
>
> En un despliegue de produccion real, esta capa debe reemplazarse con una integracion al
> **RENAPER (Registro Nacional de las Personas)** a traves de su API oficial, lo cual
> requiere convenio institucional con el Ministerio del Interior de la Republica Argentina.
>
> **No utilizar este sistema con datos reales de ciudadanos sin reemplazar esta integracion.**

---

## Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                     FRONTEND (React + Vite)                 │
│  pages/  ──►  hooks/  ──►  services/  ──►  components/     │
│  (UI pura)   (logica)   (clientes API)    (UI reutilizable) │
└──────────────────────────┬──────────────────────────────────┘
                           │  HTTP REST / WebSocket
┌──────────────────────────▼──────────────────────────────────┐
│              CAPA DE PRESENTACION (Controllers)             │
│   AuthController  AvistadorController  DesaparecidoController│
│   AvistamientoController  FileController                    │
│                  ↕ DTOs de request/response                 │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│              CAPA DE APLICACION (Use Cases)                 │
│   CreateDesaparecidoUseCase   ReadAvistamientoGeoUseCase    │
│   LoginUseCase   UploadFileUseCase   ...                    │
│                                                             │
│   Policies ──► Rules (validacion encadenada con @Order)     │
│   Mappers  (normalizan datos: sin acentos, DNI sin puntos)  │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                CAPA DE DOMINIO (Core)                       │
│   Modelos: Desaparecido  Avistamiento  Avistador  File      │
│   Servicios: AuthService  AvistadorService  FileStorageService│
│   Helpers: UrgenciaCalculator  ExposicionFilter  ExifStripper│
└──────────────────────────┬──────────────────────────────────┘
                           │  Puertos (interfaces)
┌──────────────────────────▼──────────────────────────────────┐
│              CAPA DE INFRAESTRUCTURA (Adaptadores)          │
│                                                             │
│  Persistencia          Seguridad        Notificaciones      │
│  ┌─────────────┐   ┌────────────────┐  ┌────────────────┐  │
│  │ PostgreSQL  │   │ JwtCookieFilter│  │ Composite      │  │
│  │ + PostGIS   │   │ FieldEncryptor │  │  ├─ Email      │  │
│  │ (JPA+GIST)  │   │ BlindIndexer   │  │  ├─ SMS/Twilio │  │
│  │ RENAPER sim │   │ RateLimitFilter│  │  └─ WebSocket  │  │
│  └─────────────┘   └────────────────┘  └────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

**Flujo de una request** (ejemplo: registrar avistamiento):

```
React (ReportModal)
  └─► AvistamientoService.crear()
        └─► POST /api/v1/avistamientos
              └─► JwtCookieFilter  (autentica)
              └─► RateLimitFilter  (throttle por IP)
              └─► AvistamientoController
                    └─► CreateAvistamientoUseCase
                          ├─► AvistamientoCreatePolicy
                          │     ├─► AvistadorExistsRule
                          │     ├─► DesaparecidoExistsRule
                          │     └─► AvistamientoCoordsRule
                          ├─► AvistamientoMapper  (normaliza)
                          ├─► AvistamientoRepository  (PostGIS)
                          └─► CompositeNotificador
                                ├─► WebSocket (tiempo real)
                                ├─► Email (Thymeleaf template)
                                └─► SMS (Twilio)
```

---

## Quick Start

Esta seccion permite levantar el proyecto completo desde cero. Tiempo estimado: **20-30 minutos**.

### Prerequisitos

| Herramienta | Version minima | Como verificar |
|-------------|----------------|----------------|
| Java JDK | 17 LTS | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| Node.js | 20+ | `node -version` |
| Docker | 24+ | `docker -version` |
| Docker Compose | 2.x | `docker compose version` |

> **Nota para Windows:** los comandos usan `mvn` directamente. Si solo tenes el Maven Wrapper del proyecto, reemplaza `mvn` por `./mvnw` (Linux/macOS) o `mvnw.cmd` (Windows).

---

### Paso 1 — Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/find-me.git
cd find-me
```

---

### Paso 2 — Levantar la base de datos con Docker

El backend requiere **PostgreSQL con la extension PostGIS**. La imagen `postgis/postgis` ya incluye ambos.

```bash
docker run -d \
  --name findme-db \
  -e POSTGRES_DB=findme \
  -e POSTGRES_USER=findme_user \
  -e POSTGRES_PASSWORD=findme_pass \
  -p 5432:5432 \
  postgis/postgis:15-3.3
```

Verificar que el contenedor esta corriendo:

```bash
docker ps | grep findme-db
```

Habilitar la extension PostGIS (solo la primera vez):

```bash
docker exec -it findme-db psql -U findme_user -d findme -c "CREATE EXTENSION IF NOT EXISTS postgis;"
```

> **Alternativa con Docker Compose:** si el proyecto incluye un `docker-compose.yml`, ejecuta `docker compose up -d db`.

---

### Paso 3 — Configurar las variables de entorno

El backend lee su configuracion desde variables de entorno. Las variables obligatorias son las siguientes.

**Crear el archivo `.env` en la raiz del proyecto** (nunca commitear este archivo):

```bash
# Base de datos (ajustar si cambiaste el docker run del paso anterior)
DB_URL=jdbc:postgresql://localhost:5432/findme
DB_USER=findme_user
DB_PASS=findme_pass

# CORS: URL del frontend en desarrollo
CORS_ORIGINS=http://localhost:5173

# JWT — secreto para firmar tokens de sesion
# Debe tener al menos 32 caracteres. Generar con:
#   openssl rand -base64 48
JWT_SECRET=cambia-esto-por-una-clave-real-de-al-menos-32-caracteres

# Cifrado AES-256-GCM — protege campos sensibles en la base de datos (DNI, etc.)
# Generar con: openssl rand -base64 32
ENCRYPTION_KEY=cambia-esto-por-clave-real-base64-32-bytes

# HMAC — blind index para busquedas sobre campos cifrados
# Clave DISTINTA a ENCRYPTION_KEY (defensa en profundidad)
# Generar con: openssl rand -base64 32
HMAC_KEY=cambia-esto-por-otra-clave-real-base64-32-bytes
```

**Como generar las claves criptograficas:**

```bash
# JWT_SECRET (cadena larga, puede ser cualquier texto de >= 32 bytes)
openssl rand -base64 48

# ENCRYPTION_KEY (exactamente 32 bytes en Base64 — AES-256)
openssl rand -base64 32

# HMAC_KEY (exactamente 32 bytes en Base64)
openssl rand -base64 32
```

**Variables opcionales** (tienen valor default en `application.properties`):

| Variable | Default | Para que sirve |
|----------|---------|----------------|
| `PORT` | `8080` | Puerto del servidor backend |
| `JWT_EXPIRATION_DAYS` | `7` | Dias de vida del token de sesion |
| `JWT_COOKIE_SECURE` | `false` | En produccion con HTTPS poner `true` |
| `MAIL_USERNAME` | _(vacio)_ | Gmail para notificaciones de avistamientos |
| `MAIL_PASSWORD` | _(vacio)_ | Contrasena de aplicacion de Gmail |
| `MAIL_ENABLED` | `false` | Activar envio de emails |
| `TWILIO_ACCOUNT_SID` | _(vacio)_ | SID de cuenta Twilio para SMS |
| `TWILIO_AUTH_TOKEN` | _(vacio)_ | Token de Twilio |
| `TWILIO_PHONE_NUMBER` | _(vacio)_ | Numero Twilio de origen |
| `SMS_ENABLED` | `false` | Activar envio de SMS |

> **Para desarrollo local**, solo son obligatorias las variables del bloque anterior. Mail y SMS funcionan con sus defaults desactivados.

**Como exportar las variables en la sesion de terminal:**

```bash
# Linux / macOS
export DB_URL=jdbc:postgresql://localhost:5432/findme
export DB_USER=findme_user
export DB_PASS=findme_pass
export CORS_ORIGINS=http://localhost:5173
export JWT_SECRET=mi-secreto-de-al-menos-32-caracteres-de-longitud
export ENCRYPTION_KEY=$(openssl rand -base64 32)
export HMAC_KEY=$(openssl rand -base64 32)
```

```powershell
# Windows PowerShell
$env:DB_URL="jdbc:postgresql://localhost:5432/findme"
$env:DB_USER="findme_user"
$env:DB_PASS="findme_pass"
$env:CORS_ORIGINS="http://localhost:5173"
$env:JWT_SECRET="mi-secreto-de-al-menos-32-caracteres-de-longitud"
$env:ENCRYPTION_KEY="<valor generado con openssl>"
$env:HMAC_KEY="<valor generado con openssl>"
```

---

### Paso 4 — Cargar datos de RENAPER (seed)

El sistema valida identidad contra la tabla `renaper.personas`. Sin datos en esa tabla, ningun registro ni login funciona.

```bash
# Cargar el seed con 10 personas ficticias para desarrollo
psql -h localhost -U findme_user -d findme -f scripts/seed-renaper.sql
```

> Si no tenes `psql` instalado localmente, usar Docker:
> ```bash
> docker exec -i findme-db psql -U findme_user -d findme < scripts/seed-renaper.sql
> ```

Despues del seed podras registrarte usando cualquiera de los DNI del archivo `scripts/seed-renaper.sql` (ver la lista completa en ese archivo).

---

### Paso 5 — Correr el backend

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run

# O con Maven instalado globalmente
mvn spring-boot:run
```

Salida esperada al arrancar correctamente:

```
Started Application in X.XXX seconds (JVM running for X.XXX)
```

El backend queda disponible en `http://localhost:8080`.

---

### Paso 6 — Correr el frontend

En una terminal separada:

```bash
cd frontend
npm install
npm run dev
```

Salida esperada:

```
  VITE v5.x.x  ready in XXX ms
  ➜  Local:   http://localhost:5173/
```

---

### Paso 7 — Verificar que todo funciona

| Recurso | URL |
|---------|-----|
| Frontend | http://localhost:5173 |
| API REST | http://localhost:8080/api/v1/ |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Health check | http://localhost:8080/actuator/health |

Prueba rapida desde la terminal:

```bash
curl http://localhost:8080/actuator/health
# Respuesta esperada: {"status":"UP"}

curl http://localhost:8080/api/v1/desaparecidos
# Respuesta esperada: JSON con pagina vacia si no hay datos aun
```

---

### Correr los tests del backend

```bash
# Ejecutar todos los tests (usa H2 en memoria, no necesita Postgres corriendo)
mvn test

# Ejecutar tests y generar reporte de cobertura JaCoCo
mvn test jacoco:report
# El reporte HTML queda en: target/site/jacoco/index.html

# Compilar sin ejecutar tests (util para verificar que compila)
mvn clean install -DskipTests
```

---

### Correr los tests del frontend

```bash
cd frontend

# Tests unitarios (Vitest)
npm run test

# Tests unitarios con reporte de cobertura
npm run test:js:cov

# Tests E2E (Playwright — requiere backend corriendo)
npm run test:e2e
```

---

## Estructura del Proyecto

```text
findme-merged/
|-- frontend/                 # React + Vite + Tailwind
|   |-- src/
|   |   |-- components/       # UI + mapa
|   |   |-- pages/            # Pantallas principales
|   |   |-- services/         # Clientes API
|   |   |-- hooks/            # Logica transversal (auth)
|   |   `-- ...
|   |-- e2e/                  # Specs Playwright
|   `-- package.json
|-- scripts/
|   |-- seed-renaper.sql      # Seed de personas ficticias para RENAPER
|   `-- e2e/                  # Scripts auxiliares para tests E2E
|-- src/main/
|   |-- java/                 # Backend Spring Boot
|   `-- resources/
|-- Dockerfile.backend
|-- Dockerfile.frontend
|-- nginx.conf
|-- build-frontend.sh
|-- build-frontend.bat
`-- pom.xml
```

---

## Build para Produccion

```bash
# Opcion recomendada (genera dist y copia a src/main/resources/static)
# Linux/macOS
./build-frontend.sh

# Windows
build-frontend.bat

# Empaquetar backend
# Linux/macOS
./mvnw clean package

# Windows
mvnw.cmd clean package
```

---

## Tecnologias

### Frontend
- React 18
- Vite
- Tailwind CSS
- shadcn/ui + Radix UI
- React Leaflet + Leaflet
- TanStack Query
- Framer Motion
- Vitest + Testing Library + Playwright

### Backend
- Spring Boot 3.3
- Spring Security
- PostgreSQL + PostGIS
- JWT (JJWT 0.11)
- Hibernate Spatial + JTS
- SpringDoc OpenAPI (Swagger)
- JaCoCo (cobertura)
- Testcontainers (tests de integracion geoespaciales)

---

## Endpoints API principales

- `POST /api/v1/auth/login` — Iniciar sesion
- `POST /api/v1/auth/logout` — Cerrar sesion
- `GET  /api/v1/auth/me` — Usuario autenticado actual
- `POST /api/v1/avistadores` — Registrar avistador
- `GET  /api/v1/desaparecidos` — Listar desaparecidos (paginado)
- `POST /api/v1/desaparecidos` — Registrar desaparecido (requiere auth)
- `GET  /api/v1/avistamientos/mapa` — Avistamientos para el mapa
- `GET  /api/v1/avistamientos/radio` — Busqueda por radio
- `GET  /api/v1/avistamientos/mapa/area` — Busqueda por area rectangular
- `POST /api/v1/avistamientos/poligono` — Busqueda por poligono
- `GET  /api/v1/avistamientos/desaparecido/{id}` — Avistamientos de un desaparecido
- `POST /api/v1/files/upload` — Subir archivo
- `GET  /api/v1/files/{fileId}` — Descargar archivo

Documentacion interactiva completa: `http://localhost:8080/swagger-ui.html`
