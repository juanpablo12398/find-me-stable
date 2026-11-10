import L from "leaflet";
import { HALO_CONFIG, PERSONA_COLORS } from "./MapConstants";

// Fix leaflet default marker icon
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png",
    iconUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png",
    shadowUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png",
});

export const getHaloFromNivel = (nivelUrgencia) => {
    if (!nivelUrgencia || nivelUrgencia === 'BAJO') return null;
    return HALO_CONFIG[nivelUrgencia] || null;
};

export const getDotColor = (nivelUrgencia) => {
    const config = getHaloFromNivel(nivelUrgencia);
    return config?.dotColor || null;
};

// Obtener color según ID de persona (funciona con UUIDs)
export const getPersonaColor = (personaId) => {
    if (!personaId) return "#9ca3af"; // gris para sin asignar

    // Usar los primeros 8 caracteres del UUID para generar un índice único
    const str = String(personaId).replace(/-/g, '').substring(0, 8);

    // Convertir hex a número
    const num = parseInt(str, 16);

    if (isNaN(num)) {
        // Fallback: hash simple si no es hex válido
        let hash = 0;
        for (let i = 0; i < String(personaId).length; i++) {
            hash = ((hash << 5) - hash) + String(personaId).charCodeAt(i);
        }
        return PERSONA_COLORS[Math.abs(hash) % PERSONA_COLORS.length];
    }

    const index = num % PERSONA_COLORS.length;
    return PERSONA_COLORS[index];
};

// Convierte haloStyle a box-shadow CSS para avatares circulares (lista lateral)
export const buildAvatarHaloCSS = (nivelUrgencia) => {
    const config = getHaloFromNivel(nivelUrgencia);
    if (!config) return undefined;
    const { rgb } = config;
    const a1 = nivelUrgencia === 'CRITICO' ? 0.7 : nivelUrgencia === 'ALTO' ? 0.45 : 0.25;
    const a2 = nivelUrgencia === 'CRITICO' ? 0.35 : nivelUrgencia === 'ALTO' ? 0.22 : 0.12;
    const a3 = nivelUrgencia === 'CRITICO' ? 0.15 : nivelUrgencia === 'ALTO' ? 0.1 : 0.05;
    return `0 0 0 2px rgba(${rgb},${a1}), 0 0 8px 3px rgba(${rgb},${a2}), 0 0 16px 6px rgba(${rgb},${a3})`;
};

// Custom marker icons con halo de urgencia
export const createCustomIcon = (color, nivelUrgencia = null, number = null) => {
    const haloConfig = getHaloFromNivel(nivelUrgencia);

    const buildHaloShadow = (config) => {
        if (!config) return "";
        const { rgb, ringOpacity } = config;

        return [
            `0 0 0 10px rgba(${rgb}, ${ringOpacity[0]})`,
            `0 0 0 20px rgba(${rgb}, ${ringOpacity[1]})`,
            `0 0 0 30px rgba(${rgb}, ${ringOpacity[2]})`,
            "0 3px 10px rgba(0,0,0,0.35)"
        ].join(", ");
    };

    const haloShadow = buildHaloShadow(haloConfig) || "0 3px 10px rgba(0,0,0,0.35)";

    const innerContent = number 
        ? `<div style="transform:rotate(45deg); display:flex; align-items:center; justify-content:center; color:white; font-size:14px; font-weight:bold;">${number}</div>`
        : `<div style="width:9px;height:9px;background:white;border-radius:50%;transform:rotate(45deg);"></div>`;

    return L.divIcon({
        className: "custom-marker-no-clip",
        html: `
      <style>
        .custom-marker-no-clip { background:transparent!important; border:none!important; overflow:visible!important; }
      </style>
      <div style="position:relative;width:64px;height:64px;display:flex;align-items:center;justify-content:center;">
        <div style="
          position:relative;z-index:2;
          background:${color};
          width:30px;height:30px;
          border-radius:50% 50% 50% 0;
          transform:rotate(-45deg);
          border:3px solid white;
          box-shadow:${haloShadow};
          display:flex;align-items:center;justify-content:center;
        ">
          ${innerContent}
        </div>
      </div>
    `,
        iconSize: [64, 64],
        iconAnchor: [32, 48],
        popupAnchor: [0, -48],
    });
};

// Convertir coordenadas a WKT
export const coordsToWKT = (coords) => {
    const wktCoords = coords.map(({ lat, lng }) => `${lng} ${lat}`).join(', ');
    const firstCoord = `${coords[0].lng} ${coords[0].lat}`;
    return `POLYGON((${wktCoords}, ${firstCoord}))`;
};
