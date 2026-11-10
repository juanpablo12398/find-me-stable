import React from "react";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { MapPin, Calendar, Phone, Eye, User } from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";
import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import { createPageUrl } from "@/utils";
import FotoProtegida from "@/components/ui/FotoProtegida";

export default function ExtraviadoCard({ persona, index }) {
    const fotoUrl = persona.foto_url || persona.fotoUrl || persona.foto || persona.imagenUrl;
    const fotoBloqueada = persona.fotoBloqueada === true;

    const estadoColors = {
        buscando: "bg-[#2d5a87] text-white border-[#2d5a87]",
        encontrado: "bg-[#2d5a87] text-white border-[#2d5a87]",
        sin_novedades: "bg-[#2d5a87] text-white border-[#2d5a87]"
    };

    const estadoLabels = {
        buscando: "Buscando",
        encontrado: "Encontrado",
        sin_novedades: "Sin novedades"
    };

    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.05 }}
        >
            <Card className="group overflow-hidden bg-white border-0 shadow-lg shadow-slate-200/50 hover:shadow-xl hover:shadow-slate-300/50 transition-all duration-300">
                <div className="flex flex-col sm:flex-row min-h-[200px] p-4 gap-4">
                    {/* Image Section */}
                    <FotoProtegida
                        src={fotoUrl}
                        alt={`${persona.nombre} ${persona.apellido}`}
                        bloqueada={fotoBloqueada}
                        className="w-full sm:w-44 h-44 flex-shrink-0 rounded-xl"
                        imgClassName="group-hover:scale-105 transition-transform duration-500"
                        fallbackIcon={
                            <div className="flex flex-col items-center">
                                <User className="w-16 h-16 text-slate-300" />
                                <span className="text-xs text-slate-400 mt-2">Sin foto</span>
                            </div>
                        }
                    />

                    {/* Content Section */}
                    <div className="flex-1 min-w-0">
                        <div className="flex flex-col h-full">
                            {/* Header */}
                            <div className="mb-3">
                                <h3 className="text-xl font-bold text-slate-800 group-hover:text-[#1e3a5f] transition-colors truncate">
                                    {persona.nombre} {persona.apellido}
                                </h3>
                                <div className="flex items-center gap-2 mt-1">
                                    {persona.dni && (
                                        <p className="text-sm text-slate-500">DNI: {persona.dni}</p>
                                    )}
                                    <Badge className={`${estadoColors[persona.estado || "buscando"]} border font-medium text-xs`}>
                                        {estadoLabels[persona.estado || "buscando"]}
                                    </Badge>
                                </div>
                            </div>

                            {/* Description */}
                            <p className="text-slate-600 text-sm line-clamp-2 mb-4 flex-1 break-all overflow-hidden">
                                {persona.descripcion || "Sin descripción disponible"}
                            </p>

                            {/* Meta Info */}
                            <div className="flex flex-wrap gap-4 text-sm text-slate-500 mb-4">
                                {persona.fecha_desaparicion && (
                                    <div className="flex items-center gap-1.5">
                                        <Calendar className="w-4 h-4 text-[#1e3a5f]" />
                                        <span>
                                            {format(new Date(persona.fecha_desaparicion), "d MMM yyyy", { locale: es })}
                                        </span>
                                    </div>
                                )}
                                {persona.ultima_ubicacion && (
                                    <div className="flex items-center gap-1.5">
                                        <MapPin className="w-4 h-4 text-[#ff6b6b]" />
                                        <span className="truncate max-w-[150px]">{persona.ultima_ubicacion}</span>
                                    </div>
                                )}
                            </div>

                            {/* Actions */}
                            <div className="flex gap-2">
                                <Link to={createPageUrl(`MapaAvistamientos?persona=${persona.id}`)} className="flex-1">
                                    <Button
                                        variant="outline"
                                        className="w-full rounded-xl border-slate-200 hover:bg-[#1e3a5f] hover:text-white hover:border-[#1e3a5f] transition-all"
                                    >
                                        <Eye className="w-4 h-4 mr-2" />
                                        Ver en mapa
                                    </Button>
                                </Link>
                                {persona.contacto_familiar && (
                                    <Button
                                        variant="outline"
                                        className="rounded-xl border-slate-200 hover:bg-emerald-500 hover:text-white hover:border-emerald-500 transition-all"
                                        onClick={() => window.open(`tel:${persona.contacto_familiar}`)}
                                    >
                                        <Phone className="w-4 h-4" />
                                    </Button>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </Card>
        </motion.div>
    );
}