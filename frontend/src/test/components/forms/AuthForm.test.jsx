import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import AuthForm from '@/components/forms/AuthForm';

vi.mock('framer-motion', () => ({
    motion: {
        div: ({ children, ...props }) => <div {...props}>{children}</div>,
    },
}));

vi.mock('@/utils', () => ({
    createPageUrl: (page) => `/${page}`,
}));

describe('AuthForm', () => {
    it('renderiza campos simples y por fila, y dispara submit', () => {
        const onSubmit = vi.fn((e) => e.preventDefault());
        const onChangeNombre = vi.fn();
        const onChangeApellido = vi.fn();
        const onChangeEmail = vi.fn();

        const fields = [
            {
                type: 'row',
                fields: [
                    { name: 'nombre', label: 'Nombre', value: '', onChange: onChangeNombre, required: true },
                    { name: 'apellido', label: 'Apellido', value: '', onChange: onChangeApellido, required: true },
                ],
            },
            { name: 'email', label: 'Email', type: 'email', value: '', onChange: onChangeEmail, required: true },
        ];

        const { container } = render(
            <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                <AuthForm
                    title="Titulo"
                    subtitle="Subtitulo"
                    fields={fields}
                    submitLabel="Enviar"
                    onSubmit={onSubmit}
                    isLoading={false}
                    alternateLink="Registro"
                    alternateLinkText="Ir a registro"
                />
            </MemoryRouter>
        );

        fireEvent.change(container.querySelector('input[name="nombre"]'), { target: { value: 'Ana' } });
        fireEvent.change(container.querySelector('input[name="apellido"]'), { target: { value: 'Gomez' } });
        fireEvent.change(container.querySelector('input[name="email"]'), { target: { value: 'ana@test.com' } });

        expect(onChangeNombre).toHaveBeenCalled();
        expect(onChangeApellido).toHaveBeenCalled();
        expect(onChangeEmail).toHaveBeenCalled();

        fireEvent.submit(container.querySelector('form'));
        expect(onSubmit).toHaveBeenCalled();
        expect(screen.getByRole('button', { name: 'Ir a registro' })).toBeInTheDocument();
    });

    it('muestra estado de carga', () => {
        render(
            <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                <AuthForm
                    title="Titulo"
                    fields={[]}
                    submitLabel="Enviar"
                    onSubmit={(e) => e.preventDefault()}
                    isLoading
                />
            </MemoryRouter>
        );

        expect(screen.getByText('Procesando...')).toBeInTheDocument();
    });
});


