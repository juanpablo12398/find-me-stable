import { describe, it, expect, vi, beforeEach } from 'vitest';
import { AvistamientoService } from '@/services/AvistamientoService';
import { fetchWithAuth } from '@/utils/fetch';
import { parseProblem, getErrorMessage } from '@/utils/errors';
import { API_ENDPOINTS, ERROR_MAPS } from '@/config/constants';
import { FileService } from '@/services/FileService';

vi.mock('@/utils/fetch', () => ({
    fetchWithAuth: vi.fn(),
}));

vi.mock('@/utils/errors', () => ({
    parseProblem: vi.fn(),
    getErrorMessage: vi.fn(),
}));

vi.mock('@/services/FileService', () => ({
    FileService: {
        upload: vi.fn()
    }
}));

describe('AvistamientoService', () => {
    const randomUUIDMock = vi.fn(() => 'tmp-uuid');

    beforeEach(() => {
        vi.clearAllMocks();
        vi.stubGlobal('crypto', { randomUUID: randomUUIDMock });
    });

    const setupErrorMock = () => {
        const problem = { status: 400, key: 'test.error', detail: 'test' };
        parseProblem.mockResolvedValue(problem);
        getErrorMessage.mockReturnValue('Expected Error Msg');
        return problem;
    };

    const expectMappedError = async (fn) => {
        fetchWithAuth.mockResolvedValue({ ok: false });
        setupErrorMock();
        await expect(fn()).rejects.toThrow('Expected Error Msg');
        expect(parseProblem).toHaveBeenCalled();
        expect(getErrorMessage).toHaveBeenCalledWith(ERROR_MAPS.AVISTAMIENTO, 400, 'test.error', 'test');
    };

    describe('queries', () => {
        it('getAvistamientosParaMapa should return data when fetch is ok', async () => {
            const mockResult = [{ id: 1 }];
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => mockResult });

            const result = await AvistamientoService.getAvistamientosParaMapa();

            expect(result).toEqual(mockResult);
            expect(fetchWithAuth).toHaveBeenCalledWith(`${API_ENDPOINTS.AVISTAMIENTOS}/mapa`);
        });

        it('getEnRadio should call endpoint with params', async () => {
            const mockResult = [{ id: 2 }];
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => mockResult });

            const result = await AvistamientoService.getEnRadio(-34.6, -58.4, 10);

            expect(result).toEqual(mockResult);
            const [url] = fetchWithAuth.mock.calls[0];
            expect(url).toContain(`${API_ENDPOINTS.AVISTAMIENTOS}/radio?`);
            expect(url).toContain('lat=-34.6');
            expect(url).toContain('lng=-58.4');
            expect(url).toContain('radioKm=10');
        });

        it('getEnArea should call endpoint with params', async () => {
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => [{ id: 3 }] });

            await AvistamientoService.getEnArea(-35, -34, -59, -58);

            const [url] = fetchWithAuth.mock.calls[0];
            expect(url).toContain(`${API_ENDPOINTS.AVISTAMIENTOS}/mapa/area?`);
            expect(url).toContain('latMin=-35');
            expect(url).toContain('latMax=-34');
            expect(url).toContain('lngMin=-59');
            expect(url).toContain('lngMax=-58');
        });

        it('contarEnArea should call endpoint with params', async () => {
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => ({ total: 8 }) });

            await AvistamientoService.contarEnArea(-35, -34, -59, -58);

            const [url] = fetchWithAuth.mock.calls[0];
            expect(url).toContain(`${API_ENDPOINTS.AVISTAMIENTOS}/area/count?`);
            expect(url).toContain('latMin=-35');
            expect(url).toContain('latMax=-34');
            expect(url).toContain('lngMin=-59');
            expect(url).toContain('lngMax=-58');
        });

        it('getEnPoligono should post encoded WKT', async () => {
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => [{ id: 4 }] });
            const wkt = 'POLYGON((0 0,1 1,0 0))';

            await AvistamientoService.getEnPoligono(wkt);

            expect(fetchWithAuth).toHaveBeenCalledWith(
                `${API_ENDPOINTS.AVISTAMIENTOS}/poligono?wkt=${encodeURIComponent(wkt)}`,
                { method: 'POST' }
            );
        });

        it('getPorDesaparecido should call endpoint', async () => {
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => [{ id: 5 }] });

            await AvistamientoService.getPorDesaparecido('d1');

            expect(fetchWithAuth).toHaveBeenCalledWith(`${API_ENDPOINTS.AVISTAMIENTOS}/desaparecido/d1`);
        });

        it('getPorDesaparecidoEnRadio should call endpoint with params', async () => {
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => [{ id: 6 }] });

            await AvistamientoService.getPorDesaparecidoEnRadio('d1', -34.6, -58.4, 4);

            const [url] = fetchWithAuth.mock.calls[0];
            expect(url).toContain(`${API_ENDPOINTS.AVISTAMIENTOS}/desaparecido/d1/radio?`);
            expect(url).toContain('lat=-34.6');
            expect(url).toContain('lng=-58.4');
            expect(url).toContain('radioKm=4');
        });

        it('getMasCercanos should use default limite=10', async () => {
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => [{ id: 7 }] });

            await AvistamientoService.getMasCercanos(-34.6, -58.4);

            const [url] = fetchWithAuth.mock.calls[0];
            expect(url).toContain(`${API_ENDPOINTS.AVISTAMIENTOS}/cercanos?`);
            expect(url).toContain('limite=10');
        });

        it('obtenerTodos should call base endpoint', async () => {
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => [{ id: 8 }] });

            await AvistamientoService.obtenerTodos();

            expect(fetchWithAuth).toHaveBeenCalledWith(API_ENDPOINTS.AVISTAMIENTOS);
        });

        it('every query method should throw mapped error', async () => {
            const calls = [
                () => AvistamientoService.getAvistamientosParaMapa(),
                () => AvistamientoService.getEnRadio(-34.6, -58.4, 10),
                () => AvistamientoService.getEnArea(-35, -34, -59, -58),
                () => AvistamientoService.contarEnArea(-35, -34, -59, -58),
                () => AvistamientoService.getEnPoligono('POLYGON((0 0,1 1,0 0))'),
                () => AvistamientoService.getPorDesaparecido('d1'),
                () => AvistamientoService.getPorDesaparecidoEnRadio('d1', -34.6, -58.4, 10),
                () => AvistamientoService.getMasCercanos(-34.6, -58.4, 10),
                () => AvistamientoService.obtenerTodos(),
            ];

            for (const fn of calls) {
                await expectMappedError(fn);
            }
        });
    });

    describe('crear', () => {
        it('should create avistamiento calling upload if fotoFile exists', async () => {
            const mockResult = { id: 1 };
            const file = new File(['abc'], 'test.png', { type: 'image/png' });
            const dto = { avistadorId: 'a1', latitud: 1 };

            FileService.upload.mockResolvedValue({ fileId: 'file123' });
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => mockResult });

            const result = await AvistamientoService.crear(dto, file);

            expect(FileService.upload).toHaveBeenCalledWith(file, 'tmp-uuid', 'AVISTAMIENTO', 'PHOTO');
            expect(fetchWithAuth).toHaveBeenCalledWith(API_ENDPOINTS.AVISTAMIENTOS, {
                method: 'POST',
                body: JSON.stringify({
                    avistadorId: 'a1',
                    desaparecidoId: undefined,
                    latitud: 1,
                    longitud: undefined,
                    descripcion: undefined,
                    fotoFileId: 'file123',
                    publico: undefined,
                    fechaAvistamiento: null
                })
            });
            expect(result).toEqual(mockResult);
        });

        it('should create avistamiento without upload if no fotoFile is provided', async () => {
            const mockResult = { id: 2 };
            const dto = { avistadorId: 'a1', fechaAvistamiento: '2026-01-01T10:00' };

            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => mockResult });

            const result = await AvistamientoService.crear(dto);

            expect(FileService.upload).not.toHaveBeenCalled();
            expect(fetchWithAuth).toHaveBeenCalledWith(API_ENDPOINTS.AVISTAMIENTOS, expect.objectContaining({
                method: 'POST',
            }));
            expect(result).toEqual(mockResult);
        });

        it('should throw mapped error if creation fails', async () => {
            fetchWithAuth.mockResolvedValue({ ok: false });
            setupErrorMock();

            await expect(AvistamientoService.crear({})).rejects.toThrow('Expected Error Msg');
            expect(parseProblem).toHaveBeenCalled();
        });
    });
});
