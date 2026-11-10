import { describe, it, expect, vi, beforeEach } from 'vitest';
import { AvistadorService } from '@/services/AvistadorService';
import { fetchWithAuth } from '@/utils/fetch';
import { parseProblem, getErrorMessage } from '@/utils/errors';
import { API_ENDPOINTS } from '@/config/constants';

vi.mock('@/utils/fetch', () => ({
    fetchWithAuth: vi.fn(),
}));

vi.mock('@/utils/errors', () => ({
    parseProblem: vi.fn(),
    getErrorMessage: vi.fn(),
}));

describe('AvistadorService', () => {

    beforeEach(() => {
        vi.clearAllMocks();
    });

    const setupErrorMock = () => {
        const problem = { status: 400, key: 'test.error', detail: 'test' };
        parseProblem.mockResolvedValue(problem);
        getErrorMessage.mockReturnValue('Expected Error Msg');
        return problem;
    };

    describe('crear', () => {
        it('should create avistador successfully', async () => {
            const mockResult = { id: 1 };
            const dto = { dni: '123' };
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => mockResult });

            const result = await AvistadorService.crear(dto);

            expect(fetchWithAuth).toHaveBeenCalledWith(API_ENDPOINTS.AVISTADORES, {
                method: "POST",
                body: JSON.stringify(dto)
            });
            expect(result).toEqual(mockResult);
        });

        it('should throw an error if creation fails', async () => {
            fetchWithAuth.mockResolvedValue({ ok: false });
            setupErrorMock();

            await expect(AvistadorService.crear({})).rejects.toThrow('Expected Error Msg');
        });
    });

    describe('obtenerTodos', () => {
        it('should fetch avistadores successfully', async () => {
            const mockResult = [{ id: 1 }];
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => mockResult });

            const result = await AvistadorService.obtenerTodos();

            expect(fetchWithAuth).toHaveBeenCalledWith(API_ENDPOINTS.AVISTADORES);
            expect(result).toEqual(mockResult);
        });

        it('should throw an error if fetching fails', async () => {
            fetchWithAuth.mockResolvedValue({ ok: false });
            setupErrorMock();

            await expect(AvistadorService.obtenerTodos()).rejects.toThrow('Expected Error Msg');
        });
    });

});
