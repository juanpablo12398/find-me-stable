-- =============================================================================
-- seed-renaper.sql
-- Datos ficticios para la tabla renaper.personas — solo para desarrollo local.
--
-- ADVERTENCIA: Estas personas NO existen. Los DNI y datos son inventados.
-- No usar en produccion ni con datos reales de ciudadanos.
--
-- Proposito:
--   Permite registrar avistadores y desaparecidos en el entorno local sin
--   necesitar conexion al RENAPER oficial. El sistema valida DNI y datos
--   personales contra esta tabla antes de permitir el registro.
--
-- Como ejecutar:
--
--   Opcion A — psql instalado localmente:
--     psql -h localhost -U findme_user -d findme -f scripts/seed-renaper.sql
--
--   Opcion B — via Docker (si la BD corre en contenedor):
--     docker exec -i findme-db psql -U findme_user -d findme < scripts/seed-renaper.sql
--
--   Opcion C — desde IntelliJ / DBeaver:
--     Abrir el archivo y ejecutarlo contra la conexion de desarrollo.
--
-- El script es idempotente: se puede correr multiples veces sin duplicar datos.
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS renaper;

CREATE TABLE IF NOT EXISTS renaper.personas (
    dni        VARCHAR(32)  PRIMARY KEY,
    nombre     VARCHAR(255) NOT NULL,
    apellido   VARCHAR(255) NOT NULL,
    fecha_nac  DATE,
    direccion  VARCHAR(255),
    localidad  VARCHAR(255),
    provincia  VARCHAR(255)
);

-- -----------------------------------------------------------------------------
-- 10 personas ficticias para desarrollo
-- Todos son mayores de 18 años (requisito de validacion del sistema).
-- Las fechas de nacimiento estan entre 1960 y 1998 para garantizar mayoria de edad.
-- -----------------------------------------------------------------------------

INSERT INTO renaper.personas (dni, nombre, apellido, fecha_nac, direccion, localidad, provincia) VALUES
    ('28456123', 'Maria',    'Gonzalez',  DATE '1985-03-12', 'Av. Corrientes 1234',   'CABA',       'Buenos Aires'),
    ('31789045', 'Roberto',  'Fernandez', DATE '1991-07-28', 'Calle Belgrano 567',    'Rosario',    'Santa Fe'),
    ('25314678', 'Laura',    'Martinez',  DATE '1978-11-05', 'Mitre 890',             'Cordoba',    'Cordoba'),
    ('37902561', 'Santiago', 'Lopez',     DATE '1995-01-19', 'San Martin 2100',       'Mendoza',    'Mendoza'),
    ('22678934', 'Patricia', 'Suarez',    DATE '1969-06-30', 'Rivadavia 44',          'La Plata',   'Buenos Aires'),
    ('40123789', 'Diego',    'Romero',    DATE '1998-09-14', 'Tucuman 320',           'Salta',      'Salta'),
    ('29874512', 'Claudia',  'Torres',    DATE '1983-04-22', 'Independencia 750',     'Mar del Plata', 'Buenos Aires'),
    ('34561890', 'Andres',   'Herrera',   DATE '1993-12-01', 'Colon 111',             'San Miguel de Tucuman', 'Tucuman'),
    ('26438901', 'Silvia',   'Morales',   DATE '1974-08-17', 'Libertad 999',          'Posadas',    'Misiones'),
    ('38745023', 'Facundo',  'Castro',    DATE '1996-02-09', 'Sarmiento 640',         'Neuquen',    'Neuquen')
ON CONFLICT (dni) DO NOTHING;

-- =============================================================================
-- Como usar estos datos para registrarse en el sistema:
--
-- Al completar el formulario de registro (/Registro), ingresar el DNI
-- y los datos exactamente como aparecen en esta tabla.
-- El sistema verifica que el DNI exista en renaper.personas y que
-- nombre + apellido coincidan con los registrados.
--
-- Ejemplo de registro valido:
--   DNI:      28456123
--   Nombre:   Maria
--   Apellido: Gonzalez
--   Edad:     (calculada a partir de fecha_nac, debe ser >= 18)
-- =============================================================================
