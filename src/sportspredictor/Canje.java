package sportspredictor;

import java.time.*;
import java.util.*;

public class Canje {
    private static long siguienteId = 1;
    private long id = siguienteId++;
    private LocalDateTime fecha = LocalDateTime.now();
    private int costoAplicado;
    private boolean anulado = false;
    private Usuario usuario;
    private Premio premio;

    public Canje(Usuario usuario, Premio premio) {
        this.usuario=Objects.requireNonNull(usuario); this.premio=Objects.requireNonNull(premio); costoAplicado=premio.getCostoPuntos();
    }
    public void anular() {
        if(anulado) throw new IllegalArgumentException("Canje ya anulado");
        anulado=true;
    }

    public long getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public int getCostoAplicado() { return costoAplicado; }
    public boolean getAnulado() { return anulado; }
    public Usuario getUsuario() { return usuario; }
    public Premio getPremio() { return premio; }
}
