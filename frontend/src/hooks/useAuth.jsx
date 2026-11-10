import React, { useState, useEffect, useCallback, useContext, createContext } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { AuthService } from '../services/AuthService';

// Contexto de autenticación
const AuthContext = createContext(null);

/**
 * Provider de autenticación
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(AuthService.getCurrentUser());
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Pasar referencia del QueryClient al AuthService
  // Nota: useQueryClient funciona porque AuthProvider está dentro de QueryClientProvider
  let queryClient = null;
  try {
    queryClient = useQueryClient();
  } catch (e) {
    // Si no hay QueryClientProvider, no pasa nada
  }

  useEffect(() => {
    if (queryClient) {
      AuthService.setQueryClient(queryClient);
    }
  }, [queryClient]);

  useEffect(() => {
    // Suscribirse a cambios de autenticación
    const unsubscribe = AuthService.subscribe((newUser) => {
      setUser(newUser);
      setIsAuthenticated(newUser !== null);
    });

    // Verificar sesión al montar
    const checkSession = async () => {
      setIsLoading(true);
      const hasSession = await AuthService.checkAuth();
      setIsAuthenticated(hasSession);
      setIsLoading(false);
    };

    checkSession();

    return unsubscribe;
  }, []);

  const login = useCallback(async (dni, email) => {
    return await AuthService.login(dni, email);
  }, []);

  const logout = useCallback(async () => {
    await AuthService.logout();
  }, []);

  const value = {
    user,
    isLoading,
    isAuthenticated,
    login,
    logout
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Hook para manejar autenticación en componentes React
 */
export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    // Fallback si no está dentro del provider
    const [user, setUser] = useState(AuthService.getCurrentUser());
    const [isLoading, setIsLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
      const unsubscribe = AuthService.subscribe((newUser) => {
        setUser(newUser);
        setIsAuthenticated(newUser !== null);
      });

      const checkSession = async () => {
        setIsLoading(true);
        const hasSession = await AuthService.checkAuth();
        setIsAuthenticated(hasSession);
        setIsLoading(false);
      };

      checkSession();
      return unsubscribe;
    }, []);

    return {
      user,
      isLoading,
      isAuthenticated,
      login: async (dni, email) => await AuthService.login(dni, email),
      logout: async () => await AuthService.logout()
    };
  }

  return context;
}