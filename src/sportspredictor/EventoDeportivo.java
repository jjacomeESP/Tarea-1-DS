package sportspredictor;

import java.time.*;
import java.util.*;

public class EventoDeportivo {
    private static long siguienteId = 1;
    private long id = siguienteId++;
    private String nombre;
    private String deporte;
    private LocalDateTime inicio;
    private boolean finalizado = false;
    private Map<String, String> resultado = new LinkedHashMap<>();
    private List<CategoriaPronostico> categorias = new ArrayList<>();
    private List<Pronostico> pronosticos = new ArrayList<>();
    private List<Participante> participantes = new ArrayList<>();

    public EventoDeportivo(String nombre, String deporte, LocalDateTime inicio, List<Participante> participantes) {
        if(participantes==null || participantes.size()<2) throw new IllegalArgumentException("Se requieren al menos dos participantes");
        this.nombre=Objects.requireNonNull(nombre); this.deporte=Objects.requireNonNull(deporte);
        this.inicio=Objects.requireNonNull(inicio); this.participantes=new ArrayList<>(participantes);
    }
    public boolean permitePronosticos(LocalDateTime ahora) { return !finalizado && ahora.isBefore(inicio); }
    public boolean tienePronostico(Usuario u, CategoriaPronostico c) {
        return pronosticos.stream().anyMatch(p->p.getUsuario()==u && p.getCategoria()==c);
    }
    public void agregarCategoria(CategoriaPronostico c) {
        if(c==null || categorias.contains(c)) throw new IllegalArgumentException("Categoría nula o repetida");
        categorias.add(c);
    }
    public void agregarPronostico(Pronostico p) {
        if(p.getEvento()!=this || tienePronostico(p.getUsuario(),p.getCategoria())) throw new IllegalArgumentException("Pronóstico ajeno o duplicado");
        pronosticos.add(p);
    }

    public void finalizar() { finalizado=true; }
    public void registrarResultado(Map<String,String> datos) {
        if(!finalizado || !resultado.isEmpty() || datos==null || datos.isEmpty()) throw new IllegalArgumentException("No se puede registrar el resultado");
        resultado=new LinkedHashMap<>(datos);
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDeporte() { return deporte; }
    public LocalDateTime getInicio() { return inicio; }
    public boolean getFinalizado() { return finalizado; }
    public Map<String, String> getResultado() { return Map.copyOf(resultado); }
    public List<CategoriaPronostico> getCategorias() { return List.copyOf(categorias); }
    public List<Pronostico> getPronosticos() { return List.copyOf(pronosticos); }
    public List<Participante> getParticipantes() { return List.copyOf(participantes); }
}
