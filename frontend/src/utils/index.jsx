/**
 * Utilidades que reemplazan la librería interna de Base44
 */

/**
 * Crea la URL para una página
 * @param {string} pageName - Nombre de la página
 * @returns {string} URL de la página
 */
export function createPageUrl(pageName) {
  // Las rutas coinciden con los nombres de página directamente
  return `/${pageName}`;
}

/**
 * Formatea una fecha para mostrar
 * @param {string|Date} date - Fecha a formatear
 * @param {string} format - Formato deseado
 * @returns {string} Fecha formateada
 */
export function formatDate(date, format = "short") {
  if (!date) return "";
  
  const d = new Date(date);
  
  if (format === "short") {
    return d.toLocaleDateString("es-AR", {
      day: "numeric",
      month: "short",
      year: "numeric"
    });
  }
  
  if (format === "long") {
    return d.toLocaleDateString("es-AR", {
      day: "numeric",
      month: "long",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit"
    });
  }
  
  return d.toLocaleDateString("es-AR");
}

/**
 * Trunca un texto a un número máximo de caracteres
 * @param {string} text - Texto a truncar
 * @param {number} maxLength - Longitud máxima
 * @returns {string} Texto truncado
 */
export function truncateText(text, maxLength = 100) {
  if (!text || text.length <= maxLength) return text;
  return text.substring(0, maxLength) + "...";
}

/**
 * Genera un ID único simple
 * @returns {string} ID único
 */
export function generateId() {
  return "id_" + Math.random().toString(36).slice(2, 10) + Date.now().toString(36);
}

/**
 * Debounce function
 * @param {Function} func - Función a ejecutar
 * @param {number} wait - Tiempo de espera en ms
 * @returns {Function} Función con debounce
 */
export function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}
