package sportspredictor;

import java.time.*;
import java.util.*;

public class EvaluadorBaloncesto implements EvaluadorPronostico {

    public boolean evaluar(Pronostico p, Map<String,String> r) {
        int a=Integer.parseInt(r.get("local")), b=Integer.parseInt(r.get("visitante"));
        return switch(p.getCategoria().getCodigo()) {
            case "RESULTADO" -> p.getDatos().get("ganador").equals(a>b?"LOCAL":a<b?"VISITANTE":"EMPATE");
            case "MARCADOR_EXACTO" -> Integer.parseInt(p.getDatos().get("local"))==a && Integer.parseInt(p.getDatos().get("visitante"))==b;
            case "RENDIMIENTO_JUGADOR" -> {
                Map<String,String> cfg=p.getCategoria().getParametros();
                String clave="jugador."+cfg.get("jugadorId")+"."+cfg.get("medida");
                yield Integer.parseInt(p.getDatos().get("valor"))==Integer.parseInt(r.get(clave));
            }
            default -> throw new IllegalArgumentException("Categoría sin validar");
        };
    }
}
