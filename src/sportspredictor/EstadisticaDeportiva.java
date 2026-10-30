package sportspredictor;

import java.time.*;
import java.util.*;

public class EstadisticaDeportiva {
    private String indicador;
    private double valor;
    private String periodo;
    private LocalDateTime actualizada;

    public EstadisticaDeportiva(String indicador, double valor, String periodo) { this.indicador=indicador; this.periodo=periodo; actualizar(valor); }
    public void actualizar(double valor) {
        if(!Double.isFinite(valor)) throw new IllegalArgumentException("Estadística inválida");
        this.valor=valor; actualizada=LocalDateTime.now();
    }

    public String getIndicador() { return indicador; }
    public double getValor() { return valor; }
    public String getPeriodo() { return periodo; }
    public LocalDateTime getActualizada() { return actualizada; }
}
