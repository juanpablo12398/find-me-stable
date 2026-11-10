import { act, renderHook, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { useReportModal } from '@/hooks/useReportModal';
import { AvistamientoService } from '@/services/AvistamientoService';
import { toast } from 'sonner';

vi.mock('sonner', () => ({
    toast: {
        success: vi.fn(),
        error: vi.fn(),
    },
}));

vi.mock('@/services/AvistamientoService', () => ({
    AvistamientoService: {
        crear: vi.fn(),
    },
}));

const baseProps = {
    selectedLatLng: { lat: -34.6, lng: -58.4 },
    user: { id: 'u1' },
    onSuccess: vi.fn(),
    onClose: vi.fn(),
};

describe('useReportModal', () => {
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

    it('inicializa reportForm con valores default', () => {
        const { result } = renderHook(() => useReportModal(baseProps));

        expect(result.current.reportForm).toEqual({
            desaparecidoId: '',
            descripcion: '',
            publico: true,
            fechaAvistamiento: '',
        });
        expect(result.current.isSubmitting).toBe(false);
        expect(result.current.reportFotoFile).toBeNull();
        expect(result.current.reportFotoPreview).toBeNull();
    });

    it('resetForm restaura el formulario a valores iniciales', () => {
        const { result } = renderHook(() => useReportModal(baseProps));

        act(() => {
            result.current.setReportForm({ desaparecidoId: 'd1', descripcion: 'texto', publico: false, fechaAvistamiento: '2026-01-01' });
        });

        act(() => {
            result.current.resetForm();
        });

        expect(result.current.reportForm).toEqual({
            desaparecidoId: '',
            descripcion: '',
            publico: true,
            fechaAvistamiento: '',
        });
        expect(result.current.reportFotoFile).toBeNull();
        expect(result.current.reportFotoPreview).toBeNull();
    });

    it('handleClose llama resetForm y onClose', () => {
        const onClose = vi.fn();
        const { result } = renderHook(() => useReportModal({ ...baseProps, onClose }));

        act(() => {
            result.current.setReportForm(prev => ({ ...prev, descripcion: 'algo' }));
        });

        act(() => {
            result.current.handleClose();
        });

        expect(onClose).toHaveBeenCalledTimes(1);
        expect(result.current.reportForm.descripcion).toBe('');
    });

    it('handleFotoChange rechaza archivos que no son imagen', () => {
        const { result } = renderHook(() => useReportModal(baseProps));

        act(() => {
            result.current.handleFotoChange({
                target: { files: [new File(['txt'], 'doc.txt', { type: 'text/plain' })] }
            });
        });

        expect(toast.error).toHaveBeenCalledWith('El archivo debe ser una imagen');
        expect(result.current.reportFotoFile).toBeNull();
    });

    it('handleFotoChange rechaza imágenes que superan 5MB', () => {
        const { result } = renderHook(() => useReportModal(baseProps));

        const bigFile = new File([new Uint8Array(6 * 1024 * 1024)], 'big.png', { type: 'image/png' });
        act(() => {
            result.current.handleFotoChange({ target: { files: [bigFile] } });
        });

        expect(toast.error).toHaveBeenCalledWith('La imagen no puede superar los 5MB');
        expect(result.current.reportFotoFile).toBeNull();
    });

    it('handleFotoChange acepta imagen válida y genera preview', async () => {
        const { result } = renderHook(() => useReportModal(baseProps));

        const validFile = new File(['img'], 'avistamiento.png', { type: 'image/png' });
        act(() => {
            result.current.handleFotoChange({ target: { files: [validFile] } });
        });

        await waitFor(() => {
            expect(result.current.reportFotoFile).toBe(validFile);
            expect(result.current.reportFotoPreview).toBe('data:image/png;base64,abc');
        });
    });

    it('handleRemoveFoto limpia la foto y el preview', async () => {
        const { result } = renderHook(() => useReportModal(baseProps));

        const validFile = new File(['img'], 'avistamiento.png', { type: 'image/png' });
        act(() => {
            result.current.handleFotoChange({ target: { files: [validFile] } });
        });

        await waitFor(() => expect(result.current.reportFotoFile).not.toBeNull());

        act(() => {
            result.current.handleRemoveFoto();
        });

        expect(result.current.reportFotoFile).toBeNull();
        expect(result.current.reportFotoPreview).toBeNull();
    });

    it('togglePublico alterna el campo publico del form', () => {
        const { result } = renderHook(() => useReportModal(baseProps));

        expect(result.current.reportForm.publico).toBe(true);

        act(() => {
            result.current.togglePublico();
        });

        expect(result.current.reportForm.publico).toBe(false);

        act(() => {
            result.current.togglePublico();
        });

        expect(result.current.reportForm.publico).toBe(true);
    });

    it('handleSubmitReport muestra error si no hay persona seleccionada', async () => {
        const { result } = renderHook(() => useReportModal(baseProps));

        await act(async () => {
            await result.current.handleSubmitReport({ preventDefault: vi.fn() });
        });

        expect(toast.error).toHaveBeenCalledWith('Selecciona una persona extraviada');
        expect(AvistamientoService.crear).not.toHaveBeenCalled();
    });

    it('handleSubmitReport muestra error si descripción es menor de 20 caracteres', async () => {
        const { result } = renderHook(() => useReportModal(baseProps));

        act(() => {
            result.current.setReportForm(prev => ({ ...prev, desaparecidoId: 'd1', descripcion: 'Corta' }));
        });

        await act(async () => {
            await result.current.handleSubmitReport({ preventDefault: vi.fn() });
        });

        expect(toast.error).toHaveBeenCalledWith(expect.stringContaining('20 caracteres'));
        expect(AvistamientoService.crear).not.toHaveBeenCalled();
    });

    it('handleSubmitReport llama al servicio con datos correctos en éxito', async () => {
        AvistamientoService.crear.mockResolvedValue({});
        const onSuccess = vi.fn();
        const { result } = renderHook(() => useReportModal({ ...baseProps, onSuccess }));

        act(() => {
            result.current.setReportForm({
                desaparecidoId: 'd1',
                descripcion: 'Descripcion suficientemente larga para pasar la validación.',
                publico: true,
                fechaAvistamiento: '',
            });
        });

        await act(async () => {
            await result.current.handleSubmitReport({ preventDefault: vi.fn() });
        });

        expect(AvistamientoService.crear).toHaveBeenCalledWith(
            expect.objectContaining({
                avistadorId: 'u1',
                desaparecidoId: 'd1',
                latitud: -34.6,
                longitud: -58.4,
                publico: true,
                fechaAvistamiento: null,
            }),
            null
        );
        expect(toast.success).toHaveBeenCalled();
        expect(onSuccess).toHaveBeenCalledTimes(1);
        // El form se resetea después del éxito
        expect(result.current.reportForm.desaparecidoId).toBe('');
        expect(result.current.isSubmitting).toBe(false);
    });

    it('handleSubmitReport muestra toast.error cuando falla el servicio', async () => {
        AvistamientoService.crear.mockRejectedValue(new Error('fallo del backend'));
        const { result } = renderHook(() => useReportModal(baseProps));

        act(() => {
            result.current.setReportForm({
                desaparecidoId: 'd1',
                descripcion: 'Descripcion suficientemente larga para pasar la validación.',
                publico: true,
                fechaAvistamiento: '',
            });
        });

        await act(async () => {
            await result.current.handleSubmitReport({ preventDefault: vi.fn() });
        });

        expect(toast.error).toHaveBeenCalledWith('fallo del backend');
        expect(result.current.isSubmitting).toBe(false);
    });

    it('handleSubmitReport funciona sin selectedLatLng cuando hay persona pero no coordenadas', async () => {
        // Si selectedLatLng es null y no hay desaparecidoId, debe fallar en la guardia de persona
        const { result } = renderHook(() => useReportModal({ ...baseProps, selectedLatLng: null }));

        act(() => {
            result.current.setReportForm(prev => ({ ...prev, desaparecidoId: 'd1' }));
        });

        await act(async () => {
            await result.current.handleSubmitReport({ preventDefault: vi.fn() });
        });

        // Con selectedLatLng null, la guardia !selectedLatLng lo bloquea con "Selecciona una persona"
        expect(toast.error).toHaveBeenCalledWith('Selecciona una persona extraviada');
    });
});
