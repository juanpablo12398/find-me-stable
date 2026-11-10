package edu.utn.proyecto.applicacion.dtos;

public class DesaparecidoEstadisticasDTO {
    private long total;
    private long buscando;
    private long encontrado;

    public DesaparecidoEstadisticasDTO(long total, long buscando, long encontrado) {
        this.total = total;
        this.buscando = buscando;
        this.encontrado = encontrado;
    }

    public long getTotal() { return total; }
    public long getBuscando() { return buscando; }
    public long getEncontrado() { return encontrado; }
}
