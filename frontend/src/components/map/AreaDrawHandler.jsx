import { useEffect, useRef } from "react";
import { useMap } from "react-leaflet";
import L from "leaflet";

export default function AreaDrawHandler({ isDrawing, onAreaComplete, onAreaUpdate }) {
    const map = useMap();
    const startRef = useRef(null);
    const isDrawingRef = useRef(false);

    useEffect(() => {
        if (!isDrawing) {
            startRef.current = null;
            isDrawingRef.current = false;
            return;
        }

        const onMouseDown = (e) => {
            if (e.originalEvent.button !== 0) return;
            startRef.current = e.latlng;
            isDrawingRef.current = true;
            map.dragging.disable();
        };

        const onMouseMove = (e) => {
            if (!isDrawingRef.current || !startRef.current) return;
            const bounds = L.latLngBounds(startRef.current, e.latlng);
            onAreaUpdate(bounds);
        };

        const onMouseUp = (e) => {
            if (!isDrawingRef.current || !startRef.current) return;
            const bounds = L.latLngBounds(startRef.current, e.latlng);
            onAreaComplete(bounds);
            startRef.current = null;
            isDrawingRef.current = false;
            map.dragging.enable();
        };

        map.on('mousedown', onMouseDown);
        map.on('mousemove', onMouseMove);
        map.on('mouseup', onMouseUp);

        return () => {
            map.off('mousedown', onMouseDown);
            map.off('mousemove', onMouseMove);
            map.off('mouseup', onMouseUp);
            map.dragging.enable();
        };
    }, [isDrawing, map, onAreaComplete, onAreaUpdate]);

    return null;
}