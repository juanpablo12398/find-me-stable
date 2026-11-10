import { useCallback, useState } from "react";
import { FILTER_TYPES } from "@/components/map/MapConstants";

export function useMapFilters() {
  const [filtroActivo, setFiltroActivo] = useState(FILTER_TYPES.TODOS);
  const [personaSeleccionada, setPersonaSeleccionada] = useState(null);
  const [showFilters, setShowFilters] = useState(true);

  const [radioCenter, setRadioCenter] = useState(null);
  const [radioKm, setRadioKm] = useState(5);
  const [isSelectingRadioCenter, setIsSelectingRadioCenter] = useState(false);

  const [areaBounds, setAreaBounds] = useState(null);
  const [isDrawingArea, setIsDrawingArea] = useState(false);
  const [tempAreaBounds, setTempAreaBounds] = useState(null);

  const [polygonPoints, setPolygonPoints] = useState([]);
  const [isDrawingPolygon, setIsDrawingPolygon] = useState(false);
  const [polygonComplete, setPolygonComplete] = useState(false);

  const activateFilter = useCallback((type, onStatusChange) => {
    setIsSelectingRadioCenter(false);
    setIsDrawingArea(false);
    setIsDrawingPolygon(false);
    setTempAreaBounds(null);

    if (type !== FILTER_TYPES.RADIO) setRadioCenter(null);
    if (type !== FILTER_TYPES.AREA) setAreaBounds(null);
    if (type !== FILTER_TYPES.POLIGONO) {
      setPolygonPoints([]);
      setPolygonComplete(false);
    }
    if (type !== FILTER_TYPES.DESAPARECIDO && type !== FILTER_TYPES.RUTA) {
      setPersonaSeleccionada(null);
    }

    setFiltroActivo(type);

    if (!onStatusChange) return;

    switch (type) {
      case FILTER_TYPES.RADIO:
        setIsSelectingRadioCenter(true);
        onStatusChange({ text: "Click en el mapa para seleccionar el centro del radio", color: "text-blue-600" });
        break;
      case FILTER_TYPES.AREA:
        setIsDrawingArea(true);
        onStatusChange({ text: "Arrastra para dibujar un rectangulo", color: "text-purple-600" });
        break;
      case FILTER_TYPES.POLIGONO:
        setIsDrawingPolygon(true);
        setPolygonPoints([]);
        setPolygonComplete(false);
        onStatusChange({ text: "Click izquierdo para puntos, click derecho para terminar", color: "text-pink-600" });
        break;
      default:
        onStatusChange({ text: "Cargando...", color: "text-blue-600" });
    }
  }, []);

  const resetFilters = useCallback((onStatusChange) => {
    setFiltroActivo(FILTER_TYPES.TODOS);
    setRadioCenter(null);
    setAreaBounds(null);
    setPolygonPoints([]);
    setPolygonComplete(false);
    setPersonaSeleccionada(null);
    setIsSelectingRadioCenter(false);
    setIsDrawingArea(false);
    setIsDrawingPolygon(false);
    setTempAreaBounds(null);

    if (onStatusChange) {
      onStatusChange({ text: "Filtros limpiados", color: "text-slate-600" });
    }
  }, []);

  return {
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
  };
}
