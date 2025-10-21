package psp_dam_acteva03b;

/**
 * Clase que representa un pedido en el restaurante
 */
public class Pedido {
    private int id;
    private String nombre_plato;
    
    public Pedido(int id, String nombre_plato) {
        this.id = id;
        this.nombre_plato = nombre_plato;
    }
    
    public int get_id() {
        return id;
    }
    
    public String get_nombre_plato() {
        return nombre_plato;
    }
    
    @Override
    public String toString() {
        return "Pedido #" + id + ": " + nombre_plato;
    }
}