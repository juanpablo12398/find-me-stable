import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { User } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { getPersonaColor, getDotColor, buildAvatarHaloCSS } from "./MapUtils";
import FotoProtegida from "@/components/ui/FotoProtegida";

export default function PersonasSidebar({ desaparecidos }) {
    const navigate = useNavigate();
    const [listaPersonasAbierta, setListaPersonasAbierta] = useState(true);

    return (
        <div className="absolute right-6 z-[1000] flex flex-col items-end gap-2"
            style={{ bottom: '88px' }}
        >
            {/* Header con toggle */}
            <button
                onClick={() => setListaPersonasAbierta(p => !p)}
                className="flex items-center gap-2 bg-white/95 backdrop-blur-sm rounded-2xl shadow-lg border border-slate-200/50 px-3 py-2 hover:bg-slate-50 transition-all"
            >
                <div className="flex -space-x-2">
                    {[...desaparecidos].slice(0, 3).map((p) => {
                        const color = getPersonaColor(p.id);
                        const dotColor = getDotColor(p.nivelUrgencia);
                        return (
                            <div key={p.id} className="relative w-7 h-7 rounded-full border-2 border-white overflow-hidden flex-shrink-0"
                                style={p.nivelUrgencia ? { boxShadow: buildAvatarHaloCSS(p.nivelUrgencia) } : {}}>
                                <FotoProtegida
                                    src={p.fotoUrl}
                                    alt={p.nombre}
                                    bloqueada={p.fotoBloqueada ?? false}
                                    className="w-full h-full"
                                    imgClassName="w-full h-full object-cover"
                                    fallbackIcon={<User className="w-3 h-3" style={{ color }} />}
                                    mensaje=""
                                />
                                {dotColor && <div className="absolute -top-px -right-px w-2 h-2 rounded-full border border-white" style={{ backgroundColor: dotColor }} />}
                            </div>
                        );
                    })}
                </div>
                <span className="text-xs font-medium text-slate-700">
                    {desaparecidos.length} {desaparecidos.length === 1 ? 'persona' : 'personas'}
                </span>
                <svg
                    width="12" height="12" viewBox="0 0 12 12" fill="none"
                    className="text-slate-400 transition-transform duration-200"
                    style={{ transform: listaPersonasAbierta ? 'rotate(90deg)' : 'rotate(-90deg)' }}
                >
                    <path d="M4.5 2.5L8 6L4.5 9.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
            </button>

            {/* Lista expandible */}
            <AnimatePresence>
                {listaPersonasAbierta && (
                    <motion.div
                        initial={{ opacity: 0, height: 0, y: 8 }}
                        animate={{ opacity: 1, height: 'auto', y: 0 }}
                        exit={{ opacity: 0, height: 0, y: 8 }}
                        transition={{ duration: 0.2, ease: 'easeOut' }}
                        className="overflow-hidden"
                    >
                        <div
                            className="flex flex-col gap-1.5 overflow-y-auto"
                            style={{ maxHeight: 'calc(100vh - 280px)', scrollbarWidth: 'none' }}
                        >
                            {[...desaparecidos].map((persona, idx) => {
                                const dotColor = getDotColor(persona.nivelUrgencia);
                                const color = getPersonaColor(persona.id);

                                return (
                                    <motion.button
                                        key={persona.id}
                                        initial={{ opacity: 0, x: 16 }}
                                        animate={{ opacity: 1, x: 0 }}
                                        transition={{ delay: idx * 0.04 }}
                                        onClick={() => navigate('/ListaDesaparecidos')}
                                        className="flex items-center gap-2.5 bg-white/95 backdrop-blur-sm rounded-2xl shadow-md border border-slate-200/50 px-3 py-2 hover:bg-slate-50 transition-all group w-52 text-left flex-shrink-0"
                                    >
                                        {/* Avatar con halo */}
                                        <div className="relative flex-shrink-0">
                                            <div className="w-9 h-9 rounded-full overflow-hidden border-2 border-white"
                                                style={persona.nivelUrgencia ? { boxShadow: buildAvatarHaloCSS(persona.nivelUrgencia) } : {}}>
                                                <FotoProtegida
                                                    src={persona.fotoUrl}
                                                    alt={persona.nombre}
                                                    bloqueada={persona.fotoBloqueada ?? false}
                                                    className="w-full h-full"
                                                    imgClassName="w-full h-full object-cover"
                                                    fallbackIcon={<User className="w-4 h-4" style={{ color }} />}
                                                    mensaje=""
                                                />
                                            </div>
                                            {dotColor && (
                                                <div className="absolute -top-0.5 -right-0.5 w-2.5 h-2.5 rounded-full border-2 border-white"
                                                    style={{ backgroundColor: dotColor }} />
                                            )}
                                        </div>

                                        {/* Nombre + estado */}
                                        <div className="flex-1 min-w-0">
                                            <p className="text-xs font-semibold text-slate-800 truncate group-hover:text-[#1e3a5f] transition-colors">
                                                {persona.nombre} {persona.apellido}
                                            </p>
                                            <p className="text-[10px] text-slate-400 mt-0.5">
                                                {persona.estado === 'ENCONTRADO' ? '✓ Encontrado' :
                                                 persona.estado === 'SIN_NOVEDADES' ? 'Sin novedades' :
                                                 'Buscando'}
                                            </p>
                                        </div>

                                        <svg width="12" height="12" viewBox="0 0 12 12" fill="none"
                                            className="text-slate-300 group-hover:text-[#1e3a5f] transition-colors flex-shrink-0">
                                            <path d="M4.5 2.5L8 6L4.5 9.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                                        </svg>
                                    </motion.button>
                                );
                            })}
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
}
