# Documentación técnica · Find Me (corte demo-estable)

Esta documentación pertenece al [snapshot público](../../PUBLIC_SNAPSHOT.md) del prototipo académico. Describe la etapa `demo-estable`, no versiones posteriores ni un servicio listo para datos reales. La documentación de gestión interna no forma parte del snapshot.

| Sección | Documento | Contenido |
| --- | --- | --- |
| Arquitectura | [Documentación técnica](../02-arquitectura/DOCUMENTACION_TECNICA.md) | Capas, dominio, adaptadores y consultas espaciales. |
| Desarrollo | [Guía de instalación](../03-desarrollo/GUIA_INSTALACION.md), [frontend](../03-desarrollo/FRONTEND.md), [stack](../03-desarrollo/STACK_TECNOLOGICO.md) | Entorno y estructura del código. |
| Uso | [Manual](../04-usuario/MANUAL_USUARIO.md) | Recorrido previsto del prototipo. |
| Despliegue | [Guía](../05-deployment/GUIA_DEPLOYMENT.md) | Diseño de despliegue; requiere revisión de seguridad y privacidad antes de cualquier uso real. |
| Calidad | [Estrategia de pruebas](../06-calidad/ESTRATEGIA_TESTING.md) | Pruebas y cobertura. |
| Decisiones | [ADR 001–008](../08-adr/001-clean-architecture.md) | Decisiones de arquitectura, PostGIS, seguridad, React y notificaciones. |

El [workflow de CI](../../.github/workflows/ci.yml) ejecuta backend, frontend y navegador en la rama pública. Las semillas de identidad son **simuladas** y no existe integración oficial con RENAPER.
