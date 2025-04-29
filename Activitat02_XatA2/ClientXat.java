import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ClientXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static Socket socket;
    private static OutputStream out;
    private BufferedReader in;

    public void connecta() {
        try {
            socket = new Socket(HOST, PORT);
            System.out.println("Client connectat a " + HOST + ":" + PORT);
            out = socket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Initialize 'in'
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            System.out.println("Error al crear el socket del servidor: " + e.getMessage());
        }
    }

    public void tanca() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("\nClient tancat");
            
            System.out.println("El servidor ha tancat la connexió");
        } catch (IOException e) {
            System.out.println("Error al tancar la connexió: " + e.getMessage());
        }
    }

    public void envia(String missatge) {
        try {
            out.write((missatge + "\n").getBytes());
            out.flush();
            System.out.println("Enviant missatge: " + missatge);
        } catch (IOException e) {
            System.out.println("Error al enviar el missatge: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();

        FilLectorCX filLector = new FilLectorCX(client.in);
        filLector.start();
        System.out.println("Rebut: Escriu el teu nom:");
        System.out.println("Fil de lectura iniciat");
        try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
            
            String input;
            while ((input = userInput.readLine()) != null) {
                client.envia(input);
                if (input.equalsIgnoreCase("sortir")) {
                    break; 
                }
            }
        } catch (IOException e) {
            System.out.println("Error al llegir l'entrada de l'usuari: " + e.getMessage());
        }

        client.tanca();
    }
}
