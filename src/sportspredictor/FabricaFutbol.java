package sportspredictor;

import java.time.*;
import java.util.*;

public class FabricaFutbol implements FabricaDeporte {
    public ValidadorFutbol crearValidador() { return new ValidadorFutbol(); }
    public EvaluadorFutbol crearEvaluador() { return new EvaluadorFutbol(); }
}
