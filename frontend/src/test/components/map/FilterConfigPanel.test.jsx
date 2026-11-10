import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { fireEvent, render, screen } from '@testing-library/react';
import FilterConfigPanel from '@/components/map/FilterConfigPanel';
import { FILTER_TYPES } from '@/components/map/MapConstants';

vi.mock('framer-motion', () => ({
    motion: {
        div: ({ children, ...props }) => <div {...props}>{children}</div>,
    },
    AnimatePresence: ({ children }) => <>{children}</>,
}));

vi.mock('@/components/ui/select', () => ({
    Select: ({ onValueChange, children }) => (
        <div data-testid="select-root">
            {React.Children.map(children, (child) =>
                React.isValidElement(child) ? React.cloneElement(child, { onValueChange }) : child
            )}
        </div>
    ),
    SelectContent: ({ children, onValueChange }) => (
        <div>
            {React.Children.map(children, (child) =>
                React.isValidElement(child) ? React.cloneElement(child, { onValueChange }) : child
            )}
        </div>
    ),
    SelectItem: ({ value, children, onValueChange }) => (
        <button type="button" data-testid={`select-item-${value}`} onClick={() => onValueChange?.(value)}>
            {children}
        </button>
    ),
    SelectTrigger: ({ children }) => <div>{children}</div>,
    SelectValue: ({ placeholder }) => <span>{placeholder}</span>,
}));

vi.mock('@/components/ui/slider', () => ({
    Slider: ({ value, onValueChange, min, max }) => (
        <input
            data-testid="slider"
            type="range"
            min={min}
            max={max}
            value={value?.[0] ?? 1}
            onChange={(e) => onValueChange?.([Number(e.target.value)])}
        />
    ),
}));

vi.mock('@/components/map/MapUtils', () => ({
    getPersonaColor: () => '#123456',
}));

describe('FilterConfigPanel', () => {
    it('muestra configuracion de radio y actualiza km', () => {
        const setRadioKm = vi.fn();

        render(
            <FilterConfigPanel
                showFilters
                filtroActivo={FILTER_TYPES.RADIO}
                radioCenter={{ lat: -34.6, lng: -58.4 }}
                radioKm={5}
                setRadioKm={setRadioKm}
                personaSeleccionada={null}
                setPersonaSeleccionada={vi.fn()}
                desaparecidos={[]}
            />
        );

        expect(screen.getByText('Radio: 5 km')).toBeInTheDocument();
        fireEvent.change(screen.getByTestId('slider'), { target: { value: '12' } });
        expect(setRadioKm).toHaveBeenCalledWith(12);
    });

    it('muestra selector de persona para filtro por desaparecido', () => {
        const setPersonaSeleccionada = vi.fn();

        render(
            <FilterConfigPanel
                showFilters
                filtroActivo={FILTER_TYPES.DESAPARECIDO}
                radioCenter={null}
                radioKm={5}
                setRadioKm={vi.fn()}
                personaSeleccionada=""
                setPersonaSeleccionada={setPersonaSeleccionada}
                desaparecidos={[{ id: '1', nombre: 'Ana', apellido: 'Gomez' }]}
            />
        );

        expect(screen.getByText('Seleccionar persona')).toBeInTheDocument();
        fireEvent.click(screen.getByTestId('select-item-1'));
        expect(setPersonaSeleccionada).toHaveBeenCalledWith('1');
    });
});
