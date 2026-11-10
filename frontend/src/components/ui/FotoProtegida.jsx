import React, { useState } from "react";
import { User, Lock } from "lucide-react";

/**
 * Componente de foto que aplica blur cuando fotoBloqueada es true.
 * Muestra un overlay con ícono de candado sobre la imagen blurreada.
 *
 * Props:
 * - src: URL de la foto
 * - alt: texto alternativo
 * - bloqueada: boolean, si true aplica blur
 * - className: clases del contenedor
 * - imgClassName: clases adicionales para el <img>
 * - fallbackIcon: componente a mostrar si no hay foto (default: User icon)
 * - mensaje: texto del overlay cuando está bloqueada (default: "Iniciá sesión para ver")
 */
export default function FotoProtegida({
    src,
    alt = "Foto",
    bloqueada = false,
    className = "w-16 h-16",
    imgClassName = "",
    fallbackIcon,
    mensaje = "Iniciá sesión para ver"
}) {
    const [imageError, setImageError] = useState(false);
    const hasFoto = src && !imageError;

    return (
        <div className={`relative overflow-hidden rounded-lg ${className}`}>
            {hasFoto ? (
                <>
                    <img
                        src={src}
                        alt={alt}
                        className={`w-full h-full object-cover ${bloqueada ? "blur-lg scale-110" : ""} transition-all duration-300 ${imgClassName}`}
                        onError={() => setImageError(true)}
                    />
                    {bloqueada && (
                        <div className="absolute inset-0 bg-slate-900/30 flex flex-col items-center justify-center">
                            <Lock className="w-5 h-5 text-white/90 mb-1" />
                            <span className="text-[10px] text-white/80 text-center px-1 leading-tight">
                                {mensaje}
                            </span>
                        </div>
                    )}
                </>
            ) : (
                <div className="w-full h-full flex flex-col items-center justify-center bg-gradient-to-br from-slate-100 to-slate-200 border-2 border-slate-200">
                    {fallbackIcon || <User className="w-8 h-8 text-slate-400" />}
                </div>
            )}
        </div>
    );
}