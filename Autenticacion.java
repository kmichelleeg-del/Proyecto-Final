import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Autenticacion {

    private ArrayList<Cuenta> cuentas;
    private ArrayList<String> sales;
    private ArrayList<String> hashes;

    public Autenticacion() throws IOException {
        cuentas = new ArrayList<>();
        sales = new ArrayList<>();
        hashes = new ArrayList<>();

        cargarCuentas();
    }

    private void cargarCuentas() throws IOException {
        Path archivo = Path.of("cuentas.txt");

        if (!Files.exists(archivo)) {
            return;
        }

        try (
                BufferedReader lector = Files.newBufferedReader(
                        archivo,
                        StandardCharsets.UTF_8
                )
        ) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }

                String[] datos = linea.split(";", -1);

                if (datos.length != 5) {
                    throw new IOException(
                            "Hay una cuenta mal escrita en cuentas.txt."
                    );
                }

                String usuario = datos[0];
                String rol = datos[1];
                String carne = datos[2];

                if (!rol.equals("COORDINADOR")
                        && !rol.equals("MIEMBRO")) {

                    throw new IOException(
                            "Una cuenta tiene un rol inválido."
                    );
                }

                if (usuario.isBlank()
                        || (rol.equals("MIEMBRO") && carne.isBlank())) {

                    throw new IOException(
                            "Faltan datos en una cuenta."
                    );
                }

                Cuenta cuenta = new Cuenta(usuario, rol, carne);

                cuentas.add(cuenta);
                sales.add(datos[3]);
                hashes.add(datos[4]);
            }
        }
    }

    public Cuenta iniciarSesion(
            String usuario,
            char[] contrasena
    ) throws GeneralSecurityException, IOException {

        if (usuario == null || contrasena == null) {
            return null;
        }

        for (int i = 0; i < cuentas.size(); i++) {
            Cuenta cuenta = cuentas.get(i);

            if (cuenta.getUsuario().equalsIgnoreCase(usuario.trim())) {
                try {
                    byte[] sal = Base64.getDecoder().decode(
                            sales.get(i)
                    );

                    byte[] hashGuardado = Base64.getDecoder().decode(
                            hashes.get(i)
                    );

                    byte[] hashIngresado = calcularHash(
                            contrasena,
                            sal
                    );

                    boolean correcta = MessageDigest.isEqual(
                            hashGuardado,
                            hashIngresado
                    );

                    Arrays.fill(hashIngresado, (byte) 0);

                    if (correcta) {
                        return cuenta;
                    }

                    return null;

                } catch (IllegalArgumentException error) {
                    throw new IOException(
                            "Los datos de la contraseña no son válidos.",
                            error
                    );
                }
            }
        }

        return null;
    }

    public static byte[] calcularHash(
            char[] contrasena,
            byte[] sal
    ) throws GeneralSecurityException {

        PBEKeySpec datos = new PBEKeySpec(
                contrasena,
                sal,
                600_000,
                256
        );

        try {
            SecretKeyFactory generador =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            return generador.generateSecret(datos).getEncoded();

        } finally {
            datos.clearPassword();
        }
    }
}