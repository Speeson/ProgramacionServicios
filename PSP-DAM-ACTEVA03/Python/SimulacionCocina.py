import threading
import time
import random
from datetime import datetime


class Pedido:
    """Clase que representa un pedido con ID y nombre del plato"""
    
    def __init__(self, id_pedido, nombre_plato):
        self.id_pedido = id_pedido
        self.nombre_plato = nombre_plato
    
    def __str__(self):
        return f"Pedido #{self.id_pedido}: {self.nombre_plato}"


class Cocinero(threading.Thread):
    """Hilo que representa un cocinero procesando pedidos"""
    
    def __init__(self, nombre, cocina):
        super().__init__()
        self.nombre = nombre
        self.cocina = cocina
    
    def run(self):
        """Método principal del hilo - procesa pedidos mientras haya disponibles"""
        while True:
            pedido = self.cocina.tomar_pedido()
            
            if pedido is None:
                break
            
            # Simular la preparación del pedido (1-3 segundos)
            tiempo_preparacion = 2
            print(f"[{self.nombre}] Preparando {pedido}...")
            time.sleep(tiempo_preparacion)
            
            # Registrar el pedido completado
            self.cocina.registrar_pedido(self.nombre, pedido)
            
            print(f"[{self.nombre}] {pedido} completado ✓")


class Cocina:
    """Clase principal que gestiona la lista de pedidos y los cocineros"""
    
    def __init__(self):
        self.pedidos = []
        self.lock = threading.Lock()
        self.archivo_log = "log_pedidos.txt"
        
        # Inicializar el archivo de log
        with open(self.archivo_log, 'w', encoding='utf-8') as f:
            f.write("=== LOG DE PEDIDOS ===\n")
            f.write(f"Inicio: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n")
    
    def agregar_pedido(self, pedido):
        """Agregar un pedido a la lista"""
        self.pedidos.append(pedido)
    
    def tomar_pedido(self):
        """
        Método sincronizado para que un cocinero tome un pedido.
        Retorna None si no hay más pedidos.
        """
        with self.lock:
            if len(self.pedidos) > 0:
                return self.pedidos.pop(0)
            else:
                return None
    
    def registrar_pedido(self, nombre_cocinero, pedido):
        """
        Método sincronizado para registrar un pedido en el archivo log.
        """
        with self.lock:
            timestamp = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            mensaje = f"[{timestamp}] {nombre_cocinero} completó {pedido}\n"
            
            with open(self.archivo_log, 'a', encoding='utf-8') as f:
                f.write(mensaje)
    
    def iniciar_servicio(self, num_cocineros):
        """
        Crear y lanzar los hilos de los cocineros.
        Esperar a que todos terminen.
        """
        print("=== INICIANDO SERVICIO DE COCINA ===\n")
        
        # Crear los cocineros
        cocineros = []
        for i in range(num_cocineros):
            nombre = f"Cocinero-{i+1}"
            cocinero = Cocinero(nombre, self)
            cocineros.append(cocinero)
        
        # Iniciar todos los hilos
        for cocinero in cocineros:
            cocinero.start()
        
        # Esperar a que todos los cocineros terminen
        for cocinero in cocineros:
            cocinero.join()
        
        # Finalizar el log
        with open(self.archivo_log, 'a', encoding='utf-8') as f:
            f.write(f"\nFin: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
        
        print("\n=== SERVICIO FINALIZADO ===")
        print("Todos los pedidos han sido procesados.")


def main():
    """Función principal del programa"""
    
    # Crear la cocina
    cocina = Cocina()
    
    # Agregar los pedidos (mínimo 6 según requisitos)
    platos = [
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
    ]
    
    num_pedidos = random.randint(6, 10)
    
    print("Pedidos en cola:")
    
    # Crear pedidos con platos aleatorios
    for i in range(num_pedidos):
        plato_aleatorio = random.choice(platos)
        pedido = Pedido(i + 1, plato_aleatorio)
        cocina.agregar_pedido(pedido)
        print(f"  - {pedido}")
    
    print()
    
    # Iniciar el servicio con 3 cocineros (mínimo según requisitos)
    num_cocineros = 3
    cocina.iniciar_servicio(num_cocineros)


if __name__ == "__main__":
    main()