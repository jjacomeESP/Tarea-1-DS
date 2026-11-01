package sportspredictor;



public class FabricaTenis implements FabricaDeporte {
    public ValidadorTenis crearValidador() { return new ValidadorTenis(); }
    public EvaluadorTenis crearEvaluador() { return new EvaluadorTenis(); }
}
