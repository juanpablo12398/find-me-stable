import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { AvistadorService } from "@/services/AvistadorService";
import { AuthService } from "@/services/AuthService";
import { createPageUrl } from "@/utils";

/**
 * Hook que centraliza la lógica de negocio del formulario de registro de avistador.
 * Construye el body con tipos correctos, llama a los servicios y navega post-registro.
 */
export function useRegistro() {
    const [formData, setFormData] = useState({
        nombre: "",
        apellido: "",
        dni: "",
        edad: "",
        direccion: "",
        email: "",
        telefono: ""
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
            const body = {
                dni: formData.dni,
                nombre: formData.nombre,
                apellido: formData.apellido,
                edad: formData.edad ? parseInt(formData.edad, 10) : null,
                direccion: formData.direccion || null,
                email: formData.email || null,
                telefono: formData.telefono || null
            };

            await AvistadorService.crear(body);
            toast.success("¡Cuenta creada exitosamente!");

            // El backend setea cookie → verificamos sesión
            await AuthService.checkAuth();

            toast.success("Ya estás registrado y logueado. Ahora podés registrar desaparecidos.");
            navigate(createPageUrl("MapaAvistamientos"));
        } catch (error) {
            toast.error(error.message);
        } finally {
            setIsLoading(false);
        }
    };

    const fields = [
        {
            type: "row",
            fields: [
                {
                    name: "nombre",
                    label: "Nombre",
                    placeholder: "Tu nombre",
                    required: true,
                    value: formData.nombre,
                    onChange: handleChange
                },
                {
                    name: "apellido",
                    label: "Apellido",
                    placeholder: "Tu apellido",
                    required: true,
                    value: formData.apellido,
                    onChange: handleChange
                }
            ]
        },
        {
            type: "row",
            fields: [
                {
                    name: "dni",
                    label: "DNI",
                    placeholder: "7 a 10 dígitos",
                    required: true,
                    value: formData.dni,
                    onChange: handleChange
                },
                {
                    name: "edad",
                    label: "Edad",
                    type: "number",
                    placeholder: "Tu edad",
                    required: false,
                    value: formData.edad,
                    onChange: handleChange
                }
            ]
        },
        {
            name: "direccion",
            label: "Dirección",
            placeholder: "Tu dirección",
            required: false,
            value: formData.direccion,
            onChange: handleChange
        },
        {
            type: "row",
            fields: [
                {
                    name: "email",
                    label: "Email",
                    type: "email",
                    placeholder: "tu@email.com (importante para login)",
                    required: true,
                    value: formData.email,
                    onChange: handleChange
                },
                {
                    name: "telefono",
                    label: "Teléfono",
                    placeholder: "Opcional",
                    required: false,
                    value: formData.telefono,
                    onChange: handleChange
                }
            ]
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
