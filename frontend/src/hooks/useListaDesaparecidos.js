import React, { useEffect, useRef, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { DesaparecidoService } from "@/services/DesaparecidoService";

export const SORT_MAP = {
    recent: "reciente",
    oldest: "antiguo",
    name: "nombre",
    urgencia: "urgencia"
};

export const PAGE_SIZE = 12;

/**
 * Hook que centraliza toda la lógica de negocio de la página ListaDesaparecidos:
 * búsqueda con debounce, filtros, ordenamiento, paginación y consultas de datos.
 */
export function useListaDesaparecidos() {
    const [searchTerm, setSearchTerm] = useState("");
    const [viewMode, setViewMode] = useState("grid");
    const [sortBy, setSortBy] = useState("urgencia");
    const [filterEstado, setFilterEstado] = useState("all");
    const [page, setPage] = useState(0);

    const [debouncedSearch, setDebouncedSearch] = useState("");
    const debounceRef = useRef(null);

    // Resetear a página 0 cuando cambian los parámetros de búsqueda
    useEffect(() => {
        setPage(0);
    }, [debouncedSearch, filterEstado, sortBy]);

    const handleSearchChange = (e) => {
        const value = e.target.value;
        setSearchTerm(value);
        clearTimeout(debounceRef.current);
        debounceRef.current = setTimeout(() => setDebouncedSearch(value), 350);
    };

    const {
        data: pageData = {
            content: [],
            page: 0,
            size: PAGE_SIZE,
            totalElements: 0,
            totalPages: 0,
            last: true
        },
        isLoading,
        refetch,
        isFetching
    } = useQuery({
        queryKey: ["desaparecidos", debouncedSearch, filterEstado, sortBy, page],
        queryFn: () => DesaparecidoService.obtenerPaginado({
            search: debouncedSearch || undefined,
            estado: filterEstado !== "all" ? filterEstado : undefined,
            sort: SORT_MAP[sortBy] || "urgencia",
            page,
            size: PAGE_SIZE
        })
    });

    const { data: estadoCounts = { total: 0, buscando: 0, encontrado: 0 } } = useQuery({
        queryKey: ["desaparecidos-counts"],
        queryFn: () => DesaparecidoService.getEstadisticas()
    });

    const desaparecidos = pageData.content ?? [];
    const canGoPrev = page > 0;
    const canGoNext = (page + 1) < (pageData.totalPages || 0);

    return {
        // Estado de búsqueda y filtros
        searchTerm,
        debouncedSearch,
        viewMode,
        setViewMode,
        sortBy,
        setSortBy,
        filterEstado,
        setFilterEstado,
        page,
        setPage,
        // Handlers
        handleSearchChange,
        // Datos
        desaparecidos,
        pageData,
        estadoCounts,
        // Estado de carga
        isLoading,
        isFetching,
        refetch,
        // Navegación de páginas
        canGoPrev,
        canGoNext,
    };
}
