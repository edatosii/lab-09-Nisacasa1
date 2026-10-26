package co.umariana.util;

/**
 * Utilidad para las vistas JSP: escapa texto antes de imprimirlo en HTML
 * (evita ataques XSS).
 *
 * Esta clase YA está completa: no es necesario modificarla.
 */
public final class Html {

    private Html() {
    }

    public static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}
