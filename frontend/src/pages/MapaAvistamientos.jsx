import React, { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { MapContainer, TileLayer, Circle, Rectangle, Polygon, Polyline, Marker, Popup } from "react-leaflet";
import MarkerClusterGroup from "react-leaflet-cluster";
import { motion, AnimatePresence } from "framer-motion";
import { toast } from "sonner";
import {
  AlertCircle,
  MapPin,
  Plus,
  RefreshCw,
  X,
  SearchX
} from "lucide-react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import MapPopupContent from "@/components/map/MapPopupContent";
import MapClickHandler from "@/components/map/MapClickHandler";
import MarkerWithCenter from "@/components/map/MarkerWithCenter";
import AreaDrawHandler from "@/components/map/AreaDrawHandler";
import FilterBar from "@/components/map/FilterBar";
import FilterConfigPanel from "@/components/map/FilterConfigPanel";
import PersonasSidebar from "@/components/map/PersonasSidebar";
import ReportModal from "@/components/map/ReportModal";

import { FILTER_TYPES, MAP_COLORS } from "@/components/map/MapConstants";
import { coordsToWKT, createCustomIcon, getPersonaColor } from "@/components/map/MapUtils";
import { useMapFilters } from "@/hooks/useMapFilters";
import { useReportMode } from "@/hooks/useReportMode";
import { useAuth } from "@/hooks/useAuth.jsx";
import { AvistamientoService } from "@/services/AvistamientoService";
import { DesaparecidoService } from "@/services/DesaparecidoService";
import { webSocketService } from "@/services/WebSocketService";

export default function MapaAvistamientos() {
  const { user, isAuthenticated } = useAuth();
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  const {
    filtroActivo,
    personaSeleccionada,
    setPersonaSeleccionada,
    showFilters,
    setShowFilters,
    radioCenter,
    setRadioCenter,
    radioKm,
    setRadioKm,
    isSelectingRadioCenter,
    setIsSelectingRadioCenter,
    areaBounds,
    setAreaBounds,
    isDrawingArea,
    setIsDrawingArea,
    tempAreaBounds,
    setTempAreaBounds,
    polygonPoints,
    setPolygonPoints,
    isDrawingPolygon,
    setIsDrawingPolygon,
    polygonComplete,
    setPolygonComplete,
    activateFilter,
    resetFilters,
  } = useMapFilters();

  const {
    modoReporte,
    selectedLatLng,
    showReportModal,
    setShowReportModal,
    toggleModoReporte,
    openReportModalAt,
    handleReportSuccess,
  } = useReportMode(isAuthenticated, navigate);

  const [statusMessage, setStatusMessage] = useState({
    text: "Mapa inicializado",
    color: "text-green-600",
  });

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const personaId = params.get("persona");
    if (personaId) {
      setPersonaSeleccionada(personaId);
      activateFilter(FILTER_TYPES.DESAPARECIDO, setStatusMessage);
    }
  }, [activateFilter, setPersonaSeleccionada]);

  useEffect(() => {
    webSocketService.connect(() => {
      console.log('WS Connected para mapa');
      // Nos suscribimos a todos los avistamientos (canal global) y al canal específico de la persona seleccionada (si la hay)
      webSocketService.subscribeToAvistamientos(null, (avistamiento) => {
        // En un mundo ideal, validamos si impacta al filtro. Por simplicidad invalidamos y refrescamos.
        queryClient.invalidateQueries({ queryKey: ["avistamientos"] });
        // Alertamos al usuario en status bar si coincide
        setStatusMessage({ text: "¡Nuevo avistamiento reportado en tiempo real!", color: "text-red-500 font-bold" });
      });

      if (personaSeleccionada) {
        webSocketService.subscribeToAvistamientos(personaSeleccionada, () => {
          queryClient.invalidateQueries({ queryKey: ["avistamientos"] });
          setStatusMessage({ text: "¡Nuevo avistamiento reportado para esta persona!", color: "text-red-500 font-bold" });
        });
      }
    });

    return () => {
      webSocketService.disconnect();
    };
  }, [personaSeleccionada, queryClient]);

  const { data: avistamientos = [], isLoading: loadingAvistamientos, refetch } = useQuery({
    queryKey: [
      "avistamientos",
      filtroActivo,
      personaSeleccionada,
      radioCenter,
      radioKm,
      areaBounds,
      polygonPoints,
      polygonComplete,
    ],
    queryFn: async () => {
      setStatusMessage({ text: "Cargando avistamientos...", color: "text-blue-600" });

      let result = [];

      switch (filtroActivo) {
        case FILTER_TYPES.TODOS:
          result = await AvistamientoService.getAvistamientosParaMapa();
          break;
        case FILTER_TYPES.RADIO:
          if (radioCenter) {
            result = await AvistamientoService.getEnRadio(radioCenter.lat, radioCenter.lng, radioKm);
          }
          break;
        case FILTER_TYPES.AREA:
          if (areaBounds) {
            const [[latMin, lngMin], [latMax, lngMax]] = [
              [areaBounds.getSouth(), areaBounds.getWest()],
              [areaBounds.getNorth(), areaBounds.getEast()],
            ];
            result = await AvistamientoService.getEnArea(latMin, latMax, lngMin, lngMax);
          }
          break;
        case FILTER_TYPES.POLIGONO:
          if (polygonComplete && polygonPoints.length >= 3) {
            result = await AvistamientoService.getEnPoligono(coordsToWKT(polygonPoints));
          }
          break;
        case FILTER_TYPES.DESAPARECIDO:
        case FILTER_TYPES.RUTA:
          if (personaSeleccionada) {
            result = await AvistamientoService.getPorDesaparecido(personaSeleccionada);
            if (filtroActivo === FILTER_TYPES.RUTA) {
              result = [...result].sort((a, b) => {
                const dateA = new Date(a.fechaHora || a.creadoEn || 0).getTime();
                const dateB = new Date(b.fechaHora || b.creadoEn || 0).getTime();
                // We want chronologically ascending order: oldest first (it gets number '1'), newest last (it gets the highest number).
                return dateA - dateB; // En Java el backend me lo devuelve mas nuevo primero. Esto lo invierte.
              });
            }
          }
          break;
        case FILTER_TYPES.ULTIMA_UBICACION:
          if (personaSeleccionada) {
            result = await AvistamientoService.getUltimaUbicacion(personaSeleccionada);
          }
          break;
        default:
          result = await AvistamientoService.getAvistamientosParaMapa();
      }

      if (result.length === 0) {
        setStatusMessage({ text: "No se encontraron avistamientos", color: "text-amber-600" });
      } else if (filtroActivo === FILTER_TYPES.RUTA && result.length < 2) {
        setStatusMessage({ text: "Se necesitan al menos 2 avistamientos para trazar una ruta", color: "text-amber-600" });
      } else {
        setStatusMessage({ text: `${result.length} avistamiento${result.length !== 1 ? "s" : ""} encontrado${result.length !== 1 ? "s" : ""}`, color: "text-green-600" });
      }

      return result;
    },
    enabled:
      filtroActivo === FILTER_TYPES.TODOS ||
      (filtroActivo === FILTER_TYPES.RADIO && !!radioCenter) ||
      (filtroActivo === FILTER_TYPES.AREA && !!areaBounds) ||
      (filtroActivo === FILTER_TYPES.POLIGONO && polygonComplete) ||
      ((filtroActivo === FILTER_TYPES.DESAPARECIDO || filtroActivo === FILTER_TYPES.RUTA || filtroActivo === FILTER_TYPES.ULTIMA_UBICACION) && !!personaSeleccionada),
  });

  const { data: desaparecidos = [] } = useQuery({
    queryKey: ["desaparecidos"],
    queryFn: () => DesaparecidoService.obtenerTodos(),
  });

  const getDesaparecidoInfo = (id) => desaparecidos.find((d) => String(d.id) === String(id));

  const mapStateRef = useRef({});
  mapStateRef.current = {
    isSelectingRadioCenter,
    isDrawingPolygon,
    polygonComplete,
    modoReporte,
    polygonPointsLength: polygonPoints.length,
  };

  const onActivateFilter = (type) => activateFilter(type, setStatusMessage);
  const onResetFilters = () => resetFilters(setStatusMessage);

  const handleMapClick = useCallback((latlng) => {
    const s = mapStateRef.current;

    if (s.isSelectingRadioCenter) {
      setRadioCenter(latlng);
      setIsSelectingRadioCenter(false);
      setStatusMessage({ text: "Centro seleccionado. Ajusta el radio.", color: "text-green-600" });
      return;
    }

    if (s.isDrawingPolygon && !s.polygonComplete) {
      setPolygonPoints((prev) => [...prev, latlng]);
      setStatusMessage({ text: `Punto ${s.polygonPointsLength + 1} agregado`, color: "text-pink-600" });
      return;
    }

    if (s.modoReporte) {
      openReportModalAt(latlng);
    }
  }, [openReportModalAt, setIsSelectingRadioCenter, setPolygonPoints, setRadioCenter]);

  const handleRightClick = useCallback(() => {
    if (isDrawingPolygon && polygonPoints.length >= 3) {
      setIsDrawingPolygon(false);
      setPolygonComplete(true);
      setStatusMessage({ text: "Poligono completado", color: "text-green-600" });
    }
  }, [isDrawingPolygon, polygonPoints.length, setIsDrawingPolygon, setPolygonComplete]);

  const handleAreaComplete = useCallback((bounds) => {
    setAreaBounds(bounds);
    setIsDrawingArea(false);
    setTempAreaBounds(null);
    setStatusMessage({ text: "Area seleccionada", color: "text-green-600" });
  }, [setAreaBounds, setIsDrawingArea, setTempAreaBounds]);

  const refreshAvistamientos = useCallback(() => {
    queryClient.invalidateQueries({ queryKey: ["avistamientos"] });
    refetch();
  }, [queryClient, refetch]);

  const onReportSuccess = useCallback(() => {
    handleReportSuccess(refreshAvistamientos);
  }, [handleReportSuccess, refreshAvistamientos]);

  const isMapInteractive = modoReporte || isSelectingRadioCenter || isDrawingPolygon;

  const shouldUseNumbering = filtroActivo === FILTER_TYPES.RUTA || filtroActivo === FILTER_TYPES.ULTIMA_UBICACION;

  const markerElements = avistamientos
    .map((avistamiento, index) => {
      const lat = avistamiento.latitud ?? avistamiento.lat;
      const lng = avistamiento.longitud ?? avistamiento.lng ?? avistamiento.lon;

      if (!lat || !lng) return null;

      const desaparecido = getDesaparecidoInfo(avistamiento.desaparecidoId || avistamiento.desaparecido_id);
      const personaId = avistamiento.desaparecidoId || avistamiento.desaparecido_id;
      const color = getPersonaColor(personaId);
      const nivelUrgencia = avistamiento?.nivelUrgencia ?? null;
      
      const numberToPass = shouldUseNumbering ? (index + 1) : null;

      return (
        <MarkerWithCenter
          key={avistamiento.id}
          position={[lat, lng]}
          icon={createCustomIcon(color, nivelUrgencia, numberToPass)}
        >
          <Popup className="custom-popup" maxWidth={320}>
            <MapPopupContent avistamiento={avistamiento} desaparecido={desaparecido} />
          </Popup>
        </MarkerWithCenter>
      );
    })
    .filter(Boolean);

  const shouldUseCluster = filtroActivo !== FILTER_TYPES.RUTA;

  return (
    <div className="h-screen w-full relative" id="main-content">
      <MapContainer
        center={[-34.6037, -58.3816]}
        zoom={12}
        zoomControl={false}
        className="h-full w-full"
        style={{ background: "#f1f5f9" }}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        <MapClickHandler
          onMapClick={handleMapClick}
          isActive={isMapInteractive}
          onRightClick={handleRightClick}
          modoReporte={modoReporte}
        />

        <AreaDrawHandler
          isDrawing={isDrawingArea}
          onAreaComplete={handleAreaComplete}
          onAreaUpdate={setTempAreaBounds}
        />

        {radioCenter && filtroActivo === FILTER_TYPES.RADIO && (
          <Circle
            center={[radioCenter.lat, radioCenter.lng]}
            radius={radioKm * 1000}
            pathOptions={{
              color: MAP_COLORS.RADIO_CIRCLE,
              fillColor: MAP_COLORS.RADIO_CIRCLE,
              fillOpacity: 0.1,
              weight: 2,
            }}
          />
        )}

        {(areaBounds || tempAreaBounds) && filtroActivo === FILTER_TYPES.AREA && (
          <Rectangle
            bounds={areaBounds || tempAreaBounds}
            pathOptions={{
              color: MAP_COLORS.AREA_RECTANGLE,
              fillColor: MAP_COLORS.AREA_RECTANGLE,
              fillOpacity: 0.1,
              weight: 2,
              dashArray: tempAreaBounds ? "5, 5" : undefined,
            }}
          />
        )}

        {polygonPoints.length >= 2 && filtroActivo === FILTER_TYPES.POLIGONO && (
          <Polygon
            positions={polygonPoints.map((p) => [p.lat, p.lng])}
            pathOptions={{
              color: MAP_COLORS.POLYGON,
              fillColor: MAP_COLORS.POLYGON,
              fillOpacity: polygonComplete ? 0.1 : 0.05,
              weight: 2,
              dashArray: polygonComplete ? undefined : "5, 5",
            }}
          />
        )}

        {selectedLatLng && modoReporte && (
          <Marker position={[selectedLatLng.lat, selectedLatLng.lng]} icon={createCustomIcon("#ef4444")}>
            <Popup>Nueva ubicacion de avistamiento</Popup>
          </Marker>
        )}

        {shouldUseCluster ? (
          <MarkerClusterGroup
            chunkedLoading
            maxClusterRadius={60}
            spiderfyOnMaxZoom
            showCoverageOnHover={false}
          >
            {markerElements}
          </MarkerClusterGroup>
        ) : (
          markerElements
        )}

        {filtroActivo === FILTER_TYPES.RUTA && avistamientos.length >= 2 && personaSeleccionada && (
          <Polyline
            positions={avistamientos.map((a) => [a.latitud ?? a.lat, a.longitud ?? a.lng ?? a.lon])}
            pathOptions={{
              color: getPersonaColor(personaSeleccionada),
              weight: 4,
              dashArray: "10, 10",
              lineCap: "round",
              opacity: 0.8,
            }}
          />
        )}
      </MapContainer>

      {loadingAvistamientos && (
        <div className="absolute inset-0 z-[999] bg-white/50 backdrop-blur-sm flex items-center justify-center pointer-events-none">
          <div className="flex items-center gap-3 bg-white rounded-xl px-6 py-4 shadow-lg" role="status" aria-live="polite">
            <RefreshCw className="w-5 h-5 animate-spin text-[#1e3a5f]" />
            <span>Cargando avistamientos...</span>
          </div>
        </div>
      )}

      {!loadingAvistamientos && avistamientos.length === 0 && (
        <div className="absolute inset-0 z-[998] flex items-center justify-center pointer-events-none">
          <div className="bg-white/90 backdrop-blur-sm border border-slate-200 rounded-2xl shadow-xl px-6 py-5 flex items-center gap-3" role="status" aria-live="polite">
            <SearchX className="w-6 h-6 text-slate-500" />
            <div>
              {filtroActivo === FILTER_TYPES.RADIO && !radioCenter ? (
                <>
                  <p className="font-semibold text-slate-800">Busqueda por Radio</p>
                  <p className="text-sm text-slate-500">Hace click en el mapa para establecer el centro del radio</p>
                </>
              ) : filtroActivo === FILTER_TYPES.AREA && !areaBounds ? (
                <>
                  <p className="font-semibold text-slate-800">Busqueda por Area</p>
                  <p className="text-sm text-slate-500">Mantene presionado y arrastra para dibujar un rectangulo en el mapa</p>
                </>
              ) : filtroActivo === FILTER_TYPES.POLIGONO && (!polygonComplete || polygonPoints.length < 3) ? (
                <>
                  <p className="font-semibold text-slate-800">Busqueda por Poligono</p>
                  <p className="text-sm text-slate-500">Hace clicks en el mapa para dibujar un area y cerrala tocando el primer punto</p>
                </>
              ) : (filtroActivo === FILTER_TYPES.DESAPARECIDO || filtroActivo === FILTER_TYPES.RUTA || filtroActivo === FILTER_TYPES.ULTIMA_UBICACION) && !personaSeleccionada ? (
                <>
                  <p className="font-semibold text-slate-800">Filtro por Persona</p>
                  <p className="text-sm text-slate-500">Selecciona a una persona desde el panel lateral derecho</p>
                </>
              ) : (
                <>
                  <p className="font-semibold text-slate-800">Sin avistamientos para este filtro</p>
                  <p className="text-sm text-slate-500">Proba otro filtro o limpia la busqueda</p>
                </>
              )}
            </div>
          </div>
        </div>
      )}

      <FilterBar
        showFilters={showFilters}
        setShowFilters={setShowFilters}
        filtroActivo={filtroActivo}
        activateFilter={onActivateFilter}
        resetFilters={onResetFilters}
      />

      <FilterConfigPanel
        showFilters={showFilters}
        filtroActivo={filtroActivo}
        radioCenter={radioCenter}
        radioKm={radioKm}
        setRadioKm={setRadioKm}
        personaSeleccionada={personaSeleccionada}
        setPersonaSeleccionada={setPersonaSeleccionada}
        desaparecidos={desaparecidos}
      />

      <div className="absolute top-24 right-4 z-[1000] flex gap-2">
        <Button
          variant="outline"
          onClick={refreshAvistamientos}
          disabled={loadingAvistamientos}
          className="bg-white/95 backdrop-blur-sm hover:bg-slate-50 rounded-xl border-slate-200/50 shadow-lg h-11 w-11 p-0"
          aria-label="Actualizar avistamientos"
        >
          <RefreshCw className={`w-5 h-5 text-[#1e3a5f] ${loadingAvistamientos ? "animate-spin" : ""}`} />
        </Button>

        <Button
          onClick={() => toggleModoReporte(setStatusMessage)}
          variant="outline"
          className={`
            rounded-xl shadow-lg h-11 px-4 border-slate-200/50
            ${
              modoReporte
                ? "bg-red-500 hover:bg-red-600 text-white border-red-500"
                : "bg-white/95 backdrop-blur-sm hover:bg-slate-50 text-[#1e3a5f]"
            }
          `}
          aria-label={modoReporte ? "Cancelar modo reporte" : "Activar modo reporte"}
        >
          {modoReporte ? (
            <>
              <X className="w-4 h-4 mr-2" />
              Cancelar
            </>
          ) : (
            <>
              <MapPin className="w-4 h-4 mr-2" />
              Reportar
            </>
          )}
        </Button>
      </div>

      <div className="absolute bottom-6 left-6 z-[1000]">
        <Badge
          className="bg-white/95 backdrop-blur-sm text-slate-700 shadow-lg py-2 px-4 rounded-full border border-slate-200/50"
          role="status"
          aria-live="polite"
        >
          <MapPin className="w-4 h-4 mr-2 text-[#1e3a5f]" />
          {avistamientos.length} avistamientos • {desaparecidos.length} personas
        </Badge>
      </div>

      <PersonasSidebar desaparecidos={desaparecidos} />

      <Button
        onClick={() => {
          if (!isAuthenticated) {
            toast.error("Inicia sesion para registrar una persona extraviada.");
            navigate("/IniciarSesion");
          } else {
            navigate("/RegistrarDesaparecido");
          }
        }}
        variant="outline"
        className="absolute bottom-6 right-6 z-[1000] w-14 h-14 rounded-full bg-white/95 backdrop-blur-sm hover:bg-slate-50 border-slate-200/50 shadow-xl"
        title="Registrar persona extraviada"
        aria-label="Registrar persona extraviada"
      >
        <Plus className="w-6 h-6 text-[#1e3a5f]" />
      </Button>

      <AnimatePresence>
        {modoReporte && (
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 10 }}
            className="absolute bottom-6 left-1/2 -translate-x-1/2 z-[1000] px-4 py-3 bg-red-50/95 backdrop-blur-sm border border-red-200 rounded-xl flex items-center gap-3 shadow-lg pointer-events-none"
          >
            <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0" />
            <p className="text-sm text-red-800">
              <span className="font-semibold">Modo reporte activo.</span> Click en el mapa para marcar ubicacion.
            </p>
          </motion.div>
        )}
      </AnimatePresence>

      <ReportModal
        show={showReportModal}
        onClose={() => setShowReportModal(false)}
        selectedLatLng={selectedLatLng}
        desaparecidos={desaparecidos}
        user={user}
        onSuccess={onReportSuccess}
      />

      <div className={`sr-only ${statusMessage.color}`}>{statusMessage.text}</div>
    </div>
  );
}
