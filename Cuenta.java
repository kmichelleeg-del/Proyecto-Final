public class Cuenta {

    private String usuario;
    private String rol;
    private String carne;

    public Cuenta(String usuario, String rol, String carne) {
        this.usuario = usuario;
        this.rol = rol;
        this.carne = carne;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getRol() {
        return rol;
    }

    public String getCarne() {
        return carne;
    }

    public boolean esCoordinador() {
        return "COORDINADOR".equals(rol);
    }
}