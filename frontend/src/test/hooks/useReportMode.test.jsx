import { act, renderHook } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { useReportMode } from '@/hooks/useReportMode';
import { toast } from 'sonner';

vi.mock('sonner', () => ({
    toast: {
        error: vi.fn(),
    },
}));

describe('useReportMode', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('si no esta autenticado muestra error y redirige a login', () => {
        const navigate = vi.fn();
        const onStatusChange = vi.fn();
        const { result } = renderHook(() => useReportMode(false, navigate));

        act(() => {
            result.current.toggleModoReporte(onStatusChange);
        });

        expect(toast.error).toHaveBeenCalledWith('Inicia sesion para reportar un avistamiento.');
        expect(navigate).toHaveBeenCalledWith('/IniciarSesion');
        expect(result.current.modoReporte).toBe(false);
        expect(onStatusChange).not.toHaveBeenCalled();
    });

    it('si esta autenticado alterna modo reporte y notifica estado', () => {
        const navigate = vi.fn();
        const onStatusChange = vi.fn();
        const { result } = renderHook(() => useReportMode(true, navigate));

        act(() => {
            result.current.toggleModoReporte(onStatusChange);
        });

        expect(result.current.modoReporte).toBe(true);
        expect(onStatusChange).toHaveBeenCalledWith({
            text: 'Click en el mapa para reportar',
            color: 'text-red-600'
        });

        act(() => {
            result.current.openReportModalAt({ lat: -34.6, lng: -58.4 });
        });

        expect(result.current.showReportModal).toBe(true);
        expect(result.current.selectedLatLng).toEqual({ lat: -34.6, lng: -58.4 });

        act(() => {
            result.current.toggleModoReporte(onStatusChange);
        });

        expect(result.current.modoReporte).toBe(false);
        expect(result.current.showReportModal).toBe(false);
        expect(result.current.selectedLatLng).toBeNull();
        expect(onStatusChange).toHaveBeenLastCalledWith({
            text: 'Modo reporte cancelado',
            color: 'text-slate-600'
        });
    });

    it('handleReportSuccess reinicia estado y ejecuta callback', () => {
        const navigate = vi.fn();
        const onAfterSuccess = vi.fn();
        const { result } = renderHook(() => useReportMode(true, navigate));

        act(() => {
            result.current.toggleModoReporte();
            result.current.openReportModalAt({ lat: -34.6, lng: -58.4 });
        });

        expect(result.current.modoReporte).toBe(true);
        expect(result.current.showReportModal).toBe(true);

        act(() => {
            result.current.handleReportSuccess(onAfterSuccess);
        });

        expect(result.current.modoReporte).toBe(false);
        expect(result.current.showReportModal).toBe(false);
        expect(result.current.selectedLatLng).toBeNull();
        expect(onAfterSuccess).toHaveBeenCalledTimes(1);
    });
});
