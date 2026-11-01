package sportspredictor;



public class NotificadorMensajeria implements Notificador {
    public void enviar(Usuario usuario, String mensaje) { System.out.println("[SIMULADO Mensajeria] " + usuario.getMensajeria() + ": " + mensaje); }
}
