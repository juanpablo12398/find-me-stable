# Guía de Deployment - Find Me

Instrucciones de despliegue alineadas con el estado actual del repositorio.

---

## Tabla de Contenidos

1. [Arquitectura de Deployment](#arquitectura-de-deployment)
2. [Entorno de Desarrollo Local](#entorno-de-desarrollo-local)
3. [Build y Empaquetado](#build-y-empaquetado)
4. [Docker](#docker)
5. [Deployment en Producción](#deployment-en-producción)
6. [Variables de Entorno](#variables-de-entorno)
7. [Verificación Post-Deployment](#verificación-post-deployment)
8. [Troubleshooting](#troubleshooting)

---

## Arquitectura de Deployment

### Opción A (separada, recomendada)

- Frontend SPA (React/Vite) desplegado por separado (por ejemplo, Vercel).
- Backend Spring Boot desplegado en Railway/Render.
- Base de datos PostgreSQL + PostGIS gestionada en cloud.

### Opción B (backend sirviendo estáticos)

- Se genera el build del frontend y se copia a `src/main/resources/static`.
- El backend Spring Boot sirve frontend + API desde el mismo servicio.

### Estado actual del repo

- Hay `Dockerfile.backend` y `Dockerfile.frontend` versionados.
- No hay `docker-compose.yml` versionado en la raíz.
- `nginx.conf` del contenedor frontend hace proxy de `/api` al backend configurado.

---

## Entorno de Desarrollo Local

### Componentes

| Componente     | Tecnología          | Puerto | Descripción                |
|---------------|---------------------|--------|----------------------------|
| Backend        | Spring Boot         | 8080   | API REST y Swagger         |
| Frontend       | Vite Dev Server     | 5173   | SPA React con proxy `/api` |
| Base de datos  | PostgreSQL + PostGIS| 5432   | Persistencia geoespacial   |

### Accesos locales

- Frontend: `http://localhost:5173`
- API: `http://localhost:8080/api/*`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Build y Empaquetado

### Backend

```bash
# Linux/macOS
./mvnw clean package -DskipTests

# Windows
mvnw.cmd clean package -DskipTests
```

### Frontend (modo separado)

```bash
cd frontend
npm install
npm run build
```

### Frontend integrado al backend (opcional)

Usar scripts del repo:

```bash
# Linux/macOS
./build-frontend.sh

# Windows
build-frontend.bat
```

Estos scripts compilan frontend y copian `frontend/dist` a `src/main/resources/static`.

---

## Docker

### Imagen backend

```bash
docker build -f Dockerfile.backend -t findme-backend .
```

### Imagen frontend

```bash
docker build -f Dockerfile.frontend -t findme-frontend .
```

### Ejecución básica

```bash
# Backend (ejemplo)
docker run --rm -p 8080:8080   -e DB_URL="jdbc:postgresql://host:5432/findme"   -e DB_USER="findme_user"   -e DB_PASS="password_seguro"   -e JWT_SECRET="tu_secret_jwt_largo"   findme-backend

# Frontend (Nginx, expone 8080 dentro del contenedor)
docker run --rm -p 8081:8080 findme-frontend
```

### Compose local (opcional)

Si se quiere orquestar con Compose, crear un `docker-compose.yml` propio.  
Este repositorio no incluye uno oficial versionado.

---

## Deployment en Producción

### Backend + DB (Railway/Render)

1. Conectar repositorio Git.
2. Configurar build/start del backend.
3. Definir variables de entorno (`DB_URL`, `DB_USER`, `DB_PASS`, `JWT_SECRET`, etc.).
4. Verificar PostGIS con:

```sql
CREATE EXTENSION IF NOT EXISTS postgis;
SELECT PostGIS_version();
```

### Frontend

Opción separada:
- Deploy en Vercel/Netlify.
- Configurar URL de API al backend productivo.

Opción integrada:
- Ejecutar `build-frontend.sh`/`.bat` antes del empaquetado del backend.
- Desplegar backend con estáticos incluidos.

---

## Variables de Entorno

### Backend

```env
DB_URL=jdbc:postgresql://host:5432/findme
DB_USER=findme_user
DB_PASS=password_seguro
JWT_SECRET=secret_muy_largo_256_bits_mínimo
CORS_ORIGINS=https://tu-frontend.com,http://localhost:5173
PORT=8080
```

### Seguridad adicional recomendada

```env
ENCRYPTION_KEY=base64_32_bytes
HMAC_KEY=base64_32_bytes
SECURITY_COOKIE_SECURE=true
SECURITY_HSTS_ENABLED=true
SECURITY_RATE_LIMIT_MAX_REQUESTS=60
SECURITY_RATE_LIMIT_WINDOW_SECONDS=60
SECURITY_SIGNED_URL_TTL_SECONDS=120
```

---

## Verificación Post-Deployment

### Checks técnicos

```bash
# 1) API pública
curl https://tu-backend.com/api/desaparecidos

# 2) Swagger
curl -I https://tu-backend.com/swagger-ui.html

# 3) PostGIS
psql "$DATABASE_URL" -c "SELECT PostGIS_version();"
```

### Checklist manual

- [ ] Backend responde en la URL productiva.
- [ ] Swagger accesible.
- [ ] Frontend carga y consume API correctamente.
- [ ] Mapa renderiza tiles y marcadores.
- [ ] PostGIS habilitado.
- [ ] CORS configurado para dominios reales.
- [ ] Cookies y HTTPS según entorno.

---

## Troubleshooting

### Error de conexión a DB

- Revisar `DB_URL`, `DB_USER`, `DB_PASS`.
- Confirmar acceso de red entre backend y DB.
- Validar que PostGIS esté instalada.

### Error CORS

- Revisar `CORS_ORIGINS`.
- Confirmar que el frontend use la URL correcta del backend.

### Error de frontend sin API

- En local, verificar proxy Vite hacia `http://localhost:8080`.
- En Docker frontend, revisar `nginx.conf` y URL de `proxy_pass`.

### Error de JWT

- Verificar longitud y formato de `JWT_SECRET`.
- Asegurar consistencia de `SECURITY_COOKIE_SECURE` con HTTPS.

---

**Última actualización:** Marzo 2026  
**Versión:** 1.1

---

## Actualizacion Marzo 2026

### Levantar con Docker Compose

Se versiono `docker-compose.yml` en la raiz del proyecto para levantar stack completo (DB + backend + frontend):

```bash
docker-compose up --build
```

Servicios expuestos:

- Frontend: `http://localhost:8081`
- Backend API: `http://localhost:8080/api/v1/*`
- Actuator health: `http://localhost:8080/actuator/health`
- PostgreSQL/PostGIS: `localhost:5432`

### Variables de entorno (compose)

- `DB_URL`
- `DB_USER`
- `DB_PASS`
- `JWT_SECRET`
- `JWT_COOKIE_SECURE`
- `CORS_ORIGINS`
- `SECURITY_COOKIE_SECURE`
- `FINDME_STORAGE_ROOT`
- `ENCRYPTION_KEY`
- `HMAC_KEY`
- `MAIL_USERNAME` — cuenta de Gmail (o proveedor SMTP) para envío de notificaciones
- `MAIL_PASSWORD` — contraseña o App Password de la cuenta de correo
- `MAIL_FROM` — dirección remitente (default: `noreply@findme.com`)
- `MAIL_ENABLED` — `true` para activar notificaciones por email; `false` (default) las deshabilita sin error
- `TWILIO_ACCOUNT_SID` — Account SID de la cuenta Twilio (para SMS al contacto familiar)
- `TWILIO_AUTH_TOKEN` — Auth Token de la cuenta Twilio
- `TWILIO_PHONE_NUMBER` — Número de Twilio desde el que se envían los SMS (ej: `+15550001234`)
- `SMS_ENABLED` — `true` para activar notificaciones por SMS; `false` (default) las deshabilita sin error

### Healthcheck operativo

El backend expone:

- `GET /actuator/health`
- `GET /actuator/info`

Se recomienda validar salud del despliegue con:

```bash
curl http://localhost:8080/actuator/health
```