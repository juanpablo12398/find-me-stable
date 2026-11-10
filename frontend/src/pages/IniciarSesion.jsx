import React from "react";
import { LogIn } from "lucide-react";
import AuthForm from "@/components/forms/AuthForm";
import { useIniciarSesion } from "@/hooks/useIniciarSesion";

export default function IniciarSesion() {
    const { fields, handleSubmit, isLoading } = useIniciarSesion();

    return (
        <AuthForm
            title="Iniciar Sesión"
            subtitle="Ingresa tus datos para continuar"
            fields={fields}
            submitLabel="Iniciar Sesión"
            onSubmit={handleSubmit}
            isLoading={isLoading}
            alternateLink="Registro"
            alternateLinkText="No tengo cuenta, quiero registrarme"
            icon={LogIn}
        />
    );
}
