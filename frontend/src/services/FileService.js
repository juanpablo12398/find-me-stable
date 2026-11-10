import { API_ENDPOINTS, ERROR_MAPS } from '../config/constants';
import { fetchWithAuth } from '../utils/fetch';
import { parseProblem, getErrorMessage } from '../utils/errors';

export const FileService = {
  /**
   * Sube un archivo al backend.
   * @param {File} file - El archivo a subir
   * @param {string} personId - UUID de la entidad asociada
   * @param {string} entityType - 'DESAPARECIDO' | 'AVISTAMIENTO'
   * @param {string} category - 'PHOTO' | 'DOC' (default: 'PHOTO')
   * @returns {{ fileId: string, downloadUrl: string, mimeType: string, sizeBytes: number }}
   */
  async upload(file, personId, entityType, category = 'PHOTO') {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('personId', personId);
    formData.append('entityType', entityType);
    formData.append('category', category);

    const resp = await fetchWithAuth(`${API_ENDPOINTS.FILES}/upload`, {
      method: 'POST',
      body: formData,
    });

    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.FILE, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }

    return await resp.json();
  },

  /**
   * Genera la URL de descarga a partir de un fileId.
   * @param {string} fileId - UUID del archivo
   * @returns {string|null}
   */
  getUrl(fileId) {
    if (!fileId) return null;
    return `${API_ENDPOINTS.FILES}/${fileId}`;
  }
};