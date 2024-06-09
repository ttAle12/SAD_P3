import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

// Para ejectuar el cliente se debe hacer de la siguiente manera:
// >> java Client localhost 8080
public class Client {

    public static void main(String[] args) throws IOException {
        // args[0] fara que agafem el primer parametre que li pasem despres d'executar
        // el programa
        //host //port
        MySocket socket = new MySocket(args[0], Integer.parseInt(args[1]));

        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
       
        // Input threat (keyboard)
        new Thread(() -> {
            String linea;
            try {
                while ((linea = teclado.readLine()) != null) {
                    socket.printLine(linea);
                    if(linea.matches("exit")){
                        socket.close();
                        break;
                    }
                }
                socket.printLine("exit");
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();

        // Output threat (console)
        new Thread(() -> {
            String linea;
            while ((linea = socket.readLine()) != null) {
                if(linea.matches("exit")){
                    break;
                }
                System.out.println(linea);
            }
            System.out.println("Client Disconnected!!!");
                socket.close();
                System.exit(0);
        }).start();
    }
}


