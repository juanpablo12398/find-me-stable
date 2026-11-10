import { Layers, Target, Square, Hexagon, User, Route, MapPin } from "lucide-react";

// Configuración de halo según nivel de urgencia (viene del backend)
export const HALO_CONFIG = {
    CRITICO: { rgb: '239,68,68', ringOpacity: [0.55, 0.28, 0.12], isPulsing: true, dotColor: '#ef4444' },
    ALTO: { rgb: '249,115,22', ringOpacity: [0.35, 0.18, 0.07], isPulsing: false, dotColor: '#f97316' },
    MEDIO: { rgb: '234,179,8', ringOpacity: [0.2, 0.1, 0.04], isPulsing: false, dotColor: '#eab308' },
    BAJO: null
};

// Colores para marcadores
export const markerColors = {
    verificado: "#10b981",
    noVerificado: "#f59e0b",
    nuevo: "#ef4444"
};

// Paleta de colores para distinguir personas - colores muy distintos entre sí
export const PERSONA_COLORS = [
    "#e63946", // rojo
    "#2a9d8f", // verde azulado
    "#e9c46a", // amarillo mostaza
    "#264653", // azul oscuro
    "#f4a261", // naranja
    "#9b5de5", // violeta
    "#00bbf9", // celeste
    "#f15bb5", // rosa fuerte
    "#00f5d4", // turquesa
    "#fee440", // amarillo brillante
    "#8338ec", // púrpura
    "#ff6b35", // naranja rojizo
    "#1982c4", // azul
    "#6a994e", // verde
    "#bc4749", // bordó
];

// Colores para filtros geoespaciales
export const MAP_COLORS = {
    RADIO_CIRCLE: "#3b82f6",
    AREA_RECTANGLE: "#8b5cf6",
    POLYGON: "#ec4899"
};

// Tipos de filtro
export const FILTER_TYPES = {
    TODOS: "todos",
    RADIO: "radio",
    AREA: "area",
    POLIGONO: "poligono",
    DESAPARECIDO: "desaparecido",
    RUTA: "ruta",
    ULTIMA_UBICACION: "ultima_ubicacion"
};

// Definición de filtros para la barra minimalista
export const filtros = [
    { id: FILTER_TYPES.TODOS, icon: Layers, label: "Todos", description: "Ver todos en el mapa" },
    { id: FILTER_TYPES.RADIO, icon: Target, label: "Por radio", description: "Buscar en radio" },
    { id: FILTER_TYPES.AREA, icon: Square, label: "Por área", description: "Dibujar rectángulo" },
    { id: FILTER_TYPES.POLIGONO, icon: Hexagon, label: "Por polígono", description: "Área personalizada" },
    { id: FILTER_TYPES.DESAPARECIDO, icon: User, label: "Por persona", description: "Filtrar por desaparecido" },
    { id: FILTER_TYPES.RUTA, icon: Route, label: "Trazar ruta", description: "Ver recorridos cronológicos" },
    { id: FILTER_TYPES.ULTIMA_UBICACION, icon: MapPin, label: "Última Ubicación", description: "Último avistamiento cronológico" }
];