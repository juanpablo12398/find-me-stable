import { describe, it, expect } from 'vitest';
import { HALO_CONFIG, markerColors, PERSONA_COLORS, MAP_COLORS, FILTER_TYPES, filtros } from '../../../components/map/MapConstants';

describe('MapConstants', () => {
    it('debe tener las configuraciones correctas de halo', () => {
        expect(HALO_CONFIG.CRITICO).toBeDefined();
        expect(HALO_CONFIG.CRITICO.isPulsing).toBe(true);
        expect(HALO_CONFIG.BAJO).toBeNull();
    });

    it('debe tener los colores correctos de marcador', () => {
        expect(markerColors.verificado).toBe('#10b981');
        expect(markerColors.nuevo).toBe('#ef4444');
    });

    it('debe tener una paleta de colores para personas', () => {
        expect(PERSONA_COLORS.length).toBeGreaterThan(0);
        expect(PERSONA_COLORS[0]).toBe('#e63946');
    });

    it('debe definir colores de mapa', () => {
        expect(MAP_COLORS.RADIO_CIRCLE).toBe('#3b82f6');
    });

    it('debe contener todos los tipos de filtro', () => {
        expect(FILTER_TYPES.TODOS).toBe('todos');
        expect(FILTER_TYPES.RUTA).toBe('ruta');
    });

    it('debe exportar array de filtros con su estructura', () => {
        expect(filtros).toBeInstanceOf(Array);
        expect(filtros.length).toBe(7);
        expect(filtros[0].id).toBe(FILTER_TYPES.TODOS);
        expect(filtros[0].label).toBe('Todos');
    });
});

