package sportspredictor;

import java.time.*;
import java.util.*;

public interface ValidadorPrediccion { boolean validar(CategoriaPronostico categoria, Map<String,String> datos); boolean validarResultado(EventoDeportivo evento, Map<String,String> datos); }
