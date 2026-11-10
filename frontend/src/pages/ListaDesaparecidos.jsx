import React from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import {
    Search,
    RefreshCw,
    Users,
    Filter,
    Grid3X3,
    List,
    SortAsc
} from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import DesaparecidoCard from "@/components/cards/DesaparecidoCard";
import { useListaDesaparecidos } from "@/hooks/useListaDesaparecidos";

export default function ListaDesaparecidos() {
    const {
        searchTerm,
        viewMode,
        setViewMode,
        sortBy,
        setSortBy,
        filterEstado,
        setFilterEstado,
        page,
        setPage,
        handleSearchChange,
        desaparecidos,
        pageData,
        estadoCounts,
        isLoading,
        isFetching,
        refetch,
        canGoPrev,
        canGoNext,
    } = useListaDesaparecidos();

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <motion.div
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                className="mb-8"
            >
                <div className="flex flex-col lg:flex-row lg:items-end lg:justify-between gap-6">
                    <div>
                        <h1 className="text-4xl font-bold text-slate-800 tracking-tight">
                            Personas Extraviadas
                        </h1>
                        <p className="text-slate-500 mt-2 max-w-xl">
                            Ayudanos a encontrar a quienes mas lo necesitan. Cada avistamiento cuenta.
                        </p>
                    </div>

                    <div className="flex gap-3">
                        <div className="bg-white rounded-2xl shadow-lg shadow-slate-200/50 p-4 text-center min-w-[100px]">
                            <span className="text-3xl font-bold text-[#1e3a5f]">{estadoCounts.total}</span>
                            <p className="text-xs text-slate-500 mt-1">Total</p>
                        </div>
                        <div className="bg-white rounded-2xl shadow-lg shadow-slate-200/50 p-4 text-center min-w-[100px]">
                            <span className="text-3xl font-bold text-amber-500">{estadoCounts.buscando}</span>
                            <p className="text-xs text-slate-500 mt-1">Buscando</p>
                        </div>
                        <div className="bg-white rounded-2xl shadow-lg shadow-slate-200/50 p-4 text-center min-w-[100px]">
                            <span className="text-3xl font-bold text-emerald-500">{estadoCounts.encontrado}</span>
                            <p className="text-xs text-slate-500 mt-1">Encontrados</p>
                        </div>
                    </div>
                </div>
            </motion.div>

            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 }}
                className="bg-white rounded-2xl shadow-lg shadow-slate-200/50 p-4 mb-8"
            >
                <div className="flex flex-col md:flex-row gap-4">
                    <div className="relative flex-1">
                        <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                        <Input
                            placeholder="Buscar por nombre, DNI o descripcion..."
                            value={searchTerm}
                            onChange={handleSearchChange}
                            className="pl-12 h-12 rounded-xl border-slate-200 focus:border-[#1e3a5f] focus:ring-[#1e3a5f]/20"
                        />
                    </div>

                    <div className="flex gap-3">
                        <Select value={filterEstado} onValueChange={setFilterEstado}>
                            <SelectTrigger className="w-40 h-12 rounded-xl border-slate-200">
                                <Filter className="w-4 h-4 mr-2 text-slate-400" />
                                <SelectValue placeholder="Estado" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">Todos</SelectItem>
                                <SelectItem value="BUSCANDO">Buscando</SelectItem>
                                <SelectItem value="ENCONTRADO">Encontrado</SelectItem>
                                <SelectItem value="SIN_NOVEDADES">Sin novedades</SelectItem>
                            </SelectContent>
                        </Select>

                        <Select value={sortBy} onValueChange={setSortBy}>
                            <SelectTrigger className="w-40 h-12 rounded-xl border-slate-200">
                                <SortAsc className="w-4 h-4 mr-2 text-slate-400" />
                                <SelectValue placeholder="Ordenar" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="urgencia">Por urgencia</SelectItem>
                                <SelectItem value="recent">Mas recientes</SelectItem>
                                <SelectItem value="oldest">Mas antiguos</SelectItem>
                                <SelectItem value="name">Por nombre</SelectItem>
                            </SelectContent>
                        </Select>

                        <Button
                            variant="outline"
                            size="icon"
                            onClick={() => refetch()}
                            disabled={isFetching}
                            className="h-12 w-12 rounded-xl border-slate-200 hover:bg-[#1e3a5f] hover:text-white hover:border-[#1e3a5f]"
                            aria-label="Actualizar resultados"
                        >
                            <RefreshCw className={`w-5 h-5 ${isFetching ? "animate-spin" : ""}`} />
                        </Button>

                        <div className="hidden sm:flex items-center bg-slate-100 rounded-xl p-1">
                            <Button
                                variant="ghost"
                                size="icon"
                                onClick={() => setViewMode("grid")}
                                className={`h-10 w-10 rounded-lg ${viewMode === "grid" ? "bg-white shadow-sm" : ""}`}
                                aria-label="Vista en grilla"
                            >
                                <Grid3X3 className="w-4 h-4" />
                            </Button>
                            <Button
                                variant="ghost"
                                size="icon"
                                onClick={() => setViewMode("list")}
                                className={`h-10 w-10 rounded-lg ${viewMode === "list" ? "bg-white shadow-sm" : ""}`}
                                aria-label="Vista en lista"
                            >
                                <List className="w-4 h-4" />
                            </Button>
                        </div>
                    </div>
                </div>
            </motion.div>

            <div className="mb-6 flex items-center justify-between">
                <p className="text-slate-500">
                    Mostrando <span className="font-semibold text-slate-700">{desaparecidos.length}</span> de <span className="font-semibold text-slate-700">{pageData.totalElements ?? 0}</span>
                </p>
                <p className="text-sm text-slate-500">Pagina {page + 1}</p>
            </div>

            {isLoading ? (
                <div className={`grid gap-6 ${viewMode === "grid" ? "md:grid-cols-2" : "grid-cols-1"}`}>
                    {[...Array(4)].map((_, i) => (
                        <div key={i} className="bg-white rounded-2xl p-6 shadow-lg">
                            <div className="flex gap-4">
                                <Skeleton className="w-32 h-32 rounded-xl" />
                                <div className="flex-1 space-y-3">
                                    <Skeleton className="h-6 w-3/4" />
                                    <Skeleton className="h-4 w-1/2" />
                                    <Skeleton className="h-4 w-full" />
                                    <Skeleton className="h-10 w-32" />
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            ) : desaparecidos.length === 0 ? (
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    className="text-center py-16"
                >
                    <div className="w-20 h-20 bg-slate-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <Users className="w-10 h-10 text-slate-400" />
                    </div>
                    <h3 className="text-xl font-semibold text-slate-700 mb-2">
                        No se encontraron resultados
                    </h3>
                    <p className="text-slate-500">
                        Intenta ajustar los filtros o el termino de busqueda
                    </p>
                </motion.div>
            ) : (
                <div className={`grid gap-6 ${viewMode === "grid" ? "md:grid-cols-2" : "grid-cols-1"}`}>
                    <AnimatePresence>
                        {desaparecidos.map((persona, index) => (
                            <DesaparecidoCard
                                key={persona.id}
                                persona={persona}
                                index={index}
                            />
                        ))}
                    </AnimatePresence>
                </div>
            )}

            <div className="flex justify-center gap-2 mt-8">
                <Button
                    onClick={() => setPage((p) => Math.max(0, p - 1))}
                    disabled={!canGoPrev || isFetching}
                    variant="outline"
                >
                    Anterior
                </Button>
                <span className="inline-flex items-center px-4 text-sm text-slate-700">
                    Pagina {page + 1}
                </span>
                <Button
                    onClick={() => setPage((p) => p + 1)}
                    disabled={!canGoNext || isFetching}
                    variant="outline"
                >
                    Siguiente
                </Button>
            </div>
        </div>
    );
}
