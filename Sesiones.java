import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

public class Sesiones {

    private static final long DURACION = 30 * 60 * 1000L;

    private final HashMap<String, Sesion> sesiones = new HashMap<>();
    private final SecureRandom generador = new SecureRandom();

    private static class Sesion {

        private final Cuenta cuenta;
        private final long vencimiento;

        public Sesion(Cuenta cuenta) {
            this.cuenta = cuenta;
            this.vencimiento = System.currentTimeMillis() + DURACION;
        }

        public boolean vencida() {
            return System.currentTimeMillis() >= vencimiento;
        }
    }

    public synchronized String crear(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "No se puede crear una sesión sin una cuenta."
            );
        }

        eliminarVencidas();

        String codigo;

        do {
            byte[] bytes = new byte[32];
            generador.nextBytes(bytes);

            codigo = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(bytes);

        } while (sesiones.containsKey(codigo));

        sesiones.put(codigo, new Sesion(cuenta));

        return codigo;
    }

    public synchronized Cuenta obtenerCuenta(String codigo) {
        if (codigo == null) {
            return null;
        }

        Sesion sesion = sesiones.get(codigo);

        if (sesion == null) {
            return null;
        }

        if (sesion.vencida()) {
            sesiones.remove(codigo);
            return null;
        }

        return sesion.cuenta;
    }

    public synchronized void cerrar(String codigo) {
        if (codigo != null) {
            sesiones.remove(codigo);
        }
    }

    private void eliminarVencidas() {
        sesiones.entrySet().removeIf(
                entrada -> entrada.getValue().vencida()
        );
    }
}