import React from 'react';
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import MapPopupContent from '../../../components/map/MapPopupContent';

// Hacer un mock de lucide-react y FotoProtegida si es necesario, 
// o dejar que renderice, render arrojará a JSDOM
describe('MapPopupContent', () => {

    it('debe renderizar la informacion del desaparecido y el estado de la verificación', () => {
        const mockAvistamiento = {
            descripcion: 'Lo vi caminando lento',
            verificado: true,
            fechaFormateada: '10 Feb 2024, 15:30'
        };
        const mockDesaparecido = {
            nombre: 'Juan',
            apellido: 'Perez',
            fotoUrl: 'http://example.com/foto.jpg'
        };

        render(
            <MapPopupContent avistamiento={mockAvistamiento} desaparecido={mockDesaparecido} />
        );

        // Verifica nombre
        expect(screen.getByText('Juan Perez')).toBeInTheDocument();

        // Verifica descripción
        expect(screen.getByText('Lo vi caminando lento')).toBeInTheDocument();

        // Verifica badge (Verificado)
        expect(screen.getByText('Verificado')).toBeInTheDocument();

        // Verifica fecha
        expect(screen.getByText('10 Feb 2024, 15:30')).toBeInTheDocument();
    });

    it('debe usar "Persona" y mostrar "Pendiente" cuando no hay datos detallados', () => {
        const mockAvistamiento = {
            verificado: false,
            fechaHora: '2024-02-12T10:00:00Z'
        };

        render(
            <MapPopupContent avistamiento={mockAvistamiento} />
        );

        expect(screen.getByText('Persona')).toBeInTheDocument();
        expect(screen.getByText('Pendiente')).toBeInTheDocument();
    });
});
