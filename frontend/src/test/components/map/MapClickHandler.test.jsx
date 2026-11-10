import { describe, it, expect, vi } from 'vitest';
import { render } from '@testing-library/react';
import MapClickHandler from '@/components/map/MapClickHandler';

// Map context mock para react-leaflet
const mockMap = {
    getContainer: vi.fn(),
};

vi.mock('react-leaflet', () => {
    return {
        useMap: () => mockMap,
        useMapEvents: (handlers) => {
            // Devolver las funciones para que se puedan simular los eventos
            mockMap.events = handlers;
            return mockMap;
        }
    };
});

// L mock
vi.mock('leaflet', () => {
    return {
        default: {
            DomEvent: {
                preventDefault: vi.fn()
            }
        }
    };
});

import L from 'leaflet';

describe('MapClickHandler', () => {
    it('sets cursor to crosshair when isActive is true', () => {
        const styleMock = { cursor: '' };
        mockMap.getContainer.mockReturnValue({ style: styleMock });

        render(<MapClickHandler onMapClick={vi.fn()} isActive={true} />);

        expect(styleMock.cursor).toBe('crosshair');
    });

    it('removes crosshair cursor when isActive is false', () => {
        const styleMock = { cursor: 'crosshair' };
        mockMap.getContainer.mockReturnValue({ style: styleMock });

        const { unmount } = render(<MapClickHandler onMapClick={vi.fn()} isActive={false} />);

        expect(styleMock.cursor).toBe('');
        // Al desmontar, también limpia
        unmount();
        expect(styleMock.cursor).toBe('');
    });

    it('triggers onMapClick correctly', () => {
        const onMapClickMock = vi.fn();
        render(<MapClickHandler onMapClick={onMapClickMock} isActive={false} />);

        // Simular click
        expect(mockMap.events.click).toBeDefined();
        mockMap.events.click({ latlng: { lat: -34, lng: -58 } });

        expect(onMapClickMock).toHaveBeenCalledWith({ lat: -34, lng: -58 });
    });

    it('triggers onRightClick and prevents default correctly', () => {
        const onRightClickMock = vi.fn();
        render(<MapClickHandler onMapClick={vi.fn()} onRightClick={onRightClickMock} isActive={false} />);

        // Simular click derecho
        expect(mockMap.events.contextmenu).toBeDefined();
        const fakeEvent = { latlng: { lat: -34, lng: -58 } };
        mockMap.events.contextmenu(fakeEvent);

        expect(L.DomEvent.preventDefault).toHaveBeenCalledWith(fakeEvent);
        expect(onRightClickMock).toHaveBeenCalledWith(fakeEvent);
    });
});
