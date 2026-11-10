# ADR 005: React + Vite sobre Next.js

## Contexto
El producto objetivo es una SPA con foco en mapa interactivo y consumo de API existente.

## Decision
Adoptar React con Vite para frontend principal.

## Consecuencias
Positivas:
- Toolchain liviano y rapido para desarrollo.
- Menor complejidad para un proyecto academico SPA.

Negativas:
- Sin capacidades SSR nativas como en Next.js.
- Algunas optimizaciones de routing/render quedan manuales.