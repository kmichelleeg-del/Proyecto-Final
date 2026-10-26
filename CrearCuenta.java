import java.io.Console;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class CrearCuenta {

    public static void main(String[] args) {
        Console consola = System.console();

        if (consola == null) {
            System.out.println(
                    "Ejecuta este programa desde la terminal "
                    + "con: java CrearCuenta"
            );
            return;
        }

        char[] contrasena = null;
        char[] confirmacion = null;

        try {
            String usuario = consola.readLine("Nombre de usuario: ");

            if (usuario == null) {
                return;
            }

            usuario = usuario.trim();

            if (!usuario.matches("[a-zA-Z0-9._-]{3,40}")) {
                System.out.println(
                        "Usa entre 3 y 40 caracteres: letras sin tildes, "
                        + "números, puntos o guiones."
                );
                return;
            }

            Path archivo = Path.of("cuentas.txt");

            if (Files.exists(archivo)) {
                for (String linea : Files.readAllLines(
                        archivo,
                        StandardCharsets.UTF_8
                )) {
                    String[] datos = linea.split(";", -1);

                    if (datos[0].equalsIgnoreCase(usuario)) {
                        System.out.println("Ese usuario ya existe.");
                        return;
                    }
                }
            }

            System.out.println("1. Coordinador");
            System.out.println("2. Miembro");

            String opcion = consola.readLine("Selecciona el rol: ");
            String rol;
            String carne = "";

            if ("1".equals(opcion)) {
                rol = "COORDINADOR";

            } else if ("2".equals(opcion)) {
                rol = "MIEMBRO";
                carne = consola.readLine(
                        "Carné del miembro registrado en el sistema: "
                );

                if (carne == null) {
                    return;
                }

                carne = carne.trim();

                if (carne.isBlank() || carne.contains(";")) {
                    System.out.println("El carné no es válido.");
                    return;
                }

            } else {
                System.out.println("Selecciona 1 o 2.");
                return;
            }

            contrasena = consola.readPassword(
                    "Contraseña (mínimo 10 caracteres): "
            );

            if (contrasena == null) {
                return;
            }

            if (contrasena.length < 10) {
                System.out.println(
                        "La contraseña debe tener al menos 10 caracteres."
                );
                return;
            }

            confirmacion = consola.readPassword(
                    "Repite la contraseña: "
            );

            if (confirmacion == null
                    || !Arrays.equals(contrasena, confirmacion)) {

                System.out.println("Las contraseñas no coinciden.");
                return;
            }

            byte[] sal = new byte[16];
            new SecureRandom().nextBytes(sal);

            byte[] hash = Autenticacion.calcularHash(
                    contrasena,
                    sal
            );

            String registro =
                    usuario + ";"
                    + rol + ";"
                    + carne + ";"
                    + Base64.getEncoder().encodeToString(sal) + ";"
                    + Base64.getEncoder().encodeToString(hash);

            Arrays.fill(hash, (byte) 0);

            Files.writeString(
                    archivo,
                    System.lineSeparator()
                            + registro
                            + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

            System.out.println("Cuenta creada correctamente.");

        } catch (IOException | GeneralSecurityException error) {
            System.out.println(
                    "No se pudo crear la cuenta: " + error.getMessage()
            );

        } finally {
            if (contrasena != null) {
                Arrays.fill(contrasena, '\0');
            }

            if (confirmacion != null) {
                Arrays.fill(confirmacion, '\0');
            }
        }
    }
}