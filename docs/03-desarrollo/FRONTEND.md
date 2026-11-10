# Documentacion Tecnica del Frontend — Find Me

**Stack:** React 18 + Vite + Tailwind CSS + shadcn/ui
**Directorio raiz:** `frontend/`

---

## Tabla de Contenidos

1. [Estructura de carpetas](#estructura-de-carpetas)
2. [Paginas](#paginas)
3. [Componentes](#componentes)
4. [Hooks](#hooks)
5. [Servicios](#servicios)
6. [Utilidades](#utilidades)
7. [Constantes y configuracion](#constantes-y-configuracion)
8. [Tests](#tests)
9. [Como agregar un nuevo componente](#como-agregar-un-nuevo-componente)

---

## Estructura de carpetas

```
frontend/src/
|-- App.jsx                  # Raiz de la aplicacion: rutas y providers globales
|-- Layout.jsx               # Layout compartido (navbar, contenedor)
|-- main.jsx                 # Punto de entrada React
|-- pages/                   # Pantallas de la aplicacion (una por ruta)
|-- components/
|   |-- cards/               # Tarjetas de entidades (DesaparecidoCard)
|   |-- forms/               # Formularios reutilizables (AuthForm)
|   |-- map/                 # Todo lo relacionado al mapa interactivo
|   `-- ui/                  # Primitivas UI generadas por shadcn/ui
|-- hooks/                   # Custom hooks de React
|-- services/                # Clientes HTTP hacia el backend
|-- utils/                   # Funciones puras de utilidad
|-- config/
|   `-- constants.jsx        # Endpoints de API y mapas de errores
|-- lib/
|   `-- utils.jsx            # Helpers de clases CSS (cn())
`-- test/                    # Tests unitarios espejando la estructura de src/
```

**Criterio de organizacion:**

- `pages/` contiene solo componentes que representan una ruta completa. No deben usarse directamente desde otro componente; siempre se invocan desde el router en `App.jsx`.
- `components/` contiene todo lo reutilizable. Se subdivide por dominio (`map/`, `cards/`, `forms/`) y por tipo de primitiva (`ui/`).
- `services/` contiene toda la logica de comunicacion HTTP. Los componentes nunca llaman a `fetch` directamente; siempre pasan por un servicio.
- `hooks/` contiene estado reactivo complejo que se comparte entre componentes.

---

## Paginas

### `pages/MapaAvistamientos.jsx`

Pantalla principal. Muestra un mapa Leaflet interactivo con marcadores de avistamientos. Permite seleccionar filtros geograficos (radio, area rectangular, poligono) y reportar nuevos avistamientos si el usuario esta autenticado.

Usa los hooks `useMapFilters` y `useReportMode` para separar la logica del estado de la vista.

### `pages/ListaDesaparecidos.jsx`

Lista paginada de personas desaparecidas. Permite buscar por nombre/apellido, filtrar por estado (buscando, encontrado, sin_novedades) y ordenar resultados. Cada item se renderiza como un `DesaparecidoCard`.

### `pages/RegistrarDesaparecido.jsx`

Formulario para registrar una nueva persona desaparecida. Requiere autenticacion. Permite adjuntar una foto que se sube previamente via `FileService.upload()` y cuyo `fileId` se incluye en el cuerpo del POST.

### `pages/Registro.jsx`

Formulario de registro de avistador. Valida el DNI contra RENAPER en el backend. Al crear la cuenta, el backend setea una cookie de sesion y el frontend llama a `AuthService.checkAuth()` para hidratar el contexto de autenticacion.

### `pages/IniciarSesion.jsx`

Formulario de login con DNI y email. Delega completamente en `AuthService.login()`. Al exito, redirige al mapa.

---

## Componentes

### `components/forms/AuthForm.jsx`

Formulario generico reutilizable para autenticacion. Recibe una lista de `fields` como prop y los renderiza dinamicamente. Soporta campos simples y filas de dos columnas (`type: "row"`). Se usa tanto en `IniciarSesion` como en `Registro`.

**Props principales:**

| Prop | Tipo | Descripcion |
|------|------|-------------|
| `title` | string | Titulo del formulario |
| `subtitle` | string | Subtitulo opcional |
| `fields` | array | Definicion de campos (ver abajo) |
| `submitLabel` | string | Texto del boton de envio |
| `onSubmit` | function | Handler del form submit |
| `isLoading` | boolean | Muestra spinner y deshabilita el boton |
| `alternateLink` | string | Nombre de ruta para el link alternativo |
| `alternateLinkText` | string | Texto del link alternativo |
| `icon` | component | Icono Lucide para el header |

Formato de un campo simple:
```js
{ name: "dni", label: "DNI", placeholder: "...", required: true, value, onChange }
```

Formato de una fila doble:
```js
{ type: "row", fields: [campoA, campoB] }
```

### `components/cards/DesaparecidoCard.jsx`

Tarjeta que muestra el resumen de una persona desaparecida: foto (con blur si `fotoBloqueada` es true), nombre, DNI, estado, descripcion, fecha de desaparicion, ultima ubicacion y botones de accion (ver en mapa, contactar).

### `components/ui/FotoProtegida.jsx`

Componente de imagen que aplica blur (`blur-lg`) cuando la prop `bloqueada` es true y muestra un overlay con icono de candado. Se usa para ocultar fotos de personas a usuarios no autenticados, controlado por el campo `fotoBloqueada` que devuelve el backend segun el nivel de exposicion (`NivelExposicion`).

**Props:**

| Prop | Tipo | Default | Descripcion |
|------|------|---------|-------------|
| `src` | string | — | URL de la imagen |
| `alt` | string | `"Foto"` | Texto alternativo |
| `bloqueada` | boolean | `false` | Si true, aplica blur + overlay |
| `className` | string | `"w-16 h-16"` | Clases del contenedor |
| `imgClassName` | string | — | Clases adicionales del `<img>` |
| `fallbackIcon` | ReactNode | icono User | Contenido cuando no hay foto |
| `mensaje` | string | `"Iniciá sesión para ver"` | Texto del overlay bloqueado |

### `components/map/FilterBar.jsx`

Barra lateral de filtros del mapa. Renderiza botones iconicos para cada tipo de filtro disponible (todos, radio, area, poligono, desaparecido, ruta). Muestra un tooltip al hacer hover. Incluye boton de reset cuando hay un filtro activo.

### `components/map/MapClickHandler.jsx`

Componente de Leaflet que maneja los clicks sobre el mapa. Segun el estado activo (seleccionando centro de radio, dibujando poligono, modo reporte), deriva el evento al handler correspondiente.

### `components/map/AreaDrawHandler.jsx`

Maneja el dibujo de rectangulos sobre el mapa via drag. Calcula el bounding box a partir de los puntos de inicio y fin del arrastre.

### `components/map/MapPopupContent.jsx`

Contenido del popup que aparece al hacer click en un marcador de avistamiento. Muestra datos del avistamiento y un link al desaparecido relacionado.

### `components/map/MarkerWithCenter.jsx`

Marcador de Leaflet que emite un evento `onMount` con su referencia para permitir centrar el mapa sobre el.

### `components/map/PersonasSidebar.jsx`

Panel lateral deslizable que lista personas desaparecidas. Se usa en el modo de filtro por desaparecido para seleccionar la persona cuya ruta de avistamientos se quiere ver.

### `components/map/ReportModal.jsx`

Modal que se abre al hacer click en el mapa en modo reporte. Contiene el formulario para crear un nuevo avistamiento en las coordenadas seleccionadas.

### `components/ui/` (primitivas shadcn/ui)

Todos los archivos en `components/ui/` son componentes de la libreria **shadcn/ui** generados con su CLI. Son wrappers sobre **Radix UI** estilados con Tailwind. No modificar directamente; si se necesita variacion de estilo, crear un wrapper nuevo en `components/`.

Los mas usados en el proyecto: `Button`, `Card`, `Badge`, `Input`, `Label`, `Dialog`, `Select`, `Separator`, `Sonner` (toasts).

---

## Hooks

### `hooks/useAuth.jsx`

Provee contexto de autenticacion a toda la aplicacion.

Exporta dos cosas:

1. **`AuthProvider`** — Provider de React Context que envuelve la app en `App.jsx`. Inicializa la sesion al montar llamando a `AuthService.checkAuth()` y se suscribe a cambios del `AuthService`.

2. **`useAuth()`** — Hook para consumir el contexto desde cualquier componente. Devuelve:

| Campo | Tipo | Descripcion |
|-------|------|-------------|
| `user` | object \| null | Datos del usuario autenticado |
| `isLoading` | boolean | True mientras se verifica la sesion inicial |
| `isAuthenticated` | boolean | True si hay sesion activa |
| `login(dni, email)` | async function | Llama a `AuthService.login()` |
| `logout()` | async function | Llama a `AuthService.logout()` |

### `hooks/useMapFilters.jsx`

Centraliza todo el estado de filtros del mapa en un unico hook. Gestiona el tipo de filtro activo, los parametros de cada filtro (centro y radio en km, bounding box, puntos del poligono, persona seleccionada) y los estados de interaccion (si se esta dibujando, si se esta seleccionando centro).

**Retorna:**

- `filtroActivo` — tipo de filtro activo (constante de `FILTER_TYPES`)
- `activateFilter(type, onStatusChange)` — cambia el filtro activo y notifica al usuario
- `resetFilters(onStatusChange)` — limpia todos los filtros
- Estado individual de cada filtro: `radioCenter`, `radioKm`, `areaBounds`, `polygonPoints`, etc.

### `hooks/useReportMode.jsx`

Gestiona el flujo de reporte de avistamientos sobre el mapa.

**Retorna:**

- `modoReporte` — boolean, si el usuario esta en modo reporte
- `toggleModoReporte(onStatusChange)` — activa/desactiva el modo. Si el usuario no esta autenticado, redirige a login.
- `openReportModalAt(latlng)` — abre el modal de reporte con las coordenadas elegidas
- `handleReportSuccess(onAfterSuccess)` — cierra el modal y sale del modo reporte al completar el envio

---

## Servicios

Todos los servicios viven en `services/` y se reexportan desde `services/index.jsx`. Siguen el mismo patron:

1. Llaman a `fetchWithAuth()` (que agrega `credentials: "include"` para enviar cookies).
2. Si la respuesta no es `ok`, parsean el cuerpo con `parseProblem()` y obtienen un mensaje amigable con `getErrorMessage()`.
3. Lanzan un `Error` con el mensaje ya formateado para el usuario.

### `services/AuthService.js`

Servicio singleton de autenticacion con estado en modulo (no en React). Mantiene el usuario actual en memoria y notifica a los suscriptores (tipicamente `AuthProvider`) ante cada cambio.

| Metodo | Descripcion |
|--------|-------------|
| `checkAuth()` | Consulta `GET /api/v1/auth/me`. Si hay sesion activa, hidrata `currentUser`. |
| `login(dni, email)` | `POST /api/v1/auth/login`. Hidrata `currentUser` e invalida el cache de TanStack Query. |
| `logout()` | `POST /api/v1/auth/logout`. Limpia `currentUser` e invalida el cache. |
| `subscribe(listener)` | Registra un listener de cambios. Devuelve la funcion de desuscripcion. |
| `getCurrentUser()` | Devuelve el usuario actual de forma sincrona. |
| `isAuthenticated()` | Devuelve `true` si hay usuario en memoria. |
| `setQueryClient(qc)` | Registra el `QueryClient` de TanStack para invalidar cache en login/logout. |

### `services/AvistadorService.js`

| Metodo | Descripcion |
|--------|-------------|
| `crear(data)` | `POST /api/v1/avistadores` — Registra un nuevo avistador. |
| `obtenerTodos()` | `GET /api/v1/avistadores` — Lista todos los avistadores. |

### `services/DesaparecidoService.js`

| Metodo | Descripcion |
|--------|-------------|
| `obtenerPaginado(filtros)` | `GET /api/v1/desaparecidos?...` con soporte de paginacion y filtros. Normaliza respuesta paginada o array plano. |
| `obtenerPorId(id)` | `GET /api/v1/desaparecidos/{id}` |
| `crear(data, fotoFile)` | Sube la foto si existe via `FileService.upload()`, luego `POST /api/v1/desaparecidos` con el `fotoFileId`. |
| `getEstadisticas()` | `GET /api/v1/desaparecidos/estadisticas` |
| `obtenerTodos(filtros)` | Deprecado. Usar `obtenerPaginado`. |

Los filtros soportados por `obtenerPaginado`: `search`, `estado`, `sort`, `page`, `size`.

### `services/FileService.js`

| Metodo | Descripcion |
|--------|-------------|
| `upload(file, personId, entityType, category)` | `POST /api/v1/files/upload` con `multipart/form-data`. Devuelve `{ fileId, downloadUrl, mimeType, sizeBytes }`. |
| `getUrl(fileId)` | Genera la URL de descarga `/api/v1/files/{fileId}` sin hacer fetch. |

`entityType`: `'DESAPARECIDO'` o `'AVISTAMIENTO'`.
`category`: `'PHOTO'` (default) o `'DOC'`.

### `services/AvistamientoService.jsx`

Metodos para crear avistamientos y obtenerlos con distintos tipos de filtro geografico (radio, area, poligono, por desaparecido). Consultar el archivo para la firma exacta de cada metodo.

---

## Utilidades

### `utils/fetch.jsx` — `fetchWithAuth(url, options)`

Wrapper de `fetch` que agrega `credentials: "include"` automaticamente para enviar cookies de sesion en cada request. Si el body es `FormData`, no agrega el header `Content-Type` (el browser lo calcula con el boundary correcto). Todos los servicios usan esta funcion.

### `utils/errors.jsx`

| Funcion | Descripcion |
|---------|-------------|
| `parseProblem(resp)` | Parsea una respuesta HTTP a `{ status, key, detail }`. Soporta ProblemDetail (RFC 9457) con el campo `key` en `properties.key`. |
| `getErrorMessage(errorMap, status, key, detail)` | Busca el mensaje amigable en el mapa de errores de `constants.jsx`. Primero por `status + key`, luego por `status + default`, luego `detail` crudo. |

### `utils/index.jsx`

Funciones de utilidad generales. Incluye `createPageUrl(pageName)` que convierte un nombre de pagina en la ruta correspondiente para `react-router-dom`.

### `lib/utils.jsx`

Exporta la funcion `cn(...classes)` (de `clsx` + `tailwind-merge`) para componer clases de Tailwind sin conflictos. Se usa en todos los componentes de `components/ui/`.

---

## Constantes y configuracion

### `config/constants.jsx`

**`API_ENDPOINTS`** — Objeto con las URLs base de cada recurso:

```js
{
  DESAPARECIDOS: "/api/v1/desaparecidos",
  AVISTADORES:   "/api/v1/avistadores",
  AUTH:          "/api/v1/auth",
  AVISTAMIENTOS: "/api/v1/avistamientos",
  FILES:         "/api/v1/files"
}
```

**`ERROR_MAPS`** — Mapas de errores por dominio (`AVISTADOR`, `DESAPARECIDO`, `AUTH`, `AVISTAMIENTO`, `FILE`). Cada entrada tiene la forma `{ [httpStatus]: { [errorKey]: "mensaje amigable", default: "..." } }`. Se consultan via `getErrorMessage()`.

---

## Tests

### Tests unitarios (Vitest + Testing Library)

```bash
cd frontend

# Correr todos los tests una vez
npm run test

# Correr con reporte de cobertura
npm run test:js:cov

# Verificar umbrales de cobertura por modulo
npm run test:js:cov:check
```

Los tests viven en `src/test/` espejando la estructura de `src/`:

```
src/test/
|-- App.test.jsx
|-- pages/
|   |-- IniciarSesion.test.jsx
|   |-- Registro.test.jsx
|   |-- ListaDesaparecidos.test.jsx
|   `-- RegistrarDesaparecido.test.jsx
|-- components/
|   |-- cards/DesaparecidoCard.test.jsx
|   |-- forms/AuthForm.test.jsx
|   `-- map/
|       |-- AreaDrawHandler.test.jsx
|       |-- FilterBar.test.jsx
|       |-- FilterConfigPanel.test.jsx
|       |-- MapClickHandler.test.jsx
|       |-- MapPopupContent.test.jsx
|       |-- MapUtils.test.jsx
|       |-- MarkerWithCenter.test.jsx
|       |-- PersonasSidebar.test.jsx
|       `-- ReportModal.test.jsx
|-- hooks/
|   |-- useAuth.test.jsx
|   |-- useMapFilters.test.jsx
|   `-- useReportMode.test.jsx
|-- services/
|   |-- AuthService.test.jsx
|   |-- AvistadoService.test.jsx
|   |-- AvistamientoService.test.jsx
|   |-- DesaparecidoService.test.jsx
|   |-- FileService.test.jsx
|   `-- index.test.jsx
`-- utils/
    |-- errors.test.js
    |-- fetch.test.jsx
    `-- index.test.js
```

### Tests E2E (Playwright)

```bash
cd frontend

# Requiere backend corriendo en localhost:8080
npm run test:e2e

# Ver el reporte HTML del ultimo run
npm run test:e2e:report
```

Los specs de Playwright viven en `frontend/e2e/`. Antes de correrlos, cargar el seed E2E:

```bash
# Seed de datos para E2E (ver variables en scripts/e2e/seed-e2e-data.mjs)
E2E_USER_DNI=44215507 E2E_USER_EMAIL=e2e@test.com \
E2E_MISSING_DNI=35123456 \
node scripts/e2e/seed-e2e-data.mjs
```

---

## Como agregar un nuevo componente

### 1. Determinar donde va

- Es una pantalla nueva con ruta propia → `pages/NombrePantalla.jsx`
- Es UI reutilizable en el mapa → `components/map/`
- Es una tarjeta de entidad de dominio → `components/cards/`
- Es un formulario reutilizable → `components/forms/`
- Es una primitiva UI sin logica de negocio → `components/ui/`

### 2. Crear el archivo

Convencion de nombres: **PascalCase** para componentes React, **camelCase** para hooks y utilidades.

```jsx
// components/cards/AvistamientoCard.jsx
import React from "react";
import { Card } from "@/components/ui/card";

/**
 * Tarjeta de avistamiento.
 * @param {object} props
 * @param {object} props.avistamiento - Datos del avistamiento
 * @param {number} props.index - Indice para la animacion escalonada
 */
export default function AvistamientoCard({ avistamiento, index }) {
  return (
    <Card>
      {/* contenido */}
    </Card>
  );
}
```

### 3. Usar el alias de path `@/`

El proyecto configura `@` como alias de `src/`. Siempre importar con `@/` en lugar de rutas relativas complejas:

```js
// Correcto
import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/useAuth";

// Evitar cuando hay mas de dos niveles de profundidad
import { Button } from "../../components/ui/button";
```

### 4. Crear el test espejo

Crear `src/test/components/cards/AvistamientoCard.test.jsx` siguiendo el patron de los tests existentes.

```jsx
import { render, screen } from "@testing-library/react";
import { describe, it, expect } from "vitest";
import AvistamientoCard from "@/components/cards/AvistamientoCard";

describe("AvistamientoCard", () => {
  it("muestra los datos del avistamiento", () => {
    const avistamiento = { id: "1", descripcion: "Visto en plaza" };
    render(<AvistamientoCard avistamiento={avistamiento} index={0} />);
    expect(screen.getByText("Visto en plaza")).toBeInTheDocument();
  });
});
```

### 5. Si necesita llamar al backend

No llamar a `fetch` directamente desde el componente. Agregar el metodo al servicio correspondiente (o crear uno nuevo en `services/`) siguiendo el patron de `fetchWithAuth` + `parseProblem` + `getErrorMessage`. Si hay un nuevo dominio de errores, agregar la entrada en `ERROR_MAPS` dentro de `config/constants.jsx`.

### 6. Si necesita estado complejo compartido

Crear un custom hook en `hooks/useNombreLogica.jsx`. El hook debe:
- Recibir solo las dependencias externas que necesita (ej: `isAuthenticated`, `navigate`)
- Devolver un objeto plano con el estado y las acciones
- Tener su propio test en `src/test/hooks/`
