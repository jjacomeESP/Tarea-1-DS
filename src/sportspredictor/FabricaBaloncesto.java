package sportspredictor;

import java.time.*;
import java.util.*;

public class FabricaBaloncesto implements FabricaDeporte {
    public ValidadorBaloncesto crearValidador() { return new ValidadorBaloncesto(); }
    public EvaluadorBaloncesto crearEvaluador() { return new EvaluadorBaloncesto(); }
}
