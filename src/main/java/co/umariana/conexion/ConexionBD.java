package co.umariana.conexion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Conexión a MySQL configurada desde afuera del código (nunca escriba
 * contraseñas dentro del código). Cada valor se busca en este orden y gana el
 * primero que exista:
 *
 *   1. Propiedad del sistema (-D); la usan las pruebas.
 *   2. Variable de entorno.
 *   3. Archivo ".env" en la carpeta del proyecto (copie ".env.example" a ".env").
 *
 * Claves (todas obligatorias, no hay valores por defecto):
 *
 *   DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
 *   DB_URL   (opcional: URL JDBC completa; si existe, ignora host, puerto y nombre)
 *
 * Las PRUEBAS no usan MySQL: Maven define las propiedades db.url, db.user y
 * db.password (ver pom.xml) para apuntar a una base H2 en memoria. Por eso el
 * código de la aplicación siempre pide la conexión con ConexionBD.conectar().
 *
 * Esta clase YA está completa: no es necesario modificarla.
 */
public final class ConexionBD {

    private ConexionBD() {
    }

    private static Map<String, String> archivoEnv;

    /** Lee el archivo ".env" una sola vez. Si no existe o no se puede leer, queda vacío. */
    private static synchronized Map<String, String> archivoEnv() {
        if (archivoEnv == null) {
            archivoEnv = new HashMap<>();
            try {
                for (String linea : Files.readAllLines(Path.of(".env"), StandardCharsets.UTF_8)) {
                    linea = linea.strip(); // también quita el \r de los archivos de Windows
                    int igual = linea.indexOf('=');
                    if (linea.isEmpty() || linea.startsWith("#") || igual < 1) {
                        continue;
                    }
                    String valor = linea.substring(igual + 1).strip();
                    if (valor.length() >= 2 && (valor.startsWith("\"") && valor.endsWith("\"")
                            || valor.startsWith("'") && valor.endsWith("'"))) {
                        valor = valor.substring(1, valor.length() - 1);
                    }
                    archivoEnv.put(linea.substring(0, igual).strip(), valor);
                }
            } catch (IOException | RuntimeException e) {
                // Sin ".env" (o ilegible) se usan los valores por defecto.
            }
        }
        return archivoEnv;
    }

    /** Variable de entorno, luego archivo ".env"; null si no existe o está vacía. */
    private static String variable(String nombre) {
        String v = System.getenv(nombre);
        if (v == null || v.isBlank()) {
            v = archivoEnv().get(nombre);
        }
        return (v == null || v.isBlank()) ? null : v;
    }

    /** Propiedad del sistema (la usan las pruebas); si no existe, la variable. */
    private static String propiedadOVariable(String propiedad, String variable) {
        String v = System.getProperty(propiedad);
        return (v == null || v.isBlank()) ? variable(variable) : v;
    }

    private static String obligatoria(String propiedad, String variable) throws SQLException {
        String v = propiedadOVariable(propiedad, variable);
        if (v == null) {
            throw new SQLException("Falta la configuración " + variable
                    + ". Copie .env.example a .env (en la carpeta del proyecto) y complete todos los valores.");
        }
        return v;
    }

    private static String url() throws SQLException {
        String url = propiedadOVariable("db.url", "DB_URL");
        if (url != null) {
            return url;
        }
        return "jdbc:mysql://" + obligatoria("db.host", "DB_HOST") + ":" + obligatoria("db.port", "DB_PORT")
                + "/" + obligatoria("db.name", "DB_NAME")
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    }

    /** Abre una conexión nueva. Quien la pide es responsable de cerrarla (try-with-resources). */
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(url(),
                obligatoria("db.user", "DB_USER"),
                obligatoria("db.password", "DB_PASSWORD"));
    }
}
