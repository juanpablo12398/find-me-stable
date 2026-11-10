import { clsx } from "clsx";
import { twMerge } from "tailwind-merge";

/**
 * Combina clases de Tailwind de forma segura
 * @param  {...any} inputs - Clases a combinar
 * @returns {string} Clases combinadas
 */
export function cn(...inputs) {
  return twMerge(clsx(...inputs));
}
