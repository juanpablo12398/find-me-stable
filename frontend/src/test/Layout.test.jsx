import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { fireEvent, render, screen, waitFor, act } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Layout from '@/Layout';

let authState = {
    user: { nombre: 'Ana' },
    isAuthenticated: true,
    logout: vi.fn(),
};

vi.mock('framer-motion', () => ({
    motion: {
        div: ({ children, ...props }) => <div {...props}>{children}</div>,
    },
    AnimatePresence: ({ children }) => <>{children}</>,
}));

vi.mock('@/utils', () => ({
    createPageUrl: (page) => `/${page}`,
}));

vi.mock('@/hooks/useAuth.jsx', () => ({
    useAuth: () => authState,
}));

const renderLayout = (ui) => render(<MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>{ui}</MemoryRouter>);

describe('Layout', () => {
    beforeEach(() => {
        vi.clearAllMocks();
        authState = {
            user: { nombre: 'Ana' },
            isAuthenticated: true,
            logout: vi.fn(),
        };
    });

    it('renderiza variante de mapa y permite logout desktop', async () => {
        authState = { user: { dni: '30111222' }, isAuthenticated: true, logout: vi.fn() };

        renderLayout(
            <Layout currentPageName="MapaAvistamientos">
                <div>Contenido mapa</div>
            </Layout>
        );

        expect(screen.getByText('Contenido mapa')).toBeInTheDocument();
        expect(screen.getByText('30111222')).toBeInTheDocument();

        await act(async () => {
            fireEvent.click(screen.getByRole('button', { name: /Salir/i }));
        });

        await waitFor(() => expect(authState.logout).toHaveBeenCalled());
    });

    it('menu movil de mapa abre, navega y cierra sesion', async () => {
        const { container } = renderLayout(
            <Layout currentPageName="MapaAvistamientos">
                <div>Contenido mapa</div>
            </Layout>
        );

        const mobileToggle = container.querySelector('button[class*="md:hidden"]');
        fireEvent.click(mobileToggle);

        await waitFor(() => {
            expect(screen.getByRole('button', { name: /Cerrar sesion/i })).toBeInTheDocument();
        });

        const mobileListLinks = container.querySelectorAll('a[href="/ListaDesaparecidos"]');
        await act(async () => {
            fireEvent.click(mobileListLinks[mobileListLinks.length - 1]);
        });

        fireEvent.click(mobileToggle);

        await act(async () => {
            fireEvent.click(screen.getByRole('button', { name: /Cerrar sesion/i }));
        });

        await waitFor(() => expect(authState.logout).toHaveBeenCalled());
    });

    it('renderiza variante normal, responde al scroll y permite abrir menu movil anonimo', async () => {
        authState = { user: null, isAuthenticated: false, logout: vi.fn() };

        const { container } = renderLayout(
            <Layout currentPageName="ListaDesaparecidos">
                <div>Contenido lista</div>
            </Layout>
        );

        expect(screen.getByText('Contenido lista')).toBeInTheDocument();
        expect(screen.getByText('Registrarme')).toBeInTheDocument();

        Object.defineProperty(window, 'scrollY', { value: 100, configurable: true });
        fireEvent.scroll(window);

        const mobileToggle = container.querySelector('button[class*="md:hidden"]');
        fireEvent.click(mobileToggle);

        const mobileRegisterLinks = container.querySelectorAll('a[href="/Registro"]');
        expect(mobileRegisterLinks.length).toBeGreaterThan(0);
        await act(async () => {
            fireEvent.click(mobileRegisterLinks[mobileRegisterLinks.length - 1]);
        });
    });

    it('variante normal autenticada usa dni como fallback y permite logout desktop/mobile', async () => {
        authState = { user: { dni: '30111222' }, isAuthenticated: true, logout: vi.fn() };

        const { container } = renderLayout(
            <Layout currentPageName="ListaDesaparecidos">
                <div>Contenido lista auth</div>
            </Layout>
        );

        Object.defineProperty(window, 'scrollY', { value: 120, configurable: true });
        fireEvent.scroll(window);

        expect(screen.getByText('Contenido lista auth')).toBeInTheDocument();
        expect(screen.getByText('30111222')).toBeInTheDocument();

        await act(async () => {
            fireEvent.click(screen.getAllByRole('button', { name: /Cerrar sesion/i })[0]);
        });
        await waitFor(() => expect(authState.logout).toHaveBeenCalledTimes(1));

        const mobileToggle = container.querySelector('button[class*="md:hidden"]');
        fireEvent.click(mobileToggle);

        await act(async () => {
            fireEvent.click(screen.getAllByRole('button', { name: /Cerrar sesion/i })[1]);
        });
        await waitFor(() => expect(authState.logout).toHaveBeenCalledTimes(2));
    });
});
