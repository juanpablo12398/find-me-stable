import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import FilterBar from '@/components/map/FilterBar';
import { FILTER_TYPES } from '@/components/map/MapConstants';

vi.mock('framer-motion', () => ({
    motion: {
        div: ({ children, initial, animate, exit, transition, ...props }) => <div {...props}>{children}</div>,
        button: ({ children, whileHover, whileTap, initial, animate, exit, transition, ...props }) => <button {...props}>{children}</button>,
    },
    AnimatePresence: ({ children }) => <>{children}</>,
}));

describe('FilterBar', () => {
    it('muestra boton abridor cuando filtros estan ocultos', () => {
        const setShowFilters = vi.fn();

        render(
            <FilterBar
                showFilters={false}
                setShowFilters={setShowFilters}
                filtroActivo={FILTER_TYPES.TODOS}
                activateFilter={vi.fn()}
                resetFilters={vi.fn()}
            />
        );

        fireEvent.click(screen.getByRole('button', { name: 'Mostrar filtros' }));
        expect(setShowFilters).toHaveBeenCalledWith(true);
    });

    it('permite ocultar filtros y activar filtros individuales', () => {
        const setShowFilters = vi.fn();
        const activateFilter = vi.fn();

        render(
            <FilterBar
                showFilters
                setShowFilters={setShowFilters}
                filtroActivo={FILTER_TYPES.TODOS}
                activateFilter={activateFilter}
                resetFilters={vi.fn()}
            />
        );

        fireEvent.click(screen.getByRole('button', { name: 'Ocultar filtros' }));
        expect(setShowFilters).toHaveBeenCalledWith(false);

        fireEvent.click(screen.getByRole('button', { name: 'Por radio' }));
        expect(activateFilter).toHaveBeenCalledWith(FILTER_TYPES.RADIO);
    });

    it('muestra tooltip al hoverear filtro activo, inactivo y reset', () => {
        const resetFilters = vi.fn();

        render(
            <FilterBar
                showFilters
                setShowFilters={vi.fn()}
                filtroActivo={FILTER_TYPES.RADIO}
                activateFilter={vi.fn()}
                resetFilters={resetFilters}
            />
        );

        const radioButton = screen.getByRole('button', { name: 'Por radio' });
        fireEvent.mouseEnter(radioButton.parentElement);
        expect(screen.getByText('Buscar en radio')).toBeInTheDocument();
        fireEvent.mouseLeave(radioButton.parentElement);

        const todosButton = screen.getByRole('button', { name: 'Todos' });
        fireEvent.mouseEnter(todosButton.parentElement);
        const inactiveDescription = screen.getByText('Ver todos en el mapa');
        expect(inactiveDescription).toBeInTheDocument();
        expect(inactiveDescription.className).toContain('text-slate-500');
        fireEvent.mouseLeave(todosButton.parentElement);

        const resetButton = screen.getByRole('button', { name: 'Limpiar filtros' });
        fireEvent.mouseEnter(resetButton.parentElement);
        expect(screen.getByText('Limpiar filtros')).toBeInTheDocument();
        fireEvent.click(resetButton);
        expect(resetFilters).toHaveBeenCalled();
        fireEvent.mouseLeave(resetButton.parentElement);
    });
});
