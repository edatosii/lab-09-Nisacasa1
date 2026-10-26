package co.umariana.dao;

import static org.junit.jupiter.api.Assertions.*;

import co.umariana.modelo.Estudiante;
import co.umariana.util.BaseDeDatosDePrueba;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del acceso a datos (EstudianteDAO) contra una base H2 en memoria.
 * NO MODIFIQUE ESTE ARCHIVO: haga que pasen escribiendo código en EstudianteDAO.java.
 *
 * Ejecutar todas:       ./scripts/test.sh
 * Ejecutar una etapa:   ./scripts/test.sh -Dtest='EstudianteDAOTest$Etapa1_Registrar'
 */
class EstudianteDAOTest {

    EstudianteDAO dao;

    @BeforeEach
    void preparar() {
        BaseDeDatosDePrueba.limpiar();
        dao = new EstudianteDAO();
    }

    /** El id lo genera la base de datos: se ignora el que traiga el objeto. */
    private static Estudiante est(String nombre, String correo, int semestre) {
        return new Estudiante(0, nombre, correo, semestre);
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Etapa 1. Registrar")
    class Etapa1_Registrar {

        @Test
        void registraUnEstudianteYRetornaTrue() {
            assertTrue(dao.registrar(est("Ana Pérez", "ana@correo.com", 3)));
        }

        @Test
        void elEstudianteQuedaGuardadoConTodosSusDatos() {
            dao.registrar(est("Ana Pérez", "ana@correo.com", 3));
            List<Estudiante> lista = dao.listar();
            assertEquals(1, lista.size());
            assertEquals("Ana Pérez", lista.get(0).getNombre());
            assertEquals("ana@correo.com", lista.get(0).getCorreo());
            assertEquals(3, lista.get(0).getSemestre());
        }

        @Test
        void laBaseDeDatosGeneraIdsConsecutivos() {
            dao.registrar(est("A", "a@x.com", 1));
            dao.registrar(est("B", "b@x.com", 1));
            dao.registrar(est("C", "c@x.com", 1));
            List<Estudiante> lista = dao.listar();
            assertEquals(1, lista.get(0).getId());
            assertEquals(2, lista.get(1).getId());
            assertEquals(3, lista.get(2).getId());
        }

        @Test
        void ignoraElIdQueTraeElObjeto() {
            dao.registrar(new Estudiante(999, "A", "a@x.com", 1));
            assertEquals(1, dao.listar().get(0).getId());
        }

        @Test
        void guardaTildesYEnies() {
            dao.registrar(est("Peña Muñoz", "pena@x.com", 2));
            assertEquals("Peña Muñoz", dao.listar().get(0).getNombre());
        }

        @Test
        void conUnCorreoRepetidoRetornaFalseYNoGuarda() {
            assertTrue(dao.registrar(est("Ana", "ana@x.com", 1)));
            assertFalse(dao.registrar(est("Otra Ana", "ana@x.com", 2)));
            assertEquals(1, dao.listar().size());
        }

        @Test
        void unCorreoRepetidoNoLanzaExcepcion() {
            dao.registrar(est("Ana", "ana@x.com", 1));
            assertDoesNotThrow(() -> dao.registrar(est("Otra", "ana@x.com", 2)));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Etapa 2. Listar")
    class Etapa2_Listar {

        @Test
        void listaVaciaAlInicio() {
            assertTrue(dao.listar().isEmpty());
        }

        @Test
        void listaLosEstudiantesOrdenadosPorId() {
            dao.registrar(est("A", "a@x.com", 1));
            dao.registrar(est("B", "b@x.com", 2));
            dao.registrar(est("C", "c@x.com", 3));
            List<Estudiante> lista = dao.listar();
            assertEquals(3, lista.size());
            assertEquals("A", lista.get(0).getNombre());
            assertEquals("B", lista.get(1).getNombre());
            assertEquals("C", lista.get(2).getNombre());
        }

        @Test
        void cargaTodosLosCampos() {
            dao.registrar(est("Ana", "ana@x.com", 7));
            Estudiante e = dao.listar().get(0);
            assertEquals(1, e.getId());
            assertEquals("Ana", e.getNombre());
            assertEquals("ana@x.com", e.getCorreo());
            assertEquals(7, e.getSemestre());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Etapa 3. Buscar por id")
    class Etapa3_Buscar {

        @Test
        void encuentraUnEstudianteExistente() {
            dao.registrar(est("A", "a@x.com", 1));
            dao.registrar(est("B", "b@x.com", 2));
            Estudiante b = dao.buscarPorId(2);
            assertNotNull(b);
            assertEquals(2, b.getId());
            assertEquals("B", b.getNombre());
            assertEquals("b@x.com", b.getCorreo());
            assertEquals(2, b.getSemestre());
        }

        @Test
        void retornaNullSiNoExiste() {
            dao.registrar(est("A", "a@x.com", 1));
            assertNull(dao.buscarPorId(99));
            assertNull(dao.buscarPorId(0));
            assertNull(dao.buscarPorId(-1));
        }

        @Test
        void retornaNullEnTablaVacia() {
            assertNull(dao.buscarPorId(1));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Etapa 4. Actualizar")
    class Etapa4_Actualizar {

        @Test
        void actualizaLosDatosYRetornaTrue() {
            dao.registrar(est("Ana", "ana@x.com", 1));
            assertTrue(dao.actualizar(new Estudiante(1, "Ana María", "anam@x.com", 4)));
            Estudiante leido = dao.buscarPorId(1);
            assertEquals("Ana María", leido.getNombre());
            assertEquals("anam@x.com", leido.getCorreo());
            assertEquals(4, leido.getSemestre());
        }

        @Test
        void actualizaSoloElEstudianteIndicado() {
            dao.registrar(est("A", "a@x.com", 1));
            dao.registrar(est("B", "b@x.com", 2));
            dao.actualizar(new Estudiante(2, "B2", "b2@x.com", 3));
            assertEquals("A", dao.buscarPorId(1).getNombre());
            assertEquals(2, dao.listar().size());
        }

        @Test
        void permiteGuardarElMismoCorreoDelPropioEstudiante() {
            dao.registrar(est("Ana", "ana@x.com", 1));
            assertTrue(dao.actualizar(new Estudiante(1, "Ana Nueva", "ana@x.com", 2)));
        }

        @Test
        void retornaFalseSiElIdNoExiste() {
            dao.registrar(est("A", "a@x.com", 1));
            assertFalse(dao.actualizar(new Estudiante(99, "X", "x@x.com", 1)));
        }

        @Test
        void conElCorreoDeOtroEstudianteRetornaFalseYNoCambiaNada() {
            dao.registrar(est("A", "a@x.com", 1));
            dao.registrar(est("B", "b@x.com", 1));
            assertFalse(dao.actualizar(new Estudiante(2, "B", "a@x.com", 1)));
            assertEquals("b@x.com", dao.buscarPorId(2).getCorreo());
        }

        @Test
        void unCorreoRepetidoNoLanzaExcepcion() {
            dao.registrar(est("A", "a@x.com", 1));
            dao.registrar(est("B", "b@x.com", 1));
            assertDoesNotThrow(() -> dao.actualizar(new Estudiante(2, "B", "a@x.com", 1)));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Etapa 5. Eliminar")
    class Etapa5_Eliminar {

        @Test
        void eliminaYRetornaTrue() {
            dao.registrar(est("A", "a@x.com", 1));
            assertTrue(dao.eliminar(1));
            assertTrue(dao.listar().isEmpty());
            assertNull(dao.buscarPorId(1));
        }

        @Test
        void eliminaSoloElEstudianteIndicado() {
            dao.registrar(est("A", "a@x.com", 1));
            dao.registrar(est("B", "b@x.com", 2));
            dao.registrar(est("C", "c@x.com", 3));
            dao.eliminar(2);
            List<Estudiante> lista = dao.listar();
            assertEquals(2, lista.size());
            assertEquals(1, lista.get(0).getId());
            assertEquals(3, lista.get(1).getId());
        }

        @Test
        void retornaFalseSiNoExiste() {
            dao.registrar(est("A", "a@x.com", 1));
            assertFalse(dao.eliminar(99));
            assertEquals(1, dao.listar().size());
        }

        @Test
        void eliminarDosVecesElMismoIdRetornaFalseLaSegunda() {
            dao.registrar(est("A", "a@x.com", 1));
            assertTrue(dao.eliminar(1));
            assertFalse(dao.eliminar(1));
        }

        @Test
        void despuesDeEliminarSePuedeVolverAUsarElCorreo() {
            dao.registrar(est("A", "a@x.com", 1));
            dao.eliminar(1);
            assertTrue(dao.registrar(est("A de nuevo", "a@x.com", 1)));
        }
    }
}
