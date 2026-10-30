package sportspredictor;

import java.time.*;
import java.util.*;

public class ProcesadorResultados {
    private final Notificador notificador;
    public ProcesadorResultados(Notificador notificador) { this.notificador=Objects.requireNonNull(notificador); }
    public void procesar(EventoDeportivo evento, Map<String,String> datos, FabricaDeporte fabrica) {
        ValidadorPrediccion validador=fabrica.crearValidador();
        EvaluadorPronostico evaluador=fabrica.crearEvaluador();
        Map<String,String> datosValidados=validarResultado(evento,datos,validador);
        Map<Pronostico,Boolean> evaluaciones=evaluarPronosticos(evento,datosValidados,evaluador);
        List<Usuario> afectados=asignarPuntos(evaluaciones);
        evento.registrarResultado(datosValidados);
        for(Usuario usuario:afectados) notificador.enviar(usuario,"Resultado de "+evento.getNombre()+": "+datosValidados+"; saldo: "+usuario.getSaldoDisponible()+"; ranking: "+usuario.getPuntosAcumulados());
    }
    public Map<String,String> validarResultado(EventoDeportivo evento, Map<String,String> datos, ValidadorPrediccion validador) {
        boolean admitidos=validador.validarResultado(evento,datos);
        if(!evento.getFinalizado() || !evento.getResultado().isEmpty() || !admitidos) throw new IllegalArgumentException("Evento no finalizado, resultado repetido o incompleto");
        return Map.copyOf(datos);
    }
    public Map<Pronostico,Boolean> evaluarPronosticos(EventoDeportivo evento, Map<String,String> datos, EvaluadorPronostico evaluador) {
        Map<Pronostico,Boolean> evaluaciones=new LinkedHashMap<>();
        for(Pronostico p:evento.getPronosticos()) if(p.getEstado()==EstadoPronostico.PENDIENTE) evaluaciones.put(p,evaluador.evaluar(p,datos));
        return evaluaciones;
    }
    public List<Usuario> asignarPuntos(Map<Pronostico,Boolean> evaluaciones) {
        for(var entrada:evaluaciones.entrySet()) if(entrada.getKey().getEstado()!=EstadoPronostico.PENDIENTE || entrada.getValue()==null) throw new IllegalArgumentException("Evaluación inválida o ya aplicada");
        Set<Usuario> afectados=new LinkedHashSet<>();
        for(var entrada:evaluaciones.entrySet()) {
            Pronostico p=entrada.getKey(); boolean acertado=entrada.getValue();
            int puntos=p.getCategoria().calcularPuntos(acertado);
            p.aplicarEvaluacion(acertado,puntos); p.getUsuario().acreditarPuntos(puntos); afectados.add(p.getUsuario());
        }
        return new ArrayList<>(afectados);
    }
}
