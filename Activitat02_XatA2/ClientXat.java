
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static Socket socket;
    private static PrintWriter out;

    public void connecta(){
        try{
            socket = new Socket(HOST, PORT);
            System.out.println("Connectat a servidor en" + HOST + ":" + PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch(IOException e){
            System.out.println("Error al crear el socket del servidor: " + e.getMessage());
        }
    }
    public void envia(String missatge){
        out.println(missatge);
        System.out.println("Enviant al servidor: " + missatge);
    }
    public void tanca(){
        try{
            out.close();
            socket.close();
            System.out.println("\n\nClient tancat");
        }
        catch(IOException e){
            System.out.println("Error al tancar la connexió: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();
        try (BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
            String input;
            while ((input = userInput.readLine()) != null) {
                client.envia(input);
            }
        } catch (IOException e) {
            System.out.println("Error al llegir l'entrada de l'usuari: " + e.getMessage());
        }
        client.tanca();
    }
}
