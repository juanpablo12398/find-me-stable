import React from 'react';
import { act, renderHook, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useRegistrarDesaparecido } from '@/hooks/useRegistrarDesaparecido';
import { DesaparecidoService } from '@/services/DesaparecidoService';
import { toast } from 'sonner';

const navigateMock = vi.fn();
let authState = { isAuthenticated: true };

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

vi.mock('@/hooks/useAuth', () => ({
    useAuth: () => authState,
}));

vi.mock('@/services/DesaparecidoService', () => ({
    DesaparecidoService: {
        crear: vi.fn(),
    },
}));

function renderHookWithQuery(hook) {
    const queryClient = new QueryClient();
    const invalidateSpy = vi.spyOn(queryClient, 'invalidateQueries');
    const wrapper = ({ children }) => (
        <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    );
    const result = renderHook(hook, { wrapper });
    return { ...result, invalidateSpy };
}

describe('useRegistrarDesaparecido', () => {
    beforeEach(() => {
        vi.clearAllMocks();
        authState = { isAuthenticated: true };
        vi.stubGlobal('FileReader', class {
            readAsDataURL() {
                if (this.onload) {
                    this.onload({ target: { result: 'data:image/png;base64,abc' } });
                }
            }
        });
    });

    it('inicializa con formData vacío, sin foto y no cargando', () => {
        const { result } = renderHookWithQuery(() => useRegistrarDesaparecido());

        expect(result.current.formData.nombre).toBe('');
        expect(result.current.formData.dni).toBe('');
        expect(result.current.fotoFile).toBeNull();
        expect(result.current.fotoPreview).toBeNull();
        expect(result.current.isLoading).toBe(false);
        expect(result.current.isAuthenticated).toBe(true);
    });

    it('redirige a IniciarSesion si no está autenticado', async () => {
        authState = { isAuthenticated: false };
        renderHookWithQuery(() => useRegistrarDesaparecido());

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('iniciar sesión'));
            expect(navigateMock).toHaveBeenCalledWith('/IniciarSesion');
        });
    });

    it('handleChange actualiza el campo correcto del formData', () => {
        const { result } = renderHookWithQuery(() => useRegistrarDesaparecido());

        act(() => {
            result.current.handleChange({ target: { name: 'nombre', value: 'Juan' } });
            result.current.handleChange({ target: { name: 'descripcion', value: 'descripcion larga' } });
        });

        expect(result.current.formData.nombre).toBe('Juan');
        expect(result.current.formData.descripcion).toBe('descripcion larga');
    });

    it('handleFotoChange rechaza archivos que no son imagen', () => {
        const { result } = renderHookWithQuery(() => useRegistrarDesaparecido());

        act(() => {
            result.current.handleFotoChange({
                target: { files: [new File(['txt'], 'doc.txt', { type: 'text/plain' })] }
            });
        });

        expect(toast.error).toHaveBeenCalledWith('El archivo debe ser una imagen');
        expect(result.current.fotoFile).toBeNull();
    });

    it('handleFotoChange rechaza imágenes que superan 5MB', () => {
        const { result } = renderHookWithQuery(() => useRegistrarDesaparecido());

        const bigFile = new File([new Uint8Array(6 * 1024 * 1024)], 'big.png', { type: 'image/png' });
        act(() => {
            result.current.handleFotoChange({ target: { files: [bigFile] } });
        });

        expect(toast.error).toHaveBeenCalledWith('La imagen no puede superar los 5MB');
        expect(result.current.fotoFile).toBeNull();
    });

    it('handleFotoChange acepta imagen válida y genera preview', async () => {
        const { result } = renderHookWithQuery(() => useRegistrarDesaparecido());

        const validFile = new File(['img'], 'persona.png', { type: 'image/png' });
        act(() => {
            result.current.handleFotoChange({ target: { files: [validFile] } });
        });

        await waitFor(() => {
            expect(result.current.fotoFile).toBe(validFile);
            expect(result.current.fotoPreview).toBe('data:image/png;base64,abc');
        });
    });

    it('handleRemoveFoto limpia la foto y el preview', async () => {
        const { result } = renderHookWithQuery(() => useRegistrarDesaparecido());

        const validFile = new File(['img'], 'persona.png', { type: 'image/png' });
        act(() => {
            result.current.handleFotoChange({ target: { files: [validFile] } });
        });

        await waitFor(() => {
            expect(result.current.fotoFile).not.toBeNull();
        });

        act(() => {
            result.current.handleRemoveFoto();
        });

        expect(result.current.fotoFile).toBeNull();
        expect(result.current.fotoPreview).toBeNull();
    });

    it('handleSubmit construye body con trim y conversión de edad', async () => {
        DesaparecidoService.crear.mockResolvedValue({});
        const { result } = renderHookWithQuery(() => useRegistrarDesaparecido());

        act(() => {
            result.current.handleChange({ target: { name: 'nombre', value: '  Ana  ' } });
            result.current.handleChange({ target: { name: 'apellido', value: '  Gomez  ' } });
            result.current.handleChange({ target: { name: 'edad', value: '25' } });
            result.current.handleChange({ target: { name: 'dni', value: '  12345678  ' } });
            result.current.handleChange({ target: { name: 'descripcion', value: '  Descripcion del caso  ' } });
            result.current.handleChange({ target: { name: 'ultimaUbicacion', value: '  Palermo  ' } });
        });

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(DesaparecidoService.crear).toHaveBeenCalledWith(
            expect.objectContaining({
                nombre: 'Ana',
                apellido: 'Gomez',
                edad: 25,
                dni: '12345678',
                descripcion: 'Descripcion del caso',
                ultimaUbicacion: 'Palermo',
            }),
            null
        );
    });

    it('handleSubmit envía null para campos opcionales vacíos y fechaDesaparicion vacía', async () => {
        DesaparecidoService.crear.mockResolvedValue({});
        const { result } = renderHookWithQuery(() => useRegistrarDesaparecido());

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(DesaparecidoService.crear).toHaveBeenCalledWith(
            expect.objectContaining({
                fechaDesaparicion: null,
                contactoFamiliar: null,
                emailContactoFamiliar: null,
                ultimaUbicacion: null,
            }),
            null
        );
    });

    it('handleSubmit invalida el cache de React Query en éxito', async () => {
        DesaparecidoService.crear.mockResolvedValue({});
        const { result, invalidateSpy } = renderHookWithQuery(() => useRegistrarDesaparecido());

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(invalidateSpy).toHaveBeenCalled();
    });

    it('handleSubmit navega a ListaDesaparecidos y muestra toast de éxito', async () => {
        DesaparecidoService.crear.mockResolvedValue({});
        const { result } = renderHookWithQuery(() => useRegistrarDesaparecido());

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(toast.success).toHaveBeenCalled();
        expect(navigateMock).toHaveBeenCalledWith('/ListaDesaparecidos');
        expect(result.current.isLoading).toBe(false);
    });

    it('handleSubmit muestra toast.error y no navega cuando falla el servicio', async () => {
        DesaparecidoService.crear.mockRejectedValue(new Error('Error del servidor'));
        const { result } = renderHookWithQuery(() => useRegistrarDesaparecido());

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(toast.error).toHaveBeenCalledWith('Error del servidor');
        expect(navigateMock).not.toHaveBeenCalled();
        expect(result.current.isLoading).toBe(false);
    });

    it('handleSubmit pasa fotoFile al servicio cuando hay foto seleccionada', async () => {
        DesaparecidoService.crear.mockResolvedValue({});
        const { result } = renderHookWithQuery(() => useRegistrarDesaparecido());

        const foto = new File(['img'], 'persona.png', { type: 'image/png' });
        act(() => {
            result.current.handleFotoChange({ target: { files: [foto] } });
        });

        await waitFor(() => expect(result.current.fotoFile).toBe(foto));

        await act(async () => {
            await result.current.handleSubmit({ preventDefault: vi.fn() });
        });

        expect(DesaparecidoService.crear).toHaveBeenCalledWith(
            expect.any(Object),
            foto
        );
    });
});
