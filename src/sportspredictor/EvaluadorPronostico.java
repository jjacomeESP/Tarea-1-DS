package sportspredictor;

import java.time.*;
import java.util.*;

public interface EvaluadorPronostico { boolean evaluar(Pronostico pronostico, Map<String,String> resultado); }
