# Alcance del snapshot público

Origen: rama privada `demo-estable`, corte del 4 de septiembre de 2026. El
historial privado no se trasladó a este repositorio.

Se conserva el código Java/Spring Boot/PostGIS, React/Leaflet, migraciones,
pruebas, guías técnicas y configuración de ejecución de esa etapa. Se excluyen
artefactos compilados, archivos de IDE, configuración local de agentes,
almacenamiento local y documentos internos de gestión. Las semillas y pruebas
usan personas, DNI y ubicaciones de ejemplo inventados.

Las dependencias del frontend se actualizaron en este snapshot para resolver
alertas de seguridad del lockfile original. El comportamiento se comprobó con
las pruebas React y el build de la copia pública.

Esta publicación permite inspeccionar la implementación de ese prototipo.
No representa versiones posteriores ni valida una integración oficial con
RENAPER. Antes de cualquier uso con datos reales se requieren integración,
seguridad, privacidad, operación y autorización institucional apropiadas.

## Verificación

GitHub Actions ejecuta pruebas Java con PostGIS, pruebas React, cobertura,
compilación y pruebas de navegador. Los resultados del workflow del propio
repositorio son la evidencia vigente; una compilación Vercel no equivale a
pasar la suite.
