import { describe, expect, it, vi, beforeEach } from 'vitest';
import { fetchWithAuth } from '@/utils/fetch';

describe('fetchWithAuth', () => {
    beforeEach(() => {
        vi.restoreAllMocks();
        vi.spyOn(globalThis, 'fetch').mockResolvedValue({ ok: true });
    });

    it('agrega credentials include y content-type json por defecto', async () => {
        await fetchWithAuth('/api/test');

        expect(fetch).toHaveBeenCalledWith('/api/test', {
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });
    });

    it('no fuerza content-type cuando body es FormData y preserva headers', async () => {
        const form = new FormData();
        form.append('file', new Blob(['abc']), 'a.txt');

        await fetchWithAuth('/api/upload', {
            method: 'POST',
            body: form,
            headers: {
                Authorization: 'Bearer token'
            }
        });

        expect(fetch).toHaveBeenCalledWith('/api/upload', {
            credentials: 'include',
            method: 'POST',
            body: form,
            headers: {
                Authorization: 'Bearer token'
            }
        });
    });
});
