import { describe, it, expect, vi, beforeEach } from 'vitest';
import { FileService } from '@/services/FileService';
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

describe('FileService', () => {

    beforeEach(() => {
        vi.clearAllMocks();
    });

    const setupErrorMock = () => {
        const problem = { status: 400, key: 'test.error', detail: 'test' };
        parseProblem.mockResolvedValue(problem);
        getErrorMessage.mockReturnValue('Expected Error Msg');
        return problem;
    };

    describe('upload', () => {
        it('should upload file successfully', async () => {
            const mockResult = { fileId: '123', downloadUrl: 'test' };
            const file = new File([''], 'test.png');
            fetchWithAuth.mockResolvedValue({ ok: true, json: async () => mockResult });

            const result = await FileService.upload(file, 'p1', 'DESAPARECIDO', 'PHOTO');

            expect(fetchWithAuth).toHaveBeenCalledWith(`${API_ENDPOINTS.FILES}/upload`, {
                method: 'POST',
                body: expect.any(FormData)
            });
            expect(result).toEqual(mockResult);
        });

        it('should throw an error if upload fails', async () => {
            const file = new File([''], 'test.png');
            fetchWithAuth.mockResolvedValue({ ok: false });
            setupErrorMock();

            await expect(FileService.upload(file, 'p1', 'DESAPARECIDO')).rejects.toThrow('Expected Error Msg');
        });
    });

    describe('getUrl', () => {
        it('should return null if no fileId is provided', () => {
            expect(FileService.getUrl(null)).toBeNull();
            expect(FileService.getUrl(undefined)).toBeNull();
        });

        it('should construct correct endpoint url given an id', () => {
            expect(FileService.getUrl('file-id-123')).toBe(`${API_ENDPOINTS.FILES}/file-id-123`);
        });
    });
});
