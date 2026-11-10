import { useEffect } from "react";
import { useMapEvents, useMap } from "react-leaflet";
import L from "leaflet";

export default function MapClickHandler({ onMapClick, isActive, onRightClick, modoReporte }) {
    const map = useMap();

    // Cursor crosshair directo en el container de Leaflet
    useEffect(() => {
        const container = map.getContainer();
        if (isActive || modoReporte) {
            container.style.cursor = "crosshair";
        } else {
            container.style.cursor = "";
        }
        return () => { container.style.cursor = ""; };
    }, [isActive, modoReporte, map]);

    // onMapClick es estable (useCallback con deps vacías + mapStateRef en padre),
    // asi que useMapEvents siempre tiene la funcion correcta sin necesitar re-subscribe
    useMapEvents({
        click: (e) => {
            onMapClick(e.latlng);
        },
        contextmenu: (e) => {
            if (onRightClick) {
                L.DomEvent.preventDefault(e);
                onRightClick(e);
            }
        }
    });
    return null;
}