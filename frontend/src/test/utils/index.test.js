import { describe, it, expect, vi } from 'vitest';
import { createPageUrl, formatDate, truncateText, generateId, debounce } from '@/utils/index';

describe('Global Utils', () => {
    it('createPageUrl debe retornar la ruta correcta', () => {
        expect(createPageUrl('Home')).toBe('/Home');
        expect(createPageUrl('Mapa')).toBe('/Mapa');
    });

    describe('formatDate', () => {
        it('debe retornar string vacio para fechas falsy', () => {
            expect(formatDate(null)).toBe('');
            expect(formatDate(undefined)).toBe('');
        });

        it('debe formatear fecha en formato short por defecto', () => {
            const date = new Date('2025-01-01T12:00:00Z');
            expect(formatDate(date)).toMatch(/1.*ene.*2025/i);
        });

        it('debe formatear fecha en formato long', () => {
            const date = new Date('2025-01-01T12:00:00Z');
            expect(formatDate(date, 'long')).toMatch(/1.*enero.*2025/i);
        });
    });

    describe('truncateText', () => {
        it('debe devolver el mismo texto si es mas corto que max length', () => {
            expect(truncateText('Hola', 10)).toBe('Hola');
        });

        it('debe truncar y añadir puntos suspensivos', () => {
            expect(truncateText('Hola mundo cruel', 10)).toBe('Hola mundo...');
        });

        it('debe manejar textos nulos o indefinidos', () => {
            expect(truncateText(null)).toBeNull();
            expect(truncateText('')).toBe('');
        });
    });

    describe('generateId', () => {
        it('debe generar un string que empieza con id_', () => {
            const id = generateId();
            expect(id.startsWith('id_')).toBe(true);
            expect(id.length).toBeGreaterThan(10);
        });
    });

    describe('debounce', () => {
        it('debe ejecutar la funcion solo despues del tiempo de espera', () => {
            vi.useFakeTimers();
            const callback = vi.fn();
            const debounced = debounce(callback, 500);

            debounced();
            debounced();
            debounced();

            expect(callback).not.toHaveBeenCalled();

            vi.advanceTimersByTime(500);

            expect(callback).toHaveBeenCalledTimes(1);
            vi.useRealTimers();
        });
    });
});
