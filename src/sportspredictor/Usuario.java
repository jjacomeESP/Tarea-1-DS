package sportspredictor;

import java.time.*;
import java.util.*;

public class Usuario {
    private static long siguienteId = 1;
    private long id = siguienteId++;
    private String nombre;
    private String correo;
    private String mensajeria;
    private int saldoDisponible = 0;
    private int puntosAcumulados = 0;
    private List<Pronostico> pronosticos = new ArrayList<>();
    private List<Canje> canjes = new ArrayList<>();

    public Usuario(String nombre, String correo, String mensajeria) {
        this.nombre=Objects.requireNonNull(nombre); this.correo=correo; this.mensajeria=mensajeria;
    }
    public void acreditarPuntos(int puntos) {
        if(puntos<0) throw new IllegalArgumentException("Puntos negativos");
        saldoDisponible+=puntos; puntosAcumulados+=puntos;
    }
    public void ajustarPuntos(int diferencia) { saldoDisponible+=diferencia; puntosAcumulados+=diferencia; }
    public void descontarPuntos(int puntos) {
        if(puntos<=0 || saldoDisponible<puntos) throw new IllegalArgumentException("Saldo insuficiente o costo inválido");
        saldoDisponible-=puntos;
    }
    public void reintegrarPuntos(int puntos) {
        if(puntos<=0) throw new IllegalArgumentException("Reintegro inválido");
        saldoDisponible+=puntos;
    }
    public void agregarPronostico(Pronostico p) {
        if(p.getUsuario()!=this || pronosticos.contains(p)) throw new IllegalArgumentException("Pronóstico ajeno o repetido");
        pronosticos.add(p);
    }
    public void agregarCanje(Canje c) {
        if(c.getUsuario()!=this || canjes.contains(c)) throw new IllegalArgumentException("Canje ajeno o repetido");
        canjes.add(c);
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getMensajeria() { return mensajeria; }
    public int getSaldoDisponible() { return saldoDisponible; }
    public int getPuntosAcumulados() { return puntosAcumulados; }
    public List<Pronostico> getPronosticos() { return List.copyOf(pronosticos); }
    public List<Canje> getCanjes() { return List.copyOf(canjes); }
}
