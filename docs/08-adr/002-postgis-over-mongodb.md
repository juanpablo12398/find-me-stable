# ADR 002: PostgreSQL + PostGIS sobre MongoDB

## Contexto
Se necesitan consultas geoespaciales (radio, poligono, nearest) con consistencia transaccional.

## Decision
Usar PostgreSQL con extension PostGIS como almacenamiento principal.

## Consecuencias
Positivas:
- Queries geograficas robustas y optimizadas con indices GIST.
- Consistencia ACID para entidades y relaciones.

Negativas:
- Mayor complejidad de setup que una base no relacional simple.
- Dependencia fuerte de SQL/PostGIS.