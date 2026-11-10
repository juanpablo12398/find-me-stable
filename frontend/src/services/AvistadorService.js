import { API_ENDPOINTS, ERROR_MAPS } from '../config/constants';
import { fetchWithAuth } from '../utils/fetch';
import { parseProblem, getErrorMessage } from '../utils/errors';

export const AvistadorService = {
  /**
   * Registrar un nuevo avistador
   */
  async crear(data) {
    const resp = await fetchWithAuth(API_ENDPOINTS.AVISTADORES, {
      method: "POST",
      body: JSON.stringify(data)
    });

    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTADOR, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }

    return await resp.json();
  },

  /**
   * Obtener todos los avistadores
   */
  async obtenerTodos() {
    const resp = await fetchWithAuth(API_ENDPOINTS.AVISTADORES);
    
    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTADOR, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }

    return await resp.json();
  }
};
