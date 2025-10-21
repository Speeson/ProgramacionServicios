package psp_dam_acteva03b;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal que gestiona la cocina y los cocineros
 */
public class Cocina {
    private static final String NOMBRE_ARCHIVO = "log_pedidos.txt";
    private static final int NUM_COCINEROS = 3;
    
    private List<Pedido> lista_pedidos;
    private Object lock_pedidos;
    private Object lock_archivo;
    
    public Cocina() {
        this.lista_pedidos = new ArrayList<>();
        this.lock_pedidos = new Object();
        this.lock_archivo = new Object();
        inicializar_archivo();
    }
    
    private void inicializar_archivo() {
        try {
            File archivo = new File(NOMBRE_ARCHIVO);
            if (archivo.exists()) {
                archivo.delete();
            }
            archivo.createNewFile();
            
            try (FileWriter writer = new FileWriter(NOMBRE_ARCHIVO)) {
                writer.write("=== LOG DE PEDIDOS ===\n");
                writer.write("Inicio de la simulación\n\n");
            }
        } catch (IOException e) {
            System.err.println("Error al inicializar el archivo: " + e.getMessage());
        }
    }
    
    public void agregar_pedido(Pedido pedido) {
        lista_pedidos.add(pedido);
    }
    
    public void iniciar_servicio() {
        System.out.println("=== INICIANDO SERVICIO DE COCINA ===");
        System.out.println("Pedidos totales: " + lista_pedidos.size());
        System.out.println("Cocineros disponibles: " + NUM_COCINEROS);
        System.out.println("=====================================\n");
        
        // Crear y lanzar los hilos de los cocineros
        Cocinero[] cocineros = new Cocinero[NUM_COCINEROS];
        
        for (int i = 0; i < NUM_COCINEROS; i++) {
            cocineros[i] = new Cocinero(
                "Cocinero-" + (i + 1),
                lista_pedidos,
                lock_pedidos,
                lock_archivo,
                NOMBRE_ARCHIVO
            );
            cocineros[i].start();
        }
        
        // Esperar a que todos los cocineros terminen
        for (int i = 0; i < NUM_COCINEROS; i++) {
            try {
                cocineros[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("\n=====================================");
        System.out.println("Todos los pedidos han sido procesados.");
        System.out.println("=====================================");
    }
    
    public static void main(String[] args) {
        Cocina cocina = new Cocina();
        
        // Lista de platos típicos españoles
        String[] platos = {
            "Tortilla de Patatas",
            "Gazpacho Andaluz",
            "Croquetas de Jamón",
            "Paella Valenciana",
            "Pulpo a la Gallega",
            "Fabada Asturiana",
            "Cochinillo Segoviano",
            "Jamón Ibérico",
            "Crema Catalana",
            "Patatas Bravas",
            "Gambas al Ajillo",
            "Salmorejo Cordobés",
            "Chuletón de Buey",
            "Marmitako",
            "Pisto Manchego",
            "Bacalao al Pil Pil",
            "Callos a la Madrileña",
            "Migas Extremeñas",
            "Rabo de Toro",
            "Churros con Chocolate"
        };
        
        // Generar número aleatorio de pedidos entre 6 y 10
        int num_pedidos = 6 + (int)(Math.random() * 5); // Entre 6 y 10
        
        System.out.println("Generando " + num_pedidos + " pedidos aleatorios...\n");
        
        // Crear pedidos con platos aleatorios
        for (int i = 0; i < num_pedidos; i++) {
            int indice_aleatorio = (int)(Math.random() * platos.length);
            cocina.agregar_pedido(new Pedido(i + 1, platos[indice_aleatorio]));
        }
        
        cocina.iniciar_servicio();
    }
}