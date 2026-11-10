import { describe, it, expect, vi } from 'vitest';

// Mockeamos leaflet porque no estamos probando un dom real acá y L.Icon.Default se modifica al importar
vi.mock('leaflet', () => {
    return {
        default: {
            Icon: {
                Default: {
                    prototype: {
                        _getIconUrl: vi.fn()
                    },
                    mergeOptions: vi.fn()
                }
            },
            divIcon: vi.fn((options) => options)
        }
    };
});

// Mock de constantes
vi.mock('@/components/map/MapConstants', () => ({
    HALO_CONFIG: {
        'CRITICO': { rgb: '255,0,0', ringOpacity: [0.5, 0.3, 0.1], dotColor: '#ff0000' },
        'ALTO': { rgb: '255,165,0', ringOpacity: [0.4, 0.2, 0.1], dotColor: '#ffa500' }
    },
    PERSONA_COLORS: ['#ff0000', '#00ff00', '#0000ff']
}));

import { getHaloFromNivel, getDotColor, getPersonaColor, buildAvatarHaloCSS, coordsToWKT, createCustomIcon } from '@/components/map/MapUtils';

describe('MapUtils', () => {
    it('getHaloFromNivel should return correct config for CRITICO', () => {
        expect(getHaloFromNivel('CRITICO')).toEqual(expect.objectContaining({ rgb: '255,0,0' }));
    });

    it('getHaloFromNivel should return null for BAJO or missing', () => {
        expect(getHaloFromNivel('BAJO')).toBeNull();
        expect(getHaloFromNivel(null)).toBeNull();
    });

    it('getDotColor should return correct color based on level', () => {
        expect(getDotColor('CRITICO')).toBe('#ff0000');
        expect(getDotColor('BAJO')).toBeNull();
    });

    it('getPersonaColor should map a UUID to a specific color deterministically', () => {
        const id1 = '123e4567-e89b-12d3-a456-426614174000';
        const id2 = '987e6543-e21b-12d3-a456-426614174000';

        const color1 = getPersonaColor(id1);
        const color1_again = getPersonaColor(id1);
        const color2 = getPersonaColor(id2);

        expect(color1).toBe(color1_again);
        // Si no se asigna ID, debe retornar un color por defecto
        expect(getPersonaColor(null)).toBe('#9ca3af');
    });

    it('buildAvatarHaloCSS should generate a CSS string containing rgb', () => {
        const css = buildAvatarHaloCSS('CRITICO');
        expect(css).toContain('rgba(255,0,0');
        expect(css).toContain('px');

        expect(buildAvatarHaloCSS('BAJO')).toBeUndefined();
    });

    it('coordsToWKT should properly format polygon coordinates to WKT with enclosed polygon', () => {
        const coords = [
            { lat: -34, lng: -58 },
            { lat: -35, lng: -59 },
            { lat: -36, lng: -60 }
        ];
        const wkt = coordsToWKT(coords);
        expect(wkt).toBe('POLYGON((-58 -34, -59 -35, -60 -36, -58 -34))');
    });

    it('createCustomIcon should return options for divIcon containing provided color', () => {
        const iconOptions = createCustomIcon('#ff00ff', 'CRITICO');
        expect(iconOptions.html).toContain('background:#ff00ff');
        expect(iconOptions.html).toContain('rgba(255,0,0'); // El halo shadow
        expect(iconOptions.className).toBe('custom-marker-no-clip');
    });
});

