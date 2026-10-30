package sportspredictor;

import java.time.*;
import java.util.*;

public class ServicioPronosticos {
    public Pronostico registrar(Usuario usuario, EventoDeportivo evento, CategoriaPronostico categoria, Map<String,String> datos, FabricaDeporte fabrica) {
        Objects.requireNonNull(usuario); Objects.requireNonNull(evento); Objects.requireNonNull(categoria); Objects.requireNonNull(fabrica);
        boolean admite=evento.permitePronosticos(LocalDateTime.now());
        boolean duplicado=evento.tienePronostico(usuario,categoria);
        ValidadorPrediccion validador=fabrica.crearValidador();
        boolean datosValidos=validador.validar(categoria,datos);
        boolean categoriaValida=evento.getCategorias().contains(categoria) && categoria.getHabilitada();
        if(!admite || duplicado || !categoriaValida || !datosValidos) throw new IllegalArgumentException("Pronóstico cerrado, duplicado o inválido");
        Pronostico nuevo=new Pronostico(usuario,evento,categoria,datos);
        evento.agregarPronostico(nuevo); usuario.agregarPronostico(nuevo); return nuevo;
    }
}
