    # Guía de Instalación - Find Me

Instrucciones completas para configurar y ejecutar el proyecto localmente.

**Tiempo estimado:** 30-45 minutos (dependiendo de la velocidad de descarga de dependencias)

---

## Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Instalación Paso a Paso](#instalación-paso-a-paso)
3. [Configuración de la Base de Datos](#configuración-de-la-base-de-datos)
4. [Configuración del Proyecto](#configuración-del-proyecto)
5. [Ejecución del Sistema](#ejecución-del-sistema)
6. [Verificación de la Instalación](#verificación-de-la-instalación)
7. [Troubleshooting](#troubleshooting)
8. [Próximos Pasos](#próximos-pasos)
9. [Configuración del IDE](#configuración-del-ide)
10. [Comandos Útiles](#comandos-útiles)

---

## Requisitos Previos

### Software Necesario

| Software | Versión Mínima | Propósito |
|----------|----------------|-----------|
| **Java JDK** | 17 LTS | Lenguaje principal del backend |
| **PostgreSQL** | 15+ | Base de datos relacional |
| **PostGIS** | 3.3+ | Extensión geoespacial para PostgreSQL |
| **Maven** | 3.8+ | Gestión de dependencias y build |
| **Node.js** | 18+ | Runtime para frontend React/Vite |
| **Git** | 2.x | Control de versiones |

### Herramientas Recomendadas

- **IDE:** IntelliJ IDEA (Community/Ultimate), Eclipse o VSCode
- **Cliente PostgreSQL:** DBeaver, pgAdmin 4 o línea de comandos (`psql`)
- **Cliente HTTP:** Postman, Thunder Client (VSCode) o Insomnia
- **Navegador:** Chrome, Firefox, Edge o Safari (versiones recientes)

---

## Instalación Paso a Paso

### 1. Clonar el Repositorio
```bash
git clone https://github.com/tu-usuario/find-me.git
cd find-me
```

**Nota:** Reemplazar `tu-usuario` por el nombre de usuario real de GitHub si corresponde.

---

### 2. Instalar PostgreSQL + PostGIS

#### Opción A: Linux (Ubuntu/Debian)
```bash
# Actualizar repositorios
sudo apt update

# Instalar PostgreSQL
sudo apt install postgresql postgresql-contrib

# Instalar PostGIS
# Nota: ajustar la versión según la versión de PostgreSQL instalada
sudo apt install postgis postgresql-15-postgis-3

# Iniciar el servicio
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### Opción B: macOS (con Homebrew)
```bash
# Instalar PostgreSQL
brew install postgresql@15

# Instalar PostGIS
brew install postgis

# Iniciar el servicio
brew services start postgresql@15
```

#### Opción C: Windows

1. Descargar el instalador desde: https://www.postgresql.org/download/windows/
2. Ejecutar el instalador
3. Durante la instalación:
   - Registrar el puerto (por defecto: 5432)
   - Definir la contraseña para el usuario `postgres`
4. Abrir **Stack Builder** (incluido con PostgreSQL)
5. Seleccionar la instalación de PostgreSQL
6. En "Spatial Extensions", seleccionar **PostGIS**
7. Completar la instalación de PostGIS

---

## Configuración de la Base de Datos

### 1. Conectarse como Superusuario

#### Linux/macOS:
```bash
# Conectar como superusuario postgres
sudo -u postgres psql
```

#### Windows:

Abrir **SQL Shell (psql)** desde el menú de inicio y conectar como `postgres`.

---

### 2. Crear Base de Datos, Usuario y Habilitar PostGIS
```sql
-- Crear base de datos
CREATE DATABASE findme;

-- Crear usuario
CREATE USER findme_user WITH PASSWORD 'tu_password_seguro';

-- Otorgar privilegios
GRANT ALL PRIVILEGES ON DATABASE findme TO findme_user;

-- Conectar a la base de datos
\c findme

-- Habilitar extensión PostGIS
CREATE EXTENSION postgis;

-- Verificar versión de PostGIS
SELECT PostGIS_version();

-- Salir
\q
```

**Salida esperada de `SELECT PostGIS_version();`:**
```
         postgis_version          
----------------------------------
 3.3 USE_GEOS=1 USE_PROJ=1 ...
(1 row)
```

---

### 3. Verificar Conectividad
```bash
# Probar conexión con el nuevo usuario
psql -h localhost -U findme_user -d findme

# Si la conexión es correcta, listar tablas (debería estar vacío inicialmente)
\dt

# Salir
\q
```

---

## Configuración del Proyecto

### 1. Configurar variables de entorno

El proyecto toma su configuración principal desde variables de entorno.

Variables mínimas requeridas:

```env
DB_URL=jdbc:postgresql://localhost:5432/findme
DB_USER=findme_user
DB_PASS=tu_password_seguro
CORS_ORIGINS=http://localhost:5173
JWT_SECRET=un_secret_de_al_menos_32_caracteres
```

Variables recomendadas:

```env
PORT=8080
SECURITY_COOKIE_SECURE=false
SECURITY_HSTS_ENABLED=false
SECURITY_RATE_LIMIT_MAX_REQUESTS=60
SECURITY_RATE_LIMIT_WINDOW_SECONDS=60
FINDME_STORAGE_ROOT=./storage
ENCRYPTION_KEY=<base64_32_bytes>
HMAC_KEY=<base64_32_bytes>
```

Notas:
- El backend usa almacenamiento local de archivos en filesystem (`findme.storage.root`).
- Las imágenes se exponen mediante `/api/files/*` con soporte de URL firmada y expiración.
- `application.properties` ya contiene placeholders para estas variables.

---

## Ejecución del Sistema

### 1. Compilar el Proyecto
```bash
# Linux/macOS
./mvnw clean install

# Windows
mvnw.cmd clean install
```

**Salida esperada:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

---

### 2. Ejecutar el Backend
```bash
# Linux/macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

**Salida esperada (fragmento):**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.x.x)

...
Started Application in X.XXX seconds
```

---

### 3. Acceder al Sistema

Abrir el navegador en:

- **Frontend (Vite):** `http://localhost:5173`
- **Backend API:** `http://localhost:8080/api/*`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **API test rápido:** `http://localhost:8080/api/desaparecidos`

---

## Verificación de la Instalación

### 1. Verificar que la API responde
```bash
curl http://localhost:8080/api/desaparecidos
```

---

### 2. Verificar Swagger

Abrir en el navegador: `http://localhost:8080/swagger-ui.html`

Se debería visualizar la documentación interactiva de la API con sus endpoints.

---

### 3. Verificar Base de Datos
```bash
# Conectar a la base de datos
psql -h localhost -U findme_user -d findme

# Verificar tablas creadas por Hibernate
\dt

# Se esperan tablas como:
# - avistadores
# - desaparecidos
# - avistamientos
# - renaper_personas (si existe)

\q
```

---

### 4. Probar un Endpoint
```bash
# Listar personas desaparecidas (debería estar vacío al inicio)
curl http://localhost:8080/api/desaparecidos
```

**Respuesta esperada:**
```json
[]
```

---

## Troubleshooting

### Error: "Failed to configure a DataSource"

**Causa:** La configuración de DB (`DB_URL`, `DB_USER`, `DB_PASS`) es incorrecta o PostgreSQL no está en ejecución.

**Solución:**
```bash
# Verificar que PostgreSQL esté ejecutándose

# Linux:
sudo systemctl status postgresql

# macOS:
brew services list | grep postgresql

# Windows:
# Abrir 'services.msc' y buscar el servicio de PostgreSQL

# Verificar variables DB_URL / DB_USER / DB_PASS
# Verificar que base de datos, usuario y contraseña coincidan con lo creado
```

---

### Error: "PostGIS not found"

**Causa:** La extensión PostGIS no está instalada o habilitada en la base de datos `findme`.

**Solución:**
```bash
# Conectar a la base de datos
psql -h localhost -U findme_user -d findme

# Habilitar PostGIS (si aún no se hizo)
CREATE EXTENSION IF NOT EXISTS postgis;

# Verificar
SELECT PostGIS_version();

\q
```

---

### Error: "Port 8080 already in use"

**Causa:** Otro proceso está utilizando el puerto 8080.

**Solución:**

**Opción 1: Cambiar el puerto**

Definir variable de entorno:
```env
PORT=8081
```

**Opción 2: Detener el proceso que usa el puerto**
```bash
# Linux/macOS:
lsof -ti:8080 | xargs kill -9

# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID_encontrado> /F
```

---

### Error: "JWT secret too short"

**Causa:** El secreto JWT configurado es demasiado corto. Se recomienda al menos 32 caracteres (256 bits).

**Solución:**

Definir variable de entorno:
```env
JWT_SECRET=este_es_un_secret_mucho_mas_largo_con_al_menos_32_caracteres_de_longitud
```

---

### Error: "Failed to load ApplicationContext" (en tests)

**Causa:** Los tests no encuentran la configuración adecuada o la base de datos de pruebas.

**Solución:**

Crear `src/test/resources/application-test.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/findme_test
spring.datasource.username=findme_user
spring.datasource.password=tu_password_seguro

spring.jpa.hibernate.ddl-auto=create-drop

jwt.secret=test_secret_de_al_menos_32_caracteres_para_testing
```

Crear la base de datos de pruebas:
```bash
psql -U postgres
CREATE DATABASE findme_test;
GRANT ALL PRIVILEGES ON DATABASE findme_test TO findme_user;
\c findme_test
CREATE EXTENSION postgis;
\q
```

---

### Error: "No plugin found for prefix 'spring-boot'"

**Causa:** Maven no reconoce el plugin de Spring Boot (configuración incompleta en `pom.xml`).

**Solución:**

Verificar que `pom.xml` contenga:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

Luego ejecutar:
```bash
mvn clean install
mvn spring-boot:run
```

---

## Próximos Pasos

Una vez que el sistema esté funcionando correctamente en local:

1. **Explorar la API con Swagger**
   - `http://localhost:8080/swagger-ui.html`
   - Probar endpoints de creación y consulta

2. **Revisar la documentación técnica**
   - [Arquitectura del Sistema](../02-arquitectura/DOCUMENTACION_TECNICA.md)
   - [Stack Tecnológico](./STACK_TECNOLOGICO.md)

3. **Ejecutar los tests**
```bash
   mvn test
```

4. **Revisar la cobertura de tests**
```bash
   mvn jacoco:report
   # Abrir en el navegador: target/site/jacoco/index.html
```

5. **Cargar datos de prueba** (si existen scripts SQL de seed)
```bash
   psql -h localhost -U findme_user -d findme -f scripts/seed.sql
```

---

## Configuración del IDE

### IntelliJ IDEA

1. **Importar proyecto:**
   - File → Open → Seleccionar carpeta `find-me`
   - Esperar que IntelliJ detecte el proyecto Maven

2. **Configurar JDK:**
   - File → Project Structure → Project
   - Project SDK: 17 (o superior)

3. **Configurar Run Configuration:**
   - Run → Edit Configurations
   - Add New → Spring Boot
   - Main class: `com.findme.Application` (ajustar al paquete real)
   - JRE: 17

---

### VSCode

1. **Extensiones recomendadas:**
   - Java Extension Pack
   - Spring Boot Extension Pack

2. **Abrir proyecto:**
   - File → Open Folder → Seleccionar `find-me`

3. **Ejecutar:**
   - F5 o Run → Start Debugging

---

## Comandos Útiles
```bash
# Compilar sin ejecutar tests
mvn clean install -DskipTests

# Ejecutar solo los tests
mvn test

# Generar reporte de cobertura (si JaCoCo está configurado)
mvn jacoco:report

# Limpiar build anterior
mvn clean

# Ejecutar con un perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Ver árbol de dependencias
mvn dependency:tree

# Ver posibles actualizaciones de dependencias
mvn versions:display-dependency-updates
```

---

**Última actualización:** Marzo 2026  
**Versión:** 1.0

    
