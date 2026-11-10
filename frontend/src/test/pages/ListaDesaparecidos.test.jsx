import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { fireEvent, render, screen, waitFor, act } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import ListaDesaparecidos from '@/pages/ListaDesaparecidos';
import { DesaparecidoService } from '@/services/DesaparecidoService';

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
            <button type="button" data-testid="select-item-unknown" onClick={() => onValueChange?.('__unknown__')}>
                Unknown
            </button>
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

vi.mock('@/components/cards/DesaparecidoCard', () => ({
    default: ({ persona }) => <div data-testid="card">{persona.nombre}</div>,
}));

vi.mock('@/services/DesaparecidoService', () => ({
    DesaparecidoService: {
        obtenerPaginado: vi.fn(),
        obtenerTodos: vi.fn(),
    },
}));

function renderWithQuery(ui) {
    const queryClient = new QueryClient();
    return render(<QueryClientProvider client={queryClient}>{ui}</QueryClientProvider>);
}

describe('ListaDesaparecidos', () => {
    beforeEach(() => {
        vi.clearAllMocks();
        DesaparecidoService.obtenerPaginado.mockResolvedValue({
            content: [{ id: '1', nombre: 'Ana', apellido: 'Gomez' }],
            page: 0,
            size: 12,
            totalElements: 13,
            totalPages: 2,
            last: false,
        });
        DesaparecidoService.obtenerTodos.mockResolvedValue([
            { id: '1', estado: 'buscando' },
            { id: '2', estado: 'encontrado' },
            { id: '3', estado: 'sin_novedades' },
        ]);
    });

    it('renderiza resultados y permite paginar hacia adelante y atras', async () => {
        renderWithQuery(<ListaDesaparecidos />);

        await waitFor(() => {
            expect(screen.getByTestId('card')).toHaveTextContent('Ana');
            expect(screen.getByRole('button', { name: 'Siguiente' })).toBeEnabled();
        });

        fireEvent.click(screen.getByRole('button', { name: 'Siguiente' }));
        await waitFor(() => expect(screen.getAllByText('Pagina 2').length).toBeGreaterThan(0));

        const prevButton = screen.getByRole('button', { name: 'Anterior' });
        await waitFor(() => expect(prevButton).toBeEnabled());
        fireEvent.click(prevButton);
        await waitFor(() => expect(screen.getAllByText('Pagina 1').length).toBeGreaterThan(0));
    });

    it('permite refrescar, cambiar vista y aplicar busqueda con debounce', async () => {
        renderWithQuery(<ListaDesaparecidos />);

        await waitFor(() => {
            expect(DesaparecidoService.obtenerPaginado).toHaveBeenCalled();
        });

        const refreshButton = screen.getByRole('button', { name: 'Actualizar resultados' });
        await waitFor(() => expect(refreshButton).toBeEnabled());
        fireEvent.click(refreshButton);

        fireEvent.click(screen.getByRole('button', { name: 'Vista en lista' }));
        fireEvent.click(screen.getByRole('button', { name: 'Vista en grilla' }));

        fireEvent.change(screen.getByPlaceholderText('Buscar por nombre, DNI o descripcion...'), {
            target: { value: 'ana' }
        });

        await act(async () => {
            await new Promise((resolve) => setTimeout(resolve, 400));
        });

        await waitFor(() => {
            expect(DesaparecidoService.obtenerPaginado).toHaveBeenLastCalledWith(expect.objectContaining({
                search: 'ana'
            }));
        });
    });

    it('muestra estado vacio y permite cambiar filtros/sort', async () => {
        DesaparecidoService.obtenerPaginado.mockResolvedValue({
            content: [],
            page: 0,
            size: 12,
            totalElements: 0,
            totalPages: 0,
            last: true,
        });

        renderWithQuery(<ListaDesaparecidos />);

        await waitFor(() => {
            expect(screen.getByText('No se encontraron resultados')).toBeInTheDocument();
        });

        fireEvent.click(screen.getByTestId('select-item-ENCONTRADO'));
        fireEvent.click(screen.getByTestId('select-item-name'));

        await waitFor(() => {
            expect(DesaparecidoService.obtenerPaginado).toHaveBeenCalledWith(expect.objectContaining({
                estado: 'ENCONTRADO',
                sort: 'nombre'
            }));
        });
    });

    it('usa fallbacks de content y totalElements cuando llegan null', async () => {
        DesaparecidoService.obtenerPaginado.mockResolvedValue({
            content: null,
            page: 0,
            size: 12,
            totalElements: null,
            totalPages: 0,
            last: true,
        });

        renderWithQuery(<ListaDesaparecidos />);

        await waitFor(() => {
            expect(screen.getByText(/No se encontraron resultados/i)).toBeInTheDocument();
            expect(screen.getAllByText('0').length).toBeGreaterThan(0);
            expect(screen.getByText('No se encontraron resultados')).toBeInTheDocument();
        });
    });

    it('aplica fallback de sort a urgencia cuando el valor es desconocido', async () => {
        renderWithQuery(<ListaDesaparecidos />);

        await waitFor(() => {
            expect(DesaparecidoService.obtenerPaginado).toHaveBeenCalled();
        });

        const unknownButtons = screen.getAllByTestId('select-item-unknown');
        fireEvent.click(unknownButtons[1]);

        await waitFor(() => {
            expect(DesaparecidoService.obtenerPaginado).toHaveBeenLastCalledWith(expect.objectContaining({
                sort: 'urgencia'
            }));
        });
    });

    it('en loading permite cambiar a vista lista y usa class grid-cols-1', async () => {
        let resolvePaged;
        DesaparecidoService.obtenerPaginado.mockImplementationOnce(() => new Promise((resolve) => {
            resolvePaged = resolve;
        }));

        const { container } = renderWithQuery(<ListaDesaparecidos />);

        await waitFor(() => {
            expect(screen.getByRole('button', { name: 'Vista en lista' })).toBeInTheDocument();
        });

        fireEvent.click(screen.getByRole('button', { name: 'Vista en lista' }));

        const loadingGrid = container.querySelector('.grid.gap-6.grid-cols-1');
        expect(loadingGrid).toBeInTheDocument();

        resolvePaged({
            content: [{ id: '1', nombre: 'Ana' }],
            page: 0,
            size: 12,
            totalElements: 1,
            totalPages: 1,
            last: true,
        });
    });
});


