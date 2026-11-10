import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import DesaparecidoCard from '@/components/cards/DesaparecidoCard';

vi.mock('framer-motion', () => ({
    motion: {
        div: ({ children, ...props }) => <div {...props}>{children}</div>,
    },
}));

vi.mock('@/utils', () => ({
    createPageUrl: (page) => `/${page}`,
}));

vi.mock('@/components/ui/FotoProtegida', () => ({
    default: ({ alt }) => <div data-testid="foto-protegida">{alt}</div>,
}));

describe('DesaparecidoCard', () => {
    beforeEach(() => {
        vi.restoreAllMocks();
        vi.spyOn(window, 'open').mockImplementation(() => null);
    });

    it('renderiza datos principales y fallback de descripcion', () => {
        const persona = {
            id: 'p1',
            nombre: 'Ana',
            apellido: 'Gomez',
            dni: '12345678',
            estado: 'buscando',
            fecha_desaparicion: '2026-01-01T10:00:00Z',
            ultima_ubicacion: 'Palermo',
            contacto_familiar: '1133344455',
        };

        render(
            <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                <DesaparecidoCard persona={persona} index={0} />
            </MemoryRouter>
        );

        expect(screen.getByRole('heading', { name: /Ana Gomez/i })).toBeInTheDocument();
        expect(screen.getByText('DNI: 12345678')).toBeInTheDocument();
        expect(screen.getByText('Buscando')).toBeInTheDocument();
        expect(screen.getByText('Palermo')).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Ver en mapa/i })).toBeInTheDocument();
    });

    it('llama al telefono al tocar boton de contacto', () => {
        const persona = {
            id: 'p1',
            nombre: 'Ana',
            apellido: 'Gomez',
            estado: 'buscando',
            descripcion: 'Descripcion valida',
            contacto_familiar: '1133344455',
        };

        const { container } = render(
            <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                <DesaparecidoCard persona={persona} index={1} />
            </MemoryRouter>
        );

        const buttons = container.querySelectorAll('button');
        fireEvent.click(buttons[1]);

        expect(window.open).toHaveBeenCalledWith('tel:1133344455');
    });
});


