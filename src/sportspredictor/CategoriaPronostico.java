package sportspredictor;

import java.time.*;
import java.util.*;

public class CategoriaPronostico {
    private String codigo;
    private String descripcion;
    private boolean habilitada = true;
    private int puntosPorAcierto;
    private Map<String,String> parametros;

    public CategoriaPronostico(String codigo, String descripcion, int puntos, Map<String,String> parametros) {
        this.codigo=Objects.requireNonNull(codigo); this.descripcion=descripcion;
        this.parametros=new LinkedHashMap<>(Objects.requireNonNull(parametros)); configurar(true,puntos);
    }
    public void configurar(boolean habilitada, int puntos) {
        if(puntos<0) throw new IllegalArgumentException("Puntuación negativa");
        this.habilitada=habilitada; puntosPorAcierto=puntos;
    }
    public int calcularPuntos(boolean acertado) { return acertado?puntosPorAcierto:0; }

    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public boolean getHabilitada() { return habilitada; }
    public int getPuntosPorAcierto() { return puntosPorAcierto; }
    public Map<String,String> getParametros() { return Map.copyOf(parametros); }
}
