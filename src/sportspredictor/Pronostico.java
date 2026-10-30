package sportspredictor;

import java.time.*;
import java.util.*;

public class Pronostico {
    private static long siguienteId = 1;
    private long id = siguienteId++;
    private Map<String,String> datos;
    private LocalDateTime fecha = LocalDateTime.now();
    private EstadoPronostico estado = EstadoPronostico.PENDIENTE;
    private EstadoPronostico estadoAnterior;
    private int puntosOtorgados = 0;
    private Usuario usuario;
    private EventoDeportivo evento;
    private CategoriaPronostico categoria;

    public Pronostico(Usuario usuario, EventoDeportivo evento, CategoriaPronostico categoria, Map<String,String> datos) {
        this.usuario=Objects.requireNonNull(usuario); this.evento=Objects.requireNonNull(evento);
        this.categoria=Objects.requireNonNull(categoria); this.datos=new LinkedHashMap<>(Objects.requireNonNull(datos));
    }

    public int aplicarEvaluacion(boolean acertado, int puntos) {
        if(puntos<0 || (!acertado && puntos!=0)) throw new IllegalArgumentException("Puntuación inconsistente");
        int diferencia=puntos-puntosOtorgados; puntosOtorgados=puntos;
        estado=acertado?EstadoPronostico.ACERTADO:EstadoPronostico.FALLIDO;
        estadoAnterior=null; return diferencia;
    }
    public void ponerEnRevision() {
        if(estado!=EstadoPronostico.ACERTADO && estado!=EstadoPronostico.FALLIDO) throw new IllegalArgumentException("No es un pronóstico evaluado");
        estadoAnterior=estado; estado=EstadoPronostico.EN_REVISION;
    }

    public long getId() { return id; }
    public Map<String,String> getDatos() { return Map.copyOf(datos); }
    public LocalDateTime getFecha() { return fecha; }
    public EstadoPronostico getEstado() { return estado; }
    public EstadoPronostico getEstadoAnterior() { return estadoAnterior; }
    public int getPuntosOtorgados() { return puntosOtorgados; }
    public Usuario getUsuario() { return usuario; }
    public EventoDeportivo getEvento() { return evento; }
    public CategoriaPronostico getCategoria() { return categoria; }
}
