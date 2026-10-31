package sportspredictor;

import java.time.*;
import java.util.*;

public class NotificadorCorreo implements Notificador {
    public void enviar(Usuario usuario, String mensaje) { System.out.println("[SIMULADO Correo] " + usuario.getCorreo() + ": " + mensaje); }
}
