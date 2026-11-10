# ADR 008: Canal SMS via Twilio para notificaciones al contacto familiar

## Contexto

El sistema notifica al `emailContactoFamiliar` por email cuando hay un nuevo avistamiento.
Sin embargo, el `contactoFamiliar` es un número de celular, y muchos familiares prefieren
o solo tienen acceso a SMS/WhatsApp en lugar de email. Se evaluaron las opciones disponibles.

## Alternativas consideradas

| Criterio | SMS via Twilio | WhatsApp Business API (Twilio) |
|---|---|---|
| **Aprobación requerida** | No (inmediato) | Sí — requiere aprobación de Meta (semanas) |
| **Costo** | ~$0.0075/SMS en Argentina | Similar a SMS, pero con proceso de onboarding |
| **Entrega** | Alta (SMS universal) | Alta, pero solo en usuarios con WhatsApp |
| **Confiabilidad** | Muy alta | Alta, depende de WhatsApp Business |
| **Tiempo de implementación** | Inmediato | Bloqueado por aprobación de Meta |

## Decisión

Implementar **SMS via Twilio** para el canal de celular (`contactoFamiliar`).

La API de Twilio para SMS y para WhatsApp es prácticamente idéntica: el único cambio
para migrar a WhatsApp es el prefijo `whatsapp:` en el número de origen y destino.
El `SmsNotificadorAdapter` incluye un comentario explícito indicando el punto de
migración exacto.

```java
// Para migrar a WhatsApp Business API via Twilio:
// new PhoneNumber("whatsapp:" + fromNumber)
// new PhoneNumber("whatsapp:" + telefono)
```

## Arquitectura del canal de notificaciones resultante

```
CompositeNotificadorAdapter (@Primary)
  ├── WebsocketNotificadorAdapter   → tiempo real (siempre activo)
  ├── EmailNotificadorAdapter       → emailContactoFamiliar (si mail.enabled=true)
  └── SmsNotificadorAdapter         → contactoFamiliar/celular (si sms.enabled=true)
```

Cada canal decide internamente si notificar según:
1. Si su flag `*.enabled` está activo.
2. Si el desaparecido tiene el campo correspondiente (`emailContactoFamiliar` o `contactoFamiliar`).

## Nota de migración futura

**WhatsApp Business API via Twilio es un reemplazo directo del SMS adapter
cuando se obtenga aprobación de Meta.** El cambio impacta únicamente en:
- `SmsNotificadorAdapter`: dos líneas de código (prefijo `whatsapp:`).
- `application.properties` / `.env.example`: `TWILIO_PHONE_NUMBER` → número aprobado por Meta.
- Nombre de la clase (opcional, por claridad).

No requiere cambios en `CompositeNotificadorAdapter` ni en ninguna otra capa.

## Consecuencias

**Positivas:**
- SMS disponible de inmediato sin aprobación externa.
- Canal de celular independiente del canal de email.
- Migración a WhatsApp futura con mínimo esfuerzo (ADR 009 cuando corresponda).
- `sms.enabled=false` desactiva completamente el canal sin afectar el resto del sistema.

**Negativas:**
- Costo por SMS en producción (bajo pero no $0).
- SMS puede ser bloqueado como spam en algunos operadores argentinos.
