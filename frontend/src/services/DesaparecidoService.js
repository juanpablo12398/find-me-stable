import { API_ENDPOINTS, ERROR_MAPS } from '../config/constants';
import { fetchWithAuth } from '../utils/fetch';
import { parseProblem, getErrorMessage } from '../utils/errors';
import { FileService } from './FileService';

function buildDesaparecidosUrl(filtros = {}) {
  const params = new URLSearchParams();
  if (filtros.search) params.append('search', filtros.search);
  if (filtros.estado) params.append('estado', filtros.estado);
  if (filtros.sort) params.append('sort', filtros.sort);
  if (Number.isInteger(filtros.page)) params.append('page', String(filtros.page));
  if (Number.isInteger(filtros.size)) params.append('size', String(filtros.size));

  const query = params.toString();
  return query
    ? `${API_ENDPOINTS.DESAPARECIDOS}?${query}`
    : API_ENDPOINTS.DESAPARECIDOS;
}

async function requestDesaparecidos(url) {
  const resp = await fetchWithAuth(url);

  if (!resp.ok) {
    const problem = await parseProblem(resp);
    const msg = getErrorMessage(ERROR_MAPS.DESAPARECIDO, problem.status, problem.key, problem.detail);
    throw new Error(msg);
  }

  return await resp.json();
}

export const DesaparecidoService = {
  async obtenerTodos(filtros = {}) {
    // Retenido por compatibilidad si es que se sigue invocando accidentalmente.
    // Aunque ya no devuelve listas enteras, llamaremos a estadisticas como fallback o simplemente lanzaremos error.
    console.warn("obtenerTodos is deprecated. Use obtenerPaginado or getEstadisticas.");
    const data = await requestDesaparecidos(buildDesaparecidosUrl(filtros));
    return Array.isArray(data) ? data : (data?.content ?? []);
  },

  async getEstadisticas() {
    const resp = await fetchWithAuth(`${API_ENDPOINTS.DESAPARECIDOS}/estadisticas`);
    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.DESAPARECIDO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }
    return await resp.json();
  },

  async obtenerPaginado(filtros = {}) {
    const data = await requestDesaparecidos(buildDesaparecidosUrl(filtros));

    if (Array.isArray(data)) {
      const size = Number.isInteger(filtros.size) ? filtros.size : data.length;
      const page = Number.isInteger(filtros.page) ? filtros.page : 0;
      return {
        content: data,
        page,
        size,
        totalElements: data.length,
        totalPages: size > 0 ? Math.ceil(data.length / size) : 0,
        last: true
      };
    }

    return {
      content: data?.content ?? [],
      page: data?.page ?? 0,
      size: data?.size ?? (Number.isInteger(filtros.size) ? filtros.size : 0),
      totalElements: data?.totalElements ?? 0,
      totalPages: data?.totalPages ?? 0,
      last: data?.last ?? true
    };
  },

  async obtenerPorId(id) {
    const resp = await fetchWithAuth(`${API_ENDPOINTS.DESAPARECIDOS}/${id}`);

    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.DESAPARECIDO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }

    return await resp.json();
  },

  /**
   * Crear un nuevo desaparecido.
   * Si hay fotoFile, sube primero -> recibe fileId -> lo manda en el JSON.
   * Los errores del upload ya vienen tipados desde FileService.
   */
  async crear(data, fotoFile = null) {
    let fotoFileId = null;

    if (fotoFile) {
      const tempId = crypto.randomUUID();
      const uploadResult = await FileService.upload(fotoFile, tempId, 'DESAPARECIDO', 'PHOTO');
      fotoFileId = uploadResult.fileId;
    }

    const body = { ...data, fotoFileId };

    const resp = await fetchWithAuth(API_ENDPOINTS.DESAPARECIDOS, {
      method: "POST",
      body: JSON.stringify(body)
    });

    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.DESAPARECIDO, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }

    return await resp.json();
  }
};
