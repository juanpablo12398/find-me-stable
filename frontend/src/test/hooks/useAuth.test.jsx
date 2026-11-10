import React from 'react';
import { act, renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { AuthProvider, useAuth } from '@/hooks/useAuth';
import { AuthService } from '@/services/AuthService';

describe('useAuth', () => {
    let subscribeCallback;
    let unsubscribeSpy;

    beforeEach(() => {
        vi.restoreAllMocks();
        subscribeCallback = null;
        unsubscribeSpy = vi.fn();

        vi.spyOn(AuthService, 'getCurrentUser').mockReturnValue(null);
        vi.spyOn(AuthService, 'setQueryClient').mockImplementation(() => {});
        vi.spyOn(AuthService, 'subscribe').mockImplementation((listener) => {
            subscribeCallback = listener;
            return unsubscribeSpy;
        });
        vi.spyOn(AuthService, 'checkAuth').mockResolvedValue(false);
        vi.spyOn(AuthService, 'login').mockResolvedValue({ id: '1' });
        vi.spyOn(AuthService, 'logout').mockResolvedValue();
    });

    it('funciona fuera de AuthProvider usando fallback local', async () => {
        const { result, unmount } = renderHook(() => useAuth());

        await waitFor(() => expect(result.current.isLoading).toBe(false));
        expect(result.current.isAuthenticated).toBe(false);
        expect(result.current.user).toBeNull();

        act(() => {
            subscribeCallback({ id: '99', email: 'user@test.com' });
        });

        expect(result.current.user).toEqual({ id: '99', email: 'user@test.com' });
        expect(result.current.isAuthenticated).toBe(true);

        await act(async () => {
            await result.current.login('12345678', 'user@test.com');
        });
        expect(AuthService.login).toHaveBeenCalledWith('12345678', 'user@test.com');

        await act(async () => {
            await result.current.logout();
        });
        expect(AuthService.logout).toHaveBeenCalledTimes(1);

        unmount();
        expect(unsubscribeSpy).toHaveBeenCalledTimes(1);
    });

    it('expone contexto cuando se usa AuthProvider y enlaza QueryClient', async () => {
        const queryClient = new QueryClient();
        const wrapper = ({ children }) => (
            <QueryClientProvider client={queryClient}>
                <AuthProvider>{children}</AuthProvider>
            </QueryClientProvider>
        );

        const { result, unmount } = renderHook(() => useAuth(), { wrapper });

        await waitFor(() => expect(result.current.isLoading).toBe(false));
        expect(AuthService.setQueryClient).toHaveBeenCalledWith(queryClient);
        expect(AuthService.checkAuth).toHaveBeenCalledTimes(1);

        act(() => {
            subscribeCallback({ id: '77' });
        });

        expect(result.current.user).toEqual({ id: '77' });
        expect(result.current.isAuthenticated).toBe(true);

        unmount();
        expect(unsubscribeSpy).toHaveBeenCalled();
    });
});
