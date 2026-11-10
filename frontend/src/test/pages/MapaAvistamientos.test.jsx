import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import MapaAvistamientos from '@/pages/MapaAvistamientos';
import { AvistamientoService } from '@/services/AvistamientoService';
import { DesaparecidoService } from '@/services/DesaparecidoService';
import { toast } from 'sonner';

const navigateMock = vi.fn();
let authState = { user: { id: 'u1' }, isAuthenticated: true };
let mapFiltersState;
let reportModeState;

vi.mock('react-router-dom', async () => {
    const actual = await vi.importActual('react-router-dom');
    return {
        ...actual,
        useNavigate: () => navigateMock,
    };
});

vi.mock('react-leaflet', () => ({
    MapContainer: ({ children }) => <div data-testid="map">{children}</div>,
    TileLayer: () => <div data-testid="tile" />,
    Circle: () => <div data-testid="circle" />,
    Rectangle: () => <div data-testid="rectangle" />,
    Polygon: () => <div data-testid="polygon" />,
    Polyline: () => <div data-testid="polyline" />,
    Marker: ({ children }) => <div data-testid="marker">{children}</div>,
    Popup: ({ children }) => <div>{children}</div>,
}));

vi.mock('react-leaflet-cluster', () => ({
    default: ({ children }) => <div data-testid="cluster">{children}</div>,
}));

vi.mock('framer-motion', () => ({
    motion: {
        div: ({ children, ...props }) => <div {...props}>{children}</div>,
    },
    AnimatePresence: ({ children }) => <>{children}</>,
}));

vi.mock('sonner', () => ({
    toast: {
        error: vi.fn(),
    },
}));

vi.mock('@/hooks/useAuth.jsx', () => ({
    useAuth: () => authState,
}));

vi.mock('@/hooks/useMapFilters', () => ({
    useMapFilters: () => mapFiltersState,
}));

vi.mock('@/hooks/useReportMode', () => ({
    useReportMode: () => reportModeState,
}));

vi.mock('@/services/AvistamientoService', () => ({
    AvistamientoService: {
        getAvistamientosParaMapa: vi.fn(),
        getEnRadio: vi.fn(),
        getEnArea: vi.fn(),
        getEnPoligono: vi.fn(),
        getPorDesaparecido: vi.fn(),
    },
}));

vi.mock('@/services/DesaparecidoService', () => ({
    DesaparecidoService: {
        obtenerTodos: vi.fn(),
    },
}));

vi.mock('@/components/map/FilterBar', () => ({
    default: ({ activateFilter, resetFilters }) => (
        <div>
            <button onClick={() => activateFilter('todos')}>Filtro</button>
            <button onClick={resetFilters}>Reset</button>
        </div>
    ),
}));
vi.mock('@/components/map/FilterConfigPanel', () => ({ default: () => <div data-testid="config" /> }));
vi.mock('@/components/map/PersonasSidebar', () => ({ default: () => <div data-testid="sidebar" /> }));
vi.mock('@/components/map/ReportModal', () => ({
    default: ({ onSuccess, onClose, show }) => (
        <div data-testid="report-modal" data-show={show ? 'open' : 'closed'}>
            <button onClick={onSuccess}>report-success</button>
            <button onClick={onClose}>report-close</button>
        </div>
    ),
}));
vi.mock('@/components/map/MapClickHandler', () => ({
    default: ({ onMapClick, onRightClick }) => (
        <div>
            <button onClick={() => onMapClick?.({ lat: -34.7, lng: -58.5 })}>map-click</button>
            <button onClick={() => onRightClick?.()}>map-right-click</button>
        </div>
    ),
}));
vi.mock('@/components/map/AreaDrawHandler', () => ({
    default: ({ onAreaComplete }) => <button onClick={() => onAreaComplete?.({ id: 'bounds' })}>area-complete</button>
}));
vi.mock('@/components/map/MarkerWithCenter', () => ({ default: ({ children }) => <div>{children}</div> }));
vi.mock('@/components/map/MapPopupContent', () => ({ default: () => <div>Popup</div> }));

vi.mock('@/components/map/MapUtils', () => ({
    coordsToWKT: () => 'POLYGON((0 0,1 1,0 0))',
    createCustomIcon: () => ({}),
    getPersonaColor: () => '#123456',
}));

vi.mock('@/components/map/MapConstants', () => ({
    FILTER_TYPES: {
        TODOS: 'todos',
        RADIO: 'radio',
        AREA: 'area',
        POLIGONO: 'poligono',
        DESAPARECIDO: 'desaparecido',
        RUTA: 'ruta',
    },
    MAP_COLORS: {
        RADIO_CIRCLE: '#3b82f6',
        AREA_RECTANGLE: '#8b5cf6',
        POLYGON: '#ec4899',
    },
}));

function renderWithProviders(ui) {
    const queryClient = new QueryClient();
    const invalidateSpy = vi.spyOn(queryClient, 'invalidateQueries');
    return {
        invalidateSpy,
        ...render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>{ui}</MemoryRouter>
            </QueryClientProvider>
        )
    };
}

const createDefaultFilterState = () => ({
    filtroActivo: 'todos',
    personaSeleccionada: null,
    setPersonaSeleccionada: vi.fn(),
    showFilters: true,
    setShowFilters: vi.fn(),
    radioCenter: null,
    setRadioCenter: vi.fn(),
    radioKm: 5,
    setRadioKm: vi.fn(),
    isSelectingRadioCenter: false,
    setIsSelectingRadioCenter: vi.fn(),
    areaBounds: null,
    setAreaBounds: vi.fn(),
    isDrawingArea: false,
    setIsDrawingArea: vi.fn(),
    tempAreaBounds: null,
    setTempAreaBounds: vi.fn(),
    polygonPoints: [],
    setPolygonPoints: vi.fn(),
    isDrawingPolygon: false,
    setIsDrawingPolygon: vi.fn(),
    polygonComplete: false,
    setPolygonComplete: vi.fn(),
    activateFilter: vi.fn(),
    resetFilters: vi.fn(),
});

const createDefaultReportState = () => ({
    modoReporte: false,
    selectedLatLng: null,
    showReportModal: false,
    setShowReportModal: vi.fn(),
    toggleModoReporte: vi.fn(),
    openReportModalAt: vi.fn(),
    handleReportSuccess: vi.fn(),
});

describe('MapaAvistamientos', () => {
    beforeEach(() => {
        vi.clearAllMocks();
        authState = { user: { id: 'u1' }, isAuthenticated: true };
        mapFiltersState = createDefaultFilterState();
        reportModeState = createDefaultReportState();
        window.history.pushState({}, '', '/');

        AvistamientoService.getAvistamientosParaMapa.mockResolvedValue([
            { id: 'a1', latitud: -34.6, longitud: -58.4, desaparecidoId: 'd1' },
        ]);
        AvistamientoService.getEnRadio.mockResolvedValue([{ id: 'r1', latitud: -34.6, longitud: -58.4, desaparecidoId: 'd1' }]);
        AvistamientoService.getEnArea.mockResolvedValue([{ id: 'ar1', latitud: -34.61, longitud: -58.41, desaparecidoId: 'd1' }]);
        AvistamientoService.getEnPoligono.mockResolvedValue([{ id: 'p1', latitud: -34.62, longitud: -58.42, desaparecidoId: 'd1' }]);
        AvistamientoService.getPorDesaparecido.mockResolvedValue([
            { id: 'd2', latitud: -34.61, longitud: -58.41, desaparecidoId: 'd1', fechaHora: '2026-01-03T12:00:00Z' },
            { id: 'd1', latitud: -34.6, longitud: -58.4, desaparecidoId: 'd1', fechaHora: '2026-01-01T10:00:00Z' },
        ]);

        DesaparecidoService.obtenerTodos.mockResolvedValue([
            { id: 'd1', nombre: 'Ana', apellido: 'Gomez' },
        ]);
    });

    it('renderiza mapa y contador de resultados', async () => {
        renderWithProviders(<MapaAvistamientos />);

        expect(screen.getByTestId('map')).toBeInTheDocument();

        await waitFor(() => {
            expect(screen.getByText(/1 avistamientos/i)).toBeInTheDocument();
            expect(screen.getByText(/1 personas/i)).toBeInTheDocument();
        });

        fireEvent.click(screen.getByRole('button', { name: /Activar modo reporte/i }));
        expect(reportModeState.toggleModoReporte).toHaveBeenCalled();
    });

    it('si no hay sesion, el boton flotante redirige a login', async () => {
        authState = { user: null, isAuthenticated: false };
        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: /Registrar persona extraviada/i }));

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalled();
            expect(navigateMock).toHaveBeenCalledWith('/IniciarSesion');
        });
    });

    it('si hay sesion, el boton flotante navega a registrar desaparecido', async () => {
        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: /Registrar persona extraviada/i }));

        await waitFor(() => {
            expect(navigateMock).toHaveBeenCalledWith('/RegistrarDesaparecido');
        });
    });

    it('usa filtro por radio y dibuja circulo', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'radio',
            radioCenter: { lat: -34.6, lng: -58.4 },
            radioKm: 8,
        };

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(AvistamientoService.getEnRadio).toHaveBeenCalledWith(-34.6, -58.4, 8);
            expect(screen.getByTestId('circle')).toBeInTheDocument();
        });
    });

    it('usa filtro por area, dibuja rectangulo y permite completar area', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'area',
            areaBounds: {
                getSouth: () => -35,
                getWest: () => -59,
                getNorth: () => -34,
                getEast: () => -58,
            },
        };

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(AvistamientoService.getEnArea).toHaveBeenCalledWith(-35, -34, -59, -58);
            expect(screen.getByTestId('rectangle')).toBeInTheDocument();
        });

        fireEvent.click(screen.getByRole('button', { name: 'area-complete' }));
        expect(mapFiltersState.setAreaBounds).toHaveBeenCalledWith({ id: 'bounds' });
        expect(mapFiltersState.setIsDrawingArea).toHaveBeenCalledWith(false);
        expect(mapFiltersState.setTempAreaBounds).toHaveBeenCalledWith(null);
    });

    it('usa filtro por area con tempAreaBounds y activa dashArray de dibujo', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'area',
            areaBounds: null,
            tempAreaBounds: { id: 'temp-bounds' },
        };

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(screen.getByTestId('rectangle')).toBeInTheDocument();
        });

        expect(AvistamientoService.getEnArea).not.toHaveBeenCalled();
    });

    it('usa filtro por poligono y dibuja poligono', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'poligono',
            polygonComplete: true,
            polygonPoints: [
                { lat: -34.6, lng: -58.4 },
                { lat: -34.7, lng: -58.5 },
                { lat: -34.8, lng: -58.6 },
            ],
        };

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(AvistamientoService.getEnPoligono).toHaveBeenCalledWith('POLYGON((0 0,1 1,0 0))');
            expect(screen.getByTestId('polygon')).toBeInTheDocument();
        });
    });

    it('poligono incompleto tambien se renderiza para guiar dibujo', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'poligono',
            polygonComplete: false,
            polygonPoints: [
                { lat: -34.6, lng: -58.4 },
                { lat: -34.7, lng: -58.5 },
            ],
        };

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(screen.getByTestId('polygon')).toBeInTheDocument();
        });

        expect(AvistamientoService.getEnPoligono).not.toHaveBeenCalled();
    });

    it('usa filtro ruta, dibuja polyline y no clusteriza', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'ruta',
            personaSeleccionada: 'd1',
        };

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(AvistamientoService.getPorDesaparecido).toHaveBeenCalledWith('d1');
            expect(screen.getByTestId('polyline')).toBeInTheDocument();
        });

        expect(screen.queryByTestId('cluster')).not.toBeInTheDocument();
    });

    it('filtro desaparecido consulta por persona seleccionada', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'desaparecido',
            personaSeleccionada: 'd1',
        };

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(AvistamientoService.getPorDesaparecido).toHaveBeenCalledWith('d1');
            expect(screen.getByTestId('cluster')).toBeInTheDocument();
        });
    });

    it('ruta usa fallback de fechas y coordenadas alternativas', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'ruta',
            personaSeleccionada: 'd1',
        };

        AvistamientoService.getPorDesaparecido.mockResolvedValueOnce([
            { id: 'r1', lat: -34.61, lon: -58.41, desaparecido_id: 'd1', creadoEn: '2026-01-02T12:00:00Z' },
            { id: 'r2', latitud: -34.6, lng: -58.4, desaparecido_id: 'd1' },
            { id: 'sin-coords', desaparecido_id: 'd1' },
        ]);

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(AvistamientoService.getPorDesaparecido).toHaveBeenCalledWith('d1');
            expect(screen.getByTestId('polyline')).toBeInTheDocument();
        });
    });

    it('si ruta tiene menos de 2 puntos, muestra mensaje explicativo', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'ruta',
            personaSeleccionada: 'd1',
        };
        AvistamientoService.getPorDesaparecido.mockResolvedValueOnce([
            { id: 'd1', latitud: -34.6, longitud: -58.4, desaparecidoId: 'd1', fechaHora: '2026-01-01T10:00:00Z' },
        ]);

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(screen.getByText(/al menos 2 avistamientos/i)).toBeInTheDocument();
        });
    });

    it('si modo reporte esta activo, click en mapa abre modal de reporte', async () => {
        reportModeState = {
            ...createDefaultReportState(),
            modoReporte: true,
        };

        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: 'map-click' }));

        await waitFor(() => {
            expect(reportModeState.openReportModalAt).toHaveBeenCalledWith({ lat: -34.7, lng: -58.5 });
        });
    });

    it('si selecciona centro de radio, click en mapa actualiza centro', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            isSelectingRadioCenter: true,
        };

        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: 'map-click' }));

        await waitFor(() => {
            expect(mapFiltersState.setRadioCenter).toHaveBeenCalledWith({ lat: -34.7, lng: -58.5 });
            expect(mapFiltersState.setIsSelectingRadioCenter).toHaveBeenCalledWith(false);
        });
    });

    it('si esta dibujando poligono, click en mapa agrega punto y ejecuta updater', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            isDrawingPolygon: true,
            polygonComplete: false,
            polygonPoints: [{ lat: -34.6, lng: -58.4 }],
        };

        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: 'map-click' }));

        await waitFor(() => {
            expect(mapFiltersState.setPolygonPoints).toHaveBeenCalled();
        });

        const updater = mapFiltersState.setPolygonPoints.mock.calls[0][0];
        expect(updater([{ lat: -34.5, lng: -58.3 }])).toEqual([
            { lat: -34.5, lng: -58.3 },
            { lat: -34.7, lng: -58.5 },
        ]);
    });

    it('right click completa el poligono cuando hay 3 o mas puntos', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            isDrawingPolygon: true,
            polygonPoints: [
                { lat: -34.6, lng: -58.4 },
                { lat: -34.7, lng: -58.5 },
                { lat: -34.8, lng: -58.6 },
            ],
        };

        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: 'map-right-click' }));

        await waitFor(() => {
            expect(mapFiltersState.setIsDrawingPolygon).toHaveBeenCalledWith(false);
            expect(mapFiltersState.setPolygonComplete).toHaveBeenCalledWith(true);
        });
    });

    it('lee persona desde query param y activa filtro desaparecido al montar', async () => {
        window.history.pushState({}, '', '/?persona=d1');

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(mapFiltersState.setPersonaSeleccionada).toHaveBeenCalledWith('d1');
            expect(mapFiltersState.activateFilter).toHaveBeenCalled();
        });
    });

    it('si report modal dispara callback, refresca avistamientos', async () => {
        reportModeState = {
            ...createDefaultReportState(),
            handleReportSuccess: vi.fn((cb) => cb()),
        };

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(AvistamientoService.getAvistamientosParaMapa).toHaveBeenCalledTimes(1);
        });

        fireEvent.click(screen.getByRole('button', { name: 'report-success' }));

        await waitFor(() => {
            expect(reportModeState.handleReportSuccess).toHaveBeenCalled();
            expect(AvistamientoService.getAvistamientosParaMapa.mock.calls.length).toBeGreaterThan(1);
        });
    });

    it('si report modal avisa exito, ejecuta callback de report mode', async () => {
        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: 'report-success' }));

        await waitFor(() => {
            expect(reportModeState.handleReportSuccess).toHaveBeenCalled();
        });
    });

    it('al cerrar report modal, actualiza estado showReportModal', async () => {
        reportModeState = {
            ...createDefaultReportState(),
            showReportModal: true,
        };

        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: 'report-close' }));

        await waitFor(() => {
            expect(reportModeState.setShowReportModal).toHaveBeenCalledWith(false);
        });
    });

    it('muestra banner y marcador cuando modo reporte esta activo con coordenadas seleccionadas', async () => {
        reportModeState = {
            ...createDefaultReportState(),
            modoReporte: true,
            selectedLatLng: { lat: -34.6, lng: -58.4 },
        };

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(screen.getByText(/Modo reporte activo/i)).toBeInTheDocument();
            expect(screen.getAllByTestId('marker').length).toBeGreaterThan(0);
        });
    });

    it('si no hay resultados, muestra estado vacio', async () => {
        AvistamientoService.getAvistamientosParaMapa.mockResolvedValueOnce([]);

        renderWithProviders(<MapaAvistamientos />);

        await waitFor(() => {
            expect(screen.getByText(/Sin avistamientos para este filtro/i)).toBeInTheDocument();
        });
    });

    it('botones de filtro delegan en activate/reset del hook', async () => {
        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: 'Filtro' }));
        fireEvent.click(screen.getByRole('button', { name: 'Reset' }));

        await waitFor(() => {
            expect(mapFiltersState.activateFilter).toHaveBeenCalled();
            expect(mapFiltersState.resetFilters).toHaveBeenCalled();
        });
    });
    it('refresh manual ejecuta rama default con filtro desconocido', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'desconocido',
        };

        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: /Actualizar avistamientos/i }));

        await waitFor(() => {
            expect(AvistamientoService.getAvistamientosParaMapa).toHaveBeenCalled();
        });
    });

    it('refresh manual con filtro radio sin centro cubre rama sin radioCenter', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'radio',
            radioCenter: null,
        };

        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: /Actualizar avistamientos/i }));

        await waitFor(() => {
            expect(screen.getByText(/No se encontraron avistamientos/i)).toBeInTheDocument();
        });
        expect(AvistamientoService.getEnRadio).not.toHaveBeenCalled();
    });

    it('refresh manual con filtro area sin bounds cubre rama sin areaBounds', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'area',
            areaBounds: null,
        };

        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: /Actualizar avistamientos/i }));

        await waitFor(() => {
            expect(screen.getByText(/No se encontraron avistamientos/i)).toBeInTheDocument();
        });
        expect(AvistamientoService.getEnArea).not.toHaveBeenCalled();
    });

    it('refresh manual con poligono incompleto cubre rama sin consulta de poligono', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'poligono',
            polygonComplete: false,
            polygonPoints: [{ lat: -34.6, lng: -58.4 }],
        };

        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: /Actualizar avistamientos/i }));

        await waitFor(() => {
            expect(screen.getByText(/No se encontraron avistamientos/i)).toBeInTheDocument();
        });
        expect(AvistamientoService.getEnPoligono).not.toHaveBeenCalled();
    });

    it('refresh manual con filtro por persona sin seleccion cubre rama sin persona', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            filtroActivo: 'desaparecido',
            personaSeleccionada: null,
        };

        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: /Actualizar avistamientos/i }));

        await waitFor(() => {
            expect(screen.getByText(/No se encontraron avistamientos/i)).toBeInTheDocument();
        });
        expect(AvistamientoService.getPorDesaparecido).not.toHaveBeenCalled();
    });

    it('si se hace click en mapa sin modos activos no dispara acciones', async () => {
        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: 'map-click' }));

        await waitFor(() => {
            expect(reportModeState.openReportModalAt).not.toHaveBeenCalled();
            expect(mapFiltersState.setRadioCenter).not.toHaveBeenCalled();
            expect(mapFiltersState.setPolygonPoints).not.toHaveBeenCalled();
        });
    });

    it('right click sin poligono completo no cambia estado', async () => {
        mapFiltersState = {
            ...createDefaultFilterState(),
            isDrawingPolygon: false,
            polygonPoints: [{ lat: -34.6, lng: -58.4 }],
        };

        renderWithProviders(<MapaAvistamientos />);

        fireEvent.click(screen.getByRole('button', { name: 'map-right-click' }));

        await waitFor(() => {
            expect(mapFiltersState.setIsDrawingPolygon).not.toHaveBeenCalled();
            expect(mapFiltersState.setPolygonComplete).not.toHaveBeenCalled();
        });
    });
});
