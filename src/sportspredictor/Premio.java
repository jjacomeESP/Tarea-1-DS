package sportspredictor;

import java.time.*;
import java.util.*;

public class Premio {
    private static long siguienteId = 1;
    private long id = siguienteId++;
    private String nombre;
    private int costoPuntos;
    private int existencias;
    private boolean habilitado;

    public Premio(String nombre, int costo, int stock) { this.nombre=Objects.requireNonNull(nombre); configurar(costo,stock,true); }
    public void configurar(int costo, int stock, boolean habilitado) {
        if(costo<=0 || stock<0) throw new IllegalArgumentException("Costo o stock inválido");
        costoPuntos=costo; existencias=stock; this.habilitado=habilitado;
    }
    public boolean estaDisponible() { return habilitado && existencias>0; }
    public void retirarUnidad() {
        if(!estaDisponible()) throw new IllegalArgumentException("Premio no disponible");
        existencias--;
    }
    public void reponerUnidad() { existencias++; }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public int getCostoPuntos() { return costoPuntos; }
    public int getExistencias() { return existencias; }
    public boolean getHabilitado() { return habilitado; }
}
