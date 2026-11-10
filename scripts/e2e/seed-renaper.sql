CREATE SCHEMA IF NOT EXISTS renaper;

CREATE TABLE IF NOT EXISTS renaper.personas (
  dni VARCHAR(32) PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  apellido VARCHAR(255) NOT NULL,
  fecha_nac DATE,
  direccion VARCHAR(255),
  localidad VARCHAR(255),
  provincia VARCHAR(255)
);

INSERT INTO renaper.personas (dni, nombre, apellido, fecha_nac, direccion, localidad, provincia)
VALUES
  ('44215507', 'Juan', 'Perez', DATE '1990-01-01', 'Calle E2E 123', 'CABA', 'Buenos Aires'),
  ('35123456', 'Carlos', 'Vazquez', DATE '1988-06-15', 'Av Siempre Viva 742', 'CABA', 'Buenos Aires')
ON CONFLICT (dni) DO UPDATE
SET nombre = EXCLUDED.nombre,
    apellido = EXCLUDED.apellido,
    fecha_nac = EXCLUDED.fecha_nac,
    direccion = EXCLUDED.direccion,
    localidad = EXCLUDED.localidad,
    provincia = EXCLUDED.provincia;

-- Dataset amplio para E2E determinista de paginacion.
INSERT INTO renaper.personas (dni, nombre, apellido, fecha_nac, direccion, localidad, provincia)
SELECT
  LPAD((36000000 + gs)::text, 8, '0') AS dni,
  'Paginacion' || gs AS nombre,
  'E2E' AS apellido,
  DATE '1992-01-01' + (gs % 365),
  'Calle Seed ' || gs,
  'CABA',
  'Buenos Aires'
FROM generate_series(1, 40) gs
ON CONFLICT (dni) DO UPDATE
SET nombre = EXCLUDED.nombre,
    apellido = EXCLUDED.apellido,
    fecha_nac = EXCLUDED.fecha_nac,
    direccion = EXCLUDED.direccion,
    localidad = EXCLUDED.localidad,
    provincia = EXCLUDED.provincia;
