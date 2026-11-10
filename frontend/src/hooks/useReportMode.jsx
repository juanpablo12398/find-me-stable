import { useCallback, useState } from "react";
import { toast } from "sonner";

export function useReportMode(isAuthenticated, navigate) {
  const [modoReporte, setModoReporte] = useState(false);
  const [selectedLatLng, setSelectedLatLng] = useState(null);
  const [showReportModal, setShowReportModal] = useState(false);

  const toggleModoReporte = useCallback((onStatusChange) => {
    if (!isAuthenticated) {
      toast.error("Inicia sesion para reportar un avistamiento.");
      navigate("/IniciarSesion");
      return;
    }

    setModoReporte((prev) => {
      const next = !prev;
      if (onStatusChange) {
        onStatusChange(
          next
            ? { text: "Click en el mapa para reportar", color: "text-red-600" }
            : { text: "Modo reporte cancelado", color: "text-slate-600" }
        );
      }
      if (!next) {
        setSelectedLatLng(null);
        setShowReportModal(false);
      }
      return next;
    });
  }, [isAuthenticated, navigate]);

  const openReportModalAt = useCallback((latlng) => {
    setSelectedLatLng(latlng);
    setShowReportModal(true);
  }, []);

  const handleReportSuccess = useCallback((onAfterSuccess) => {
    setShowReportModal(false);
    setModoReporte(false);
    setSelectedLatLng(null);
    if (onAfterSuccess) {
      onAfterSuccess();
    }
  }, []);

  return {
    modoReporte,
    selectedLatLng,
    showReportModal,
    setShowReportModal,
    toggleModoReporte,
    openReportModalAt,
    handleReportSuccess,
  };
}