import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { useQueryClient } from "@tanstack/react-query";
import { DesaparecidoService } from "@/services/DesaparecidoService";
import { useAuth } from "@/hooks/useAuth";
import { createPageUrl } from "@/utils";

/**
 * Hook que centraliza la lógica de negocio de la página RegistrarDesaparecido:
 * - Guard de autenticación
 * - Estado y validación del formulario
 * - Manejo de foto (validación tipo/tamaño, preview, remoción)
 * - Submit (construcción del body, upload de foto, invalidación de cache)
 */
export function useRegistrarDesaparecido() {
    const { isAuthenticated } = useAuth();
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const fileInputRef = useRef(null);

    const [formData, setFormData] = useState({
        nombre: "",
        apellido: "",
        edad: "",
        dni: "",
        descripcion: "",
        fechaDesaparicion: "",
        contactoFamiliar: "",
        emailContactoFamiliar: "",
        ultimaUbicacion: "",
    });

    const [fotoPreview, setFotoPreview] = useState(null);
    const [fotoFile, setFotoFile] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    // Guard: redirigir si no está autenticado
    useEffect(() => {
        if (!isAuthenticated) {
            toast.error("Debes iniciar sesión para registrar una persona extraviada");
            navigate(createPageUrl("IniciarSesion"));
        }
    }, [isAuthenticated, navigate]);

    const handleChange = (e) => {
        setFormData((prev) => ({
            ...prev,
            [e.target.name]: e.target.value,
        }));
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
            if (fileInputRef.current) fileInputRef.current.value = "";
            return;
        }

        setFotoFile(file);

        // Preview local (solo para mostrar en el form)
        const reader = new FileReader();
        reader.onload = (ev) => setFotoPreview(ev?.target?.result);
        reader.readAsDataURL(file);
    };

    const handleRemoveFoto = () => {
        setFotoFile(null);
        setFotoPreview(null);
        if (fileInputRef.current) fileInputRef.current.value = "";
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            const body = {
                nombre: formData.nombre.trim(),
                apellido: formData.apellido.trim(),
                edad: formData.edad ? Number(formData.edad) : 0,
                dni: formData.dni.trim(),
                descripcion: formData.descripcion.trim(),
                fechaDesaparicion: formData.fechaDesaparicion || null,
                contactoFamiliar: formData.contactoFamiliar.trim() || null,
                emailContactoFamiliar: formData.emailContactoFamiliar.trim() || null,
                ultimaUbicacion: formData.ultimaUbicacion.trim() || null,
            };

            await DesaparecidoService.crear(body, fotoFile);

            // Invalidar cache para que lista y mapa se actualicen sin F5
            queryClient.invalidateQueries({ queryKey: ["desaparecidos"] });
            queryClient.invalidateQueries({ queryKey: ["desaparecidos-counts"] });

            toast.success("¡Persona registrada exitosamente!");
            navigate(createPageUrl("ListaDesaparecidos"));
        } catch (error) {
            toast.error(error?.message || "No se pudo registrar la persona");
        } finally {
            setIsLoading(false);
        }
    };

    return {
        isAuthenticated,
        formData,
        fotoPreview,
        fotoFile,
        isLoading,
        fileInputRef,
        handleChange,
        handleFotoChange,
        handleRemoveFoto,
        handleSubmit,
    };
}
