package sportspredictor;

import java.time.*;
import java.util.*;

public class ServicioCanjes {
    public Canje canjear(Usuario usuario, Premio premio, int costoConfirmado) {
        Objects.requireNonNull(usuario); Objects.requireNonNull(premio);
        boolean disponible=premio.estaDisponible();
        if(!disponible || costoConfirmado!=premio.getCostoPuntos() || usuario.getSaldoDisponible()<costoConfirmado) throw new IllegalArgumentException("Premio agotado, costo cambiado o saldo insuficiente");
        Canje canje=new Canje(usuario,premio);
        usuario.descontarPuntos(canje.getCostoAplicado()); premio.retirarUnidad(); usuario.agregarCanje(canje); return canje;
    }
}
