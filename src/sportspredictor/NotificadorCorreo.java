package sportspredictor;



public class NotificadorCorreo implements Notificador {
    public void enviar(Usuario usuario, String mensaje) { System.out.println("[SIMULADO Correo] " + usuario.getCorreo() + ": " + mensaje); }
}
