package sportspredictor;

import java.time.LocalDateTime;
import java.util.*;

public class Verificacion {
    private static int comprobaciones;
    private static void comprobar(boolean condicion,String mensaje) {
        comprobaciones++;
        if(!condicion) throw new AssertionError(mensaje);
    }
    private static void rechaza(Runnable accion,String mensaje) {
        comprobaciones++;
        try { accion.run(); } catch(IllegalArgumentException ex) { return; }
        throw new AssertionError("Debió rechazar: "+mensaje);
    }
    private static Usuario usuario() { return new Usuario("Prueba","prueba@ejemplo.com","0990000000"); }
    private static EventoDeportivo evento(String deporte) {
        return new EventoDeportivo("Evento de prueba",deporte,LocalDateTime.now().plusHours(1),List.of(
            new Participante("Local","EQUIPO"), new Participante("Visitante","EQUIPO")));
    }
    private static CategoriaPronostico categoria(String codigo) {
        return new CategoriaPronostico(codigo,codigo,10,codigo.equals("RENDIMIENTO_JUGADOR")?Map.of("jugadorId","7","medida","valor"):Map.of());
    }
    public static void main(String[] args) {
        Notificador canal=new NotificadorCorreo();
        ServicioPronosticos registros=new ServicioPronosticos();
        ProcesadorResultados resultados=new ProcesadorResultados(canal);
        ServicioCanjes canjes=new ServicioCanjes();
        Map<String,FabricaDeporte> fabricas=Map.of("Futbol",new FabricaFutbol(),"Baloncesto",new FabricaBaloncesto(),"Tenis",new FabricaTenis());

        for(var familia:fabricas.entrySet()) {
            String deporte=familia.getKey(); FabricaDeporte fabrica=familia.getValue();
            EventoDeportivo e=evento(deporte); Usuario u=usuario();
            CategoriaPronostico ganador=categoria("RESULTADO"), marcador=categoria("MARCADOR_EXACTO"), jugador=categoria("RENDIMIENTO_JUGADOR");
            e.agregarCategoria(ganador);e.agregarCategoria(marcador);e.agregarCategoria(jugador);
            String a=deporte.equals("Baloncesto")?"80":"2", b=deporte.equals("Baloncesto")?"70":"1";
            Pronostico p=registros.registrar(u,e,ganador,Map.of("ganador","LOCAL"),fabrica);
            Pronostico q=registros.registrar(u,e,marcador,Map.of("local",a,"visitante",b),fabrica);
            Pronostico r=registros.registrar(u,e,jugador,Map.of("valor","3"),fabrica);
            comprobar(p.getEstado()==EstadoPronostico.PENDIENTE && u.getSaldoDisponible()==0,"El registro no acredita puntos");
            rechaza(()->registros.registrar(u,e,ganador,Map.of("ganador","VISITANTE"),fabrica),"pronóstico duplicado");
            comprobar(e.getPronosticos().size()==3 && u.getPronosticos().size()==3,"Sin registros parciales por duplicado");
            Map<String,String> dato=Map.of("local",a,"visitante",b,"jugador.7.valor","4");
            rechaza(()->resultados.procesar(e,dato,fabrica),"evento sin finalizar");
            comprobar(u.getSaldoDisponible()==0 && e.getResultado().isEmpty(),"Validar no modifica datos");
            e.finalizar();
            rechaza(()->resultados.procesar(e,Map.of("local",a,"visitante",b),fabrica),"resultado sin rendimiento requerido");
            comprobar(p.getEstado()==EstadoPronostico.PENDIENTE,"Error deja pronóstico pendiente");
            resultados.procesar(e,dato,fabrica);
            comprobar(p.getEstado()==EstadoPronostico.ACERTADO && q.getEstado()==EstadoPronostico.ACERTADO && r.getEstado()==EstadoPronostico.FALLIDO,"Estados de las tres categorías");
            comprobar(u.getSaldoDisponible()==20 && u.getPuntosAcumulados()==20,"Puntuación esperada por familia");
            rechaza(()->resultados.procesar(e,dato,fabrica),"resultado repetido");
            comprobar(u.getSaldoDisponible()==20,"No acreditar dos veces");
        }
        EventoDeportivo vacio=evento("Futbol");vacio.finalizar();
        resultados.procesar(vacio,Map.of("local","0","visitante","0"),new FabricaFutbol());
        comprobar(!vacio.getResultado().isEmpty(),"Guardar resultado sin pronósticos");
        EventoDeportivo e=evento("Futbol");CategoriaPronostico c=categoria("RESULTADO");e.agregarCategoria(c);
        Usuario u=usuario();
        comprobar(!e.permitePronosticos(e.getInicio()),"Cierre exacto al inicio");
        CategoriaPronostico ajena=categoria("RESULTADO");
        rechaza(()->registros.registrar(u,e,ajena,Map.of("ganador","LOCAL"),new FabricaFutbol()),"categoría ajena");
        c.configurar(false,10);
        rechaza(()->registros.registrar(u,e,c,Map.of("ganador","LOCAL"),new FabricaFutbol()),"categoría inhabilitada");
        c.configurar(true,10);
        rechaza(()->registros.registrar(u,e,c,Map.of("ganador","OTRO"),new FabricaFutbol()),"predicción inválida");
        Pronostico p=registros.registrar(u,e,c,Map.of("ganador","LOCAL"),new FabricaFutbol());
        Map<String,String> datos=new HashMap<>(Map.of("local","1","visitante","0"));
        e.finalizar();resultados.procesar(e,datos,new FabricaFutbol());datos.put("local","99");
        comprobar(e.getResultado().get("local").equals("1"),"Resultado guardado como copia");
        rechaza(()->registros.registrar(usuario(),e,c,Map.of("ganador","LOCAL"),new FabricaFutbol()),"evento cerrado");
        Premio premio=new Premio("Premio",6,1);
        rechaza(()->canjes.canjear(usuario(),premio,6),"saldo insuficiente");
        rechaza(()->canjes.canjear(u,premio,5),"precio desactualizado");
        comprobar(u.getSaldoDisponible()==10 && premio.getExistencias()==1 && u.getCanjes().isEmpty(),"Rechazos de canje no alteran datos");
        Canje canje=canjes.canjear(u,premio,6);
        comprobar(u.getSaldoDisponible()==4 && u.getPuntosAcumulados()==10 && premio.getExistencias()==0,"Canjear conserva ranking y descuenta stock");
        rechaza(()->canjes.canjear(u,premio,6),"stock agotado");
        ServicioReportes soporte=new ServicioReportes(canal);
        rechaza(()->soporte.registrar(usuario(),p,"No es mío",null),"reporte ajeno");
        Reporte reporte=soporte.registrar(u,p,"Revisar premio y puntuación",canje);
        rechaza(()->soporte.registrar(u,p,"Duplicado",null),"reporte abierto repetido");
        rechaza(()->soporte.resolverPorSoporte(reporte," "),"respuesta vacía");
        rechaza(()->soporte.resolverDesdeCalidad(reporte,true,5,true,"Ajuste"),"calidad antes de escalar");
        soporte.escalar(reporte,"Posible error");
        comprobar(p.getEstado()==EstadoPronostico.EN_REVISION && p.getEstadoAnterior()==EstadoPronostico.ACERTADO && p.getPuntosOtorgados()==10 && u.getSaldoDisponible()==4,"Escalar conserva evaluación y puntos");
        rechaza(()->soporte.resolverPorSoporte(reporte,"Cerrar"),"soporte no cierra escalados");
        premio.configurar(9,0,true);
        rechaza(()->soporte.resolverDesdeCalidad(reporte,false,5,true,"Inconsistente"),"fallido no puede tener puntos");
        comprobar(u.getSaldoDisponible()==4 && !canje.getAnulado(),"Corrección inválida no aplica parcialmente");
        soporte.resolverDesdeCalidad(reporte,true,5,true,"Corrección verificada");
        comprobar(p.getPuntosOtorgados()==5 && u.getSaldoDisponible()==5 && u.getPuntosAcumulados()==5,"Delta -5 y reintegro histórico +6");
        comprobar(canje.getAnulado() && premio.getExistencias()==1 && reporte.getEstado()==EstadoReporte.RESUELTO,"Anular, reponer y cerrar");
        rechaza(()->soporte.resolverDesdeCalidad(reporte,true,5,true,"Repetir"),"resolución repetida");
        comprobar(u.getSaldoDisponible()==5 && premio.getExistencias()==1,"Sin doble reintegro");
        Reporte confirmar=soporte.registrar(u,p,"Confirmar evaluación",null);soporte.escalar(confirmar,"Revisión");
        soporte.resolverDesdeCalidad(confirmar,true,5,false,"Evaluación confirmada");
        comprobar(u.getSaldoDisponible()==5 && u.getPuntosAcumulados()==5,"Confirmar evaluación tiene delta cero");
        Reporte explicar=soporte.registrar(u,p,"Explicar regla",null);soporte.resolverPorSoporte(explicar,"Regla explicada");
        comprobar(p.getEstado()==EstadoPronostico.ACERTADO && u.getSaldoDisponible()==5,"Soporte no cambia puntos");
        Premio barato=new Premio("Otro",4,1);canjes.canjear(u,barato,4);
        Reporte retirar=soporte.registrar(u,p,"Retirar puntuación incorrecta",null);soporte.escalar(retirar,"Revisar");
        soporte.resolverDesdeCalidad(retirar,false,0,false,"Se retiran los puntos");
        comprobar(u.getSaldoDisponible()==-4 && u.getPuntosAcumulados()==0,"Corrección retroactiva con puntos ya gastados");
        rechaza(()->canjes.canjear(u,new Premio("Nuevo",1,1),1),"saldo negativo no permite canje");
        comprobar(!new ValidadorTenis().validar(categoria("RESULTADO"),Map.of("ganador","EMPATE")),"Tenis no admite empate");
        comprobar(!new ValidadorBaloncesto().validar(categoria("MARCADOR_EXACTO"),Map.of("local","1","visitante","1")),"Baloncesto final sin empate");
        comprobar(!new ValidadorFutbol().validar(categoria("MARCADOR_EXACTO"),Map.of("local","-1","visitante","0")),"Marcadores no negativos");
        System.out.println("VERIFICACIÓN CORRECTA: "+comprobaciones+" comprobaciones.");
    }
}
