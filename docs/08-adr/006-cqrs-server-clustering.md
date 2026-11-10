# ADR 006: Refactor CQRS en Servicio de Avistamientos y Estrategia de Clustering en Mapa

## Contexto y Problema

El servicio `AvistamientoService` creció desproporcionadamente (~250 líneas), aglomerando responsabilidades cruzadas: inserción, validaciones de seguridad transaccionales, consultas enriquecidas geoespaciales, filtros de exposición (perfilamiento de privacidad), inyección masiva de repositorios, y mapeo de dominios hacia el frontend. Este diseño violaba el Principio de Responsabilidad Única (SRP) dificultando su mantenimiento y escalabilidad.

Simultáneamente, conforme la base de datos de avistamientos escala (potencialmente de 10k a 100k registros), retornar arreglos planos completos de `List<AvistamientoFrontDTO>` desde `/api/avistamientos/mapa` representa una sobrecarga de memoria inaceptable tanto para PostGIS, la JVM de Spring Boot, como para la renderización de Leaflet en el cliente. Si bien el frontend ya agrupaba visualmente apoyado en `react-leaflet-cluster` (wrapper de Supercluster), el cuello de botella persistía en la transferencia de red y carga persistente.

## Decisiones

### 1. Refactor hacia Patrón CQRS (Command Query Responsibility Segregation)

Se ha dividido `AvistamientoService` en dos componentes especializados:
- **`AvistamientoCommandService`**: Responsable exclusivamente de los escenarios transaccionales de escritura y validaciones de negocio (ej. inserciones).
- **`AvistamientoQueryService`**: Dedicado a extraer flujos de lectura eficientes hacia la capa de presentación (frontend), enriquecimientos en memoria y chequeos de perfilado de privacidad y exposición geoespacial. 

### 2. Estrategia Paginada y de Clustering Espacial

Para resolver la penalización de carga en el frontend y optimizar memoria:
- **Decisión Activa**: Utilizar las capacidades perimetrales ya construidas al consumir por "Bounding Box" (`/api/avistamientos/mapa/area`). Cada vez que el cliente panea (zoom/drag) en React, actualiza las cotas (bounds) para hacer *fetch* sólo de los puntos ubicables geográficamente en pantalla. El frontend continúa consolidando nodos menores de zoom usando **Supercluster**.
- **Decisión Futura**: Migrar a una estrategia agresiva Server-Side mediante PostGIS utilizando `ST_ClusterKMeans` empotrada en repositorios nativos en niveles de zoom alejados, agrupando puntos geográficos en centroides del lado de la base de datos.

## Consecuencias

* Positivas
  * Facilita el TDD y la segregación natural de Casos de Uso del core backend.
  * Reduce la complejidad ciclomática por clase. 
  * Optimización potencial de memoria RAM en clientes, disminuyendo sobrecargas.
* Negativas
  * Aumenta la fricción conceptual para nuevos desarrolladores al introducir CQRS.
