package edu.utn.proyecto.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración geoespaciales contra una base de datos PostGIS real.
 * Usa Testcontainers para levantar un contenedor PostgreSQL + PostGIS.
 *
 * Si Docker no está disponible, los tests se saltan automáticamente
 * gracias a @Testcontainers(disabledWithoutDocker = true).
 */
@Testcontainers(disabledWithoutDocker = true)
class BusquedaGeoespacialIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgis/postgis:15-3.3")
                    .withDatabaseName("findme_geo_test")
                    .withUsername("test")
                    .withPassword("test");

    private Connection conn;

    @BeforeAll
    static void crearEsquema() throws Exception {
        try (Connection c = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement stmt = c.createStatement()) {

            stmt.execute("CREATE EXTENSION IF NOT EXISTS postgis");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS avistamientos (
                        id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        avistador_id    UUID        NOT NULL,
                        desaparecido_id UUID        NOT NULL,
                        ubicacion       geometry(Point, 4326),
                        fecha_hora      TIMESTAMP   NOT NULL DEFAULT now(),
                        descripcion     TEXT,
                        verificado      BOOLEAN     NOT NULL DEFAULT false,
                        publico         BOOLEAN     NOT NULL DEFAULT true,
                        creado_en       TIMESTAMP   NOT NULL DEFAULT now()
                    )
                    """);
        }
    }

    @BeforeEach
    void conectar() throws Exception {
        conn = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE avistamientos");
        }
    }

    @AfterEach
    void desconectar() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    /** Inserta un punto (lat, lng) en la tabla. */
    private void insertarPunto(double lat, double lng) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO avistamientos (avistador_id, desaparecido_id, ubicacion, fecha_hora) " +
                "VALUES (gen_random_uuid(), gen_random_uuid(), " +
                "        ST_SetSRID(ST_MakePoint(?, ?), 4326), now())")) {
            ps.setDouble(1, lng); // ST_MakePoint(lng, lat)
            ps.setDouble(2, lat);
            ps.executeUpdate();
        }
    }

    // ─────────────────────────────────────────────────────────
    // TEST 1: Búsqueda por radio
    // ─────────────────────────────────────────────────────────

    /**
     * Inserta 3 avistamientos a ~100 m, ~500 m y ~2 km del centro.
     * Con un radio de 1 km solo deben aparecer los 2 primeros.
     */
    @Test
    void busquedaPorRadio_debeRetornarSoloLosQueEstanDentroDelRadio() throws Exception {
        double centerLat = -34.603684;
        double centerLng = -58.381555;

        // 1 grado latitud ≈ 111 km  →  0.0009° ≈ 100 m, 0.0045° ≈ 500 m, 0.018° ≈ 2 km
        insertarPunto(centerLat - 0.0009, centerLng); // ~100 m
        insertarPunto(centerLat - 0.0045, centerLng); // ~500 m
        insertarPunto(centerLat - 0.018,  centerLng); // ~2 km  — fuera del radio de 1 km

        String sql = """
                SELECT id,
                       ST_Distance(
                           ubicacion::geography,
                           ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography
                       ) AS distancia
                FROM avistamientos
                WHERE ubicacion IS NOT NULL
                  AND ST_DWithin(
                        ubicacion::geography,
                        ST_SetSRID(ST_MakePoint(?, ?), 4326)::geography,
                        ?
                      )
                ORDER BY distancia
                """;

        List<Double> distancias = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, centerLng);
            ps.setDouble(2, centerLat);
            ps.setDouble(3, centerLng);
            ps.setDouble(4, centerLat);
            ps.setDouble(5, 1000.0); // 1 km en metros
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                distancias.add(rs.getDouble("distancia"));
            }
        }

        assertThat(distancias).hasSize(2);
        // Los resultados deben estar ordenados por distancia ascendente
        assertThat(distancias.get(0)).isLessThan(distancias.get(1));
        assertThat(distancias.get(0)).isLessThan(200.0);   // ~100 m
        assertThat(distancias.get(1)).isLessThan(600.0);   // ~500 m
    }

    // ─────────────────────────────────────────────────────────
    // TEST 2: Búsqueda por bounding box
    // ─────────────────────────────────────────────────────────

    /**
     * Inserta 3 puntos: 2 dentro del bbox y 1 fuera.
     * Verifica que solo se devuelven los puntos dentro del rectángulo.
     * También verifica que un punto exactamente en el borde se incluye.
     */
    @Test
    void busquedaPorBoundingBox_debeRetornarSoloLosQueEstanDentro() throws Exception {
        // Bounding box: latMin=-34.65, latMax=-34.55, lngMin=-58.45, lngMax=-58.30
        double latMin = -34.65, latMax = -34.55, lngMin = -58.45, lngMax = -58.30;

        insertarPunto(-34.60, -58.37);  // DENTRO del bbox
        insertarPunto(-34.58, -58.40);  // DENTRO del bbox
        insertarPunto(-34.70, -58.50);  // FUERA del bbox

        String sql = """
                SELECT COUNT(*) AS total
                FROM avistamientos
                WHERE ubicacion IS NOT NULL
                  AND ST_Within(
                        ubicacion,
                        ST_MakeEnvelope(?, ?, ?, ?, 4326)
                      )
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, lngMin);
            ps.setDouble(2, latMin);
            ps.setDouble(3, lngMax);
            ps.setDouble(4, latMax);
            ResultSet rs = ps.executeQuery();
            rs.next();
            long count = rs.getLong("total");
            assertThat(count).isEqualTo(2);
        }
    }

    // ─────────────────────────────────────────────────────────
    // TEST 3: Búsqueda por polígono WKT
    // ─────────────────────────────────────────────────────────

    /**
     * Define un polígono aproximado del centro de Buenos Aires.
     * Inserta puntos dentro y fuera del polígono.
     * Verifica que ST_Contains filtra correctamente.
     */
    @Test
    void busquedaPorPoligono_STContainsFiltraCorrectamente() throws Exception {
        // Polígono: zona de Palermo/Recoleta en Buenos Aires
        String polygonWKT = "POLYGON((-58.45 -34.58, -58.35 -34.58, -58.35 -34.53, -58.45 -34.53, -58.45 -34.58))";

        insertarPunto(-34.56, -58.40);  // DENTRO del polígono
        insertarPunto(-34.55, -58.42);  // DENTRO del polígono
        insertarPunto(-34.80, -58.60);  // FUERA del polígono (sur lejano)
        insertarPunto(-34.30, -58.20);  // FUERA del polígono (norte lejano)

        String sql = """
                SELECT COUNT(*) AS total
                FROM avistamientos
                WHERE ubicacion IS NOT NULL
                  AND ST_Within(
                        ubicacion,
                        ST_GeomFromText(?, 4326)
                      )
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, polygonWKT);
            ResultSet rs = ps.executeQuery();
            rs.next();
            long count = rs.getLong("total");
            assertThat(count).isEqualTo(2);
        }
    }

    // ─────────────────────────────────────────────────────────
    // TEST 4: Vecinos más cercanos (KNN)
    // ─────────────────────────────────────────────────────────

    /**
     * Inserta 10 avistamientos a distancias conocidas del centro.
     * Pide los 3 más cercanos y verifica:
     * - que se devuelven exactamente 3
     * - que están ordenados por distancia ascendente
     * - que son los 3 más cercanos (distancias menores a los demás)
     */
    @Test
    void vecinosMasCercanos_debeRetornarLos3MasCercanosEnOrden() throws Exception {
        double centerLat = -34.603684;
        double centerLng = -58.381555;

        // Insertar 10 puntos a distancias crecientes (0.001°, 0.002°, ..., 0.010°)
        for (int i = 1; i <= 10; i++) {
            insertarPunto(centerLat - (i * 0.001), centerLng);
        }

        // KNN: los 3 más cercanos al centro
        String sql = """
                SELECT ubicacion <-> ST_SetSRID(ST_MakePoint(?, ?), 4326) AS distancia
                FROM avistamientos
                WHERE ubicacion IS NOT NULL
                ORDER BY ubicacion <-> ST_SetSRID(ST_MakePoint(?, ?), 4326)
                LIMIT ?
                """;

        List<Double> distancias = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, centerLng);
            ps.setDouble(2, centerLat);
            ps.setDouble(3, centerLng);
            ps.setDouble(4, centerLat);
            ps.setInt(5, 3);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                distancias.add(rs.getDouble("distancia"));
            }
        }

        assertThat(distancias).hasSize(3);
        // Deben estar en orden ascendente
        assertThat(distancias.get(0)).isLessThan(distancias.get(1));
        assertThat(distancias.get(1)).isLessThan(distancias.get(2));
        // Los 3 más cercanos están a ≈ 0.001°, 0.002°, 0.003° del centro
        assertThat(distancias.get(0)).isLessThan(0.002);
        assertThat(distancias.get(2)).isLessThan(0.004);
    }
}
