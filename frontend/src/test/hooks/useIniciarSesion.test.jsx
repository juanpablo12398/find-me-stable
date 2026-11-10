import { act, renderHook } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { useIniciarSesion } from '@/hooks/useIniciarSesion';
import { AuthService } from '@/services/AuthService';
import { toast } from 'sonner';

const navigateMock = vi.fn();

vi.mock('react-router-dom', async () => {
    const actual = await vi.importActual('react-router-dom');
    return {
        ...actual,
        useNavigate: () => navigateMock,
    };
});

vi.mock('@/utils', () => ({
    createPageUrl: (page) => `/${page}`,
}));

vi.mock('sonner', () => ({
    toast: {
        success: vi.fn(),
        error: vi.fn(),
    },
}));

vi.mock('@/services/AuthService', () => ({
    AuthService: {
        login: vi.fn(),
    },
}));

describe('useIniciarSesion', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('inicializa con formData vacío y no cargando', () => {
        const { result } = renderHook(() => useIniciarSesion());

        expect(result.current.formData).toEqual({ dni: '', email: '' });
        expect(result.current.isLoading).toBe(false);
        expect(result.current.fields).toHaveLength(2);
    });

    it('handleChange actualiza el campo correcto del formData', () => {
        const { result } = renderHook(() => useIniciarSesion());

        act(() => {
            result.current.handleChange({ target: { name: 'dni', value: '12345678' } });
        });

        expect(result.current.formData.dni).toBe('12345678');
        expect(result.current.formData.email).toBe('');
    });

    it('fields refleja los valores actuales del formData', () => {
        const { result } = renderHook(() => useIniciarSesion());

        act(() => {
            result.current.handleChange({ target: { name: 'email', value: 'test@mail.com' } });
        });

        const emailField = result.current.fields.find(f => f.name === 'email');
        expect(emailField.value).toBe('test@mail.com');
    });

    it('handleSubmit llama AuthService.login con los datos del form y navega al éxito', async () => {
        AuthService.login.mockResolvedValue({ id: '1' });
        const { result } = renderHook(() => useIniciarSesion());

        act(() => {
            result.current.handleChange({ target: { name: 'dni', value: '12345678' } });
            result.current.handleChange({ target: { name: 'email', value: 'test@mail.com' } });
        });

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(AuthService.login).toHaveBeenCalledWith('12345678', 'test@mail.com');
        expect(toast.success).toHaveBeenCalled();
        expect(navigateMock).toHaveBeenCalledWith('/MapaAvistamientos');
        expect(result.current.isLoading).toBe(false);
    });

    it('handleSubmit muestra toast.error y no navega cuando el login falla', async () => {
        AuthService.login.mockRejectedValue(new Error('Credenciales inválidas'));
        const { result } = renderHook(() => useIniciarSesion());

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(toast.error).toHaveBeenCalledWith('Credenciales inválidas');
        expect(navigateMock).not.toHaveBeenCalled();
        expect(result.current.isLoading).toBe(false);
    });

    it('isLoading se activa durante el submit y vuelve a false al finalizar', async () => {
        let resolve;
        AuthService.login.mockImplementation(() => new Promise(r => { resolve = r; }));

        const { result } = renderHook(() => useIniciarSesion());

        act(() => {
            result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(result.current.isLoading).toBe(true);

        await act(async () => {
            resolve({ id: '1' });
        });

        expect(result.current.isLoading).toBe(false);
    });
});
