package edu.utn.proyecto.infrastructure.normalize;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class GeometryNormalizer {

    private static final int SRID_WGS84 = 4326;
    private final GeometryFactory geometryFactory;

    public GeometryNormalizer() {
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), SRID_WGS84);
    }

    public Point toPoint(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            throw new IllegalArgumentException(
                    "Latitud y longitud no pueden ser nulos al normalizar a Point"
            );
        }

        Coordinate coordinate = new Coordinate(longitud, latitud);
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(SRID_WGS84);

        return point;
    }

    public Double fromPointToLatitud(Point point) {
        return point != null ? point.getY() : null;
    }

    public Double fromPointToLongitud(Point point) {
        return point != null ? point.getX() : null;
    }
}

