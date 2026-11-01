package sportspredictor;



public class FabricaFutbol implements FabricaDeporte {
    public ValidadorFutbol crearValidador() { return new ValidadorFutbol(); }
    public EvaluadorFutbol crearEvaluador() { return new EvaluadorFutbol(); }
}
