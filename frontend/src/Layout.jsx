import React, { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import { MapPin, List, LogIn, UserPlus, Menu, X, Heart, LogOut } from "lucide-react";
import { Button } from "@/components/ui/button";
import { motion, AnimatePresence } from "framer-motion";
import { createPageUrl } from "@/utils";
import { useAuth } from "@/hooks/useAuth.jsx";

export default function Layout({ children, currentPageName }) {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [scrolled, setScrolled] = useState(false);
    const { user, isAuthenticated, logout } = useAuth();

    useEffect(() => {
        const handleScroll = () => {
            setScrolled(window.scrollY > 20);
        };
        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    const navItems = isAuthenticated
        ? [
            { name: "Mapa", page: "MapaAvistamientos", icon: MapPin },
            { name: "Ver lista", page: "ListaDesaparecidos", icon: List },
        ]
        : [
            { name: "Registrarme", page: "Registro", icon: UserPlus },
            { name: "Ver lista", page: "ListaDesaparecidos", icon: List },
            { name: "Mapa", page: "MapaAvistamientos", icon: MapPin },
            { name: "Iniciar sesion", page: "IniciarSesion", icon: LogIn },
        ];

    const handleLogout = async () => {
        await logout();
        setIsMenuOpen(false);
    };

    const isMapPage = currentPageName === "MapaAvistamientos";

    // Header para pagina de mapa - transparente con elementos flotantes
    if (isMapPage) {
        return (
            <div className="min-h-screen bg-slate-50">
            <a href="#main-content" className="sr-only focus:not-sr-only focus:absolute focus:top-2 focus:left-2 focus:z-[2000] focus:bg-white focus:text-slate-900 focus:px-3 focus:py-2 focus:rounded-md">Saltar al contenido</a>
                <header className="fixed top-0 left-0 right-0 z-[1001] pointer-events-none">
                    <div className="w-full px-4 py-3">
                        <div className="flex items-center justify-between">
                            {/* Logo flotante */}
                            <Link to={createPageUrl("MapaAvistamientos")} className="pointer-events-auto">
                                <div className="flex items-center gap-3 px-4 py-2.5 bg-white/95 backdrop-blur-sm rounded-xl shadow-lg border border-slate-200/50">
                                    <div className="p-1.5 rounded-lg bg-gradient-to-br from-[#1e3a5f] to-[#2d5a87]">
                                        <MapPin className="w-5 h-5 text-white" />
                                    </div>
                                    <div>
                                        <span className="text-lg font-bold tracking-tight text-[#1e3a5f]">
                                            Find Me
                                        </span>
                                        <p className="text-[10px] font-medium tracking-wider uppercase text-slate-500">
                                            Sistema de Avistamientos
                                        </p>
                                    </div>
                                </div>
                            </Link>

                            {/* Navegacion flotante desktop */}
                            <nav className="hidden md:flex items-center gap-2 pointer-events-auto">
                                <div className="flex items-center gap-1 px-2 py-1.5 bg-white/95 backdrop-blur-sm rounded-xl shadow-lg border border-slate-200/50">
                                    {navItems.map((item) => {
                                        const Icon = item.icon;
                                        const isActive = currentPageName === item.page;
                                        return (
                                            <Link key={item.page} to={createPageUrl(item.page)}>
                                                <Button
                                                    variant="ghost"
                                                    className={`px-4 py-2 rounded-lg font-medium transition-all duration-300
                                                        ${isActive
                                                            ? "bg-[#1e3a5f] text-white"
                                                            : "text-[#1e3a5f] hover:bg-slate-100"
                                                        }`}
                                                >
                                                    <Icon className="w-4 h-4 mr-2" />
                                                    {item.name}
                                                </Button>
                                            </Link>
                                        );
                                    })}
                                    {isAuthenticated && (
                                        <>
                                            <div className="w-px h-6 bg-slate-200 mx-1" />
                                            <Button
                                                variant="ghost"
                                                onClick={handleLogout}
                                                className="px-4 py-2 rounded-lg font-medium text-red-600 hover:bg-red-50 transition-all duration-300"
                                            >
                                                <LogOut className="w-4 h-4 mr-2" />
                                                Salir
                                            </Button>
                                        </>
                                    )}
                                </div>

                                {/* Badge usuario */}
                                {isAuthenticated && user && (
                                    <div className="flex items-center gap-2 px-3 py-2 bg-white/95 backdrop-blur-sm rounded-xl shadow-lg border border-slate-200/50 text-sm">
                                        <div className="w-2 h-2 bg-emerald-500 rounded-full animate-pulse" />
                                        <span className="text-slate-700 font-medium">{user.nombre || user.dni}</span>
                                    </div>
                                )}
                            </nav>

                            {/* Menu movil */}
                            <Button
                                variant="ghost"
                                size="icon"
                                className="md:hidden w-11 h-11 rounded-xl bg-white/95 backdrop-blur-sm shadow-lg border border-slate-200/50 text-[#1e3a5f] pointer-events-auto"
                                onClick={() => setIsMenuOpen(!isMenuOpen)}
                            >
                                {isMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
                            </Button>
                        </div>
                    </div>

                    {/* Menu movil expandido */}
                    <AnimatePresence>
                        {isMenuOpen && (
                            <motion.div
                                initial={{ opacity: 0, y: -10 }}
                                animate={{ opacity: 1, y: 0 }}
                                exit={{ opacity: 0, y: -10 }}
                                className="md:hidden mx-4 mt-2 bg-white/95 backdrop-blur-sm rounded-xl shadow-lg border border-slate-200/50 overflow-hidden pointer-events-auto"
                            >
                                <nav className="p-2 space-y-1">
                                    {navItems.map((item) => {
                                        const Icon = item.icon;
                                        const isActive = currentPageName === item.page;
                                        return (
                                            <Link key={item.page} to={createPageUrl(item.page)} onClick={() => setIsMenuOpen(false)}>
                                                <div className={`flex items-center gap-3 px-4 py-3 rounded-lg transition-all
                                                    ${isActive ? "bg-[#1e3a5f] text-white" : "text-[#1e3a5f] hover:bg-slate-100"}`}>
                                                    <Icon className="w-5 h-5" />
                                                    <span className="font-medium">{item.name}</span>
                                                </div>
                                            </Link>
                                        );
                                    })}
                                    {isAuthenticated && (
                                        <button onClick={handleLogout} className="w-full flex items-center gap-3 px-4 py-3 rounded-lg text-red-600 hover:bg-red-50">
                                            <LogOut className="w-5 h-5" />
                                            <span className="font-medium">Cerrar sesion</span>
                                        </button>
                                    )}
                                </nav>
                            </motion.div>
                        )}
                    </AnimatePresence>
                </header>

                <main id="main-content">
                    {children}
                </main>
            </div>
        );
    }

    // Header normal para otras paginas
    return (
        <div className="min-h-screen bg-slate-50">
            <a href="#main-content" className="sr-only focus:not-sr-only focus:absolute focus:top-2 focus:left-2 focus:z-[2000] focus:bg-white focus:text-slate-900 focus:px-3 focus:py-2 focus:rounded-md">Saltar al contenido</a>
            <header
                className={`fixed top-0 left-0 right-0 z-50 transition-all duration-500 ${
                    !scrolled
                        ? "bg-white/95 backdrop-blur-md shadow-lg shadow-slate-200/50"
                        : "bg-gradient-to-br from-[#1e3a5f]/95 via-[#2d5a87]/95 to-[#3d6a97]/95 backdrop-blur-md"
                }`}
            >
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center justify-between h-20">
                        <Link to={createPageUrl("MapaAvistamientos")} className="flex items-center gap-3 group">
                            <div className={`p-2.5 rounded-xl transition-all duration-300 ${
                                !scrolled
                                    ? "bg-gradient-to-br from-[#1e3a5f] to-[#2d5a87]"
                                    : "bg-white/20 backdrop-blur-sm"
                            }`}>
                                <MapPin className="w-6 h-6 text-white" />
                            </div>
                            <div>
                                <span className={`text-2xl font-bold tracking-tight transition-colors ${
                                    !scrolled ? "text-[#1e3a5f]" : "text-white"
                                }`}>
                                    Find Me
                                </span>
                                <p className={`text-xs font-medium tracking-wider uppercase transition-colors ${
                                    !scrolled ? "text-slate-500" : "text-white/80"
                                }`}>
                                    Sistema de Avistamientos
                                </p>
                            </div>
                        </Link>

                        <nav className="hidden md:flex items-center gap-2">
                            {navItems.map((item) => {
                                const Icon = item.icon;
                                const isActive = currentPageName === item.page;
                                return (
                                    <Link key={item.page} to={createPageUrl(item.page)}>
                                        <Button
                                            variant={isActive ? "default" : "ghost"}
                                            className={`relative px-5 py-2.5 rounded-full font-medium transition-all duration-300
                                                ${isActive
                                                    ? "bg-white text-[#1e3a5f] hover:bg-white/90 shadow-lg shadow-white/25"
                                                    : !scrolled
                                                        ? "text-slate-600 hover:text-[#1e3a5f] hover:bg-slate-100"
                                                        : "text-white/90 hover:text-white hover:bg-white/10"
                                                }`}
                                        >
                                            <Icon className="w-4 h-4 mr-2" />
                                            {item.name}
                                        </Button>
                                    </Link>
                                );
                            })}

                            {isAuthenticated && (
                                <Button
                                    variant="ghost"
                                    onClick={handleLogout}
                                    className={`px-5 py-2.5 rounded-full font-medium transition-all duration-300
                                        ${!scrolled
                                            ? "text-slate-600 hover:text-red-600 hover:bg-red-50"
                                            : "text-white/90 hover:text-white hover:bg-white/10"}`}
                                >
                                    <LogOut className="w-4 h-4 mr-2" />
                                    Cerrar sesion
                                </Button>
                            )}
                        </nav>

                        {isAuthenticated && user && (
                            <div className={`hidden lg:flex items-center gap-2 px-3 py-1.5 rounded-full text-sm ${
                                !scrolled ? "bg-emerald-50 text-emerald-700" : "bg-white/20 text-white"
                            }`}>
                                <div className="w-2 h-2 bg-emerald-500 rounded-full animate-pulse" />
                                <span>{user.nombre || user.dni}</span>
                            </div>
                        )}

                        <Button
                            variant="ghost"
                            size="icon"
                            className={`md:hidden rounded-full ${!scrolled ? "text-[#1e3a5f]" : "text-white"}`}
                            onClick={() => setIsMenuOpen(!isMenuOpen)}
                        >
                            {isMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
                        </Button>
                    </div>
                </div>

                <AnimatePresence>
                    {isMenuOpen && (
                        <motion.div
                            initial={{ opacity: 0, height: 0 }}
                            animate={{ opacity: 1, height: "auto" }}
                            exit={{ opacity: 0, height: 0 }}
                            className="md:hidden bg-white border-t border-slate-100 shadow-xl"
                        >
                            <nav className="px-4 py-4 space-y-2">
                                {navItems.map((item) => {
                                    const Icon = item.icon;
                                    const isActive = currentPageName === item.page;
                                    return (
                                        <Link key={item.page} to={createPageUrl(item.page)} onClick={() => setIsMenuOpen(false)}>
                                            <div className={`flex items-center gap-3 px-4 py-3 rounded-xl transition-all
                                                ${isActive ? "bg-[#1e3a5f] text-white" : "text-slate-600 hover:bg-slate-100"}`}>
                                                <Icon className="w-5 h-5" />
                                                <span className="font-medium">{item.name}</span>
                                            </div>
                                        </Link>
                                    );
                                })}
                                {isAuthenticated && (
                                    <button onClick={handleLogout} className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-red-600 hover:bg-red-50">
                                        <LogOut className="w-5 h-5" />
                                        <span className="font-medium">Cerrar sesion</span>
                                    </button>
                                )}
                            </nav>
                        </motion.div>
                    )}
                </AnimatePresence>
            </header>

            <div className="fixed top-0 left-0 right-0 h-80 bg-gradient-to-br from-[#1e3a5f] via-[#2d5a87] to-[#3d6a97] -z-10" />

            <main id="main-content" className="pt-32">
                {children}
            </main>

            <footer className="bg-[#1e3a5f] text-white mt-20">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
                    <div className="flex flex-col md:flex-row items-center justify-between gap-6">
                        <div className="flex items-center gap-3">
                            <div className="p-2 bg-white/10 rounded-lg">
                                <Heart className="w-5 h-5 text-[#ff6b6b]" />
                            </div>
                            <span className="text-sm text-white/70">Juntos podemos encontrarlos</span>
                        </div>
                        <p className="text-sm text-white/50">(c) 2025 Find Me - Proyecto UTN. Todos los derechos reservados.</p>
                    </div>
                </div>
            </footer>
        </div>
    );
}

