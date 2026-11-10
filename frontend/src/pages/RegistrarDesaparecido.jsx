import React from "react";
import { UserPlus2, Upload, X } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { motion } from "framer-motion";
import { useRegistrarDesaparecido } from "@/hooks/useRegistrarDesaparecido";

export default function RegistrarDesaparecido() {
  const {
    isAuthenticated,
    formData,
    fotoPreview,
    fotoFile,
    isLoading,
    fileInputRef,
    handleChange,
    handleFotoChange,
    handleRemoveFoto,
    handleSubmit,
  } = useRegistrarDesaparecido();

  if (!isAuthenticated) return null;

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
      >
        <Card className="p-8 bg-white shadow-xl shadow-slate-200/50 rounded-2xl">
          <div className="text-center mb-8">
            <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-br from-[#1e3a5f] to-[#2d5a87] mb-4">
              <UserPlus2 className="w-8 h-8 text-white" />
            </div>
            <h1 className="text-2xl font-bold text-slate-800">
              Registrar Persona Extraviada
            </h1>
            <p className="text-slate-500 mt-2">
              Completa los datos para dar de alta un caso
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="nombre">Nombre *</Label>
                <Input
                  id="nombre"
                  name="nombre"
                  value={formData.nombre}
                  onChange={handleChange}
                  placeholder="Nombre"
                  required
                  className="h-12 rounded-xl"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="apellido">Apellido *</Label>
                <Input
                  id="apellido"
                  name="apellido"
                  value={formData.apellido}
                  onChange={handleChange}
                  placeholder="Apellido"
                  required
                  className="h-12 rounded-xl"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="edad">Edad</Label>
                <Input
                  id="edad"
                  name="edad"
                  type="number"
                  min="0"
                  value={formData.edad}
                  onChange={handleChange}
                  placeholder="Edad"
                  className="h-12 rounded-xl"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="dni">DNI *</Label>
                <Input
                  id="dni"
                  name="dni"
                  value={formData.dni}
                  onChange={handleChange}
                  placeholder="DNI"
                  inputMode="numeric"
                  required
                  className="h-12 rounded-xl"
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="descripcion">Descripción *</Label>
              <Textarea
                id="descripcion"
                name="descripcion"
                value={formData.descripcion}
                onChange={handleChange}
                placeholder="Descripción física, contexto, etc."
                required
                className="min-h-[120px] rounded-xl"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="fechaDesaparicion">Fecha de desaparición</Label>
              <Input
                id="fechaDesaparicion"
                name="fechaDesaparicion"
                type="datetime-local"
                value={formData.fechaDesaparicion}
                onChange={handleChange}
                className="h-12 rounded-xl"
              />
            </div>

            <div className="space-y-3 p-4 rounded-xl border border-slate-200 bg-slate-50">
              <div>
                <p className="text-sm font-medium text-slate-700 mb-1">Contacto familiar</p>
                <p className="text-xs text-slate-500 mb-3">
                  Si ingresás celular, recibirá un SMS. Si ingresás email, recibirá notificaciones por correo. Podés completar ambos.
                </p>
              </div>
              <div className="space-y-2">
                <Label htmlFor="contactoFamiliar">Celular del contacto familiar</Label>
                <Input
                  id="contactoFamiliar"
                  name="contactoFamiliar"
                  value={formData.contactoFamiliar}
                  onChange={handleChange}
                  placeholder="Ej: 1154321098 o +541154321098"
                  inputMode="tel"
                  className="h-12 rounded-xl"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="emailContactoFamiliar">Email de contacto familiar <span className="text-slate-400 font-normal">(opcional)</span></Label>
                <Input
                  id="emailContactoFamiliar"
                  name="emailContactoFamiliar"
                  type="email"
                  value={formData.emailContactoFamiliar}
                  onChange={handleChange}
                  placeholder="ejemplo@email.com"
                  className="h-12 rounded-xl"
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="ultimaUbicacion">Última ubicación conocida</Label>
              <Input
                id="ultimaUbicacion"
                name="ultimaUbicacion"
                value={formData.ultimaUbicacion}
                onChange={handleChange}
                placeholder="Dirección, zona o punto de referencia"
                className="h-12 rounded-xl"
              />
            </div>

            <div className="space-y-2">
              <Label>Foto (opcional)</Label>

              {fotoPreview ? (
                <div className="relative rounded-xl overflow-hidden border-2 border-slate-200 bg-slate-50">
                  <img
                    src={fotoPreview}
                    alt="Preview"
                    className="w-full h-48 object-cover"
                  />
                  <button
                    type="button"
                    onClick={handleRemoveFoto}
                    className="absolute top-2 right-2 w-8 h-8 rounded-full bg-white/90 backdrop-blur-sm border border-slate-200 shadow flex items-center justify-center hover:bg-red-50 hover:border-red-300 transition-colors"
                  >
                    <X className="w-4 h-4 text-slate-600 hover:text-red-500" />
                  </button>
                  <div className="absolute bottom-0 left-0 right-0 px-3 py-2 bg-gradient-to-t from-black/40 to-transparent">
                    <p className="text-white text-xs truncate">{fotoFile?.name}</p>
                  </div>
                </div>
              ) : (
                <div
                  onClick={() => fileInputRef.current?.click()}
                  className="flex flex-col items-center justify-center gap-3 h-32 rounded-xl border-2 border-dashed border-slate-300 bg-slate-50 hover:bg-slate-100 hover:border-[#1e3a5f]/50 cursor-pointer transition-all group"
                >
                  <div className="w-10 h-10 rounded-full bg-slate-200 group-hover:bg-[#1e3a5f]/10 flex items-center justify-center transition-colors">
                    <Upload className="w-5 h-5 text-slate-500 group-hover:text-[#1e3a5f] transition-colors" />
                  </div>
                  <div className="text-center">
                    <p className="text-sm font-medium text-slate-600 group-hover:text-[#1e3a5f] transition-colors">
                      Hacé click para subir una foto
                    </p>
                    <p className="text-xs text-slate-400 mt-0.5">
                      JPG, PNG, WEBP — máx. 5MB
                    </p>
                  </div>
                </div>
              )}

              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handleFotoChange}
                className="hidden"
              />
            </div>

            <Button
              type="submit"
              disabled={isLoading}
              className="w-full h-12 rounded-xl bg-gradient-to-r from-[#1e3a5f] to-[#2d5a87] hover:from-[#2d4a6f] hover:to-[#3d6a97] text-white font-medium"
            >
              {isLoading ? (
                <>
                  <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin mr-2" />
                  Registrando...
                </>
              ) : (
                "Registrar Persona"
              )}
            </Button>
          </form>
        </Card>
      </motion.div>
    </div>
  );
}
