import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import Registro from '@/pages/Registro';
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

vi.mock('@/components/forms/AuthForm', () => ({
    default: ({ fields, onSubmit, submitLabel }) => {
        const flatFields = fields.flatMap((f) => (f.type === 'row' ? f.fields : [f]));
        return (
            <form onSubmit={onSubmit}>
                {flatFields.map((f) => (
                    <input
                        key={f.name}
                        aria-label={f.name}
                        name={f.name}
                        value={f.value}
                        onChange={f.onChange}
                    />
                ))}
                <button type="submit">{submitLabel}</button>
            </form>
        );
    },
}));

describe('Registro', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('crea cuenta, verifica sesion y navega', async () => {
        AvistadorService.crear.mockResolvedValue({});
        AuthService.checkAuth.mockResolvedValue(true);

        render(<Registro />);

        fireEvent.change(screen.getByLabelText('nombre'), { target: { name: 'nombre', value: 'Ana' } });
        fireEvent.change(screen.getByLabelText('apellido'), { target: { name: 'apellido', value: 'Gomez' } });
        fireEvent.change(screen.getByLabelText('dni'), { target: { name: 'dni', value: '12345678' } });
        fireEvent.change(screen.getByLabelText('edad'), { target: { name: 'edad', value: '30' } });
        fireEvent.change(screen.getByLabelText('direccion'), { target: { name: 'direccion', value: 'Calle 1' } });
        fireEvent.change(screen.getByLabelText('email'), { target: { name: 'email', value: 'ana@mail.com' } });
        fireEvent.change(screen.getByLabelText('telefono'), { target: { name: 'telefono', value: '11333' } });

        fireEvent.click(screen.getByRole('button', { name: /Registrarme/i }));

        await waitFor(() => {
            expect(AvistadorService.crear).toHaveBeenCalledWith({
                dni: '12345678',
                nombre: 'Ana',
                apellido: 'Gomez',
                edad: 30,
                direccion: 'Calle 1',
                email: 'ana@mail.com',
                telefono: '11333',
            });
            expect(AuthService.checkAuth).toHaveBeenCalled();
            expect(toast.success).toHaveBeenCalled();
            expect(navigateMock).toHaveBeenCalledWith('/MapaAvistamientos');
        });
    });

    it('muestra error cuando falla el alta', async () => {
        AvistadorService.crear.mockRejectedValue(new Error('Error alta'));

        render(<Registro />);

        fireEvent.change(screen.getByLabelText('nombre'), { target: { name: 'nombre', value: 'Ana' } });
        fireEvent.change(screen.getByLabelText('apellido'), { target: { name: 'apellido', value: 'Gomez' } });
        fireEvent.change(screen.getByLabelText('dni'), { target: { name: 'dni', value: '12345678' } });
        fireEvent.change(screen.getByLabelText('email'), { target: { name: 'email', value: 'ana@mail.com' } });

        fireEvent.click(screen.getByRole('button', { name: /Registrarme/i }));

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('Error alta');
        });
    });
});
