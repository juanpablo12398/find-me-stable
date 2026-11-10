import { describe, it, expect, vi, beforeEach } from 'vitest';
import { DesaparecidoService } from '@/services/DesaparecidoService';
import { fetchWithAuth } from '@/utils/fetch';
import { parseProblem, getErrorMessage } from '@/utils/errors';
import { API_ENDPOINTS } from '@/config/constants';
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

describe('DesaparecidoService', () => {

    beforeEach(() => {
        vi.clearAllMocks();
    });

    const setupErrorMock = () => {
        const problem = { status: 400, key: 'test.error', detail: 'test' };
        parseProblem.mockResolvedValue(problem);
        getErrorMessage.mockReturnValue('Expected Error Msg');
        return problem;
    };

    describe('obtenerTodos', () => {
        it('should call endpoint with search params properly handled', async () => {
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => [] });

            await DesaparecidoService.obtenerTodos({ search: 'juan', estado: 'ACTIVO' });

            expect(fetchWithAuth).toHaveBeenCalledWith(`${API_ENDPOINTS.DESAPARECIDOS}?search=juan&estado=ACTIVO`);
        });

        it('should return content list when backend returns paged wrapper', async () => {
            fetchWithAuth.mockResolvedValue({
                ok: true,
                json: async () => ({ content: [{ id: 1 }], totalElements: 1 })
            });

            const result = await DesaparecidoService.obtenerTodos({ page: 0, size: 10 });

            expect(result).toEqual([{ id: 1 }]);
        });

        it('should throw parsed error when fetch is not ok', async () => {
            fetchWithAuth.mockResolvedValue({ ok: false });
            setupErrorMock();

            await expect(DesaparecidoService.obtenerTodos()).rejects.toThrow('Expected Error Msg');
        });
    });

    describe('obtenerPaginado', () => {
        it('should normalize paged response payload', async () => {
            fetchWithAuth.mockResolvedValue({
                ok: true,
                json: async () => ({
                    content: [{ id: 1 }],
                    page: 2,
                    size: 5,
                    totalElements: 24,
                    totalPages: 5,
                    last: false
                })
            });

            const result = await DesaparecidoService.obtenerPaginado({ page: 2, size: 5 });

            expect(result).toEqual({
                content: [{ id: 1 }],
                page: 2,
                size: 5,
                totalElements: 24,
                totalPages: 5,
                last: false
            });
        });
    });

    describe('crear', () => {
        it('should create desaparecido correctly calling file upload', async () => {
            const mockResult = { id: 1 };
            const file = new File([''], 'test.png');
            const dto = { nombre: 'Juan' };

            FileService.upload.mockResolvedValue({ fileId: 'file456' });
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => mockResult });

            const result = await DesaparecidoService.crear(dto, file);

            expect(FileService.upload).toHaveBeenCalledWith(file, expect.any(String), 'DESAPARECIDO', 'PHOTO');
            expect(fetchWithAuth).toHaveBeenCalledWith(API_ENDPOINTS.DESAPARECIDOS, {
                method: "POST",
                body: JSON.stringify({ nombre: 'Juan', fotoFileId: 'file456' })
            });
            expect(result).toEqual(mockResult);
        });

        it('should throw error if creation fails', async () => {
            fetchWithAuth.mockResolvedValue({ ok: false });
            setupErrorMock();

            await expect(DesaparecidoService.crear({})).rejects.toThrow('Expected Error Msg');
        });
    });
});
