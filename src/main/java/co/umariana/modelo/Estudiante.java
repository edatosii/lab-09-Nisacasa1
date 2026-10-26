package co.umariana.modelo;

/**
 * Un estudiante.
 *
 * Esta clase YA está completa: no es necesario modificarla.
 */
public class Estudiante {

    private int id;
    private String nombre;
    private String correo;
    private int semestre;

    public Estudiante(int id, String nombre, String correo, int semestre) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.semestre = semestre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }
}
