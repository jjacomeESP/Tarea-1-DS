package sportspredictor;

import java.time.*;
import java.util.*;

public interface FabricaDeporte { ValidadorPrediccion crearValidador(); EvaluadorPronostico crearEvaluador(); }
