import React from "react";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Loader2 } from "lucide-react";
import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import { createPageUrl } from "@/utils";

export default function AuthForm({ 
    title, 
    subtitle,
    fields, 
    submitLabel, 
    onSubmit, 
    isLoading,
    alternateLink,
    alternateLinkText,
    alternateText,
    icon: Icon
}) {
    return (
        <div className="min-h-[calc(100vh-200px)] flex items-center justify-center px-4 py-12">
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="w-full max-w-md"
            >
                <Card className="bg-white/80 backdrop-blur-xl border-0 shadow-2xl shadow-slate-300/30 rounded-3xl overflow-hidden">
                    {/* Header */}
                    <div className="p-8 pb-0 text-center">
                        {Icon && (
                            <div className="mx-auto w-16 h-16 bg-gradient-to-br from-[#1e3a5f] to-[#2d5a87] rounded-2xl flex items-center justify-center mb-6 shadow-lg shadow-[#1e3a5f]/20">
                                <Icon className="w-8 h-8 text-white" />
                            </div>
                        )}
                        <h1 className="text-2xl font-bold text-slate-800">{title}</h1>
                        {subtitle && (
                            <p className="text-slate-500 mt-2">{subtitle}</p>
                        )}
                    </div>

                    {/* Form */}
                    <form onSubmit={onSubmit} className="p-8 space-y-5">
                        {fields.map((field, index) => (
                            <motion.div
                                key={field.name || `field-${index}`}
                                initial={{ opacity: 0, x: -20 }}
                                animate={{ opacity: 1, x: 0 }}
                                transition={{ delay: index * 0.1 }}
                            >
                                {field.type === "row" ? (
                                    <div className="grid grid-cols-2 gap-4">
                                        {field.fields.map((subField) => (
                                            <div key={subField.name} className="space-y-2">
                                                <Label className="text-slate-700 font-medium">
                                                    {subField.label}
                                                    {subField.required && <span className="text-[#ff6b6b] ml-1">*</span>}
                                                </Label>
                                                <Input
                                                    type={subField.type || "text"}
                                                    name={subField.name}
                                                    placeholder={subField.placeholder}
                                                    required={subField.required}
                                                    value={subField.value}
                                                    onChange={subField.onChange}
                                                    className="rounded-xl border-slate-200 focus:border-[#1e3a5f] focus:ring-[#1e3a5f]/20 h-12"
                                                />
                                            </div>
                                        ))}
                                    </div>
                                ) : (
                                    <div className="space-y-2">
                                        <Label className="text-slate-700 font-medium">
                                            {field.label}
                                            {field.required && <span className="text-[#ff6b6b] ml-1">*</span>}
                                        </Label>
                                        <Input
                                            type={field.type || "text"}
                                            name={field.name}
                                            placeholder={field.placeholder}
                                            required={field.required}
                                            value={field.value}
                                            onChange={field.onChange}
                                            className="rounded-xl border-slate-200 focus:border-[#1e3a5f] focus:ring-[#1e3a5f]/20 h-12"
                                        />
                                    </div>
                                )}
                            </motion.div>
                        ))}

                        <motion.div
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: 0.3 }}
                        >
                            <Button
                                type="submit"
                                disabled={isLoading}
                                className="w-full h-12 bg-gradient-to-r from-[#1e3a5f] to-[#2d5a87] hover:from-[#2d4a6f] hover:to-[#3d6a97] text-white rounded-xl font-semibold shadow-lg shadow-[#1e3a5f]/25 transition-all duration-300"
                            >
                                {isLoading ? (
                                    <>
                                        <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                                        Procesando...
                                    </>
                                ) : (
                                    submitLabel
                                )}
                            </Button>
                        </motion.div>

                        {alternateLink && (
                            <div className="pt-4 text-center">
                                <div className="relative">
                                    <div className="absolute inset-0 flex items-center">
                                        <span className="w-full border-t border-slate-200" />
                                    </div>
                                    <div className="relative flex justify-center text-xs uppercase">
                                        <span className="bg-white px-4 text-slate-400">o</span>
                                    </div>
                                </div>
                                <Link to={createPageUrl(alternateLink)}>
                                    <Button
                                        type="button"
                                        variant="outline"
                                        className="w-full mt-4 h-12 rounded-xl border-slate-200 hover:bg-slate-50 font-medium"
                                    >
                                        {alternateLinkText}
                                    </Button>
                                </Link>
                            </div>
                        )}
                    </form>
                </Card>
            </motion.div>
        </div>
    );
}
