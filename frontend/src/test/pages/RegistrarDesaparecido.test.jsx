import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import RegistrarDesaparecido from '@/pages/RegistrarDesaparecido';
import { DesaparecidoService } from '@/services/DesaparecidoService';
import { toast } from 'sonner';

const navigateMock = vi.fn();
let authState = { isAuthenticated: true };

vi.mock('framer-motion', () => ({
    motion: {
        div: ({ children, ...props }) => <div {...props}>{children}</div>,
    },
}));

vi.mock('react-router-dom', async () => {
    const actual = await vi.importActual('react-router-dom');
    return {
        ...actual,
        useNavigate: () => navigateMock,
    };
});

vi.mock('@/hooks/useAuth', () => ({
    useAuth: () => authState,
}));

vi.mock('@/utils', () => ({
    createPageUrl: (page) => `/${page}`,
}));

vi.mock('sonner', () => ({
    toast: {
        error: vi.fn(),
        success: vi.fn(),
    },
}));

vi.mock('@/services/DesaparecidoService', () => ({
    DesaparecidoService: {
        crear: vi.fn(),
    },
}));

function renderWithQuery(ui) {
    const queryClient = new QueryClient();
    const invalidateSpy = vi.spyOn(queryClient, 'invalidateQueries');
    const view = render(<QueryClientProvider client={queryClient}>{ui}</QueryClientProvider>);
    return { ...view, invalidateSpy };
}

describe('RegistrarDesaparecido', () => {
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

    it('redirige si no hay sesion', async () => {
        authState = { isAuthenticated: false };
        renderWithQuery(<RegistrarDesaparecido />);

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalled();
            expect(navigateMock).toHaveBeenCalledWith('/IniciarSesion');
        });
        expect(screen.queryByText('Registrar Persona Extraviada')).not.toBeInTheDocument();
    });

    it('envia alta correctamente e invalida cache', async () => {
        DesaparecidoService.crear.mockResolvedValue({});
        const { invalidateSpy, container } = renderWithQuery(<RegistrarDesaparecido />);

        fireEvent.change(container.querySelector('input[name="nombre"]'), { target: { name: 'nombre', value: ' Ana ' } });
        fireEvent.change(container.querySelector('input[name="apellido"]'), { target: { name: 'apellido', value: ' Gomez ' } });
        fireEvent.change(container.querySelector('input[name="edad"]'), { target: { name: 'edad', value: '25' } });
        fireEvent.change(container.querySelector('input[name="dni"]'), { target: { name: 'dni', value: ' 12345678 ' } });
        fireEvent.change(container.querySelector('textarea[name="descripcion"]'), { target: { name: 'descripcion', value: ' Descripcion larga de prueba ' } });
        fireEvent.change(container.querySelector('input[name="contactoFamiliar"]'), { target: { name: 'contactoFamiliar', value: ' 11333 ' } });
        fireEvent.change(container.querySelector('input[name="ultimaUbicacion"]'), { target: { name: 'ultimaUbicacion', value: ' Palermo ' } });

        fireEvent.click(screen.getByRole('button', { name: /Registrar Persona/i }));

        await waitFor(() => {
            expect(DesaparecidoService.crear).toHaveBeenCalledWith(
                {
                    nombre: 'Ana',
                    apellido: 'Gomez',
                    edad: 25,
                    dni: '12345678',
                    descripcion: 'Descripcion larga de prueba',
                    fechaDesaparicion: null,
                    contactoFamiliar: '11333',
                    emailContactoFamiliar: null,
                    ultimaUbicacion: 'Palermo',
                },
                null
            );
            expect(invalidateSpy).toHaveBeenCalled();
            expect(toast.success).toHaveBeenCalled();
            expect(navigateMock).toHaveBeenCalledWith('/ListaDesaparecidos');
        });
    });

    it('valida tipo y tama�o de foto', async () => {
        const { container } = renderWithQuery(<RegistrarDesaparecido />);
        const fileInput = container.querySelector('input[type="file"]');

        const invalidFile = new File(['x'], 'nota.txt', { type: 'text/plain' });
        fireEvent.change(fileInput, { target: { files: [invalidFile] } });

        const bigFile = new File([new Uint8Array(6 * 1024 * 1024)], 'big.png', { type: 'image/png' });
        fireEvent.change(fileInput, { target: { files: [bigFile] } });

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('El archivo debe ser una imagen');
            expect(toast.error).toHaveBeenCalledWith('La imagen no puede superar los 5MB');
        });
    });

    it('muestra preview de foto y permite remover', async () => {
        const { container } = renderWithQuery(<RegistrarDesaparecido />);
        const fileInput = container.querySelector('input[type="file"]');
        const imgFile = new File(['img'], 'persona.png', { type: 'image/png' });

        fireEvent.change(fileInput, { target: { files: [imgFile] } });

        await waitFor(() => {
            expect(screen.getByAltText('Preview')).toBeInTheDocument();
            expect(screen.getByText('persona.png')).toBeInTheDocument();
        });

        const removeButton = container.querySelector('button.absolute.top-2.right-2');
        fireEvent.click(removeButton);

        await waitFor(() => {
            expect(screen.queryByAltText('Preview')).not.toBeInTheDocument();
        });
    });

    it('muestra error si falla alta', async () => {
        DesaparecidoService.crear.mockRejectedValue(new Error('error controlado'));
        const { container } = renderWithQuery(<RegistrarDesaparecido />);

        fireEvent.change(container.querySelector('input[name="nombre"]'), { target: { name: 'nombre', value: 'Ana' } });
        fireEvent.change(container.querySelector('input[name="apellido"]'), { target: { name: 'apellido', value: 'Gomez' } });
        fireEvent.change(container.querySelector('input[name="dni"]'), { target: { name: 'dni', value: '12345678' } });
        fireEvent.change(container.querySelector('textarea[name="descripcion"]'), { target: { name: 'descripcion', value: 'Descripcion larga para pasar validaciones' } });

        fireEvent.click(screen.getByRole('button', { name: /Registrar Persona/i }));

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('error controlado');
        });
    });
});
