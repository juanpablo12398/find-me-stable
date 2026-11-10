import React, { useState } from "react";
import { X, RotateCcw, Layers } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { filtros, FILTER_TYPES } from "./MapConstants";

export default function FilterBar({
  showFilters,
  setShowFilters,
  filtroActivo,
  activateFilter,
  resetFilters,
}) {
  const [hoveredFilter, setHoveredFilter] = useState(null);

  return (
    <>
      <AnimatePresence>
        {showFilters && (
          <motion.div
            initial={{ x: -60, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            exit={{ x: -60, opacity: 0 }}
            transition={{ type: "spring", damping: 25, stiffness: 300 }}
            className="absolute top-24 left-4 z-[1000] flex flex-col gap-1"
          >
            <motion.button
              onClick={() => setShowFilters(false)}
              className="w-11 h-11 rounded-xl bg-white/95 backdrop-blur-sm shadow-lg border border-slate-200/50 flex items-center justify-center text-slate-500 hover:text-slate-700 hover:bg-slate-50 transition-all"
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              aria-label="Ocultar filtros"
            >
              <X className="w-5 h-5" />
            </motion.button>

            <div className="h-2" />

            {filtros.map((filtro) => {
              const Icon = filtro.icon;
              const isActive = filtroActivo === filtro.id;
              const isHovered = hoveredFilter === filtro.id;

              return (
                <motion.div
                  key={filtro.id}
                  className="relative"
                  onMouseEnter={() => setHoveredFilter(filtro.id)}
                  onMouseLeave={() => setHoveredFilter(null)}
                >
                  <motion.button
                    onClick={() => activateFilter(filtro.id)}
                    className={`
                      w-11 h-11 rounded-xl flex items-center justify-center transition-all shadow-lg border
                      ${
                        isActive
                          ? "bg-[#1e3a5f] text-white border-[#1e3a5f]"
                          : "bg-white/95 backdrop-blur-sm text-slate-600 border-slate-200/50 hover:bg-slate-50 hover:text-slate-800"
                      }
                    `}
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    aria-label={filtro.label}
                  >
                    <Icon className="w-5 h-5" />
                  </motion.button>

                  <AnimatePresence>
                    {isHovered && (
                      <motion.div
                        initial={{ opacity: 0, x: -10, scale: 0.95 }}
                        animate={{ opacity: 1, x: 0, scale: 1 }}
                        exit={{ opacity: 0, x: -10, scale: 0.95 }}
                        transition={{ duration: 0.15 }}
                        className="absolute left-full top-0 ml-2 whitespace-nowrap"
                      >
                        <div
                          className={`
                            px-3 py-2 rounded-xl shadow-lg border
                            ${
                              isActive
                                ? "bg-[#1e3a5f] text-white border-[#1e3a5f]"
                                : "bg-white/95 backdrop-blur-sm text-slate-700 border-slate-200/50"
                            }
                          `}
                        >
                          <div className="font-medium text-sm">{filtro.label}</div>
                          <div className={`text-xs ${isActive ? "text-white/70" : "text-slate-500"}`}>
                            {filtro.description}
                          </div>
                        </div>
                      </motion.div>
                    )}
                  </AnimatePresence>
                </motion.div>
              );
            })}

            <div className="h-2" />

            {filtroActivo !== FILTER_TYPES.TODOS && (
              <motion.div
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                className="relative"
                onMouseEnter={() => setHoveredFilter("reset")}
                onMouseLeave={() => setHoveredFilter(null)}
              >
                <motion.button
                  onClick={resetFilters}
                  className="w-11 h-11 rounded-xl bg-amber-500 text-white shadow-lg border border-amber-500 flex items-center justify-center hover:bg-amber-600 transition-all"
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  aria-label="Limpiar filtros"
                >
                  <RotateCcw className="w-5 h-5" />
                </motion.button>

                <AnimatePresence>
                  {hoveredFilter === "reset" && (
                    <motion.div
                      initial={{ opacity: 0, x: -10 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: -10 }}
                      className="absolute left-full top-0 ml-2 whitespace-nowrap"
                    >
                      <div className="px-3 py-2 rounded-xl bg-amber-500 text-white shadow-lg">
                        <div className="font-medium text-sm">Limpiar filtros</div>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </motion.div>
            )}
          </motion.div>
        )}
      </AnimatePresence>

      <AnimatePresence>
        {!showFilters && (
          <motion.button
            initial={{ x: -60, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            exit={{ x: -60, opacity: 0 }}
            onClick={() => setShowFilters(true)}
            className="absolute top-24 left-4 z-[1000] w-11 h-11 rounded-xl bg-white/95 backdrop-blur-sm shadow-lg border border-slate-200/50 flex items-center justify-center text-slate-600 hover:text-slate-800 hover:bg-slate-50 transition-all"
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            aria-label="Mostrar filtros"
          >
            <Layers className="w-5 h-5" />
          </motion.button>
        )}
      </AnimatePresence>
    </>
  );
}
