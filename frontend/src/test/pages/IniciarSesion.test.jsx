import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import IniciarSesion from '@/pages/IniciarSesion';
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

vi.mock('@/components/forms/AuthForm', () => ({
    default: ({ fields, onSubmit, submitLabel, isLoading }) => (
        <form onSubmit={onSubmit}>
            {fields.map((f) => (
                <input
                    key={f.name}
                    aria-label={f.name}
                    name={f.name}
                    value={f.value}
                    onChange={f.onChange}
                />
            ))}
            <button type="submit" disabled={isLoading}>{submitLabel}</button>
        </form>
    ),
}));

describe('IniciarSesion', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('loguea y navega al mapa', async () => {
        AuthService.login.mockResolvedValue({ id: '1' });

        render(<IniciarSesion />);

        fireEvent.change(screen.getByLabelText('dni'), { target: { name: 'dni', value: '12345678' } });
        fireEvent.change(screen.getByLabelText('email'), { target: { name: 'email', value: 'test@mail.com' } });
        fireEvent.click(screen.getByRole('button', { name: /Iniciar/i }));

        await waitFor(() => {
            expect(AuthService.login).toHaveBeenCalledWith('12345678', 'test@mail.com');
            expect(toast.success).toHaveBeenCalled();
            expect(navigateMock).toHaveBeenCalledWith('/MapaAvistamientos');
        });
    });

    it('muestra error cuando falla login', async () => {
        AuthService.login.mockRejectedValue(new Error('Credenciales invalidas'));

        render(<IniciarSesion />);

        fireEvent.change(screen.getByLabelText('dni'), { target: { name: 'dni', value: '12345678' } });
        fireEvent.change(screen.getByLabelText('email'), { target: { name: 'email', value: 'bad@mail.com' } });
        fireEvent.click(screen.getByRole('button', { name: /Iniciar/i }));

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('Credenciales invalidas');
        });
    });
});
