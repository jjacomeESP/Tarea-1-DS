package sportspredictor;

import java.time.*;
import java.util.*;

public class ValidadorFutbol implements ValidadorPrediccion {
    private boolean numero(String s) {
        try { return s!=null && Integer.parseInt(s)>=0; } catch(NumberFormatException ex) { return false; }
    }
    private boolean marcador(Map<String,String> d) {
        if(d==null || !numero(d.get("local")) || !numero(d.get("visitante"))) return false;
        int a=Integer.parseInt(d.get("local")), b=Integer.parseInt(d.get("visitante"));
        return true;
    }
    public boolean validar(CategoriaPronostico c, Map<String,String> d) {
        if(c==null || d==null) return false;
        return switch(c.getCodigo()) {
            case "RESULTADO" -> Set.of("LOCAL","EMPATE","VISITANTE").contains(d.getOrDefault("ganador",""));
            case "MARCADOR_EXACTO" -> marcador(d);
            case "RENDIMIENTO_JUGADOR" -> c.getParametros().containsKey("jugadorId") && c.getParametros().containsKey("medida") && numero(d.get("valor"));
            default -> false;
        };
    }
    public boolean validarResultado(EventoDeportivo e, Map<String,String> d) {
        if(e==null || !"Futbol".equals(e.getDeporte()) || !marcador(d)) return false;
        Set<CategoriaPronostico> necesarias=new LinkedHashSet<>();
        for(CategoriaPronostico c:e.getCategorias()) if(c.getHabilitada()) necesarias.add(c);
        for(Pronostico p:e.getPronosticos()) if(p.getEstado()==EstadoPronostico.PENDIENTE) necesarias.add(p.getCategoria());
        for(CategoriaPronostico c:necesarias) {
            if(!Set.of("RESULTADO","MARCADOR_EXACTO","RENDIMIENTO_JUGADOR").contains(c.getCodigo())) return false;
            if(c.getCodigo().equals("RENDIMIENTO_JUGADOR")) {
                String id=c.getParametros().get("jugadorId"), medida=c.getParametros().get("medida");
                if(id==null || medida==null || !numero(d.get("jugador."+id+"."+medida))) return false;
            }
        }
        return true;
    }
}
