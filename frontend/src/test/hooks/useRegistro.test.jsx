import { act, renderHook } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { useRegistro } from '@/hooks/useRegistro';
import { AvistadorService } from '@/services/AvistadorService';
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

vi.mock('@/services/AvistadorService', () => ({
    AvistadorService: {
        crear: vi.fn(),
    },
}));

vi.mock('@/services/AuthService', () => ({
    AuthService: {
        checkAuth: vi.fn(),
    },
}));

describe('useRegistro', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('inicializa con formData vacío y no cargando', () => {
        const { result } = renderHook(() => useRegistro());

        expect(result.current.formData).toEqual({
            nombre: '', apellido: '', dni: '', edad: '',
            direccion: '', email: '', telefono: ''
        });
        expect(result.current.isLoading).toBe(false);
        expect(result.current.fields).toHaveLength(4); // 4 grupos de campos
    });

    it('handleChange actualiza el campo correcto del formData', () => {
        const { result } = renderHook(() => useRegistro());

        act(() => {
            result.current.handleChange({ target: { name: 'nombre', value: 'Ana' } });
            result.current.handleChange({ target: { name: 'email', value: 'ana@mail.com' } });
        });

        expect(result.current.formData.nombre).toBe('Ana');
        expect(result.current.formData.email).toBe('ana@mail.com');
        expect(result.current.formData.apellido).toBe('');
    });

    it('handleSubmit llama AvistadorService.crear con body correcto (parsea edad, null para opcionales vacíos)', async () => {
        AvistadorService.crear.mockResolvedValue({});
        AuthService.checkAuth.mockResolvedValue(true);

        const { result } = renderHook(() => useRegistro());

        act(() => {
            result.current.handleChange({ target: { name: 'nombre', value: 'Ana' } });
            result.current.handleChange({ target: { name: 'apellido', value: 'Gomez' } });
            result.current.handleChange({ target: { name: 'dni', value: '12345678' } });
            result.current.handleChange({ target: { name: 'edad', value: '30' } });
            result.current.handleChange({ target: { name: 'direccion', value: 'Calle 123' } });
            result.current.handleChange({ target: { name: 'email', value: 'ana@mail.com' } });
            result.current.handleChange({ target: { name: 'telefono', value: '11999' } });
        });

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(AvistadorService.crear).toHaveBeenCalledWith({
            dni: '12345678',
            nombre: 'Ana',
            apellido: 'Gomez',
            edad: 30,
            direccion: 'Calle 123',
            email: 'ana@mail.com',
            telefono: '11999',
        });
    });

    it('handleSubmit envía null para edad y campos opcionales vacíos', async () => {
        AvistadorService.crear.mockResolvedValue({});
        AuthService.checkAuth.mockResolvedValue(true);

        const { result } = renderHook(() => useRegistro());

        act(() => {
            result.current.handleChange({ target: { name: 'nombre', value: 'Ana' } });
            result.current.handleChange({ target: { name: 'apellido', value: 'Gomez' } });
            result.current.handleChange({ target: { name: 'dni', value: '12345678' } });
            result.current.handleChange({ target: { name: 'email', value: 'ana@mail.com' } });
        });

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(AvistadorService.crear).toHaveBeenCalledWith(expect.objectContaining({
            edad: null,
            direccion: null,
            telefono: null,
        }));
    });

    it('handleSubmit llama AuthService.checkAuth tras crear exitosamente', async () => {
        AvistadorService.crear.mockResolvedValue({});
        AuthService.checkAuth.mockResolvedValue(true);

        const { result } = renderHook(() => useRegistro());

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(AuthService.checkAuth).toHaveBeenCalledTimes(1);
    });

    it('handleSubmit navega a MapaAvistamientos en éxito', async () => {
        AvistadorService.crear.mockResolvedValue({});
        AuthService.checkAuth.mockResolvedValue(true);

        const { result } = renderHook(() => useRegistro());

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(navigateMock).toHaveBeenCalledWith('/MapaAvistamientos');
        expect(toast.success).toHaveBeenCalled();
    });

    it('handleSubmit muestra toast.error y no navega cuando falla el alta', async () => {
        AvistadorService.crear.mockRejectedValue(new Error('DNI ya registrado'));

        const { result } = renderHook(() => useRegistro());

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(toast.error).toHaveBeenCalledWith('DNI ya registrado');
        expect(navigateMock).not.toHaveBeenCalled();
        expect(result.current.isLoading).toBe(false);
    });
});
