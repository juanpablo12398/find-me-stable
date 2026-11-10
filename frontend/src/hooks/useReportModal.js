import { useRef, useState } from "react";
import { toast } from "sonner";
import { AvistamientoService } from "@/services/AvistamientoService";

/**
 * Hook que centraliza la lógica de negocio del modal de reporte de avistamiento:
 * - Estado del formulario (persona, descripcion, publico, fecha)
 * - Validación frontend (persona requerida, descripción mínima)
 * - Manejo de foto (validación tipo/tamaño, preview, remoción)
 * - Submit con llamada al servicio
 */
export function useReportModal({ selectedLatLng, user, onSuccess, onClose }) {
    const [reportForm, setReportForm] = useState({
        desaparecidoId: "",
        descripcion: "",
        publico: true,
        fechaAvistamiento: ""
    });
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [reportFotoPreview, setReportFotoPreview] = useState(null);
    const [reportFotoFile, setReportFotoFile] = useState(null);
    const reportFileInputRef = useRef(null);

    const resetForm = () => {
        setReportForm({ desaparecidoId: "", descripcion: "", publico: true, fechaAvistamiento: "" });
        setReportFotoPreview(null);
        setReportFotoFile(null);
        if (reportFileInputRef.current) reportFileInputRef.current.value = "";
    };

    const handleClose = () => {
        resetForm();
        onClose();
    };

    const handleFotoChange = (e) => {
        const file = e.target.files?.[0];
        if (!file) return;
        if (!file.type.startsWith("image/")) {
            toast.error("El archivo debe ser una imagen");
            return;
        }
        if (file.size > 5 * 1024 * 1024) {
            toast.error("La imagen no puede superar los 5MB");
            return;
        }
        setReportFotoFile(file);
        const reader = new FileReader();
        reader.onload = (ev) => setReportFotoPreview(ev.target.result);
        reader.readAsDataURL(file);
    };

    const handleRemoveFoto = () => {
        setReportFotoPreview(null);
        setReportFotoFile(null);
        if (reportFileInputRef.current) reportFileInputRef.current.value = "";
    };

    const handleSubmitReport = async (e) => {
        e.preventDefault();

        if (!selectedLatLng || !reportForm.desaparecidoId) {
            toast.error("Selecciona una persona extraviada");
            return;
        }

        if (reportForm.descripcion.length < 20) {
            toast.error("La descripción debe tener al menos 20 caracteres");
            return;
        }

        setIsSubmitting(true);

        try {
            await AvistamientoService.crear(
                {
                    avistadorId: String(user?.id),
                    desaparecidoId: String(reportForm.desaparecidoId),
                    latitud: selectedLatLng.lat,
                    longitud: selectedLatLng.lng,
                    descripcion: reportForm.descripcion,
                    publico: reportForm.publico,
                    fechaAvistamiento: reportForm.fechaAvistamiento || null
                },
                reportFotoFile
            );

            toast.success("¡Avistamiento reportado exitosamente!");
            resetForm();
            onSuccess();
        } catch (error) {
            toast.error(error.message || "Error al reportar avistamiento");
        } finally {
            setIsSubmitting(false);
        }
    };

    const togglePublico = () => {
        setReportForm(prev => ({ ...prev, publico: !prev.publico }));
    };

    return {
        reportForm,
        setReportForm,
        isSubmitting,
        reportFotoPreview,
        reportFotoFile,
        reportFileInputRef,
        resetForm,
        handleClose,
        handleFotoChange,
        handleRemoveFoto,
        handleSubmitReport,
        togglePublico,
    };
}
