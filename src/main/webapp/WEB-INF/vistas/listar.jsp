<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="co.umariana.modelo.Estudiante" %>
<%@ page import="co.umariana.util.Html" %>
<%
    @SuppressWarnings("unchecked")
    List<Estudiante> estudiantes = (List<Estudiante>) request.getAttribute("estudiantes");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Estudiantes</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
    <header>
        <h1>Estudiantes</h1>
    </header>
    <main>
        <p><a class="boton" href="estudiantes?accion=nuevo">Nuevo estudiante</a></p>
        <table>
            <thead>
                <tr><th>Id</th><th>Nombre</th><th>Correo</th><th>Semestre</th><th>Acciones</th></tr>
            </thead>
            <tbody>
                <% for (Estudiante e : estudiantes) { %>
                <tr>
                    <td><%= e.getId() %></td>
                    <td><%= Html.escapar(e.getNombre()) %></td>
                    <td><%= Html.escapar(e.getCorreo()) %></td>
                    <td><%= e.getSemestre() %></td>
                    <td class="acciones">
                        <a href="estudiantes?accion=editar&id=<%= e.getId() %>">Editar</a>
                        <form action="estudiantes" method="post"
                              onsubmit="return confirm('¿Eliminar este estudiante?');">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="id" value="<%= e.getId() %>">
                            <button type="submit" class="peligro">Eliminar</button>
                        </form>
                    </td>
                </tr>
                <% } %>
                <% if (estudiantes.isEmpty()) { %>
                <tr><td colspan="5" class="vacio">Aún no hay estudiantes registrados.</td></tr>
                <% } %>
            </tbody>
        </table>
    </main>
</body>
</html>
