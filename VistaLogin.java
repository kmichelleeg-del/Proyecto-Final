public class VistaLogin {

    public static String renderizar(String mensaje) {
        String aviso = "";

        if (mensaje != null && !mensaje.isBlank()) {
            aviso = "<p class='aviso' role='alert'>"
                    + escapar(mensaje)
                    + "</p>";
        }

        return INICIO + aviso + FORMULARIO;
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static final String INICIO = """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport"
                  content="width=device-width, initial-scale=1">

            <title>Iniciar sesión | Voces del Valle</title>

            <style>
                * {
                    box-sizing: border-box;
                }

                body {
                    margin: 0;
                    font-family: 'Times New Roman', Times, serif;
                    background: #f2f5f3;
                    color: #263238;
                }

                header {
                    padding: 30px 20px;
                    text-align: center;
                    color: white;
                    background: #28734f;
                    border-bottom: 8px solid #dce9df;
                }

                header h1 {
                    margin: 0;
                }

                header p {
                    margin-bottom: 0;
                }

                main {
                    max-width: 480px;
                    margin: 40px auto;
                    padding: 0 20px;
                }

                section {
                    padding: 28px;
                    background: white;
                    border-left: 6px solid #184d35;
                    border-radius: 12px;
                    box-shadow: 0 3px 12px #184d3526;
                }

                h2 {
                    margin-top: 0;
                    color: #184d35;
                }

                label {
                    display: block;
                    margin-top: 16px;
                    color: #184d35;
                    font-weight: bold;
                }

                input,
                button {
                    width: 100%;
                    margin-top: 8px;
                    padding: 11px;
                    font-size: 16px;
                    border-radius: 6px;
                }

                input {
                    border: 1px solid #b8c1c5;
                }

                input:focus {
                    outline: 2px solid #28734f;
                    border-color: #28734f;
                }

                button {
                    margin-top: 24px;
                    border: none;
                    background: #184d35;
                    color: white;
                    font-weight: bold;
                    cursor: pointer;
                }

                button:hover {
                    background: #184d35;
                }

                button:focus-visible {
                    outline: 3px solid #184d35;
                    outline-offset: 3px;
                }

                .aviso {
                    padding: 12px;
                    background: #fff0ee;
                    color: #8b2525;
                    border-radius: 6px;
                }

                footer {
                    padding: 22px;
                    text-align: center;
                    color: #184d35;
                }
                    .mascota-login {
                        position: fixed;
                        right: 24px;
                        bottom: 0;
                        width: 220px;
                        max-height: 55vh;
                        object-fit: contain;
                        object-position: bottom;
                        pointer-events: none;
                    }

                    @media (max-width: 1000px) {
                        .mascota-login {
                            position: static;
                            display: block;
                            width: 160px;
                            max-height: none;
                            margin: 20px auto 0;
                        }
                    }
            </style>
        </head>

        <body>
            <header>
                <h1>Voces del Valle</h1>
                <p>Universidad del Valle de Guatemala</p>
            </header>

            <main>
                <section>
                    <h2>Iniciar sesión</h2>
                    <p>Ingresa tus datos para acceder al sistema.</p>
        """;

    private static final String FORMULARIO = """
                    <form action="/login" method="post">
                        <label for="usuario">Usuario</label>
                        <input
                            id="usuario"
                            name="usuario"
                            type="text"
                            autocomplete="username"
                            maxlength="40"
                            required
                        >

                        <label for="contrasena">Contraseña</label>
                        <input
                            id="contrasena"
                            name="contrasena"
                            type="password"
                            autocomplete="current-password"
                            required
                        >

                        <button type="submit">Ingresar</button>
                    </form>
                </section>
            </main>
            <img
                src="/mascota.png"
                class="mascota-login"
                alt="Mascota de Voces del Valle"
            >
        </body>
        </html>
        """;
}