package sportspredictor;

import java.time.LocalDateTime;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Map<String,FabricaDeporte> fabricas=Map.of(
            "Futbol",new FabricaFutbol(), "Baloncesto",new FabricaBaloncesto(), "Tenis",new FabricaTenis());
        Notificador canal=new NotificadorCorreo();
        ServicioPronosticos pronosticos=new ServicioPronosticos();
        ProcesadorResultados resultados=new ProcesadorResultados(canal);
        ServicioCanjes canjes=new ServicioCanjes();
        ServicioReportes reportes=new ServicioReportes(canal);
        Usuario ana=new Usuario("Ana","ana@ejemplo.com","0990000001");
        Usuario luis=new Usuario("Luis","luis@ejemplo.com","0990000002");
        List<Usuario> usuarios=new ArrayList<>(List.of(ana,luis));
        Participante local=new Participante("Equipo local","EQUIPO");
        Participante visitante=new Participante("Equipo visitante","EQUIPO");
        local.agregarEstadistica(new EstadisticaDeportiva("Victorias",7,"Últimos 10 encuentros"));
        EventoDeportivo evento=new EventoDeportivo("Local vs Visitante","Futbol",LocalDateTime.now().plusHours(1),List.of(local,visitante));
        CategoriaPronostico categoria=new CategoriaPronostico("RESULTADO","Ganador del partido",10,Map.of());
        evento.agregarCategoria(categoria);
        List<EventoDeportivo> eventos=new ArrayList<>(List.of(evento));
        Premio premio=new Premio("Premio de demostración",6,2);
        List<Premio> catalogo=new ArrayList<>(List.of(premio));

        System.out.println("CALENDARIO Y ESTADÍSTICAS");
        eventos.sort(Comparator.comparing(EventoDeportivo::getInicio));
        for(EventoDeportivo e:eventos) {
            System.out.println(e.getNombre()+" | "+e.getInicio());
            for(Participante participante:e.getParticipantes())
                for(EstadisticaDeportiva est:participante.getEstadisticas())
                    System.out.println(participante.getNombre()+": "+est.getIndicador()+" = "+est.getValor()+" ("+est.getPeriodo()+")");
        }
        System.out.println("CU01: REGISTRAR PRONÓSTICO");
        Pronostico p=pronosticos.registrar(ana,evento,categoria,Map.of("ganador","LOCAL"),fabricas.get(evento.getDeporte()));
        System.out.println("Estado inicial: "+p.getEstado()+"; puntos del pronóstico: "+p.getPuntosOtorgados());
        System.out.println("CU02: PROCESAR RESULTADO (se simula la declaración de fin del evento)");
        evento.finalizar();
        resultados.procesar(evento,Map.of("local","2","visitante","1"),fabricas.get(evento.getDeporte()));
        System.out.println("Evaluación: "+p.getEstado()+"; saldo: "+ana.getSaldoDisponible()+"; puntos para ranking: "+ana.getPuntosAcumulados());
        System.out.println("CU03: CANJEAR PREMIO");
        for(Premio item:catalogo) System.out.println(item.getNombre()+": "+item.getCostoPuntos()+" puntos");
        Canje canje=canjes.canjear(ana,premio,premio.getCostoPuntos());
        System.out.println("Saldo: "+ana.getSaldoDisponible()+"; puntos para ranking: "+ana.getPuntosAcumulados()+"; stock: "+premio.getExistencias());
        System.out.println("CU04: GESTIONAR REPORTE");
        Reporte reporte=reportes.registrar(ana,p,"Solicito revisar los puntos y el premio",canje);
        reportes.escalar(reporte,"Soporte identifica una posible puntuación incorrecta");
        System.out.println("Reporte: "+reporte.getEstado()+"; pronóstico: "+p.getEstado()+"; saldo conservado: "+ana.getSaldoDisponible());

        reportes.resolverDesdeCalidad(reporte,true,5,true,"Calidad ajustó los puntos y reintegró el canje");
        System.out.println("Reporte: "+reporte.getEstado()+"; pronóstico: "+p.getEstado());
        System.out.println("Después de calidad: saldo: "+ana.getSaldoDisponible()+"; puntos para ranking: "+ana.getPuntosAcumulados()+"; stock: "+premio.getExistencias()+"; canje anulado: "+canje.getAnulado());
        Reporte consulta=reportes.registrar(ana,p,"Solicito una explicación de la evaluación corregida",null);
        reportes.resolverPorSoporte(consulta,"Se explica la evaluación corregida sin modificar puntos");
        System.out.println("Consulta resuelta por soporte: "+consulta.getEstado()+"; saldo conservado: "+ana.getSaldoDisponible());
        usuarios.sort(Comparator.comparingInt(Usuario::getPuntosAcumulados).reversed());
        System.out.println("RANKING");
        for(Usuario u:usuarios) System.out.println(u.getNombre()+": "+u.getPuntosAcumulados());
    }
}
