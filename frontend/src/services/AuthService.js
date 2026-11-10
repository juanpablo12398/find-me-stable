import { API_ENDPOINTS, ERROR_MAPS } from '../config/constants';
import { fetchWithAuth } from '../utils/fetch';
import { parseProblem, getErrorMessage } from '../utils/errors';

// Estado global simple para auth
let currentUser = null;
const listeners = new Set();

// Referencia al QueryClient para invalidar cache al cambiar sesión
let queryClientRef = null;

function notifyListeners() {
  listeners.forEach(listener => listener(currentUser));
}

function invalidateAllQueries() {
  if (queryClientRef) {
    queryClientRef.invalidateQueries();
  }
}

export const AuthService = {
  setQueryClient(qc) {
    queryClientRef = qc;
  },

  /**
   * Suscribirse a cambios de autenticación
   */
  subscribe(listener) {
    listeners.add(listener);
    return () => listeners.delete(listener);
  },

  /**
   * Obtener usuario actual
   */
  getCurrentUser() {
    return currentUser;
  },

  /**
   * Verificar si está autenticado
   */
  isAuthenticated() {
    return currentUser !== null;
  },

  /**
   * Verificar sesión existente al cargar la app
   */
  async checkAuth() {
    try {
      const resp = await fetchWithAuth(`${API_ENDPOINTS.AUTH}/me`);
      if (resp.ok) {
        const user = await resp.json();
        user.id = String(user.id);
        currentUser = user;
        console.log('✓ Sesión activa encontrada:', user);
        notifyListeners();
        return true;
      }
    } catch (error) {
      console.error('Error checking auth:', error);
    }
    currentUser = null;
    notifyListeners();
    return false;
  },

  /**
   * Iniciar sesión
   */
  async login(dni, email) {
    const resp = await fetchWithAuth(`${API_ENDPOINTS.AUTH}/login`, {
      method: "POST",
      body: JSON.stringify({ dni, email })
    });

    if (!resp.ok) {
      const problem = await parseProblem(resp);
      const msg = getErrorMessage(ERROR_MAPS.AUTH, problem.status, problem.key, problem.detail);
      throw new Error(msg);
    }

    const user = await resp.json();
    user.id = String(user.id);
    currentUser = user;
    console.log('✓ Login exitoso:', user);
    notifyListeners();

    invalidateAllQueries();

    return user;
  },

  /**
   * Cerrar sesión
   */
  async logout() {
    try {
      await fetchWithAuth(`${API_ENDPOINTS.AUTH}/logout`, { method: "POST" });
      console.log('✓ Logout exitoso');
    } catch (error) {
      console.error('Error during logout:', error);
    }
    currentUser = null;
    notifyListeners();

    // Invalidar cache para que los datos se refetcheen como NO_REGISTRADO
    invalidateAllQueries();
  }
};