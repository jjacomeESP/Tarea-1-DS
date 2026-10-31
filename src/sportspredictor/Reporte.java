package sportspredictor;

import java.time.*;
import java.util.*;

public class Reporte {
    private static long siguienteId = 1;
    private long id = siguienteId++;
    private String descripcion;
    private EstadoReporte estado = EstadoReporte.PENDIENTE;
    private String respuesta = "";
    private String motivoEscalamiento = "";
    private Pronostico pronostico;
    private Canje canjeAfectado;

    public Reporte(Pronostico pronostico, String descripcion, Canje canje) {
        if(descripcion==null || descripcion.isBlank()) throw new IllegalArgumentException("Descripción requerida");
        this.pronostico=Objects.requireNonNull(pronostico); this.descripcion=descripcion; canjeAfectado=canje;
    }
    public void resolver(String respuesta) {
        if(estado==EstadoReporte.RESUELTO || respuesta==null || respuesta.isBlank()) throw new IllegalArgumentException("Respuesta inválida o reporte resuelto");
        this.respuesta=respuesta; estado=EstadoReporte.RESUELTO;
    }
    public void escalar(String motivo) {
        if(estado!=EstadoReporte.PENDIENTE || motivo==null || motivo.isBlank()) throw new IllegalArgumentException("No se puede escalar");
        motivoEscalamiento=motivo; estado=EstadoReporte.ESCALADO;
    }

    public long getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public EstadoReporte getEstado() { return estado; }
    public String getRespuesta() { return respuesta; }
    public String getMotivoEscalamiento() { return motivoEscalamiento; }
    public Pronostico getPronostico() { return pronostico; }
    public Canje getCanjeAfectado() { return canjeAfectado; }
}
