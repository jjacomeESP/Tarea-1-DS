package sportspredictor;

import java.time.*;
import java.util.*;

public class NotificadorMensajeria implements Notificador {
    public void enviar(Usuario usuario, String mensaje) { System.out.println("[SIMULADO Mensajeria] " + usuario.getMensajeria() + ": " + mensaje); }
}
