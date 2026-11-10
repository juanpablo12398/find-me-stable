# ADR 003: JWT en cookies HttpOnly sobre sesiones server-side

## Contexto
Se requiere autenticacion web sin almacenar estado de sesion server-side.

## Decision
Usar JWT firmado en cookie HttpOnly (SameSite) para sesiones de usuario.

## Consecuencias
Positivas:
- Escalabilidad horizontal mas simple.
- Menor exposicion del token a JavaScript.

Negativas:
- Requiere configuracion cuidadosa de CORS/cookies.
- Revocacion temprana de tokens no trivial.