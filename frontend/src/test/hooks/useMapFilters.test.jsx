import { act, renderHook } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { useMapFilters } from '@/hooks/useMapFilters';
import { FILTER_TYPES } from '@/components/map/MapConstants';

describe('useMapFilters', () => {
    it('inicializa con valores por defecto', () => {
        const { result } = renderHook(() => useMapFilters());

        expect(result.current.filtroActivo).toBe(FILTER_TYPES.TODOS);
        expect(result.current.personaSeleccionada).toBeNull();
        expect(result.current.showFilters).toBe(true);
        expect(result.current.radioCenter).toBeNull();
        expect(result.current.radioKm).toBe(5);
        expect(result.current.isSelectingRadioCenter).toBe(false);
        expect(result.current.areaBounds).toBeNull();
        expect(result.current.isDrawingArea).toBe(false);
        expect(result.current.polygonPoints).toEqual([]);
        expect(result.current.isDrawingPolygon).toBe(false);
        expect(result.current.polygonComplete).toBe(false);
    });

    it('activa filtro de radio y setea estado de seleccion', () => {
        const { result } = renderHook(() => useMapFilters());
        const onStatusChange = vi.fn();

        act(() => {
            result.current.activateFilter(FILTER_TYPES.RADIO, onStatusChange);
        });

        expect(result.current.filtroActivo).toBe(FILTER_TYPES.RADIO);
        expect(result.current.isSelectingRadioCenter).toBe(true);
        expect(onStatusChange).toHaveBeenCalledWith({
            text: 'Click en el mapa para seleccionar el centro del radio',
            color: 'text-blue-600'
        });
    });

    it('activa filtro de area y resetea estados no compatibles', () => {
        const { result } = renderHook(() => useMapFilters());
        const onStatusChange = vi.fn();

        act(() => {
            result.current.setRadioCenter({ lat: -34.6, lng: -58.4 });
            result.current.setPolygonPoints([{ lat: -34.5, lng: -58.5 }]);
            result.current.setPolygonComplete(true);
            result.current.setPersonaSeleccionada('persona-1');
        });

        act(() => {
            result.current.activateFilter(FILTER_TYPES.AREA, onStatusChange);
        });

        expect(result.current.filtroActivo).toBe(FILTER_TYPES.AREA);
        expect(result.current.isDrawingArea).toBe(true);
        expect(result.current.radioCenter).toBeNull();
        expect(result.current.polygonPoints).toEqual([]);
        expect(result.current.polygonComplete).toBe(false);
        expect(result.current.personaSeleccionada).toBeNull();
        expect(onStatusChange).toHaveBeenCalledWith({
            text: 'Arrastra para dibujar un rectangulo',
            color: 'text-purple-600'
        });
    });

    it('activa filtro de poligono y prepara dibujo', () => {
        const { result } = renderHook(() => useMapFilters());
        const onStatusChange = vi.fn();

        act(() => {
            result.current.activateFilter(FILTER_TYPES.POLIGONO, onStatusChange);
        });

        expect(result.current.filtroActivo).toBe(FILTER_TYPES.POLIGONO);
        expect(result.current.isDrawingPolygon).toBe(true);
        expect(result.current.polygonPoints).toEqual([]);
        expect(result.current.polygonComplete).toBe(false);
        expect(onStatusChange).toHaveBeenCalledWith({
            text: 'Click izquierdo para puntos, click derecho para terminar',
            color: 'text-pink-600'
        });
    });

    it('resetea filtros y notifica estado', () => {
        const { result } = renderHook(() => useMapFilters());
        const onStatusChange = vi.fn();

        act(() => {
            result.current.setRadioCenter({ lat: -34.6, lng: -58.4 });
            result.current.setAreaBounds({ north: 1, south: 0, east: 1, west: 0 });
            result.current.setPolygonPoints([{ lat: 1, lng: 1 }]);
            result.current.setPolygonComplete(true);
            result.current.setPersonaSeleccionada('persona-1');
            result.current.setIsSelectingRadioCenter(true);
            result.current.setIsDrawingArea(true);
            result.current.setIsDrawingPolygon(true);
            result.current.setTempAreaBounds({ north: 2, south: 1, east: 2, west: 1 });
        });

        act(() => {
            result.current.resetFilters(onStatusChange);
        });

        expect(result.current.filtroActivo).toBe(FILTER_TYPES.TODOS);
        expect(result.current.radioCenter).toBeNull();
        expect(result.current.areaBounds).toBeNull();
        expect(result.current.polygonPoints).toEqual([]);
        expect(result.current.polygonComplete).toBe(false);
        expect(result.current.personaSeleccionada).toBeNull();
        expect(result.current.isSelectingRadioCenter).toBe(false);
        expect(result.current.isDrawingArea).toBe(false);
        expect(result.current.isDrawingPolygon).toBe(false);
        expect(result.current.tempAreaBounds).toBeNull();
        expect(onStatusChange).toHaveBeenCalledWith({
            text: 'Filtros limpiados',
            color: 'text-slate-600'
        });
    });
});
