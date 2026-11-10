import React from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Label } from "@/components/ui/label";
import { Slider } from "@/components/ui/slider";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { FILTER_TYPES } from "./MapConstants";
import { getPersonaColor } from "./MapUtils";

export default function FilterConfigPanel({
    showFilters,
    filtroActivo,
    radioCenter,
    radioKm,
    setRadioKm,
    personaSeleccionada,
    setPersonaSeleccionada,
    desaparecidos
}) {
    return (
        <AnimatePresence>
            {showFilters && filtroActivo === FILTER_TYPES.RADIO && radioCenter && (
                <motion.div
                    initial={{ opacity: 0, y: -10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -10 }}
                    className="absolute top-24 left-20 z-[1000] bg-white/95 backdrop-blur-sm rounded-xl shadow-lg border border-slate-200/50 p-4 w-64"
                >
                    <Label className="text-sm font-medium text-slate-700">Radio: {radioKm} km</Label>
                    <Slider
                        value={[radioKm]}
                        onValueChange={(val) => setRadioKm(val[0])}
                        min={1}
                        max={50}
                        step={1}
                        className="mt-2"
                    />
                </motion.div>
            )}

            {showFilters && (filtroActivo === FILTER_TYPES.DESAPARECIDO || filtroActivo === FILTER_TYPES.RUTA || filtroActivo === FILTER_TYPES.ULTIMA_UBICACION) && (
                <motion.div
                    initial={{ opacity: 0, y: -10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -10 }}
                    className="absolute top-24 left-20 z-[1000] bg-white/95 backdrop-blur-sm rounded-xl shadow-lg border border-slate-200/50 p-4 w-72"
                >
                    <Label className="text-sm font-medium text-slate-700 mb-2 block">
                        {filtroActivo === FILTER_TYPES.RUTA ? "Trazar ruta de:" : filtroActivo === FILTER_TYPES.ULTIMA_UBICACION ? "Última ubicación de:" : "Seleccionar persona"}
                    </Label>
                    <Select value={personaSeleccionada || ""} onValueChange={setPersonaSeleccionada}>
                        <SelectTrigger className="w-full bg-white">
                            <SelectValue placeholder="Elegir persona..." />
                        </SelectTrigger>
                        <SelectContent className="bg-white border border-slate-200 shadow-lg">
                            {desaparecidos.map((d) => (
                                <SelectItem key={d.id} value={String(d.id)}>
                                    <div className="flex items-center gap-2">
                                        <div
                                            className="w-3 h-3 rounded-full"
                                            style={{ backgroundColor: getPersonaColor(d.id) }}
                                        />
                                        {d.nombre} {d.apellido}
                                    </div>
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                </motion.div>
            )}
        </AnimatePresence>
    );
}
