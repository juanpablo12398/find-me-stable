# ADR 004: AES-GCM + blind index para DNI

## Contexto
El DNI es dato sensible y debe protegerse en reposo, manteniendo busqueda exacta.

## Decision
Cifrar DNI con AES-GCM y mantener columna de indice ciego (HMAC) para busquedas exactas.

## Consecuencias
Positivas:
- Proteccion fuerte de datos sensibles.
- Permite lookup exacto sin exponer valor original.

Negativas:
- Mayor complejidad operacional de claves.
- No habilita busquedas parciales sobre DNI cifrado.