import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import App from '@/App';

vi.mock('@/hooks/useAuth', () => ({
    AuthProvider: ({ children }) => <>{children}</>,
}));

vi.mock('@/components/ui/sonner', () => ({
    Toaster: () => <div data-testid="toaster" />,
}));

vi.mock('@/Layout', () => ({
    default: ({ children }) => <div data-testid="layout">{children}</div>,
}));

vi.mock('@/pages/MapaAvistamientos', () => ({ default: () => <div>Pagina mapa</div> }));
vi.mock('@/pages/ListaDesaparecidos', () => ({ default: () => <div>Pagina lista</div> }));
vi.mock('@/pages/IniciarSesion', () => ({ default: () => <div>Pagina login</div> }));
vi.mock('@/pages/Registro', () => ({ default: () => <div>Pagina registro</div> }));
vi.mock('@/pages/RegistrarDesaparecido', () => ({ default: () => <div>Pagina alta</div> }));

describe('App', () => {
    it('redirige la raiz al mapa y renderiza toaster', async () => {
        window.history.pushState({}, '', '/');
        render(<App />);

        await waitFor(() => {
            expect(screen.getByText('Pagina mapa')).toBeInTheDocument();
        });
        expect(screen.getByTestId('toaster')).toBeInTheDocument();
    });
});
