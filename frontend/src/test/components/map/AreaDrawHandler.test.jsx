import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render } from '@testing-library/react';
import AreaDrawHandler from '../../../components/map/AreaDrawHandler';
import * as ReactLeaflet from 'react-leaflet';
import L from 'leaflet';

// Mock react-leaflet
vi.mock('react-leaflet', () => ({
    useMap: vi.fn()
}));

describe('AreaDrawHandler', () => {
    let mockMap;

    beforeEach(() => {
        mockMap = {
            on: vi.fn(),
            off: vi.fn(),
            dragging: {
                disable: vi.fn(),
                enable: vi.fn()
            }
        };
        ReactLeaflet.useMap.mockReturnValue(mockMap);
        vi.clearAllMocks();
    });

    it('debe registrar eventos de mapa cuando isDrawing es true', () => {
        render(<AreaDrawHandler isDrawing={true} onAreaComplete={vi.fn()} onAreaUpdate={vi.fn()} />);

        expect(mockMap.on).toHaveBeenCalledWith('mousedown', expect.any(Function));
        expect(mockMap.on).toHaveBeenCalledWith('mousemove', expect.any(Function));
        expect(mockMap.on).toHaveBeenCalledWith('mouseup', expect.any(Function));
    });

    it('no debe registrar eventos ni habilitar arrastre cuando isDrawing es false al montar', () => {
        // En useEffect, si !isDrawing retorna temprano. Note que el cleanup no deshabilita cosas si no se registraron.
        render(<AreaDrawHandler isDrawing={false} onAreaComplete={vi.fn()} onAreaUpdate={vi.fn()} />);

        expect(mockMap.on).not.toHaveBeenCalled();
    });

    it('debe simular dibujo y llamar onAreaComplete con bounds de Leaflet correctos', () => {
        const onAreaCompleteFunc = vi.fn();
        const onAreaUpdateFunc = vi.fn();

        render(<AreaDrawHandler isDrawing={true} onAreaComplete={onAreaCompleteFunc} onAreaUpdate={onAreaUpdateFunc} />);

        // Obtener los callbacks registrados
        const mousedownCallback = mockMap.on.mock.calls.find(call => call[0] === 'mousedown')[1];
        const mousemoveCallback = mockMap.on.mock.calls.find(call => call[0] === 'mousemove')[1];
        const mouseupCallback = mockMap.on.mock.calls.find(call => call[0] === 'mouseup')[1];

        // Simular mousedown (botón izquierdo = 0)
        const startLatLng = L.latLng(-34.0, -58.0);
        mousedownCallback({ originalEvent: { button: 0 }, latlng: startLatLng });

        expect(mockMap.dragging.disable).toHaveBeenCalled();

        // Simular mousemove
        const moveLatLng = L.latLng(-35.0, -59.0);
        mousemoveCallback({ latlng: moveLatLng });

        expect(onAreaUpdateFunc).toHaveBeenCalledTimes(1);

        // Simular mouseup
        mouseupCallback({ latlng: moveLatLng });

        expect(onAreaCompleteFunc).toHaveBeenCalledTimes(1);
        expect(mockMap.dragging.enable).toHaveBeenCalled();

        // Validar que el bound pasado sea válido
        const resultingBounds = onAreaCompleteFunc.mock.calls[0][0];
        expect(resultingBounds.getNorthWest()).toBeDefined();
        expect(resultingBounds.getSouthEast()).toBeDefined();
    });
});
