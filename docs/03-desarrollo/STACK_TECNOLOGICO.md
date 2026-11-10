    # Stack Tecnológico - Find Me

Detalle completo de las tecnologías utilizadas y su justificación.

---

## Tabla de Contenidos

1. [Backend](#backend)
2. [Frontend](#frontend)
3. [Base de Datos](#base-de-datos)
4. [Testing](#testing)
5. [Infraestructura y DevOps](#infraestructura-y-devops)
6. [Herramientas de Desarrollo](#herramientas-de-desarrollo)
7. [Decisiones Arquitectónicas Clave](#decisiones-arquitectónicas-clave)
8. [Dependencias Maven Principales](#dependencias-maven-principales)
9. [Resumen Ejecutivo](#resumen-ejecutivo)
10. [Referencias](#referencias)

**Nota:** Las versiones indicadas son las utilizadas durante el desarrollo. Pueden actualizarse a versiones compatibles dentro de la misma línea mayor (por ejemplo, 3.x en Spring Boot).

---

## Backend

| Tecnología | Versión (ref.) | Propósito | Justificación |
|------------|----------------|-----------|---------------|
| **Java** | 17 LTS | Lenguaje principal | Robusto, maduro, amplio ecosistema. Requisito académico UTN. |
| **Spring Boot** | 3.x | Framework de aplicación | Convención sobre configuración, alta productividad, ecosistema completo. |
| **Spring Security** | 6.x | Autenticación y autorización | Integración nativa con Spring Boot, estándar de la industria. |
| **Spring Data JPA** | 3.x | Capa de persistencia (ORM) | Abstracción de base de datos, reduce boilerplate. |
| **Hibernate** | 6.x | Implementación JPA | ORM maduro con soporte geoespacial (Hibernate Spatial). |
| **Hibernate Spatial** | 6.x | Soporte geométrico en JPA | Mapeo de tipos PostGIS a objetos Java (JTS). |
| **JJWT** | 0.11.5 | Generación y validación de JWT | Librería madura, ampliamente usada y bien documentada. |
| **JTS Topology Suite** | 1.19+ | Geometrías en Java | Representación y manipulación de objetos geométricos (`Point`, `Polygon`, etc.). |
| **Jackson** | 2.16+ | Serialización/deserialización JSON | Integrado con Spring Boot, soporte para tipos complejos. |
| **Lombok** | 1.18+ | Reducción de boilerplate | Generación automática de getters, setters, constructores, builders. |
| **Springdoc OpenAPI** | 2.x | Documentación de API | Generación automática de Swagger UI y especificación OpenAPI 3. |
| **Spring Boot Mail** | 3.x | Envío de emails | JavaMailSender integrado en Spring Boot; notificaciones al contacto familiar. |
| **Thymeleaf** | 3.x | Templates de email HTML | Motor de plantillas server-side para generar el cuerpo HTML de los emails. |
| **Twilio SDK** | 9.x | Notificaciones SMS | Envío de SMS al celular del contacto familiar. Migración a WhatsApp Business API con cambio mínimo (ver ADR 008). |

### Justificación del Stack Backend

- **Java 17 LTS:** Versión estable con soporte extendido, incorpora features modernos (records, pattern matching, text blocks).
- **Spring Boot 3:** Línea actual de Spring, soporte nativo para Java 17+, mejoras de rendimiento y configuración simplificada.
- **Spring Security 6:** Modelo de configuración basado en lambdas, seguridad mejorada por defecto.
- **Hibernate Spatial + PostGIS:** Combinación estándar en el ecosistema Java para soportar queries geoespaciales avanzadas dentro de JPA.

---

## Frontend

| Tecnología | Versión (ref.) | Propósito | Justificación |
|------------|----------------|-----------|---------------|
| **React** | 18.x | Framework de UI | Componentización, reactividad declarativa y ecosistema maduro. |
| **Vite** | 5.x | Bundler/dev server | Arranque rápido en desarrollo y build de producción simple. |
| **React Router DOM** | 6.x | Enrutamiento cliente | Navegación por páginas sin recargar la app. |
| **Leaflet + React Leaflet** | 1.9.x / 4.x | Mapas interactivos | Open source, sin API keys, integración natural con componentes React. |
| **Tailwind CSS** | 3.x | Estilos utilitarios | Velocidad para construir UI consistente y responsive. |
| **shadcn/ui + Radix UI** | - | Base de componentes accesibles | Componentes reutilizables, composables y consistentes. |
| **TanStack React Query** | 5.x | Caché y fetch de datos | Manejo robusto de estado asíncrono y sincronización con API. |
| **Framer Motion** | 10.x | Animaciones UI | Transiciones y microinteracciones sin sobrecarga manual de CSS. |

### Decisión Clave: React + Vite para una UI evolutiva

**Motivos principales:**

- El frontend actual ya está implementado como SPA en React.
- El dominio visual creció (mapa, paneles, formularios, modales), y el modelo por componentes reduce acoplamiento.
- Vite simplifica el flujo de desarrollo y build.
- React Query y la separación por `services/`, `hooks/`, `components/` favorecen mantenibilidad.

**Trade-offs asumidos:**

- Mayor complejidad inicial que una web estática.
- Dependencia de toolchain Node para desarrollo/build.
- Necesidad de disciplina en arquitectura de componentes para evitar sobrecarga de estado.

---

## Base de Datos

| Tecnología | Versión (ref.) | Propósito | Justificación |
|------------|----------------|-----------|---------------|
| **PostgreSQL** | 15+ | Base de datos relacional | Open source, robusta. Se eligió la versión 15 por ser la más estable ofrecida en tiers gratuitos de proveedores cloud (Railway/Render) al momento del diseño, evitando problemas de compatibilidad reciente de versiones 16 y 17 en ciertos entornos, con soporte nativo completo para PostGIS 3.3. |
| **PostGIS** | 3.3+ | Extensión geoespacial | Estándar de facto para datos geoespaciales y consultas avanzadas. |

### PostgreSQL + PostGIS vs. MongoDB Geospatial

| Criterio | PostgreSQL + PostGIS | MongoDB Geospatial |
|----------|---------------------|-------------------|
| **Consultas espaciales avanzadas** | Completo (ST_DWithin, ST_Within, KNN, buffers, polígonos) | Limitado (queries geoespaciales básicas) |
| **Índices espaciales** | Índices GIST optimizados para geometrías | Índices `2dsphere` menos eficientes en casos complejos |
| **Integridad referencial** | Foreign keys nativas | Requiere validación manual |
| **Transacciones ACID** | Soporte completo | Limitado (a replica sets) |
| **Ecosistema Java** | Hibernate Spatial maduro | Menos maduro para geoespacial |

**Decisión:** PostgreSQL + PostGIS ofrece mejores capacidades para consultas geoespaciales complejas y garantiza integridad referencial, crítico para vincular personas, avistadores y avistamientos.

---

## Testing

| Tecnología | Versión (ref.) | Propósito |
|------------|----------------|-----------|
| **JUnit 5** | 5.10+ | Testing unitario |
| **Mockito** | 5.8+ | Mocking de dependencias |
| **AssertJ** | 3.25+ | Assertions fluidas y legibles |
| **Spring Boot Test** | 3.x | Testing de integración con Spring |
| **H2 / PostgreSQL test** | - | Base de datos para pruebas |
| **Vitest** | 4.x | Testing unitario de frontend |
| **Testing Library (React/Jest-DOM)** | 16.x / 6.x | Testing de componentes e interacción |
| **Playwright** | 1.58.x | Tests E2E del frontend |
| **JaCoCo** | 0.8.11+ | Medición de cobertura de código |

### Estrategia de Testing

- **Tests unitarios (backend):**
  - Casos de uso, mapeadores, validaciones, políticas de negocio
  - Objetivo: alta cobertura sobre lógica de dominio

- **Tests de integración (backend):**
  - Controladores + repositorios, utilizando base de datos de prueba (H2 o PostgreSQL de test con PostGIS si se requieren queries geoespaciales reales)

- **Tests de frontend:**
  - Componentes, servicios y utilidades con Vitest + Testing Library
  - Suite E2E definida con Playwright (frontend/e2e)

- **Cobertura objetivo:**
  - Entrega 1 (E1): ≥ 70%
  - Entrega 2 (E2): ≥ 80%

---

## Infraestructura y DevOps

| Tecnología | Propósito | Tier / Costo |
|------------|-----------|--------------|
| **Maven** | Build y gestión de dependencias | - |
| **npm** | Gestión de dependencias/build del frontend | - |
| **Git + GitHub** | Control de versiones | Gratuito |
| **Docker** | Contenerización (backend y frontend) | Gratuito |
| **Railway** | Hosting del backend + PostgreSQL | Gratuito (crédito y horas limitadas) |
| **Render** (opción) | Hosting alternativo del backend | Gratuito (750 hrs/mes aprox.) |
| **Vercel** (opcional) | Hosting del frontend SPA separado | Gratuito (Hobby) |
| **Storage local + URLs firmadas** | Almacenamiento de imágenes | Incluido en backend (filesystem + firma HMAC) |
| **GitHub Actions** (futuro) | CI/CD automatizado | Gratuito (límites de uso) |

**Restricción del proyecto:** Presupuesto $0 ARS → uso exclusivo de tiers gratuitos.

---

## Herramientas de Desarrollo

| Herramienta | Propósito |
|-------------|-----------|
| **IntelliJ IDEA** | IDE principal para el backend |
| **VSCode** | Editor alternativo (frontend + Java opcional) |
| **DBeaver** | Cliente de base de datos (PostgreSQL + PostGIS) |
| **pgAdmin** | Administración visual de PostgreSQL |
| **Postman / Thunder Client** | Testing de APIs REST |
| **Figma** | Diseño de mockups UI/UX |
| **Docker Desktop** | Contenerización y pruebas locales |

---

## Decisiones Arquitectónicas Clave

### 1. Clean Architecture + Hexagonal (Ports & Adapters)

**Alternativas consideradas:**
- Arquitectura en capas tradicional (3-tier)
- Arquitectura MVC simple acoplada al framework

**Decisión:** Clean Architecture + Hexagonal

**Justificación:**
- Separación clara de responsabilidades
- Dominio independiente de frameworks y detalles de infraestructura
- Alta testabilidad (casos de uso aislados, interfaces porteadas)
- Facilidad para cambiar adaptadores (por ejemplo, reemplazar PostgreSQL por otro datastore)
- Alineado con principios SOLID
- La mayor complejidad inicial es aceptable en el contexto académico y se compensa con mejor mantenibilidad

---

### 2. Leaflet vs. Google Maps

| Criterio | Leaflet + OpenStreetMap | Google Maps API |
|----------|-------------------------|-----------------|
| **Costo** | $0 (open source) | Requiere tarjeta y facturación |
| **Límites de uso** | Sin límites estrictos | 28.000 cargas/mes gratis aprox. |
| **Tamaño de librería** | ~40 KB | ~300 KB |
| **Dependencia** | Comunidad open source | Servicio comercial de Google |
| **Flexibilidad** | Muy alta (sin vendor lock-in) | Condicionada a los Términos de Uso |

**Decisión:** Leaflet es la opción más adecuada para un proyecto académico sin presupuesto, con total control sobre los mapas y sin dependencia de servicios comerciales.

---

### 3. JWT vs. Sesiones Tradicionales

| Criterio | JWT en cookies HttpOnly | Sesiones tradicionales en servidor |
|----------|------------------------|-----------------------------------|
| **Estado (stateless)** | Sí | No (requiere store de sesión) |
| **Escalabilidad horizontal** | Alta | Menor (necesita sticky sessions o store compartido) |
| **Performance** | Alta (sin consulta extra por request) | Depende de la consulta al store de sesión |
| **Seguridad** | Elevada (HttpOnly + SameSite) | Equivalente si se implementa correctamente |

**Decisión:** JWT es más adecuado para una API RESTful stateless, con buenas propiedades de escalabilidad y sin necesidad de mantener estado de sesión en el servidor.

---

### 4. Hibernate Spatial vs. JDBC Puro

**Problema:** JPA estándar no soporta de forma nativa los tipos geométricos de PostGIS (`GEOMETRY`, `POINT`, `POLYGON`, etc.).

**Solución adoptada:** Hibernate Spatial, que permite:
- Mapear tipos `GEOMETRY(Point, 4326)` a `org.locationtech.jts.geom.Point`
- Ejecutar consultas espaciales (ST_DWithin, ST_Within, etc.) a través de JPA
- Integrarse con Spring Data JPA manteniendo la consistencia del repositorio

**Alternativa descartada:** Consultas JDBC puras para toda la lógica geoespacial, que aumentarían el boilerplate y la complejidad del código.

---

## Dependencias Maven Principales
```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- PostgreSQL + PostGIS -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-spatial</artifactId>
    </dependency>

    <!-- JTS para geometrías -->
    <dependency>
        <groupId>org.locationtech.jts</groupId>
        <artifactId>jts-core</artifactId>
        <version>1.19.0</version>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Swagger/OpenAPI -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.2.0</version>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## Resumen Ejecutivo

**Backend:** Java 17 + Spring Boot 3 + Spring Security + Spring Data JPA + Hibernate Spatial

**Base de datos:** PostgreSQL 15 + PostGIS 3.3

**Frontend:** React 18 + Vite + React Leaflet + Tailwind + shadcn/ui + React Query

**Testing:** JUnit 5, Mockito, Spring Boot Test, Vitest, Testing Library, Playwright, JaCoCo

**Deploy:** Railway/Render para backend + BD, con opción de frontend separado (Vercel) o empaquetado estático en backend

**Presupuesto:** $0 ARS (uso de tiers gratuitos)

---

## Referencias

- [Documentación oficial de Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Documentación oficial de PostGIS](https://postgis.net/documentation/)
- [Documentación de Leaflet](https://leafletjs.com/reference.html)
- [Guía de Hibernate Spatial](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#spatial)
- [JTS Topology Suite – documentación y ejemplos](https://locationtech.github.io/jts/)

---

**Última actualización:** Marzo 2026  
**Versión:** 1.0

    
