import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Para ejecutar el servidor se debe hacer de la siguiente manera:
// >> java Servidor

public class Servidor {
    // Puerto en el que se ejecutará el servidor
    private static final int PUERTO = 8080;
    // Diccionario de clientes por su apodo y su socket
    private static Map<String, MiSocket> diccionarioClientes = new ConcurrentHashMap<>();
    public MiSocket miSocket;

    public static void main(String[] args) throws IOException {

        MiServerSocket miServerSocket = null;

        miServerSocket = new MiServerSocket(PUERTO);
        System.out.println("¡Servidor INICIADO!");

        while (true) {
            // Esperamos la siguiente conexión del cliente
            MiSocket cliente = miServerSocket.accept();
            cliente.imprimirLinea("¡Conectado al Servidor!");

            new Thread() {
                public void run() {
                    cliente.imprimirLinea("Introduce tu nombre de usuario: ");
                    String nombre = cliente.leerLinea();
                    cliente.imprimirLinea("¡Hola " + nombre + ", ahora estás en el chat!");
                    diccionarioClientes.put(nombre, cliente);
                    String mensaje;
                    while ((mensaje = cliente.leerLinea()) != null) {
                        difundir(mensaje, nombre);
                        System.out.println(nombre + " dice: " + mensaje);
                    }
                    System.out.println(nombre + " ha abandonado el chat");
                    diccionarioClientes.remove(nombre);
                    cliente.cerrar();
                }
            }.start();
        }
    }

    public static void difundir(String mensaje, String nombre) {
        MiSocket cliente = diccionarioClientes.get(nombre);

        for (Map.Entry<String, MiSocket> entrada : diccionarioClientes.entrySet()) {
            String usuarioActual = entrada.getKey();
            MiSocket socketActual = entrada.getValue();
            if (!usuarioActual.equals(nombre)) {
                socketActual.imprimirLinea(nombre + "> " + mensaje);
            }
        }
    }
}

