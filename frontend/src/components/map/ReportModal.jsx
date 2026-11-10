import React from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import {
    MapPin,
    Calendar,
    X,
    RefreshCw,
    Send,
    Upload,
    Globe,
    Lock
} from "lucide-react";
import { getPersonaColor } from "./MapUtils";
import { useReportModal } from "@/hooks/useReportModal";

export default function ReportModal({
    show,
    onClose,
    selectedLatLng,
    desaparecidos,
    user,
    onSuccess
}) {
    const {
        reportForm,
        setReportForm,
        isSubmitting,
        reportFotoPreview,
        reportFotoFile,
        reportFileInputRef,
        handleClose,
        handleFotoChange,
        handleRemoveFoto,
        handleSubmitReport,
        togglePublico,
    } = useReportModal({ selectedLatLng, user, onSuccess, onClose });

    if (!show) return null;

    return (
        <div
            className="fixed inset-0 flex items-center justify-center"
            style={{ zIndex: 2000 }}
        >
            <div
                className="absolute inset-0 bg-black/50 backdrop-blur-sm"
                onClick={handleClose}
            />

            <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 p-6 space-y-4">
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                        <MapPin className="w-5 h-5 text-[#1e3a5f]" />
                        <h2 className="text-lg font-semibold text-slate-800">Reportar Avistamiento</h2>
                    </div>
                    <button
                        type="button"
                        onClick={handleClose}
                        className="w-8 h-8 rounded-full flex items-center justify-center hover:bg-slate-100 transition-colors"
                    >
                        <X className="w-4 h-4 text-slate-500" />
                    </button>
                </div>

                <p className="text-sm text-slate-500">Completa los datos del avistamiento</p>

                <form onSubmit={handleSubmitReport} className="space-y-4">
                    {selectedLatLng && (
                        <div className="text-sm text-slate-500 bg-slate-50 p-3 rounded-lg">
                            📍 Ubicación: {selectedLatLng.lat.toFixed(6)}, {selectedLatLng.lng.toFixed(6)}
                        </div>
                    )}

                    <div className="space-y-2">
                        <Label>Persona extraviada *</Label>
                        <Select
                            value={reportForm.desaparecidoId}
                            onValueChange={(val) => setReportForm(prev => ({ ...prev, desaparecidoId: val }))}
                        >
                            <SelectTrigger className="bg-white">
                                <SelectValue placeholder="Seleccionar persona..." />
                            </SelectTrigger>
                            <SelectContent className="bg-white border border-slate-200 shadow-lg" style={{ zIndex: 2100 }}>
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
                    </div>

                    <div className="space-y-2">
                        <Label>Fecha y hora del avistamiento</Label>
                        <div className="relative">
                            <Input
                                type="datetime-local"
                                value={reportForm.fechaAvistamiento}
                                onChange={(e) => setReportForm(prev => ({ ...prev, fechaAvistamiento: e.target.value }))}
                                className="h-10 rounded-lg pl-10"
                            />
                            <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                        </div>
                        <p className="text-xs text-slate-400">Si no se especifica, se usa la fecha actual</p>
                    </div>

                    <div className="space-y-2">
                        <Label>Descripción * (mín. 20 caracteres)</Label>
                        <Textarea
                            value={reportForm.descripcion}
                            onChange={(e) => setReportForm(prev => ({ ...prev, descripcion: e.target.value }))}
                            placeholder="Describe las circunstancias del avistamiento..."
                            rows={3}
                        />
                        <p className="text-xs text-slate-500">{reportForm.descripcion.length}/20 caracteres mínimos</p>
                    </div>

                    <div className="space-y-2">
                        <Label>Foto (opcional)</Label>
                        {reportFotoPreview ? (
                            <div className="relative rounded-xl overflow-hidden border-2 border-slate-200">
                                <img
                                    src={reportFotoPreview}
                                    alt="Preview"
                                    className="w-full h-36 object-cover"
                                />
                                <button
                                    type="button"
                                    onClick={handleRemoveFoto}
                                    className="absolute top-2 right-2 w-7 h-7 rounded-full bg-white/90 border border-slate-200 shadow flex items-center justify-center hover:bg-red-50 hover:border-red-300 transition-colors"
                                >
                                    <X className="w-3.5 h-3.5 text-slate-500 hover:text-red-500" />
                                </button>
                                <div className="absolute bottom-0 left-0 right-0 px-3 py-1.5 bg-gradient-to-t from-black/40 to-transparent">
                                    <p className="text-white text-xs truncate">{reportFotoFile?.name}</p>
                                </div>
                            </div>
                        ) : (
                            <div
                                onClick={() => reportFileInputRef.current?.click()}
                                className="flex flex-col items-center justify-center gap-2 h-24 rounded-xl border-2 border-dashed border-slate-300 bg-slate-50 hover:bg-slate-100 hover:border-[#1e3a5f]/50 cursor-pointer transition-all group"
                            >
                                <div className="w-8 h-8 rounded-full bg-slate-200 group-hover:bg-[#1e3a5f]/10 flex items-center justify-center transition-colors">
                                    <Upload className="w-4 h-4 text-slate-500 group-hover:text-[#1e3a5f] transition-colors" />
                                </div>
                                <p className="text-xs text-slate-500 group-hover:text-[#1e3a5f] transition-colors">
                                    Click para subir una foto · JPG, PNG, WEBP — máx. 5MB
                                </p>
                            </div>
                        )}
                        <input
                            ref={reportFileInputRef}
                            type="file"
                            accept="image/*"
                            className="hidden"
                            onChange={handleFotoChange}
                        />
                    </div>

                    <button
                        type="button"
                        onClick={togglePublico}
                        className={`
                            w-full flex items-center justify-between px-4 py-3 rounded-xl border-2 transition-all duration-200
                            ${reportForm.publico
                                ? "bg-[#1e3a5f]/5 border-[#1e3a5f]/30 text-[#1e3a5f]"
                                : "bg-slate-50 border-slate-200 text-slate-500"
                            }
                        `}
                    >
                        <div className="flex items-center gap-2.5">
                            {reportForm.publico
                                ? <Globe className="w-4 h-4" />
                                : <Lock className="w-4 h-4" />
                            }
                            <span className="text-sm font-medium">
                                {reportForm.publico ? "Reporte público" : "Reporte privado"}
                            </span>
                            <span className={`text-xs ${reportForm.publico ? "text-[#1e3a5f]/60" : "text-slate-400"}`}>
                                {reportForm.publico ? "· visible para todos" : "· solo vos lo ves"}
                            </span>
                        </div>
                        <div className={`
                            relative w-10 h-5 rounded-full transition-all duration-200 flex-shrink-0
                            ${reportForm.publico ? "bg-[#1e3a5f]" : "bg-slate-300"}
                        `}>
                            <div className={`
                                absolute top-0.5 w-4 h-4 rounded-full bg-white shadow transition-all duration-200
                                ${reportForm.publico ? "left-5" : "left-0.5"}
                            `} />
                        </div>
                    </button>

                    <div className="flex gap-2 pt-2">
                        <Button
                            type="button"
                            variant="outline"
                            onClick={handleClose}
                            className="flex-1"
                        >
                            Cancelar
                        </Button>
                        <Button
                            type="submit"
                            disabled={isSubmitting}
                            className="flex-1 bg-[#1e3a5f] hover:bg-[#2d4a6f]"
                        >
                            {isSubmitting ? (
                                <RefreshCw className="w-4 h-4 animate-spin mr-2" />
                            ) : (
                                <Send className="w-4 h-4 mr-2" />
                            )}
                            Enviar reporte
                        </Button>
                    </div>
                </form>
            </div>
        </div>
    );
}
