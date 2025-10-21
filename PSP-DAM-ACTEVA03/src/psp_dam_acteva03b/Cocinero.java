package psp_dam_acteva03b;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Clase que representa un cocinero (hilo) que procesa pedidos
 */
public class Cocinero extends Thread {
    private String nombre_cocinero;
    private List<Pedido> lista_pedidos;
    private Object lock_pedidos;
    private Object lock_archivo;
    private String nombre_archivo;
    
    public Cocinero(String nombre_cocinero, List<Pedido> lista_pedidos, 
        Object lock_pedidos, Object lock_archivo, String nombre_archivo) {
        this.nombre_cocinero = nombre_cocinero;
        this.lista_pedidos = lista_pedidos;
        this.lock_pedidos = lock_pedidos;
        this.lock_archivo = lock_archivo;
        this.nombre_archivo = nombre_archivo;
    }
    
    @Override
    public void run() {
        while (true) {
            Pedido pedido_actual = null;
            
            // Sección crítica: obtener pedido de la lista compartida
            synchronized (lock_pedidos) {
                if (lista_pedidos.isEmpty()) {
                    break; // No hay más pedidos
                }
                pedido_actual = lista_pedidos.remove(0);
            }
            
            // Preparar el pedido (fuera de la sección crítica)
            preparar_pedido(pedido_actual);
            
            // Sección crítica: escribir en el archivo
            synchronized (lock_archivo) {
                registrar_en_log(pedido_actual);
            }
        }
    }
    
    private void preparar_pedido(Pedido pedido) {
        System.out.println(nombre_cocinero + " está preparando " + pedido);
        
        try {
            // Simulación del tiempo de preparación (2-4 segundos)
            Thread.sleep((long) (2000 + Math.random() * 2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println(nombre_cocinero + " ha terminado " + pedido);
    }
    
    private void registrar_en_log(Pedido pedido) {
        try (FileWriter writer = new FileWriter(nombre_archivo, true)) {
            String log_entrada = nombre_cocinero + " procesó " + pedido + "\n";
            writer.write(log_entrada);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }
}