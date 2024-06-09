import java.net.ServerSocket;
import java.io.IOException;

public class MiServidorSocket extends ServerSocket {

    public ServerSocket servidorSocket;
    public boolean conectado;

    // Crea un socket de servidor, vinculado al puerto especificado.
    public MiServidorSocket(int puerto) throws IOException {
        this.servidorSocket = new ServerSocket(puerto);
        this.conectado = true;
    }

    // Retorna el puerto en el que se encuentra el servidor
    public int obtenerPuertoLocal() {
        return servidorSocket.getLocalPort();
    }

    // Retorna true si hay alguien conectado al servidor
    public boolean estaConectado() {
        return conectado;
    }

    // Retorna true si el servidor está cerrado
    public boolean estaCerrado() {
        return !conectado;
    }

    // Escucha si se realiza una conexión a este socket y la acepta
    public MiSocket aceptar() {
        try {
            if (conectado) {
                return new MiSocket(servidorSocket.accept());
            } else {
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Cerramos el socket
    public void cerrar() {
        try {
            conectado = false;
            if (this.servidorSocket != null && !this.servidorSocket.isClosed()) {
                servidorSocket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

