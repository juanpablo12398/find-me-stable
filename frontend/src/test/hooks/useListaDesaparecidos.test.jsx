import React from 'react';
import { act, renderHook, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useListaDesaparecidos, SORT_MAP, PAGE_SIZE } from '@/hooks/useListaDesaparecidos';
import { DesaparecidoService } from '@/services/DesaparecidoService';

vi.mock('@/services/DesaparecidoService', () => ({
    DesaparecidoService: {
        obtenerPaginado: vi.fn(),
        getEstadisticas: vi.fn(),
    },
}));

const defaultPagedResponse = {
    content: [{ id: '1', nombre: 'Ana' }],
    page: 0,
    size: PAGE_SIZE,
    totalElements: 13,
    totalPages: 2,
    last: false,
};

const defaultStats = { total: 5, buscando: 3, encontrado: 2 };

function renderHookWithQuery(hook) {
    const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false } }
    });
    const wrapper = ({ children }) => (
        <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    );
    return renderHook(hook, { wrapper });
}

describe('useListaDesaparecidos', () => {
    beforeEach(() => {
        vi.clearAllMocks();
        DesaparecidoService.obtenerPaginado.mockResolvedValue(defaultPagedResponse);
        DesaparecidoService.getEstadisticas.mockResolvedValue(defaultStats);
    });

    it('inicializa con valores default correctos', () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        expect(result.current.searchTerm).toBe('');
        expect(result.current.debouncedSearch).toBe('');
        expect(result.current.viewMode).toBe('grid');
        expect(result.current.sortBy).toBe('urgencia');
        expect(result.current.filterEstado).toBe('all');
        expect(result.current.page).toBe(0);
    });

    it('handleSearchChange actualiza searchTerm inmediatamente', () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        act(() => {
            result.current.handleSearchChange({ target: { value: 'ana' } });
        });

        expect(result.current.searchTerm).toBe('ana');
        // debouncedSearch no se actualiza aún (sin el timeout)
        expect(result.current.debouncedSearch).toBe('');
    });

    it('debouncedSearch se actualiza tras 350ms', async () => {
        vi.useFakeTimers({ shouldAdvanceTime: true });
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        act(() => {
            result.current.handleSearchChange({ target: { value: 'juan' } });
        });

        expect(result.current.debouncedSearch).toBe('');

        await act(async () => {
            vi.advanceTimersByTime(400);
        });

        expect(result.current.debouncedSearch).toBe('juan');
        vi.useRealTimers();
    });

    it('setViewMode cambia entre grid y list', () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        act(() => {
            result.current.setViewMode('list');
        });

        expect(result.current.viewMode).toBe('list');

        act(() => {
            result.current.setViewMode('grid');
        });

        expect(result.current.viewMode).toBe('grid');
    });

    it('setSortBy actualiza el criterio de orden', () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        act(() => {
            result.current.setSortBy('recent');
        });

        expect(result.current.sortBy).toBe('recent');
    });

    it('setFilterEstado actualiza el filtro de estado', () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        act(() => {
            result.current.setFilterEstado('BUSCANDO');
        });

        expect(result.current.filterEstado).toBe('BUSCANDO');
    });

    it('page se resetea a 0 cuando cambia sortBy', async () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        act(() => {
            result.current.setPage(1);
        });

        expect(result.current.page).toBe(1);

        act(() => {
            result.current.setSortBy('name');
        });

        // El useEffect reseteará la page como efecto secundario
        await act(async () => {});

        expect(result.current.page).toBe(0);
    });

    it('page se resetea a 0 cuando cambia filterEstado', async () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        act(() => {
            result.current.setPage(1);
        });

        act(() => {
            result.current.setFilterEstado('ENCONTRADO');
        });

        await act(async () => {});

        expect(result.current.page).toBe(0);
    });

    it('canGoPrev es false en página 0', () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        expect(result.current.page).toBe(0);
        expect(result.current.canGoPrev).toBe(false);
    });

    it('canGoPrev es true cuando page > 0', () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        act(() => {
            result.current.setPage(1);
        });

        expect(result.current.canGoPrev).toBe(true);
    });

    it('canGoNext es true cuando hay más páginas disponibles', async () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        // defaultPagedResponse.totalPages = 2, page = 0 → canGoNext = true
        await waitFor(() => {
            expect(result.current.pageData.totalPages).toBe(2);
        });

        expect(result.current.canGoNext).toBe(true);
    });

    it('canGoNext es false cuando estamos en la última página', async () => {
        DesaparecidoService.obtenerPaginado.mockResolvedValue({
            ...defaultPagedResponse,
            totalPages: 1,
            last: true,
        });

        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        await waitFor(() => {
            expect(result.current.pageData.totalPages).toBe(1);
        });

        expect(result.current.canGoNext).toBe(false);
    });

    it('SORT_MAP incluye todos los criterios de orden conocidos', () => {
        expect(SORT_MAP.recent).toBe('reciente');
        expect(SORT_MAP.oldest).toBe('antiguo');
        expect(SORT_MAP.name).toBe('nombre');
        expect(SORT_MAP.urgencia).toBe('urgencia');
    });

    it('PAGE_SIZE es 12', () => {
        expect(PAGE_SIZE).toBe(12);
    });

    it('consulta el servicio con los parámetros correctos cuando hay filtro activo', async () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        act(() => {
            result.current.setSortBy('name');
            result.current.setFilterEstado('BUSCANDO');
        });

        await waitFor(() => {
            const calls = DesaparecidoService.obtenerPaginado.mock.calls;
            const lastCall = calls[calls.length - 1]?.[0];
            expect(lastCall?.estado).toBe('BUSCANDO');
            expect(lastCall?.sort).toBe('nombre');
        });
    });

    it('no envía el estado al servicio cuando filterEstado es "all"', async () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        await waitFor(() => {
            expect(DesaparecidoService.obtenerPaginado).toHaveBeenCalled();
        });

        const firstCall = DesaparecidoService.obtenerPaginado.mock.calls[0]?.[0];
        expect(firstCall?.estado).toBeUndefined();
    });

    it('devuelve desaparecidos y estadísticas cargadas del servicio', async () => {
        const { result } = renderHookWithQuery(() => useListaDesaparecidos());

        await waitFor(() => {
            expect(result.current.desaparecidos).toHaveLength(1);
        });

        expect(result.current.desaparecidos[0].nombre).toBe('Ana');

        await waitFor(() => {
            expect(result.current.estadoCounts.total).toBe(5);
        });

        expect(result.current.estadoCounts.buscando).toBe(3);
    });
});
