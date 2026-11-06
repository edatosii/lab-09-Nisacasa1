
package co.umariana.dao;

import co.umariana.conexion.ConexionBD;
import co.umariana.modelo.Estudiante;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object): la única clase que habla con la base de datos.
 * Aquí viven las operaciones del CRUD sobre la tabla "estudiantes".
 */
public class EstudianteDAO {

    // ETAPA 1. Registrar (INSERT)

    /**
     * Inserta un estudiante nuevo.
     *
     * @return true si se insertó; false si hubo un error.
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

    // ETAPA 2. Listar (SELECT)

    /**
     * @return todos los estudiantes ordenados por id;
     * lista vacía si no hay resultados o si ocurre un error.
     */
    public List<Estudiante> listar() {
        List<Estudiante> estudiantes = new ArrayList<>();
        String sql = "SELECT id, nombre, correo, semestre FROM estudiantes ORDER BY id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Estudiante estudiante = new Estudiante(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getInt("semestre")
                );

                estudiantes.add(estudiante);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar estudiantes: " + e.getMessage());
            return new ArrayList<>();
        }

        return estudiantes;
    }

    // ETAPA 3. Buscar por id (SELECT ... WHERE)

    /**
     * @return el estudiante con ese id, o null si no existe
     * o si ocurre un error.
     */
    public Estudiante buscarPorId(int id) {
        String sql = "SELECT id, nombre, correo, semestre FROM estudiantes WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Estudiante(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getInt("semestre")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar estudiante: " + e.getMessage());
        }

        return null;
    }

    // ETAPA 4. Actualizar (UPDATE)

    /**
     * Cambia nombre, correo y semestre del estudiante.
     *
     * @return true si se actualizó; false si no existe o hubo un error.
     */
    public boolean actualizar(Estudiante estudiante) {
        String sql = "UPDATE estudiantes SET nombre = ?, correo = ?, semestre = ? WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getCorreo());
            ps.setInt(3, estudiante.getSemestre());
            ps.setInt(4, estudiante.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar estudiante: " + e.getMessage());
            return false;
        }
    }

    // ETAPA 5. Eliminar (DELETE)

    /**
     * @return true si se eliminó; false si no existía o hubo un error.
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM estudiantes WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar estudiante: " + e.getMessage());
            return false;
        }
    }
}
