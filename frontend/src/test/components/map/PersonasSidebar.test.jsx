import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import PersonasSidebar from '../../../components/map/PersonasSidebar';
import { BrowserRouter } from 'react-router-dom';

describe('PersonasSidebar', () => {
    const mockDesaparecidos = [
        { id: 1, nombre: 'Ana', apellido: 'Gomez', estado: 'BUSCANDO', nivelUrgencia: 'ALTO' },
        { id: 2, nombre: 'Carlos', apellido: 'Perez', estado: 'ENCONTRADO' }
    ];

    it('debe renderizar el sidebar indicando cantidad y la lista de desaparecidos', () => {
        render(
            <BrowserRouter>
                <PersonasSidebar desaparecidos={mockDesaparecidos} />
            </BrowserRouter>
        );

        // Header indica cantidad
        expect(screen.getByText('2 personas')).toBeInTheDocument();

        // Nombres en la lista
        expect(screen.getByText('Ana Gomez')).toBeInTheDocument();
        expect(screen.getByText('Buscando')).toBeInTheDocument();

        expect(screen.getByText('Carlos Perez')).toBeInTheDocument();
        expect(screen.getByText('✓ Encontrado')).toBeInTheDocument();
    });

    it('debe colapsar la lista al presionar el encabezado', async () => {
        render(
            <BrowserRouter>
                <PersonasSidebar desaparecidos={mockDesaparecidos} />
            </BrowserRouter>
        );

        expect(screen.getByText('Ana Gomez')).toBeInTheDocument();

        // Boton toggle
        const toggleBtn = screen.getByRole('button', { name: /2 personas/i });
        fireEvent.click(toggleBtn);

        // Esperar a que AnimatePresence lo remueva
        await import('@testing-library/react').then(({ waitFor }) => {
            return waitFor(() => {
                expect(screen.queryByText('Ana Gomez')).not.toBeInTheDocument();
            });
        });
    });
});
