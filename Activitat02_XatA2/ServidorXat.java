

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ServidorXat{
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static String MSG_SORTIR = "sortir";
    private static FilServidorXat fsx;

    public static void iniciarServidor(){
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a " + HOST + ":" + PORT);
            socket = serverSocket.accept();
            
        } catch (IOException e) {
            System.out.println("Error al crear el socket del servidor: " + e.getMessage());
        }
    }
    public static void  pararServidor(){
        try{
            serverSocket.close();
            socket.close();
            System.out.println("Servidor aturat.");
        }
        catch(IOException e){
            System.out.println("Error al tancar la connexió: " + e.getMessage());
        }
    }

    public static String getNom(BufferedReader in) {
        String nomClient = "";
        try {
            nomClient = in.readLine();
            System.out.println("Nom rebut: " + nomClient);
        } catch (IOException e) {
            System.out.println("Error al llegir el nom del client: " + e.getMessage());
        }
        return nomClient;
    }

    public static void main(String[] args) {
        iniciarServidor();
        try {
            System.out.println("Client connectat: /127.0.0.1");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String nomClient = getNom(in);

            FilServidorXat fil = new FilServidorXat(in, MSG_SORTIR);
            System.out.println();
            fil.start();
            System.out.println("Fil de  " + nomClient+" creat");
            fil.join();
            System.out.println("Fil de  " + nomClient+" iniciat");
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al servidor: " + e.getMessage());
        } finally {
            pararServidor();
        }
    }
}