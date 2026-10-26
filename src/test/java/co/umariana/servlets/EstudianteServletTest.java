package co.umariana.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.umariana.dao.EstudianteDAO;
import co.umariana.modelo.Estudiante;
import co.umariana.util.BaseDeDatosDePrueba;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Pruebas del controlador (EstudianteServlet). No hay navegador ni servidor: la
 * petición y la respuesta HTTP se simulan con Mockito, y el DAO usa una base H2
 * en memoria.
 * NO MODIFIQUE ESTE ARCHIVO: haga que pasen escribiendo código en EstudianteServlet.java.
 *
 * Ejecutar una etapa:   ./scripts/test.sh -Dtest='EstudianteServletTest$Etapa6_Mostrar'
 */
class EstudianteServletTest {

    EstudianteDAO dao;
    EstudianteServlet servlet;
    HttpServletRequest request;
    HttpServletResponse response;
    RequestDispatcher dispatcherListar;
    RequestDispatcher dispatcherFormulario;
    Map<String, String> parametros;

    @BeforeEach
    void preparar() {
        BaseDeDatosDePrueba.limpiar();
        dao = new EstudianteDAO();
        servlet = new EstudianteServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcherListar = mock(RequestDispatcher.class);
        dispatcherFormulario = mock(RequestDispatcher.class);
        parametros = new HashMap<>();

        when(request.getParameter(anyString())).thenAnswer(inv -> parametros.get(inv.<String>getArgument(0)));
        when(request.getRequestDispatcher("/WEB-INF/vistas/listar.jsp")).thenReturn(dispatcherListar);
        when(request.getRequestDispatcher("/WEB-INF/vistas/formulario.jsp")).thenReturn(dispatcherFormulario);
    }

    private void parametro(String nombre, String valor) {
        parametros.put(nombre, valor);
    }

    /** Guarda un estudiante directamente con el DAO y lo devuelve ya con su id. */
    private Estudiante guardar(String nombre, String correo, int semestre) {
        dao.registrar(new Estudiante(0, nombre, correo, semestre));
        List<Estudiante> lista = dao.listar();
        return lista.get(lista.size() - 1);
    }

    private void formulario(String accion, String id, String nombre, String correo, String semestre) {
        parametro("accion", accion);
        if (id != null) {
            parametro("id", id);
        }
        parametro("nombre", nombre);
        parametro("correo", correo);
        parametro("semestre", semestre);
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Etapa 6. Mostrar (GET)")
    class Etapa6_Mostrar {

        @Test
        void sinAccionMuestraElListado() throws Exception {
            guardar("Ana", "ana@x.com", 1);
            guardar("Luis", "luis@x.com", 2);

            servlet.doGet(request, response);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<Estudiante>> captor = ArgumentCaptor.forClass(List.class);
            verify(request).setAttribute(eq("estudiantes"), captor.capture());
            assertEquals(2, captor.getValue().size());
            verify(dispatcherListar).forward(request, response);
        }

        @Test
        void accionListarTambienMuestraElListado() throws Exception {
            parametro("accion", "listar");
            servlet.doGet(request, response);
            verify(dispatcherListar).forward(request, response);
        }

        @Test
        void accionNuevoMuestraElFormularioVacio() throws Exception {
            parametro("accion", "nuevo");
            servlet.doGet(request, response);
            verify(dispatcherFormulario).forward(request, response);
            verify(request, never()).setAttribute(eq("estudiante"), any());
        }

        @Test
        void accionEditarMuestraElFormularioConElEstudiante() throws Exception {
            Estudiante e = guardar("Ana", "ana@x.com", 3);
            parametro("accion", "editar");
            parametro("id", String.valueOf(e.getId()));

            servlet.doGet(request, response);

            ArgumentCaptor<Estudiante> captor = ArgumentCaptor.forClass(Estudiante.class);
            verify(request).setAttribute(eq("estudiante"), captor.capture());
            assertEquals("Ana", captor.getValue().getNombre());
            verify(dispatcherFormulario).forward(request, response);
        }

        @Test
        void editarUnIdInexistenteResponde404() throws Exception {
            parametro("accion", "editar");
            parametro("id", "99");
            servlet.doGet(request, response);
            verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
            verify(dispatcherFormulario, never()).forward(any(), any());
        }

        @Test
        void editarConUnIdQueNoEsNumeroResponde404() throws Exception {
            parametro("accion", "editar");
            parametro("id", "abc");
            servlet.doGet(request, response);
            verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        @Test
        void editarSinIdResponde404() throws Exception {
            parametro("accion", "editar");
            servlet.doGet(request, response);
            verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        @Test
        void unaAccionDesconocidaResponde400() throws Exception {
            parametro("accion", "borrarTodo");
            servlet.doGet(request, response);
            verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Etapa 7. Crear, actualizar y eliminar (POST)")
    class Etapa7_Modificar {

        @Test
        void usaCodificacionUtf8() throws Exception {
            formulario("crear", null, "Peña", "p@x.com", "1");
            servlet.doPost(request, response);
            verify(request).setCharacterEncoding("UTF-8");
        }

        @Test
        void crearGuardaElEstudianteYRedirige() throws Exception {
            formulario("crear", null, "Ana Pérez", "ana@x.com", "4");

            servlet.doPost(request, response);

            assertEquals(1, dao.listar().size());
            Estudiante e = dao.listar().get(0);
            assertEquals("Ana Pérez", e.getNombre());
            assertEquals("ana@x.com", e.getCorreo());
            assertEquals(4, e.getSemestre());
            verify(response).sendRedirect("estudiantes");
        }

        @Test
        void crearGuardaNombreYCorreoSinEspaciosSobrantes() throws Exception {
            formulario("crear", null, "  Ana  ", "  ana@x.com ", "4");
            servlet.doPost(request, response);
            assertEquals("Ana", dao.listar().get(0).getNombre());
            assertEquals("ana@x.com", dao.listar().get(0).getCorreo());
        }

        @Test
        void crearConDatosInvalidosMuestraElErrorYNoRedirige() throws Exception {
            formulario("crear", null, "Ana", "correo-malo", "1");

            servlet.doPost(request, response);

            assertTrue(dao.listar().isEmpty());
            verify(request).setAttribute("error", "El correo no es válido");
            verify(dispatcherFormulario).forward(request, response);
            verify(response, never()).sendRedirect(anyString());
        }

        @Test
        void crearConSemestreFueraDeRangoMuestraElError() throws Exception {
            formulario("crear", null, "Ana", "ana@x.com", "11");
            servlet.doPost(request, response);
            assertTrue(dao.listar().isEmpty());
            verify(request).setAttribute(eq("error"), anyString());
            verify(dispatcherFormulario).forward(request, response);
        }

        @Test
        void crearConUnNumeroMalEscritoMuestraElError() throws Exception {
            formulario("crear", null, "Ana", "ana@x.com", "abc");
            servlet.doPost(request, response);
            assertTrue(dao.listar().isEmpty());
            verify(request).setAttribute(eq("error"), anyString());
            verify(dispatcherFormulario).forward(request, response);
        }

        @Test
        void crearSinParametrosMuestraElErrorYNoFalla() throws Exception {
            parametro("accion", "crear");
            servlet.doPost(request, response);
            assertTrue(dao.listar().isEmpty());
            verify(request).setAttribute(eq("error"), anyString());
            verify(dispatcherFormulario).forward(request, response);
        }

        @Test
        void crearConUnCorreoRepetidoMuestraElError() throws Exception {
            guardar("Ana", "ana@x.com", 1);
            formulario("crear", null, "Otra", "ana@x.com", "2");

            servlet.doPost(request, response);

            assertEquals(1, dao.listar().size());
            verify(request).setAttribute(eq("error"), anyString());
            verify(dispatcherFormulario).forward(request, response);
            verify(response, never()).sendRedirect(anyString());
        }

        @Test
        void actualizarCambiaElEstudianteYRedirige() throws Exception {
            Estudiante e = guardar("Ana", "ana@x.com", 1);
            formulario("actualizar", String.valueOf(e.getId()), "Ana María", "anam@x.com", "5");

            servlet.doPost(request, response);

            Estudiante leido = dao.buscarPorId(e.getId());
            assertEquals("Ana María", leido.getNombre());
            assertEquals("anam@x.com", leido.getCorreo());
            assertEquals(5, leido.getSemestre());
            verify(response).sendRedirect("estudiantes");
        }

        @Test
        void actualizarUnIdInexistenteResponde404() throws Exception {
            formulario("actualizar", "99", "X", "x@x.com", "1");
            servlet.doPost(request, response);
            verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
            verify(response, never()).sendRedirect(anyString());
        }

        @Test
        void actualizarConUnIdQueNoEsNumeroResponde404() throws Exception {
            formulario("actualizar", "abc", "X", "x@x.com", "1");
            servlet.doPost(request, response);
            verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        @Test
        void actualizarConDatosInvalidosMuestraElErrorYNoCambiaNada() throws Exception {
            Estudiante e = guardar("Ana", "ana@x.com", 1);
            formulario("actualizar", String.valueOf(e.getId()), "", "x@x.com", "1");

            servlet.doPost(request, response);

            assertEquals("Ana", dao.buscarPorId(e.getId()).getNombre());
            verify(request).setAttribute("error", "El nombre es obligatorio");
            verify(dispatcherFormulario).forward(request, response);
        }

        @Test
        void actualizarConElCorreoDeOtroMuestraElErrorYNoCambiaNada() throws Exception {
            guardar("A", "a@x.com", 1);
            Estudiante b = guardar("B", "b@x.com", 1);
            formulario("actualizar", String.valueOf(b.getId()), "B", "a@x.com", "1");

            servlet.doPost(request, response);

            assertEquals("b@x.com", dao.buscarPorId(b.getId()).getCorreo());
            verify(request).setAttribute(eq("error"), anyString());
            verify(dispatcherFormulario).forward(request, response);
            verify(response, never()).sendRedirect(anyString());
        }

        @Test
        void eliminarQuitaElEstudianteYRedirige() throws Exception {
            Estudiante a = guardar("A", "a@x.com", 1);
            Estudiante b = guardar("B", "b@x.com", 2);
            parametro("accion", "eliminar");
            parametro("id", String.valueOf(a.getId()));

            servlet.doPost(request, response);

            assertEquals(1, dao.listar().size());
            assertEquals(b.getId(), dao.listar().get(0).getId());
            verify(response).sendRedirect("estudiantes");
        }

        @Test
        void eliminarUnIdInexistenteResponde404() throws Exception {
            parametro("accion", "eliminar");
            parametro("id", "99");
            servlet.doPost(request, response);
            verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        @Test
        void eliminarConUnIdQueNoEsNumeroResponde404() throws Exception {
            parametro("accion", "eliminar");
            parametro("id", "abc");
            servlet.doPost(request, response);
            verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        @Test
        void unaAccionDesconocidaResponde400() throws Exception {
            parametro("accion", "explotar");
            servlet.doPost(request, response);
            verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        @Test
        void sinAccionResponde400() throws Exception {
            servlet.doPost(request, response);
            verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
