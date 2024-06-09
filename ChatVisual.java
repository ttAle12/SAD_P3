import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.text.*;
import java.io.IOException;

public class ChatVisual {
    private static JTextPane areaEntrada;
    private static JButton botonEnviar;
    private static JTextField campoUsuario;
    private static JFrame inicioChat;
    private static JFrame ventanaPrincipal;
    private static JTextPane areaMensajes;
    private DefaultListModel<String> modeloUsuarios;
    private JList<String> listaUsuarios;
    private static MiSocket socket;
    private String nombreUsuario;
    private String mensajeUsuario;
    private JTextField campoTexto;

    public ChatVisual() throws IOException {
        socket = new MiSocket("localhost", 8080);
        modeloUsuarios = new DefaultListModel<>();
        listaUsuarios = new JList<>(modeloUsuarios);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ChatVisual chat = new ChatVisual();
                chat.iniciarChat();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void iniciarChat() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        inicioChat = new JFrame("Chat");
        inicioChat.getContentPane().setLayout(new BoxLayout(inicioChat.getContentPane(), BoxLayout.PAGE_AXIS));
        inicioChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelUsuario = new JPanel();
        panelUsuario.setLayout(new BoxLayout(panelUsuario, BoxLayout.PAGE_AXIS));
        JLabel etiquetaNuevoUsuario = new JLabel("Introduce tu nombre de usuario");
        campoUsuario = new JTextField(25);
        JButton botonCrear = new JButton("Crear");
        botonCrear.addActionListener(new AccionBotonEntrar());

        panelUsuario.add(etiquetaNuevoUsuario);
        panelUsuario.add(campoUsuario);
        panelUsuario.add(botonCrear);

        // Agregamos el campo de texto campoTexto
        campoTexto = new JTextField(25);
        panelUsuario.add(campoTexto);

        inicioChat.add(panelUsuario, BorderLayout.PAGE_END);

        inicioChat.setSize(400, 150);
        inicioChat.setLocationRelativeTo(null);
        inicioChat.setResizable(false);
        inicioChat.setVisible(true);
    }

    public void pantallaPrincipal() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        ventanaPrincipal = new JFrame("Chat");
        ventanaPrincipal.getContentPane().setLayout(new BoxLayout(ventanaPrincipal.getContentPane(), BoxLayout.PAGE_AXIS));
        ventanaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaPrincipal.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                socket.escribirLinea("Exit " + nombreUsuario);
                actualizarListaUsuarios("Exit " + nombreUsuario);
            }
        });

        areaMensajes = new JTextPane();
        areaMensajes.setEditable(false);
        JScrollPane scrollMensajes = new JScrollPane(areaMensajes);
        scrollMensajes.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        areaEntrada = new JTextPane();
        areaEntrada.setLayout(new BoxLayout(areaEntrada, BoxLayout.LINE_AXIS));

        botonEnviar = new JButton("Enviar");
        botonEnviar.addActionListener(new AccionBotonEnviar());

        areaEntrada.add(campoTexto);
        areaEntrada.add(botonEnviar);
        areaEntrada.setMaximumSize(new Dimension(areaEntrada.getMaximumSize().width, areaEntrada.getMinimumSize().height));

        ventanaPrincipal.add(scrollMensajes);
        ventanaPrincipal.add(areaEntrada);

        ventanaPrincipal.setSize(400, 500);
        ventanaPrincipal.setLocationRelativeTo(null);
        ventanaPrincipal.setResizable(false);
        ventanaPrincipal.setVisible(true);
    }

    private class AccionBotonEnviar implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            mensajeUsuario = campoTexto.getText();
            if (mensajeUsuario.length() >= 1) {
                campoTexto.setText("");
                agregarMensaje(nombreUsuario + ": " + mensajeUsuario + "\n", Color.GREEN);
                socket.escribirLinea(mensajeUsuario);
            }
            campoTexto.requestFocusInWindow();
        }
    }

    private void agregarMensaje(String mensaje, Color color) {
        StyledDocument documento = areaMensajes.getStyledDocument();
        Style estilo = documento.addStyle("Estilo", null);
        StyleConstants.setForeground(estilo, color);
        try {
            documento.insertString(documento.getLength(), mensaje, estilo);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(new JFrame(), mensaje, "Dialogo", JOptionPane.ERROR_MESSAGE);
    }

    void entrarServidor() {
        nombreUsuario = campoUsuario.getText();

        if (nombreUsuario.length() < 1) {
            mostrarMensajeError("Por favor, complete los campos requeridos");
        } else {
            try {
                socket.escribirLinea(nombreUsuario);
                System.out.println("Usuario " + nombreUsuario);
                String conectado = socket.leerLinea();
                if (conectado.equals("Exist")) {
                    System.out.println("El nombre de usuario ya existe");
                    mostrarMensajeError("El usuario ya existe");
                } else {
                    actualizarListaUsuarios(nombreUsuario);
                    inicioChat.setVisible(false);
                    pantallaPrincipal();
                    iniciarEscucha();
                }

            } catch (Exception e) {
                mostrarMensajeError(
                        "Parece que los datos que ingresaste son incorrectos, asegúrate de que los campos están en el formato correcto y que un servidor está escuchando en el puerto especificado");
            }

        }
    }

    private void iniciarEscucha() {
        new Thread(() -> {
            String linea;
            try {
                while ((linea = socket.leerLinea()) != null) {
                    agregarMensaje(linea + "\n", Color.RED);
                    if (linea.contains("Exit")) {
                        actualizarListaUsuarios(linea);
                    } else {
                        String[] partes = linea.split(":");
                        actualizarListaUsuarios(partes[0]);
                    }
                }
            } catch (Exception ex) {
                socket.cerrar();
                System.exit(0);
            }
        }).start();
    }

    private class AccionBotonEntrar implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            entrarServidor();
        }
    }

    private void actualizarListaUsuarios(String nombreUsuario) {
        if (nombreUsuario.contains("Exit")) {
            String nombre = nombreUsuario.substring(5);
            if (modeloUsuarios.contains(nombre)) {
                modeloUsuarios.removeElement(nombre);
                System.out.println("Eliminamos NOMBRE" + nombre);
            }
        } else {
            String nombre = nombreUsuario.substring(0);
            if (!modeloUsuarios.contains(nombre)) {
                modeloUsuarios.addElement(nombre);

                for (int i = 0; i < modeloUsuarios.size(); i++) {
                    System.out.println(listaUsuarios.getModel().getElementAt(i));
                }
                System.out.println(nombre);
            }
        }
    }
}


