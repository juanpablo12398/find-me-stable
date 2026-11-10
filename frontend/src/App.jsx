import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Toaster } from "@/components/ui/sonner";
import { AuthProvider } from "@/hooks/useAuth";

import Layout from "./Layout";
import MapaAvistamientos from "./pages/MapaAvistamientos";
import ListaDesaparecidos from "./pages/ListaDesaparecidos";
import IniciarSesion from "./pages/IniciarSesion";
import Registro from "./pages/Registro";
import RegistrarDesaparecido from "./pages/RegistrarDesaparecido";

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            staleTime: 1000 * 60 * 5, // 5 minutos
            retry: 1,
        },
    },
});

// Wrapper para páginas con Layout
function PageWrapper({ children, pageName }) {
    return (
        <Layout currentPageName={pageName}>
            {children}
        </Layout>
    );
}

export default function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <AuthProvider>
                <Router>
                    <Routes>
                        {/* Ruta principal - Mapa */}
                        <Route 
                            path="/" 
                            element={<Navigate to="/MapaAvistamientos" replace />} 
                        />
                        
                        {/* Mapa de Avistamientos */}
                        <Route 
                            path="/MapaAvistamientos" 
                            element={
                                <PageWrapper pageName="MapaAvistamientos">
                                    <MapaAvistamientos />
                                </PageWrapper>
                            } 
                        />
                        
                        {/* Lista de Desaparecidos */}
                        <Route 
                            path="/ListaDesaparecidos" 
                            element={
                                <PageWrapper pageName="ListaDesaparecidos">
                                    <ListaDesaparecidos />
                                </PageWrapper>
                            } 
                        />
                        
                        {/* Iniciar Sesión */}
                        <Route 
                            path="/IniciarSesion" 
                            element={
                                <PageWrapper pageName="IniciarSesion">
                                    <IniciarSesion />
                                </PageWrapper>
                            } 
                        />
                        
                        {/* Registro de Avistador */}
                        <Route 
                            path="/Registro" 
                            element={
                                <PageWrapper pageName="Registro">
                                    <Registro />
                                </PageWrapper>
                            } 
                        />
                        
                        {/* Registrar Desaparecido */}
                        <Route 
                            path="/RegistrarDesaparecido" 
                            element={
                                <PageWrapper pageName="RegistrarDesaparecido">
                                    <RegistrarDesaparecido />
                                </PageWrapper>
                            } 
                        />
                        
                        {/* Fallback - redirige al mapa */}
                        <Route 
                            path="*" 
                            element={<Navigate to="/MapaAvistamientos" replace />} 
                        />
                    </Routes>
                </Router>
                <Toaster position="top-right" richColors />
            </AuthProvider>
        </QueryClientProvider>
    );
}
