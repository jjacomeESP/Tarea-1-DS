package sportspredictor;

import java.time.*;
import java.util.*;

public class ServicioReportes {
    private final List<Reporte> reportes=new ArrayList<>();
    private final Notificador notificador;
    public ServicioReportes(Notificador notificador) { this.notificador=Objects.requireNonNull(notificador); }
    public List<Reporte> getReportes() { return List.copyOf(reportes); }
    public Reporte registrar(Usuario usuario, Pronostico pronostico, String descripcion, Canje canje) {
        if(usuario==null || pronostico==null || pronostico.getUsuario()!=usuario || descripcion==null || descripcion.isBlank()) throw new IllegalArgumentException("Reporte inválido o pronóstico ajeno");
        if(pronostico.getEstado()!=EstadoPronostico.ACERTADO && pronostico.getEstado()!=EstadoPronostico.FALLIDO) throw new IllegalArgumentException("Pronóstico sin evaluación o en revisión");
        if(canje!=null && (canje.getUsuario()!=usuario || !usuario.getCanjes().contains(canje) || canje.getAnulado())) throw new IllegalArgumentException("Canje ajeno, inexistente o anulado");
        if(reportes.stream().anyMatch(r->r.getPronostico()==pronostico && r.getEstado()!=EstadoReporte.RESUELTO)) throw new IllegalArgumentException("Ya existe un reporte abierto");
        Reporte r=new Reporte(pronostico,descripcion,canje); reportes.add(r); return r;
    }
    private void validar(Reporte r, EstadoReporte esperado, String texto) {
        if(r==null || !reportes.contains(r) || r.getEstado()!=esperado || texto==null || texto.isBlank()) throw new IllegalArgumentException("Reporte, estado o explicación inválidos");
    }
    public void resolverPorSoporte(Reporte reporte, String respuesta) {
        validar(reporte,EstadoReporte.PENDIENTE,respuesta);
        reporte.resolver(respuesta); notificador.enviar(reporte.getPronostico().getUsuario(),respuesta);
    }
    public void escalar(Reporte reporte, String motivo) {
        validar(reporte,EstadoReporte.PENDIENTE,motivo);
        Pronostico p=reporte.getPronostico();
        if(p.getEstado()!=EstadoPronostico.ACERTADO && p.getEstado()!=EstadoPronostico.FALLIDO) throw new IllegalArgumentException("Pronóstico no evaluado");
        reporte.escalar(motivo); p.ponerEnRevision(); notificador.enviar(p.getUsuario(),"Reporte escalado: "+motivo);
    }
    public void resolverDesdeCalidad(Reporte reporte, boolean acertado, int puntos, boolean anularCanje, String respuesta) {
        validar(reporte,EstadoReporte.ESCALADO,respuesta);
        Pronostico p=reporte.getPronostico(); Canje c=reporte.getCanjeAfectado();
        if(p.getEstado()!=EstadoPronostico.EN_REVISION || puntos<0 || (!acertado && puntos!=0)) throw new IllegalArgumentException("Evaluación corregida inválida");
        if(anularCanje && (c==null || c.getAnulado() || c.getUsuario()!=p.getUsuario())) throw new IllegalArgumentException("No procede anular el canje");
        int diferencia=p.aplicarEvaluacion(acertado,puntos); p.getUsuario().ajustarPuntos(diferencia);
        if(anularCanje) { c.anular(); c.getUsuario().reintegrarPuntos(c.getCostoAplicado()); c.getPremio().reponerUnidad(); }
        reporte.resolver(respuesta); notificador.enviar(p.getUsuario(),respuesta+"; ajuste: "+diferencia);
    }
}
