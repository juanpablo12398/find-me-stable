import React from "react";
import { UserPlus } from "lucide-react";
import AuthForm from "@/components/forms/AuthForm";
import { useRegistro } from "@/hooks/useRegistro";

export default function Registro() {
    const { fields, handleSubmit, isLoading } = useRegistro();

    return (
        <AuthForm
            title="Crear mi cuenta"
            subtitle="Regístrate como Avistador para ayudar a encontrar personas"
            fields={fields}
            submitLabel="Registrarme"
            onSubmit={handleSubmit}
            isLoading={isLoading}
            alternateLink="IniciarSesion"
            alternateLinkText="Ya tengo cuenta, iniciar sesión"
            icon={UserPlus}
        />
    );
}
