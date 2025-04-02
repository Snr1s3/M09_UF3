import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
public class Servidor{
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private static ServerSocket serverSocket;
    private static Socket socket;

    public void connecta() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a " + HOST + ":" + PORT);
            System.out.println("Esperant connexions a " + HOST + ":" + PORT);
            socket = serverSocket.accept();
            System.out.println("Client connectat: /127.0.0.1");
        } catch (IOException e) {
            System.out.println("Error al crear el socket del servidor: " + e.getMessage());
        }
    }

    public void repDades() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Rebut: " + line);
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Error al acceptar la connexió: " + e.getMessage());
        }
    }
    public void tanca(){
        try{
            serverSocket.close();
            socket.close();
            System.out.println("Servidor tancat.");
        }
        catch(IOException e){
            System.out.println("Error al tancar la connexió: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.connecta();
        servidor.repDades();
        servidor.tanca();
    }
}