import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { AuthService } from "@/services/AuthService";
import { createPageUrl } from "@/utils";

/**
 * Hook que centraliza la lógica de negocio del formulario de inicio de sesión.
 * Maneja el estado del form, la llamada al servicio y la navegación post-login.
 */
export function useIniciarSesion() {
    const [formData, setFormData] = useState({
        dni: "",
        email: ""
    });
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData(prev => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            await AuthService.login(formData.dni, formData.email);
            toast.success("¡Bienvenido de vuelta!");
            navigate(createPageUrl("MapaAvistamientos"));
        } catch (error) {
            toast.error(error.message);
        } finally {
            setIsLoading(false);
        }
    };

    const fields = [
        {
            name: "dni",
            label: "DNI",
            placeholder: "Ingresa tu DNI (7 a 10 dígitos)",
            required: true,
            value: formData.dni,
            onChange: handleChange
        },
        {
            name: "email",
            label: "Email",
            type: "email",
            placeholder: "tu@email.com",
            required: true,
            value: formData.email,
            onChange: handleChange
        }
    ];

    return {
        formData,
        isLoading,
        handleChange,
        handleSubmit,
        fields,
    };
}
