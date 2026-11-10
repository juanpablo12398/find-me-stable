import React from "react";
import { Badge } from "@/components/ui/badge";
import { Calendar, User } from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";
import FotoProtegida from "@/components/ui/FotoProtegida";

export default function MapPopupContent({ avistamiento, desaparecido }) {
    const fotoPersona = avistamiento.desaparecidoFoto
        || desaparecido?.fotoUrl || desaparecido?.foto
        || desaparecido?.imagenUrl || desaparecido?.imagen;

    const fotoDesaparecidoBloqueada = avistamiento.desaparecidoFotoBloqueada === true;
    const isVerified = avistamiento.verificado;

    const nombre = desaparecido?.nombre || avistamiento.desaparecidoNombre || "Persona";
    const apellido = desaparecido?.apellido || avistamiento.desaparecidoApellido || "";

    return (
        <div className="p-2">
            <div className="flex items-start gap-3 mb-3">
                <FotoProtegida
                    src={fotoPersona}
                    alt={nombre}
                    bloqueada={fotoDesaparecidoBloqueada}
                    className="w-16 h-16"
                    imgClassName="border-2 border-slate-200"
                    fallbackIcon={<User className="w-8 h-8 text-slate-400" />}
                    mensaje="Iniciá sesión"
                />
                <div className="flex-1 min-w-0">
                    <h4 className="font-semibold text-slate-800">
                        {nombre} {apellido}
                    </h4>
                    <Badge variant={isVerified ? "default" : "secondary"} className="mt-1">
                        {isVerified ? "Verificado" : "Pendiente"}
                    </Badge>
                </div>
            </div>

            <div className="space-y-2 text-sm">
                {avistamiento.descripcion && (
                    <p className="text-slate-600 break-words whitespace-pre-wrap">
                        {avistamiento.descripcion}
                    </p>
                )}
                <div className="flex items-center gap-2 text-slate-500">
                    <Calendar className="w-4 h-4" />
                    <span>
                        {avistamiento.fechaFormateada
                            || (avistamiento.fechaAvistamiento || avistamiento.fecha_avistamiento || avistamiento.fechaHora
                                ? format(
                                    new Date(avistamiento.fechaAvistamiento || avistamiento.fecha_avistamiento || avistamiento.fechaHora),
                                    "d MMM yyyy, HH:mm",
                                    { locale: es }
                                )
                                : "Fecha no especificada"
                            )
                        }
                    </span>
                </div>
            </div>
        </div>
    );
}