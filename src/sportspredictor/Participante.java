package sportspredictor;

import java.time.*;
import java.util.*;

public class Participante {
    private static long siguienteId = 1;
    private long id = siguienteId++;
    private String nombre;
    private String tipo;
    private List<EstadisticaDeportiva> estadisticas = new ArrayList<>();

    public Participante(String nombre, String tipo) {
        if(!Set.of("EQUIPO","JUGADOR").contains(tipo)) throw new IllegalArgumentException("Tipo inválido");
        this.nombre=Objects.requireNonNull(nombre); this.tipo=tipo;
    }
    public void agregarEstadistica(EstadisticaDeportiva estadistica) { estadisticas.add(Objects.requireNonNull(estadistica)); }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public List<EstadisticaDeportiva> getEstadisticas() { return List.copyOf(estadisticas); }
}
