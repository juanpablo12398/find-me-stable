import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { AuthService } from '@/services/AuthService';
import { fetchWithAuth } from '@/utils/fetch';
import { parseProblem, getErrorMessage } from '@/utils/errors';
import { API_ENDPOINTS, ERROR_MAPS } from '@/config/constants';

vi.mock('@/utils/fetch', () => ({
    fetchWithAuth: vi.fn(),
}));

vi.mock('@/utils/errors', () => ({
    parseProblem: vi.fn(),
    getErrorMessage: vi.fn(),
}));

describe('AuthService', () => {
    let logSpy;
    let errorSpy;

    beforeEach(() => {
        vi.clearAllMocks();
        logSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
        errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
    });

    afterEach(async () => {
        try {
            await AuthService.logout();
        } catch (e) {
            // no-op in cleanup
        }
        logSpy.mockRestore();
        errorSpy.mockRestore();
    });

    it('should return null for currentUser initially', () => {
        expect(AuthService.getCurrentUser()).toBeNull();
        expect(AuthService.isAuthenticated()).toBe(false);
    });

    describe('checkAuth', () => {
        it('should set currentUser and return true if response is ok', async () => {
            const mockUser = { id: 123, email: 'test@test.com' };
            fetchWithAuth.mockResolvedValue({
                ok: true,
                json: async () => mockUser,
            });

            const result = await AuthService.checkAuth();

            expect(result).toBe(true);
            expect(AuthService.getCurrentUser()).toEqual({ id: '123', email: 'test@test.com' });
            expect(fetchWithAuth).toHaveBeenCalledWith(`${API_ENDPOINTS.AUTH}/me`);
        });

        it('should return false and set currentUser to null if response is not ok', async () => {
            fetchWithAuth.mockResolvedValue({
                ok: false,
            });

            const result = await AuthService.checkAuth();

            expect(result).toBe(false);
            expect(AuthService.getCurrentUser()).toBeNull();
        });

        it('should return false if fetch fails', async () => {
            fetchWithAuth.mockRejectedValue(new Error('Network error'));

            const result = await AuthService.checkAuth();

            expect(result).toBe(false);
            expect(AuthService.getCurrentUser()).toBeNull();
        });
    });

    describe('login', () => {
        it('should login successfully and return user', async () => {
            const mockUser = { id: 123, email: 'test@test.com' };
            fetchWithAuth.mockResolvedValue({
                ok: true,
                json: async () => mockUser,
            });

            const result = await AuthService.login('12345678', 'test@test.com');

            expect(result).toEqual({ id: '123', email: 'test@test.com' });
            expect(AuthService.getCurrentUser()).toEqual({ id: '123', email: 'test@test.com' });
            expect(AuthService.isAuthenticated()).toBe(true);
            expect(fetchWithAuth).toHaveBeenCalledWith(`${API_ENDPOINTS.AUTH}/login`, {
                method: "POST",
                body: JSON.stringify({ dni: '12345678', email: 'test@test.com' })
            });
        });

        it('should throw an error if login fails', async () => {
            fetchWithAuth.mockResolvedValue({
                ok: false,
                status: 401,
            });
            const problem = { status: 401, key: 'mock.key', detail: 'Mock detail' };
            parseProblem.mockResolvedValue(problem);
            getErrorMessage.mockReturnValue('Mock error message');

            await expect(AuthService.login('12345678', 'wrong@test.com')).rejects.toThrow('Mock error message');

            expect(parseProblem).toHaveBeenCalled();
            expect(getErrorMessage).toHaveBeenCalledWith(
                ERROR_MAPS.AUTH,
                problem.status,
                problem.key,
                problem.detail
            );
        });
    });

    describe('logout', () => {
        it('should call logout endpoint and clear currentUser', async () => {
            fetchWithAuth.mockResolvedValue({ ok: true });

            await AuthService.logout();

            expect(AuthService.getCurrentUser()).toBeNull();
            expect(AuthService.isAuthenticated()).toBe(false);
            expect(fetchWithAuth).toHaveBeenCalledWith(`${API_ENDPOINTS.AUTH}/logout`, {
                method: "POST"
            });
        });
    });

    describe('subscriptions', () => {
        it('should notify listeners on auth state change', async () => {
            const listener = vi.fn();
            const unsubscribe = AuthService.subscribe(listener);

            fetchWithAuth.mockResolvedValue({
                ok: true,
                json: async () => ({ id: 123 }),
            });

            await AuthService.checkAuth();

            expect(listener).toHaveBeenCalledWith({ id: '123' });

            unsubscribe();
        });
    });

});
