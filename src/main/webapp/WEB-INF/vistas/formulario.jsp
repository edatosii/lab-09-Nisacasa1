<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="co.umariana.modelo.Estudiante" %>
<%@ page import="co.umariana.util.Html" %>
<%
    Estudiante estudiante = (Estudiante) request.getAttribute("estudiante");
    String error = (String) request.getAttribute("error");

    // Si hubo un error de validación, se vuelven a mostrar los valores escritos.
    String id = estudiante != null ? String.valueOf(estudiante.getId()) : request.getParameter("id");
    String nombre = estudiante != null ? estudiante.getNombre() : request.getParameter("nombre");
    String correo = estudiante != null ? estudiante.getCorreo() : request.getParameter("correo");
    String semestre = estudiante != null ? String.valueOf(estudiante.getSemestre()) : request.getParameter("semestre");
    boolean edicion = id != null && !id.isEmpty();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= edicion ? "Editar estudiante" : "Nuevo estudiante" %></title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
    <header>
        <h1><%= edicion ? "Editar estudiante" : "Nuevo estudiante" %></h1>
    </header>
    <main>
        <% if (error != null) { %>
        <p class="error"><%= Html.escapar(error) %></p>
        <% } %>
        <form action="estudiantes" method="post">
            <input type="hidden" name="accion" value="<%= edicion ? "actualizar" : "crear" %>">
            <% if (edicion) { %>
            <input type="hidden" name="id" value="<%= Html.escapar(id) %>">
            <% } %>
            <p>
                <label for="nombre">Nombre</label>
                <input type="text" id="nombre" name="nombre" value="<%= Html.escapar(nombre) %>" required>
            </p>
            <p>
                <label for="correo">Correo</label>
                <input type="email" id="correo" name="correo" value="<%= Html.escapar(correo) %>" required>
            </p>
            <p>
                <label for="semestre">Semestre (1 a 10)</label>
                <input type="number" id="semestre" name="semestre" min="1" max="10" value="<%= Html.escapar(semestre) %>" required>
            </p>
            <button type="submit">Guardar</button>
            <a href="estudiantes">Cancelar</a>
        </form>
    </main>
</body>
</html>
