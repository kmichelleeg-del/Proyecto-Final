import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VistaMiembro {

    public static String renderizar(List<Actividad> actividades) {
        return renderizar(actividades, null, "");
    }

    public static String renderizar(
            List<Actividad> actividades,
            Estudiante miembro,
            String mensaje
    ) {        List<Actividad> ordenadas = new ArrayList<>(actividades);
        ordenadas.sort(Comparator.comparing(Actividad::getFechaHora));

        LocalDateTime ahora =
                LocalDateTime.now(ZoneId.of("America/Guatemala"));

        StringBuilder proximas = new StringBuilder();
        StringBuilder anteriores = new StringBuilder();
        int cantidadProximas = 0;

        for (Actividad actividad : ordenadas) {
            if (!actividad.getFechaHora().isBefore(ahora)) {
                proximas.append(crearTarjeta(actividad));
                cantidadProximas++;
            }
        }

        for (int i = ordenadas.size() - 1; i >= 0; i--) {
            Actividad actividad = ordenadas.get(i);

            if (actividad.getFechaHora().isBefore(ahora)) {
                anteriores.append(crearTarjeta(actividad));
            }
        }

        if (proximas.length() == 0) {
            proximas.append(
                    "<p class='vacio'>Todavía no hay próximas "
                    + "actividades. ¡Vuelve pronto!</p>"
            );
        }

        if (anteriores.length() == 0) {
            anteriores.append(
                    "<p class='vacio'>Todavía no hay "
                    + "actividades anteriores.</p>"
            );
        }

        return CABECERA
                + "<div class='resumen'><strong>"
                + cantidadProximas
                + "</strong> próximas actividades"
                + "</div>"                
                + crearConsultaMiembro(miembro, mensaje)
                + "<section id='proximas'>"
                + "<div class='titulo'><h2>Próximas actividades</h2>"
                + "<a href='/miembro'>Actualizar ↻</a></div>"
                + "<p class='descripcion'>Consulta las fechas y los lugares de las "
                + "próximas actividades.</p>"
                + "<div class='tarjetas'>" + proximas + "</div>"
                + "</section>"

                + "<section class='historial'>"
                + "<h2>Actividades anteriores</h2>"
                + "<p class='descripcion'>Consulta las actividades "
                + "cuya fecha ya pasó.</p>"
                + "<div class='tarjetas'>" + anteriores + "</div>"
                + "</section></main>"

                + "<footer>Programación Orientada a Objetos Kathy y Abril"
                + "</footer>"
                + "</body></html>";
    }
    
    private static String crearConsultaMiembro(
            Estudiante miembro,
            String mensaje
    ) {
        String contenido =
                "<section>"
                + "<h2>Mis datos</h2>";

        if (mensaje != null && !mensaje.isBlank()) {
            contenido +=
                    "<p role='status'>"
                    + escapar(mensaje)
                    + "</p>";
        }

        if (miembro != null) {
            String estado = switch (miembro.getEstado()) {
                case ACTIVO -> "Activo";
                case INACTIVO -> "Inactivo";
                case PENDIENTE_CONFIRMACION ->
                        "Pendiente de confirmación";
                case RETIRADO -> "Retirado";
            };

            String explicacion = switch (miembro.getEstado()) {
                case ACTIVO ->
                        "Actualmente participas como miembro del club.";
                case INACTIVO ->
                        "Tu participación en el club está pausada por el momento.";
                case PENDIENTE_CONFIRMACION ->
                        "Está pendiente confirmar tu continuidad en el club.";
                case RETIRADO ->
                        "Ya no participas como miembro del club.";
            };

            contenido +=
                    "<p><strong>Nombre:</strong> "
                    + escapar(miembro.getNombre()) + "</p>"
                    + "<p><strong>Carné:</strong> "
                    + escapar(miembro.getCarne()) + "</p>"
                    + "<p><strong>Estado en el club:</strong> "
                    + estado + "</p>"
                    + "<p class='explicacion-estado'>"
                    + explicacion + "</p>";        }

        contenido +=
                "<a class='boton' href='/miembro'>"
                + "Actualizar mis datos</a>"
                + "</section>";

        return contenido;
    }
    private static String crearTarjeta(Actividad actividad) {
        LocalDateTime fecha = actividad.getFechaHora();

        String fechaTexto = fecha.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );

        String horaTexto = fecha.format(
                DateTimeFormatter.ofPattern("HH:mm")
        );

        return "<article class='tarjeta'>"
                + "<span class='etiqueta'>"
                + escapar(actividad.getTipo()) + "</span>"
                + "<h3>" + escapar(actividad.getNombre()) + "</h3>"
                + "<p><strong>Fecha:</strong> " + fechaTexto + "</p>"
                + "<p><strong>Hora:</strong> " + horaTexto + "</p>"
                + "<p><strong>Lugar:</strong> "
                + escapar(actividad.getLugar()) + "</p>"
                + "<details><summary>Ver detalles</summary>"
                + "<p>Actividad publicada por el encargado del club. "
                + "Para participar, consulta con el encargado "
                + "los requisitos de esta actividad.</p>"
                + "</details></article>";
    }

    private static String escapar(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static final String CABECERA = """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport"
                  content="width=device-width, initial-scale=1">

            <title>Mi club | Vista del miembro</title>

            <style>
                * {
                    box-sizing: border-box;
                }

                html {
                    scroll-behavior: smooth;
                }

                body {
                    margin: 0;
                    color: #263238;
                    font-family: 'Times New Roman', Times, serif;
                    background: #f2f5f3;
                }

                header {
                    padding: 30px 20px;
                    color: white;
                    text-align: center;
                    background: #28734f;
                    border-bottom: 8px solid #dce9df;
                }

                nav {
                    max-width: 900px;
                    margin: auto;
                }

                .marca {
                    font-size: 32px;
                    font-weight: bold;
                }

                .marca small {
                    display: block;
                    margin-top: 16px;
                    font-size: 16px;
                    font-weight: normal;
                }

                main {
                    max-width: 900px;
                    margin: 30px auto;
                    padding: 0 20px;
                }

                section {
                    margin-bottom: 24px;
                    padding: 24px;
                    background: white;
                    border-left: 6px solid #184d35;
                    border-radius: 12px;
                    box-shadow: 0 3px 12px #184d3526;
                }

                h1,
                h2,
                h3 {
                    color: #184d35;
                }

                h1,
                h2 {
                    margin-top: 0;
                    font-size: 24px;
                }

                h3 {
                    margin: 16px 0;
                    font-size: 20px;
                }

                p {
                    line-height: 1.5;
                }

                .bienvenida {
                    text-align: center;
                    border-left-color: #dce9df;
                }

                .bienvenida span {
                    color: #184d35;
                    font-size: 14px;
                }

                .bienvenida h1 {
                    margin-top: 12px;
                }

                .boton,
                button {
                    display: inline-block;
                    padding: 11px 18px;
                    color: white;
                    font-family: Arial, sans-serif;
                    font-size: 16px;
                    font-weight: bold;
                    text-decoration: none;
                    background: #28734f;
                    border: none;
                    border-radius: 6px;
                    cursor: pointer;
                }

                .boton:hover,
                button:hover {
                    background: #184d35;
                }

                .cerrar-sesion {
                    margin-top: 18px;
                    background: #184d35;
                }

                .cerrar-sesion:hover {
                    background: #103c19;
                }

                .resumen {
                    margin-bottom: 24px;
                    padding: 16px 24px;
                    color: #184d35;
                    background: white;
                    border-left: 6px solid #dce9df;
                    border-radius: 8px;
                }

                .resumen strong {
                    font-size: 20px;
                }

                .titulo {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    gap: 16px;
                }

                .titulo h2 {
                    margin-bottom: 0;
                }

                .titulo a {
                    color: #184d35;
                }

                .descripcion {
                    margin-bottom: 20px;
                }

                .tarjetas {
                    display: grid;
                    grid-template-columns: repeat(2, minmax(0, 1fr));
                    gap: 16px;
                }

                .tarjeta {
                    padding: 20px;
                    background: white;
                    border: 1px solid #b8c1c5;
                    border-radius: 8px;
                    overflow-wrap: anywhere;
                }

                section.tarjeta {
                    border-left: 6px solid #184d35;
                    border-radius: 12px;
                }

                .etiqueta {
                    display: inline-block;
                    padding: 5px 10px;
                    color: #184d35;
                    background: #f2f5f3;
                    border-radius: 4px;
                    font-size: 14px;
                }

                details {
                    margin-top: 16px;
                    padding-top: 12px;
                    border-top: 1px solid #b8c1c5;
                }

                summary {
                    color: #184d35;
                    font-weight: bold;
                    cursor: pointer;
                }

                label {
                    display: block;
                    margin-top: 10px;
                    color: #184d35;
                    font-weight: bold;
                }

                input {
                    padding: 11px;
                    font-size: 16px;
                    border: 1px solid #b8c1c5;
                    border-radius: 6px;
                }

                input:focus {
                    outline: 2px solid #28734f;
                    border-color: #28734f;
                }

                .vacio {
                    grid-column: 1 / -1;
                    padding: 20px;
                    text-align: center;
                    background: #f2f5f3;
                    border-radius: 6px;
                }

                footer {
                    padding: 22px;
                    color: white;
                    text-align: center;
                    background: #184d35;
                }

                :focus-visible {
                    outline: 2px solid #184d35;
                    outline-offset: 3px;
                }

                @media (max-width: 600px) {
                    main {
                        margin: 15px auto;
                        padding: 0 12px;
                    }

                    section {
                        padding: 18px;
                    }

                    .tarjetas {
                        grid-template-columns: 1fr;
                    }

                    .titulo {
                        align-items: flex-start;
                        flex-direction: column;
                    }
                }

                .explicacion-estado {
                    margin: 12px 0 20px;
                    padding: 14px 16px;
                    color: #184d35;
                    background: #dce9df;
                    border-left: 4px solid #28734f;
                    border-radius: 6px;
                    line-height: 1.5;
                }
            </style>   
        </head>

        <body>
            <header>
                <nav>
                <div class="marca">
                    Voces del Valle
                    <small>Universidad del Valle de Guatemala</small>
                </div>

                <form action="/logout" method="post">
                    <button type="submit" class="cerrar-sesion">
                        Cerrar sesión
                    </button>
                </form>
                </nav>
            </header>

            <main>
        """;
}