# ADR 007: Notificaciones por email con Composite Adapter

## Contexto

El sistema ya notificaba nuevos avistamientos a clientes web conectados mediante WebSocket
(`WebsocketNotificadorAdapter`). Se requería extender las notificaciones para enviar un email
al `contactoFamiliar` del desaparecido (campo cifrado con AES-256-GCM) cada vez que se
registra un nuevo avistamiento.

El puerto de salida `INotificadorAvistamiento` ya estaba integrado en `AvistamientoCommandService`
(llamado tras el `save`, sin modificar el Use Case).

## Decisión

Aplicar el patrón **Composite** para agregar canales de notificación sin modificar:
- El Use Case ni su servicio de aplicación.
- El `WebsocketNotificadorAdapter` existente.

Se crean dos clases nuevas en `infrastructure/adapters/out/email/`:

1. **`EmailNotificadorAdapter`** (`@Component`): encapsula el envío de email con
   `JavaMailSender` + `TemplateEngine` (Thymeleaf). Recupera el desaparecido por id para
   obtener el `contactoFamiliar` (ya descifrado por el converter JPA). Captura cualquier
   excepción internamente y la loguea sin propagarla, para no romper el flujo principal.

2. **`CompositeNotificadorAdapter`** (`@Primary @Service`, implementa `INotificadorAvistamiento`):
   delega secuencialmente a `WebsocketNotificadorAdapter` (por tipo concreto) y a
   `EmailNotificadorAdapter`. Al ser `@Primary`, Spring lo inyecta donde se requiere
   `INotificadorAvistamiento`.

El envío de email se controla con la propiedad `mail.enabled` (default `false`).
Cuando está deshabilitado, `EmailNotificadorAdapter` retorna inmediatamente sin tocar
`JavaMailSender`, lo que permite que el sistema funcione igual en desarrollo y tests.

## Template

Se usa **Thymeleaf** (`notificacion-avistamiento.html`) para generar el HTML del email.
Incluye: nombre del desaparecido, fecha/hora del avistamiento, coordenadas, descripción,
enlace a la foto (si existe) y link al avistamiento en el sistema.

## Consecuencias

**Positivas:**
- El Use Case no se toca (REGLA FUNDAMENTAL del proyecto).
- Agregar un tercer canal (SMS, push) requiere solo un nuevo `@Component` y una línea
  adicional en `CompositeNotificadorAdapter`.
- Email falla de forma silenciosa: el avistamiento se persiste y el WebSocket notifica
  aunque SMTP no esté disponible.
- `mail.enabled=false` en tests y desarrollo evita dependencias externas.

**Negativas:**
- El composite llama los adaptadores en secuencia; si el WebSocket lanza excepción,
  el email no se intenta. Se acepta este trade-off por simplicidad (raro que WebSocket falle).
- La recuperación del desaparecido dentro del adaptador de email implica una consulta
  extra a la base de datos por avistamiento. Se acepta dado el bajo volumen del caso de uso.
