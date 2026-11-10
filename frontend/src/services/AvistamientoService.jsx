import { API_ENDPOINTS, ERROR_MAPS } from '../config/constants';
import { fetchWithAuth } from '../utils/fetch';
import { parseProblem, getErrorMessage } from '../utils/errors';
import { FileService } from './FileService';

export const AvistamientoService = {
  async getAvistamientosParaMapa() {
    const resp = await fetchWithAuth(`${API_ENDPOINTS.AVISTAMIENTOS}/mapa`);
    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTAMIENTO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }
    return await resp.json();
  },

  async getEnRadio(lat, lng, radioKm) {
    const params = new URLSearchParams({ lat, lng, radioKm });
    const resp = await fetchWithAuth(`${API_ENDPOINTS.AVISTAMIENTOS}/radio?${params}`);
    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTAMIENTO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }
    return await resp.json();
  },

  async getEnArea(latMin, latMax, lngMin, lngMax) {
    const params = new URLSearchParams({ latMin, latMax, lngMin, lngMax });
    const resp = await fetchWithAuth(`${API_ENDPOINTS.AVISTAMIENTOS}/mapa/area?${params}`);
    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTAMIENTO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }
    return await resp.json();
  },

  async contarEnArea(latMin, latMax, lngMin, lngMax) {
    const params = new URLSearchParams({ latMin, latMax, lngMin, lngMax });
    const resp = await fetchWithAuth(`${API_ENDPOINTS.AVISTAMIENTOS}/area/count?${params}`);
    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTAMIENTO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }
    return await resp.json();
  },

  async getEnPoligono(wkt) {
    const resp = await fetchWithAuth(`${API_ENDPOINTS.AVISTAMIENTOS}/poligono?wkt=${encodeURIComponent(wkt)}`, {
      method: 'POST'
    });
    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTAMIENTO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }
    return await resp.json();
  },

  async getPorDesaparecido(desaparecidoId) {
    const resp = await fetchWithAuth(`${API_ENDPOINTS.AVISTAMIENTOS}/desaparecido/${desaparecidoId}`);
    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTAMIENTO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }
    return await resp.json();
  },

  async getUltimaUbicacion(desaparecidoId) {
    const resp = await fetchWithAuth(`${API_ENDPOINTS.AVISTAMIENTOS}/desaparecido/${desaparecidoId}/ultimo`);
    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTAMIENTO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }
    return await resp.json();
  },

  async getPorDesaparecidoEnRadio(desaparecidoId, lat, lng, radioKm) {
    const params = new URLSearchParams({ lat, lng, radioKm });
    const resp = await fetchWithAuth(`${API_ENDPOINTS.AVISTAMIENTOS}/desaparecido/${desaparecidoId}/radio?${params}`);
    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTAMIENTO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }
    return await resp.json();
  },

  async getMasCercanos(lat, lng, limite = 10) {
    const params = new URLSearchParams({ lat, lng, limite });
    const resp = await fetchWithAuth(`${API_ENDPOINTS.AVISTAMIENTOS}/cercanos?${params}`);
    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTAMIENTO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }
    return await resp.json();
  },

  /**
   * Crea un nuevo avistamiento.
   * Si hay fotoFile, sube primero → recibe fileId → manda fotoFileId en el JSON.
   * Los errores del upload ya vienen tipados desde FileService.
   */
  async crear(data, fotoFile = null) {
    let fotoFileId = null;

    if (fotoFile) {
      const tempId = crypto.randomUUID();
      const uploadResult = await FileService.upload(fotoFile, tempId, 'AVISTAMIENTO', 'PHOTO');
      fotoFileId = uploadResult.fileId;
    }

    const body = {
      avistadorId: data.avistadorId,
      desaparecidoId: data.desaparecidoId,
      latitud: data.latitud,
      longitud: data.longitud,
      descripcion: data.descripcion,
      fotoFileId: fotoFileId,
      publico: data.publico,
      fechaAvistamiento: data.fechaAvistamiento || null
    };

    const resp = await fetchWithAuth(API_ENDPOINTS.AVISTAMIENTOS, {
      method: "POST",
      body: JSON.stringify(body)
    });

    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTAMIENTO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }

    return await resp.json();
  },

  async obtenerTodos() {
    const resp = await fetchWithAuth(API_ENDPOINTS.AVISTAMIENTOS);
    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AVISTAMIENTO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }
    return await resp.json();
  }
};
