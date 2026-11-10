import { describe, it, expect } from 'vitest';
import { parseProblem, getErrorMessage } from '@/utils/errors';

describe('Error Utils', () => {
    describe('parseProblem', () => {
        it('deberia decodificar json parseable con keys validas', async () => {
            const mockResponse = {
                status: 400,
                text: () => Promise.resolve(JSON.stringify({ detail: 'Error bad request', key: 'TEST_KEY' }))
            };

            const res = await parseProblem(mockResponse);
            expect(res.status).toBe(400);
            expect(res.key).toBe('TEST_KEY');
            expect(res.detail).toBe('Error bad request');
        });

        it('deberia resolver con status basico si el json no es parseable', async () => {
            const mockResponse = {
                status: 500,
                text: () => Promise.resolve('Internal Server Error HTML response')
            };

            const res = await parseProblem(mockResponse);
            expect(res.status).toBe(500);
            expect(res.key).toBeNull();
            expect(res.detail).toBe('Internal Server Error HTML response');
        });
    });

    describe('getErrorMessage', () => {
        const errorMap = {
            404: {
                USER_NOT_FOUND: 'El usuario no existe',
                default: 'Recurso no encontrado'
            },
            500: {
                default: 'Error interno en el servidor'
            }
        };

        it('deberia devolver mensaje por status exacto y key', () => {
            expect(getErrorMessage(errorMap, 404, 'USER_NOT_FOUND', 'detail original')).toBe('El usuario no existe');
        });

        it('deberia devolver default si no encuentra key', () => {
            expect(getErrorMessage(errorMap, 404, 'OTHER_KEY', 'detail original')).toBe('Recurso no encontrado');
        });

        it('deberia usar detail si no existe el mapeo especifico', () => {
            expect(getErrorMessage(errorMap, 400, 'KEY', 'Custom Error')).toBe('Custom Error');
        });

        it('deberia devolver fallback con status por defecto si no hay detail', () => {
            expect(getErrorMessage(errorMap, 400, null, null)).toBe('Error HTTP 400');
        });
    });
});
