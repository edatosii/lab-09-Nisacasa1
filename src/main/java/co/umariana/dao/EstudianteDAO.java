package co.umariana.dao;

import co.umariana.conexion.ConexionBD;
import co.umariana.modelo.Estudiante;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO (Data Access Object): la única clase que habla con la base de datos.
 * Aquí viven las operaciones del CRUD sobre la tabla "estudiantes"
 * (ver db/schema.sql). Se construye igual que ClienteDAO visto en clase.
 *
 * INSTRUCCIONES PARA EL ESTUDIANTE
 * --------------------------------
 * 1. Cada método marcado con "TODO" lanza UnsupportedOperationException.
 *    Reemplace esa línea por su implementación con JDBC.
 * 2. NO modifique las pruebas (src/test). Las pruebas NO necesitan MySQL:
 *    usan una base de datos H2 en memoria.
 * 3. La Etapa 1 (registrar) YA está resuelta: úsela como modelo para las demás.
 *    Resuelva el resto en orden (Etapa 2, 3...): cada etapa usa las anteriores.
 *
 * PATRÓN (el mismo de ClienteDAO.registrar):
 *
 *     String sql = "INSERT INTO ... VALUES (?, ?, ?)";
 *     try (Connection con = ConexionBD.conectar();
 *          PreparedStatement ps = con.prepareStatement(sql)) {
 *         ps.setString(1, ...);
 *         return ps.executeUpdate() > 0;
 *     } catch (SQLException e) {
 *         System.out.println("Error al registrar: " + e.getMessage());
 *         return false;
 *     }
 *
 * REGLAS (las pruebas las verifican)
 *  - Los métodos NO lanzan SQLException: la capturan, imprimen el mensaje con
 *    System.out.println y devuelven un valor "de error" (false, lista vacía o null).
 *  - Use SIEMPRE PreparedStatement con parámetros (?), nunca concatene datos
 *    dentro del SQL (inyección SQL).
 *  - El id lo genera la base de datos (AUTO_INCREMENT): al registrar se ignora
 *    el id del objeto Estudiante.
 *  - El correo es UNIQUE: registrar o actualizar con un correo repetido provoca un
 *    SQLException; el DAO lo captura y devuelve false (no se lanza ninguna excepción).
 */
public class EstudianteDAO {

    // =====================================================================
    // ETAPA 1. Registrar (INSERT)  -- YA RESUELTA, úsela como modelo
    // =====================================================================

    /**
     * Inserta un estudiante nuevo (nombre, correo, semestre).
     *
     * @return true si se insertó; false si hubo un error (por ejemplo, correo repetido)
     */
    public boolean registrar(Estudiante estudiante) {
        String sql = "INSERT INTO estudiantes (nombre, correo, semestre) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getCorreo());
            ps.setInt(3, estudiante.getSemestre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar: " + e.getMessage());
            return false;
        }
    }

    // =====================================================================
    // ETAPA 2. Listar (SELECT)
    // =====================================================================

    /** @return todos los estudiantes ordenados por id; lista vacía si no hay o si hay un error */
    public List<Estudiante> listar() {
        // TODO Etapa 2: SELECT id, nombre, correo, semestre FROM estudiantes ORDER BY id
        //               Use executeQuery() y recorra el ResultSet con while (rs.next()),
        //               creando un Estudiante por cada fila y agregándolo a un ArrayList.
        throw new UnsupportedOperationException("TODO: listar");
    }

    // =====================================================================
    // ETAPA 3. Buscar por id (SELECT ... WHERE)
    // =====================================================================

    /** @return el estudiante con ese id, o null si no existe (o si hay un error) */
    public Estudiante buscarPorId(int id) {
        // TODO Etapa 3: SELECT id, nombre, correo, semestre FROM estudiantes WHERE id = ?
        throw new UnsupportedOperationException("TODO: buscarPorId");
    }

    // =====================================================================
    // ETAPA 4. Actualizar (UPDATE)
    // =====================================================================

    /**
     * Cambia nombre, correo y semestre del estudiante cuyo id es estudiante.getId().
     *
     * @return true si se actualizó; false si no existe ese id o si hubo un error
     *         (por ejemplo, correo ya usado por OTRO estudiante)
     */
    public boolean actualizar(Estudiante estudiante) {
        // TODO Etapa 4: UPDATE estudiantes SET nombre = ?, correo = ?, semestre = ? WHERE id = ?
        throw new UnsupportedOperationException("TODO: actualizar");
    }

    // =====================================================================
    // ETAPA 5. Eliminar (DELETE)
    // =====================================================================

    /** @return true si se eliminó; false si no existía ese id o si hubo un error */
    public boolean eliminar(int id) {
        // TODO Etapa 5: DELETE FROM estudiantes WHERE id = ?
        throw new UnsupportedOperationException("TODO: eliminar");
    }
}
