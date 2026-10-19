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
                + "<section class='bienvenida'>"
                + "<span>ESPACIO DEL MIEMBRO</span>"
                + "<h1>Actividades del club</h1>"
                + "<p>Consulta las actividades "
                + "publicadas por el encargado del club.</p>"
                + "<a class='boton' href='#proximas'>"
                + "Ver actividades</a></section>"

                + "<div class='resumen'><strong>"
                + cantidadProximas
                + "</strong> próximas actividades"
                + "<span>Información del club</span></div>"
                
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

                + "<footer>Universidad del Valle de Guatemala "
                + "· Gestión de clubes</footer>"
                + "</body></html>";
    }
    
    private static String crearConsultaMiembro(
            Estudiante miembro,
            String mensaje
    ) {
        String contenido =
                "<section class='tarjeta' style='margin-bottom:30px'>"
                + "<h2>Mi estado en el club</h2>"
                + "<p>Ingresa tu carné para consultar tu estado actual.</p>"
                + "<form action='/miembro' method='post'>"
                + "<label for='carne'>Carné del estudiante</label>"
                + "<div style='display:flex;gap:12px;"
                + "flex-wrap:wrap;margin-top:10px'>"
                + "<input id='carne' name='carne' type='text' "
                + "placeholder='Escribe tu carné' required "
                + "value='"
                + (miembro == null ? "" : escapar(miembro.getCarne()))
                + "' style='padding:12px;border:1px solid #b9cbb5;"
                + "border-radius:8px;font:inherit;max-width:100%'>"
                + "<button type='submit' class='boton' "
                + "style='margin:0;border:0;cursor:pointer;font:inherit'>"
                + "Consultar estado</button>"
                + "</div></form>";

        if (mensaje != null && !mensaje.isBlank()) {
            contenido +=
                    "<p role='status'>" + escapar(mensaje) + "</p>";
        }

        if (miembro != null) {
            String estado = switch (miembro.getEstado()) {
                case ACTIVO -> "Activo";
                case INACTIVO -> "Inactivo";
                case PENDIENTE_CONFIRMACION ->
                        "Pendiente de confirmación";
                case RETIRADO -> "Retirado";
            };

            contenido +=
                    "<div role='status' style='margin-top:20px;"
                    + "padding:18px;background:#edf3e4;border-radius:10px'>"
                    + "<p><strong>Nombre:</strong> "
                    + escapar(miembro.getNombre()) + "</p>"
                    + "<p><strong>Carné:</strong> "
                    + escapar(miembro.getCarne()) + "</p>"
                    + "<p><strong>Estado actual:</strong> "
                    + estado + "</p>"
                    + "</div>"
                    + "<p>Si el coordinador cambia tu estado, "
                    + "presiona Consultar estado para ver el cambio.</p>";
        }

        return contenido + "</section>";
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
                    font-family: 'Segoe UI', Arial, sans-serif;
                    background: #f4f6f0;
                    color: #234333;
                }

                header {
                    background: white;
                    border-bottom: 1px solid #dce5d8;
                }

                nav {
                    max-width: 1100px;
                    margin: auto;
                    padding: 20px 24px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    gap: 16px;
                }

                .marca {
                    font-size: 22px;
                    font-weight: bold;
                }

                .marca small {
                    display: block;
                    font-size: 11px;
                    font-weight: normal;
                    margin-top: 4px;
                }

                a {
                    color: #245b3e;
                }

                nav a {
                    font-size: 14px;
                }

                main {
                    max-width: 1100px;
                    margin: auto;
                    padding: 30px 24px 60px;
                }

                .bienvenida {
                    background: #174d37;
                    color: white;
                    padding: 30px;
                    border-radius: 22px;
                }

                .bienvenida span {
                    color: #d4e9ae;
                    font-size: 12px;
                    letter-spacing: 2px;
                }

                h1 {
                    font-size: 32px;
                    line-height: 1.1;
                    margin: 20px 0;
                }

                .bienvenida p {
                    color: #e0eadf;
                    line-height: 1.6;
                }

                .boton {
                    display: inline-block;
                    background: #d4e9ae;
                    padding: 14px 20px;
                    margin-top: 14px;
                    border-radius: 9px;
                    text-decoration: none;
                    font-weight: bold;
                }

                .resumen {
                    background: white;
                    border: 1px solid #dce5d8;
                    border-radius: 12px;
                    padding: 22px;
                    margin: 22px 0 40px;
                }

                .resumen strong {
                    font-size: 26px;
                    margin-right: 8px;
                }

                .resumen span {
                    float: right;
                    color: #627366;
                    padding-top: 8px;
                }

                .titulo {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    gap: 16px;
                }

                h2 {
                    font-size: 27px;
                    margin: 0;
                }

                .descripcion {
                    color: #627366;
                    line-height: 1.5;
                    margin-bottom: 24px;
                }

                .tarjetas {
                    display: grid;
                    grid-template-columns: repeat(3, minmax(0, 1fr));
                    gap: 20px;
                }

                .tarjeta {
                    background: white;
                    border: 1px solid #dce5d8;
                    border-radius: 16px;
                    padding: 25px;
                    overflow-wrap: anywhere;
                }

                .etiqueta {
                    display: inline-block;
                    background: #edf3e4;
                    color: #366037;
                    padding: 7px 12px;
                    border-radius: 20px;
                    font-size: 12px;
                }

                h3 {
                    font-size: 22px;
                    margin: 22px 0;
                }

                .tarjeta p {
                    color: #526859;
                    font-size: 14px;
                    line-height: 1.6;
                }

                details {
                    border-top: 1px solid #e4e9df;
                    margin-top: 22px;
                    padding-top: 16px;
                }

                summary {
                    cursor: pointer;
                    font-size: 14px;
                    font-weight: bold;
                }

                .historial {
                    margin-top: 45px;
                }

                .vacio {
                    grid-column: 1 / -1;
                    border: 1px dashed #b9cbb5;
                    border-radius: 12px;
                    padding: 35px;
                    text-align: center;
                    color: #627366;
                }

                footer {
                    text-align: center;
                    padding: 24px;
                    border-top: 1px solid #dce5d8;
                    color: #627366;
                    font-size: 12px;
                }

                :focus-visible {
                    outline: 3px solid #ae791b;
                    outline-offset: 4px;
                }

                @media (max-width: 850px) {
                    .tarjetas {
                        grid-template-columns: repeat(2, minmax(0, 1fr));
                    }
                }

                @media (max-width: 600px) {
                    main {
                        padding: 20px 16px 40px;
                    }

                    .bienvenida {
                        padding: 28px;
                    }

                    .tarjetas {
                        grid-template-columns: 1fr;
                    }

                    .resumen span {
                        float: none;
                        display: block;
                        margin-top: 8px;
                    }

                    .titulo {
                        align-items: start;
                    }

                    h2 {
                        font-size: 23px;
                    }
                }
            </style>
        </head>

        <body>
            <header>
                <nav>
                    <div class="marca">
                        Mi club
                        <small>Universidad del Valle de Guatemala</small>
                    </div>
                </nav>
            </header>

            <main>
        """;
}