import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import ReportModal from '@/components/map/ReportModal';
import { AvistamientoService } from '@/services/AvistamientoService';
import { toast } from 'sonner';

vi.mock('@/components/ui/select', async () => {
    const ReactModule = await vi.importActual('react');
    const SelectContext = ReactModule.createContext(null);

    const Select = ({ onValueChange, children }) => (
        <SelectContext.Provider value={onValueChange}>
            <div data-testid="select-root">{children}</div>
        </SelectContext.Provider>
    );

    const SelectContent = ({ children }) => <div>{children}</div>;
    const SelectTrigger = ({ children }) => <div>{children}</div>;
    const SelectValue = ({ placeholder }) => <span>{placeholder}</span>;
    const SelectItem = ({ value, children }) => {
        const onValueChange = ReactModule.useContext(SelectContext);
        return (
            <button type="button" data-testid={`select-item-${value}`} onClick={() => onValueChange?.(value)}>
                {children}
            </button>
        );
    };

    return { Select, SelectContent, SelectItem, SelectTrigger, SelectValue };
});

vi.mock('@/components/map/MapUtils', () => ({
    getPersonaColor: () => '#123456',
}));

vi.mock('@/services/AvistamientoService', () => ({
    AvistamientoService: {
        crear: vi.fn(),
    },
}));

vi.mock('sonner', () => ({
    toast: {
        error: vi.fn(),
        success: vi.fn(),
    },
}));

describe('ReportModal', () => {
    const baseProps = {
        show: true,
        onClose: vi.fn(),
        selectedLatLng: { lat: -34.6, lng: -58.4 },
        desaparecidos: [{ id: 'd1', nombre: 'Ana', apellido: 'Gomez' }],
        user: { id: 'u1' },
        onSuccess: vi.fn(),
    };

    beforeEach(() => {
        vi.clearAllMocks();
        vi.stubGlobal('FileReader', class {
            readAsDataURL() {
                if (this.onload) {
                    this.onload({ target: { result: 'data:image/png;base64,abc' } });
                }
            }
        });
    });

    it('no renderiza cuando show es false', () => {
        render(<ReportModal {...baseProps} show={false} />);
        expect(screen.queryByText('Reportar Avistamiento')).not.toBeInTheDocument();
    });

    it('valida que haya persona seleccionada', async () => {
        render(<ReportModal {...baseProps} />);

        fireEvent.change(screen.getByPlaceholderText('Describe las circunstancias del avistamiento...'), {
            target: { value: 'Descripcion suficientemente larga para validar caso.' },
        });
        fireEvent.click(screen.getByRole('button', { name: /Enviar reporte/i }));

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('Selecciona una persona extraviada');
        });
    });

    it('valida longitud minima de descripcion', async () => {
        render(<ReportModal {...baseProps} />);

        fireEvent.click(screen.getByTestId('select-item-d1'));
        fireEvent.change(screen.getByPlaceholderText('Describe las circunstancias del avistamiento...'), {
            target: { value: 'Muy corta' },
        });
        fireEvent.click(screen.getByRole('button', { name: /Enviar reporte/i }));

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('20 caracteres'));
        });
    });

    it('valida archivos no imagen y archivos demasiado grandes', async () => {
        const { container } = render(<ReportModal {...baseProps} />);
        const fileInput = container.querySelector('input[type="file"]');

        const invalidFile = new File(['texto'], 'nota.txt', { type: 'text/plain' });
        fireEvent.change(fileInput, { target: { files: [invalidFile] } });

        const bigFile = new File([new Uint8Array(6 * 1024 * 1024)], 'big.png', { type: 'image/png' });
        fireEvent.change(fileInput, { target: { files: [bigFile] } });

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('El archivo debe ser una imagen');
            expect(toast.error).toHaveBeenCalledWith('La imagen no puede superar los 5MB');
        });
    });

    it('muestra preview de foto y permite removerla', async () => {
        const { container } = render(<ReportModal {...baseProps} />);
        const fileInput = container.querySelector('input[type="file"]');

        const validFile = new File(['img'], 'test.png', { type: 'image/png' });
        fireEvent.change(fileInput, { target: { files: [validFile] } });

        await waitFor(() => {
            expect(screen.getByAltText('Preview')).toBeInTheDocument();
            expect(screen.getByText('test.png')).toBeInTheDocument();
        });

        const removeButton = container.querySelector('button.absolute.top-2.right-2');
        fireEvent.click(removeButton);

        await waitFor(() => {
            expect(screen.queryByAltText('Preview')).not.toBeInTheDocument();
        });
    });

    it('envia reporte correctamente con reporte privado', async () => {
        AvistamientoService.crear.mockResolvedValue({});
        render(<ReportModal {...baseProps} />);

        fireEvent.click(screen.getByTestId('select-item-d1'));
        fireEvent.click(screen.getByText(/Reporte/).closest('button'));
        fireEvent.change(screen.getByPlaceholderText('Describe las circunstancias del avistamiento...'), {
            target: { value: 'Descripcion suficientemente larga para enviar reporte.' },
        });

        fireEvent.click(screen.getByRole('button', { name: /Enviar reporte/i }));

        await waitFor(() => {
            expect(AvistamientoService.crear).toHaveBeenCalledTimes(1);
            expect(AvistamientoService.crear).toHaveBeenCalledWith(
                expect.objectContaining({
                    avistadorId: 'u1',
                    desaparecidoId: 'd1',
                    latitud: -34.6,
                    longitud: -58.4,
                    publico: false,
                }),
                null
            );
            expect(toast.success).toHaveBeenCalled();
            expect(baseProps.onSuccess).toHaveBeenCalled();
        });
    });

    it('muestra error si falla el envio', async () => {
        AvistamientoService.crear.mockRejectedValue(new Error('fallo controlado'));
        render(<ReportModal {...baseProps} />);

        fireEvent.click(screen.getByTestId('select-item-d1'));
        fireEvent.change(screen.getByPlaceholderText('Describe las circunstancias del avistamiento...'), {
            target: { value: 'Descripcion suficientemente larga para enviar reporte.' },
        });
        fireEvent.click(screen.getByRole('button', { name: /Enviar reporte/i }));

        await waitFor(() => {
            expect(toast.error).toHaveBeenCalledWith('fallo controlado');
        });
    });

    it('cancelar dispara onClose', () => {
        render(<ReportModal {...baseProps} />);
        fireEvent.click(screen.getByRole('button', { name: /Cancelar/i }));
        expect(baseProps.onClose).toHaveBeenCalled();
    });
});


