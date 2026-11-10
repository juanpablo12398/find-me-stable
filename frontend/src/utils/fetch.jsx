/**
 * Wrapper de fetch con autenticación automática (cookies)
 * @param {string} url - URL del endpoint
 * @param {object} options - Opciones de fetch
 * @returns {Promise<Response>}
 */
export async function fetchWithAuth(url, options = {}) {
  const isFormData = options.body instanceof FormData;
  const baseHeaders = isFormData ? {} : { "Content-Type": "application/json" };

  return fetch(url, {
    credentials: "include",
    ...options,
    headers: {
      ...baseHeaders,
      ...(options.headers || {}),
    },
  });
}
